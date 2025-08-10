import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IOThreadBenchmark
   {

    static int[] THREAD_COUNTS = {1000, 10000, 50000, 100000};

    public static void main(String[] args) throws Exception {
        for (int count : THREAD_COUNTS) {
            System.out.println("\n===== Benchmark: " + count + " Threads =====");
            runPlatformThreads(count);
            runVirtualThreads(count);
            runHybridThreads(count);
        }
    }

    // Simulated I/O-bound task
    public static void simulateIOHeavyTask() {
        try {
            Thread.sleep(60); // Simulate network delay
            Thread.sleep(90); // Simulate DB delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void runPlatformThreads(int threadCount) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(200);
        runBenchmark("Platform", threadCount, executor);
    }

    public static void runVirtualThreads(int threadCount) throws InterruptedException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        runBenchmark("Virtual", threadCount, executor);
    }

    public static void runHybridThreads(int threadCount) throws InterruptedException {
        int half = threadCount / 2;
        ExecutorService platform = Executors.newFixedThreadPool(100);
        ExecutorService virtual = Executors.newVirtualThreadPerTaskExecutor();

        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger completedTasks = new AtomicInteger();

        Instant creationStart = Instant.now();
        for (int i = 0; i < half; i++) {
            platform.submit(() -> {
                recordTaskLatency(latencies);
                completedTasks.incrementAndGet();
                latch.countDown();
            });
        }
        for (int i = 0; i < threadCount - half; i++) {
            virtual.submit(() -> {
                recordTaskLatency(latencies);
                completedTasks.incrementAndGet();
                latch.countDown();
            });
        }
        Instant creationEnd = Instant.now();
        Duration creationTime = Duration.between(creationStart, creationEnd);

        Instant start = Instant.now();
        latch.await();
        Instant end = Instant.now();

        platform.shutdown();
        virtual.shutdown();

        printMetrics("Hybrid", threadCount, completedTasks.get(), latencies, start, end, creationTime);
    }

    public static void runBenchmark(String model, int threadCount, ExecutorService executor) throws InterruptedException {
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger completedTasks = new AtomicInteger();

        Instant creationStart = Instant.now();
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                recordTaskLatency(latencies);
                completedTasks.incrementAndGet();
                latch.countDown();
            });
        }
        Instant creationEnd = Instant.now();
        Duration creationTime = Duration.between(creationStart, creationEnd);

        Instant start = Instant.now();
        latch.await();
        Instant end = Instant.now();
        executor.shutdown();

        printMetrics(model, threadCount, completedTasks.get(), latencies, start, end, creationTime);
    }

    public static void recordTaskLatency(List<Long> latencies) {
        Instant taskStart = Instant.now();
        simulateIOHeavyTask();
        Instant taskEnd = Instant.now();
        long latency = Duration.between(taskStart, taskEnd).toMillis();
        latencies.add(latency);
    }

    public static void printMetrics(String model, int submittedTasks, int completedTasks,
                                    List<Long> latencies, Instant start, Instant end, Duration creationTime) {
        Duration duration = Duration.between(start, end);
        double totalTimeSec = duration.toMillis() / 1000.0;
        double throughput = completedTasks / totalTimeSec;
        double avgLatency = latencies.stream().mapToLong(Long::longValue).average().orElse(-1);
        long creationTimeMs = creationTime.toMillis();

        System.out.printf("Model: %-9s | Submitted: %-6d | Completed: %-6d | Time: %.2fs | Throughput: %.2f req/sec | Avg Latency: %.2f ms | Creation Time: %d ms%n",
                model, submittedTasks, completedTasks, totalTimeSec, throughput, avgLatency, creationTimeMs);

        if (completedTasks != submittedTasks) {
            System.out.printf("⚠ Warning: %d tasks did not complete!%n", submittedTasks - completedTasks);
        }
    }
}
