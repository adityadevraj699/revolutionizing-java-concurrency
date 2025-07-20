import java.lang.management.ManagementFactory; // JVM thread management tools
import java.lang.management.ThreadMXBean;       // Used to get thread stats
import java.time.Duration;                      // Used to measure elapsed time
import java.time.Instant;                       // Current timestamp
import java.util.*;                              // Utility classes like List, Collections
import java.util.concurrent.CountDownLatch;     // Thread synchronization tool
import java.util.concurrent.ThreadFactory;      // To create platform or virtual threads

public class IOMetricBenchmark {

    public static void main(String[] args) throws InterruptedException {
        int[] requests = {100, 500, 1000, 2000}; // Define workloads for testing

        // Header for output
        System.out.printf("%-20s %-20s %-20s %-20s%n", "Requests", "Metric", "Platform Thread", "Virtual Thread");
        System.out.println("--------------------------------------------------------------------------------");

        for (int threadCount : requests) { // Loop through each test case
            Metrics platformMetrics = runBenchmark(threadCount, false); // Run with platform threads
            Metrics virtualMetrics = runBenchmark(threadCount, true);   // Run with virtual threads

            // Print results for throughput and latency
            System.out.printf("%-20d %-20s %-20d %-20d%n", threadCount, "Throughput (r/s)",
                    platformMetrics.throughput, virtualMetrics.throughput);

            System.out.printf("%-20s %-20s %-20.2f %-20.2f%n", "", "Avg Latency (ms)",
                    platformMetrics.avgLatency, virtualMetrics.avgLatency);

            System.out.printf("%-20s %-20s %-20.2f %-20.2f%n", "", "P95 Latency (ms)",
                    platformMetrics.p95Latency, virtualMetrics.p95Latency);

            System.out.printf("%-20s %-20s %-20.2f %-20.2f%n", "", "P99 Latency (ms)",
                    platformMetrics.p99Latency, virtualMetrics.p99Latency);

            System.out.printf("%-20s %-20s %-20d %-20d%n", "", "Thread Creation Time (ms)",
                    platformMetrics.creationTimeMillis, virtualMetrics.creationTimeMillis);

            System.out.printf("%-20s %-20s %-20d %-20d%n", "", "Live Threads",
                    platformMetrics.liveThreads, virtualMetrics.liveThreads);

            System.out.printf("%-20s %-20s %-20d %-20d%n", "", "Total Threads Created",
                    platformMetrics.totalThreadsCreated, virtualMetrics.totalThreadsCreated);

            System.out.printf("%-20s %-20s %-20d KB %-20d KB%n", "", "Memory Used",
                    platformMetrics.memoryUsedKB, virtualMetrics.memoryUsedKB);

            System.out.println("--------------------------------------------------------------------------------");
        }
    }

    private static Metrics runBenchmark(int threadCount, boolean isVirtual) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount); // Waits until all threads finish
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>()); // Store latencies

        // Choose thread type: virtual or platform
        ThreadFactory threadFactory = isVirtual
                ? Thread.ofVirtual().name("VThread-", 0).factory()
                : Thread.ofPlatform().name("PThread-", 0).factory();

        // Monitoring setup: count threads and memory before
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long threadsBefore = threadBean.getTotalStartedThreadCount();
        int liveBefore = threadBean.getThreadCount();

        Runtime runtime = Runtime.getRuntime(); // JVM runtime
        long memoryBeforeKB = (runtime.totalMemory() - runtime.freeMemory()) / 1024; // Used memory before

        // Task for each thread to run
        Runnable task = () -> {
            long start = System.nanoTime(); // Start time
            try {
                simulateIO(); // Simulate I/O delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt flag
            } finally {
                long latency = Duration.ofNanos(System.nanoTime() - start).toMillis(); // Time taken
                latencies.add(latency); // Add to list
                latch.countDown(); // Mark this thread done
            }
        };

        List<Thread> threads = new ArrayList<>(); // To hold all threads
        Instant creationStart = Instant.now(); // Record thread creation start
        for (int i = 0; i < threadCount; i++) {
            threads.add(threadFactory.newThread(task)); // Create threads
        }
        Instant creationEnd = Instant.now(); // Thread creation end
        long creationDuration = Duration.between(creationStart, creationEnd).toMillis(); // Time to create threads

        Instant start = Instant.now(); // Benchmark start
        for (Thread thread : threads) {
            thread.start(); // Start all threads
        }

        latch.await(); // Wait until all threads complete
        Instant end = Instant.now(); // Benchmark end

        long totalDurationMillis = Duration.between(start, end).toMillis(); // Total execution time
        if (totalDurationMillis == 0) totalDurationMillis = 1; // Avoid division by zero
        long throughput = (threadCount * 1000L) / totalDurationMillis; // Requests per second

        // Latency calculations
        double avg = latencies.stream().mapToLong(Long::longValue).average().orElse(0);
        Collections.sort(latencies);
        double p95 = latencies.get((int) (latencies.size() * 0.95) - 1);
        double p99 = latencies.get((int) (latencies.size() * 0.99) - 1);

        // Get updated thread and memory stats
        long threadsAfter = threadBean.getTotalStartedThreadCount();
        int liveAfter = threadBean.getThreadCount();
        long memoryAfterKB = (runtime.totalMemory() - runtime.freeMemory()) / 1024;

        // Return metrics for this test
        return new Metrics(
                throughput,
                avg,
                p95,
                p99,
                creationDuration,
                (int) (threadsAfter - threadsBefore),
                liveAfter,
                (int) (memoryAfterKB - memoryBeforeKB)
        );
    }

    private static void simulateIO() throws InterruptedException {
        Thread.sleep(150); // Simulate a blocking I/O delay (150ms)
    }

    // Metrics class to hold result data
    static class Metrics {
        long throughput;              // Requests per second
        double avgLatency;           // Average latency in ms
        double p95Latency;           // 95th percentile latency
        double p99Latency;           // 99th percentile latency
        long creationTimeMillis;     // Thread creation time in ms
        int totalThreadsCreated;     // Total threads created during run
        int liveThreads;             // Live threads after completion
        int memoryUsedKB;            // Memory used in KB

        public Metrics(long throughput, double avgLatency, double p95Latency, double p99Latency,
                       long creationTimeMillis, int totalThreadsCreated, int liveThreads, int memoryUsedKB) {
            this.throughput = throughput;
            this.avgLatency = avgLatency;
            this.p95Latency = p95Latency;
            this.p99Latency = p99Latency;
            this.creationTimeMillis = creationTimeMillis;
            this.totalThreadsCreated = totalThreadsCreated;
            this.liveThreads = liveThreads;
            this.memoryUsedKB = memoryUsedKB;
        }
    }
}
