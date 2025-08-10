import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class MixedThreadBenchmark {

    static int[] THREAD_COUNTS = {1000, 5000, 10000,20000}; // You can modify as needed

    public static void main(String[] args) throws Exception {
        for (int count : THREAD_COUNTS) {
            System.out.println("\n===== Benchmark: " + count + " Threads =====");
            runPlatformThreads(count);
            runVirtualThreads(count);
            runHybridThreads(count); // I/O on virtual, CPU on platform
        }
    }

    // Simulated I/O task
    public static void simulateIOTask() {
        try {
            Thread.sleep(60); // Simulated network delay
            Thread.sleep(90); // Simulated DB delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Simulated CPU-intensive task (prime number summation)
    public static void simulateCPUTask() {
        long sum = 0;
        for (int i = 2; i < 5000; i++) {
            if (isPrime(i)) {
                sum += i;
            }
        }
    }

    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    // Run all mixed tasks (both I/O + CPU) on platform threads
    public static void runPlatformThreads(int threadCount) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(200);
        runBenchmark("Platform", threadCount, executor, true);
    }

    // Run all mixed tasks (both I/O + CPU) on virtual threads
    public static void runVirtualThreads(int threadCount) throws InterruptedException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        runBenchmark("Virtual", threadCount, executor, true);
    }

    // Run I/O tasks on virtual threads, CPU tasks on platform threads
    public static void runHybridThreads(int threadCount) throws InterruptedException {
        int half = threadCount / 2;
        ExecutorService platform = Executors.newFixedThreadPool(100); // CPU tasks
        ExecutorService virtual = Executors.newVirtualThreadPerTaskExecutor(); // I/O tasks

        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(threadCount);

        Instant creationStart = Instant.now();

        // CPU-bound tasks on platform threads
        for (int i = 0; i < half; i++) {
            platform.submit(() -> {
                recordCPULatency(latencies);
                latch.countDown();
            });
        }

        // I/O-bound tasks on virtual threads
        for (int i = 0; i < threadCount - half; i++) {
            virtual.submit(() -> {
                recordIOLatency(latencies);
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

        printMetrics("Hybrid", threadCount, latencies, start, end, creationTime);
    }

    // Generic benchmark runner
    public static void runBenchmark(String model, int threadCount, ExecutorService executor, boolean runMixedTask) throws InterruptedException {
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(threadCount);

        Instant creationStart = Instant.now();
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                if (runMixedTask) {
                    recordMixedLatency(latencies);
                }
                latch.countDown();
            });
        }
        Instant creationEnd = Instant.now();
        Duration creationTime = Duration.between(creationStart, creationEnd);

        Instant start = Instant.now();
        latch.await();
        Instant end = Instant.now();

        executor.shutdown();

        printMetrics(model, threadCount, latencies, start, end, creationTime);
    }

    // Record latency for both I/O + CPU task
    public static void recordMixedLatency(List<Long> latencies) {
        Instant taskStart = Instant.now();
        simulateIOTask();
        simulateCPUTask();
        Instant taskEnd = Instant.now();
        long latency = Duration.between(taskStart, taskEnd).toMillis();
        latencies.add(latency);
    }

    // Record latency for CPU-only (Hybrid model)
    public static void recordCPULatency(List<Long> latencies) {
        Instant taskStart = Instant.now();
        simulateCPUTask();
        Instant taskEnd = Instant.now();
        long latency = Duration.between(taskStart, taskEnd).toMillis();
        latencies.add(latency);
    }

    // Record latency for IO-only (Hybrid model)
    public static void recordIOLatency(List<Long> latencies) {
        Instant taskStart = Instant.now();
        simulateIOTask();
        Instant taskEnd = Instant.now();
        long latency = Duration.between(taskStart, taskEnd).toMillis();
        latencies.add(latency);
    }

    // Print results
    public static void printMetrics(String model, int threadCount, List<Long> latencies,
                                    Instant start, Instant end, Duration creationTime) {
        Duration duration = Duration.between(start, end);
        double totalTimeSec = duration.toMillis() / 1000.0;
        double throughput = threadCount / totalTimeSec;
        double avgLatency = latencies.stream().mapToLong(Long::longValue).average().orElse(-1);
        long creationTimeMs = creationTime.toMillis();

        System.out.printf("Model: %-9s | Threads: %-6d | Time: %.2fs | Throughput: %.2f req/sec | Avg Latency: %.2f ms | Creation Time: %d ms%n",
                model, threadCount, totalTimeSec, throughput, avgLatency, creationTimeMs);
    }
}
