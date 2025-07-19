import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

public class IOMetricBenchmark {

    public static void main(String[] args) throws InterruptedException {
        int[] requests = {100, 500, 1000, 2000};

        System.out.printf("%-20s %-20s %-20s %-20s%n", "Requests", "Metric", "Platform Thread", "Virtual Thread");
        System.out.println("--------------------------------------------------------------------------------");

        for (int threadCount : requests) {
            Metrics platformMetrics = runBenchmark(threadCount, false);
            Metrics virtualMetrics = runBenchmark(threadCount, true);

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
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());

        ThreadFactory threadFactory = isVirtual
                ? Thread.ofVirtual().name("VThread-", 0).factory()
                : Thread.ofPlatform().name("PThread-", 0).factory();

        // Monitoring setup
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long threadsBefore = threadBean.getTotalStartedThreadCount();
        int liveBefore = threadBean.getThreadCount();

        Runtime runtime = Runtime.getRuntime();
        long memoryBeforeKB = (runtime.totalMemory() - runtime.freeMemory()) / 1024;

        Runnable task = () -> {
            long start = System.nanoTime();
            try {
                simulateIO();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                long latency = Duration.ofNanos(System.nanoTime() - start).toMillis();
                latencies.add(latency);
                latch.countDown();
            }
        };

        List<Thread> threads = new ArrayList<>();
        Instant creationStart = Instant.now();
        for (int i = 0; i < threadCount; i++) {
            threads.add(threadFactory.newThread(task));
        }
        Instant creationEnd = Instant.now();
        long creationDuration = Duration.between(creationStart, creationEnd).toMillis();

        Instant start = Instant.now();
        for (Thread thread : threads) {
            thread.start();
        }

        latch.await();
        Instant end = Instant.now();

        long totalDurationMillis = Duration.between(start, end).toMillis();
        if (totalDurationMillis == 0) totalDurationMillis = 1;
        long throughput = (threadCount * 1000L) / totalDurationMillis;

        double avg = latencies.stream().mapToLong(Long::longValue).average().orElse(0);
        Collections.sort(latencies);
        double p95 = latencies.get((int) (latencies.size() * 0.95) - 1);
        double p99 = latencies.get((int) (latencies.size() * 0.99) - 1);

        long threadsAfter = threadBean.getTotalStartedThreadCount();
        int liveAfter = threadBean.getThreadCount();
        long memoryAfterKB = (runtime.totalMemory() - runtime.freeMemory()) / 1024;

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
        Thread.sleep(150); // Simulate IO latency
    }

    static class Metrics {
        long throughput;
        double avgLatency;
        double p95Latency;
        double p99Latency;
        long creationTimeMillis;
        int totalThreadsCreated;
        int liveThreads;
        int memoryUsedKB;

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
