import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class MixedBenchmark {

    public static void main(String[] args) throws InterruptedException {
        int taskCount = 10_000;

        System.out.println("=".repeat(30));
        System.out.println("📊 Mixed Workload Benchmark Report");
        System.out.println("=".repeat(30));

        System.out.println("\n🧵 Platform Threads:");
        runBenchmark(Executors.newFixedThreadPool(100), taskCount, "Platform");

        System.out.println("\n🧶 Virtual Threads:");
        runBenchmark(Executors.newVirtualThreadPerTaskExecutor(), taskCount, "Virtual");
    }

    static void runBenchmark(ExecutorService executor, int taskCount, String label) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(taskCount);
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());

        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long startThreadCount = threadBean.getTotalStartedThreadCount();

        Instant benchmarkStart = Instant.now();

        for (int i = 0; i < taskCount; i++) {
            executor.submit(() -> {
                Instant start = Instant.now();
                try {
                    simulateMixedWorkload();
                } finally {
                    long latency = Duration.between(start, Instant.now()).toMillis();
                    latencies.add(latency);
                    latch.countDown();
                }
            });
        }

        latch.await();
        Instant benchmarkEnd = Instant.now();

        long endThreadCount = threadBean.getTotalStartedThreadCount();
        long threadCreated = endThreadCount - startThreadCount;

        long totalTimeMs = Duration.between(benchmarkStart, benchmarkEnd).toMillis();
        long memoryKB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;

        // Latency calculations
        List<Long> sortedLatencies = new ArrayList<>(latencies);
        Collections.sort(sortedLatencies);
        double avgLatency = sortedLatencies.stream().mapToLong(Long::longValue).average().orElse(0);
        long p95 = sortedLatencies.get((int)(sortedLatencies.size() * 0.95));
        long p99 = sortedLatencies.get((int)(sortedLatencies.size() * 0.99));

        long throughput = (taskCount * 1000L) / Math.max(1, totalTimeMs);

        // Output
        System.out.println("🔁 Total Tasks       : " + taskCount);
        System.out.println("⏱ Total Time        : " + totalTimeMs + " ms");
        System.out.println("🚀 Throughput        : " + throughput + " tasks/sec");
        System.out.println("⏳ Avg Latency       : " + String.format("%.2f", avgLatency) + " ms");
        System.out.println("📈 P95 Latency       : " + p95 + " ms");
        System.out.println("📉 P99 Latency       : " + p99 + " ms");
        System.out.println("📦 Memory Used       : " + memoryKB + " KB");
        System.out.println("🧵 Threads Created   : " + threadCreated);
        System.out.println("🔒 Live Threads Now  : " + threadBean.getThreadCount());

        executor.shutdown();
    }

    static void simulateMixedWorkload() {
        // Simulated I/O delay
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {}

        // CPU-bound task
        double sum = 0;
        for (int i = 1; i <= 10_000; i++) {
            sum += Math.sqrt(i);
        }
    }
}
