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
        int taskCount = 10_000; // Number of total tasks to be run

        // Heading for the benchmark report
        System.out.println("=".repeat(30));
        System.out.println("📊 Mixed Workload Benchmark Report");
        System.out.println("=".repeat(30));

        // Benchmark using platform threads
        System.out.println("\n🧵 Platform Threads:");
        runBenchmark(Executors.newFixedThreadPool(100), taskCount, "Platform");

        // Benchmark using virtual threads
        System.out.println("\n🧶 Virtual Threads:");
        runBenchmark(Executors.newVirtualThreadPerTaskExecutor(), taskCount, "Virtual");
    }

    // Benchmark method to evaluate thread performance
    static void runBenchmark(ExecutorService executor, int taskCount, String label) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(taskCount); // Synchronization aid
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>()); // Store latencies

        // Thread monitoring setup
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long startThreadCount = threadBean.getTotalStartedThreadCount(); // Threads before start

        Instant benchmarkStart = Instant.now(); // Start time for the entire benchmark

        // Submit each task to the executor
        for (int i = 0; i < taskCount; i++) {
            executor.submit(() -> {
                Instant start = Instant.now(); // Task start time
                try {
                    simulateMixedWorkload(); // Simulated workload (I/O + CPU)
                } finally {
                    // Record task duration in milliseconds
                    long latency = Duration.between(start, Instant.now()).toMillis();
                    latencies.add(latency);
                    latch.countDown(); // Mark task as completed
                }
            });
        }

        latch.await(); // Wait for all tasks to finish
        Instant benchmarkEnd = Instant.now(); // End time for the benchmark

        // Thread and memory metrics
        long endThreadCount = threadBean.getTotalStartedThreadCount();
        long threadCreated = endThreadCount - startThreadCount;
        long totalTimeMs = Duration.between(benchmarkStart, benchmarkEnd).toMillis();
        long memoryKB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;

        // Calculate average and percentile latencies
        List<Long> sortedLatencies = new ArrayList<>(latencies);
        Collections.sort(sortedLatencies);
        double avgLatency = sortedLatencies.stream().mapToLong(Long::longValue).average().orElse(0);
        long p95 = sortedLatencies.get((int)(sortedLatencies.size() * 0.95)); // 95th percentile
        long p99 = sortedLatencies.get((int)(sortedLatencies.size() * 0.99)); // 99th percentile

        // Calculate throughput: tasks per second
        long throughput = (taskCount * 1000L) / Math.max(1, totalTimeMs);

        // Print performance metrics
        System.out.println("🔁 Total Tasks       : " + taskCount);
        System.out.println("⏱ Total Time        : " + totalTimeMs + " ms");
        System.out.println("🚀 Throughput        : " + throughput + " tasks/sec");
        System.out.println("⏳ Avg Latency       : " + String.format("%.2f", avgLatency) + " ms");
        System.out.println("📈 P95 Latency       : " + p95 + " ms");
        System.out.println("📉 P99 Latency       : " + p99 + " ms");
        System.out.println("📦 Memory Used       : " + memoryKB + " KB");
        System.out.println("🧵 Threads Created   : " + threadCreated);
        System.out.println("🔒 Live Threads Now  : " + threadBean.getThreadCount());

        executor.shutdown(); // Cleanly shutdown executor
    }

    // Simulate a mixed workload: first I/O delay, then CPU computation
    static void simulateMixedWorkload() {
        // Simulate I/O wait using sleep
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {}

        // Simulate CPU-bound computation by calculating square roots
        double sum = 0;
        for (int i = 1; i <= 10_000; i++) {
            sum += Math.sqrt(i);
        }
    }
}
