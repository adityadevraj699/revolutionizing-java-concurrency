import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

public class IoBoundVirtualVsPlatform {

    // Test configurations
    static final int[] REQUEST_LEVELS = { 1000, 10000, 50000 };

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(25) + " Benchmark Summary " + "=".repeat(25));
        System.out.printf("%-20s %-20s %-20s %-20s%n", "Concurrent Requests", "Metric", "Platform Threads",
                "Virtual Threads");
        System.out.println("-".repeat(80));

        for (int req : REQUEST_LEVELS) {
            long platformThroughput = runBenchmark(req, false);
            long virtualThroughput = runBenchmark(req, true);

            System.out.printf("%-20s %-20s %-20d %-20d%n", req, "Throughput (r/s)", platformThroughput,
                    virtualThroughput);
            System.out.printf("%-20s %-20s %-20d %-20d%n", "", "Avg Latency (ms)", 0, 0); // Latency optional
        }

        System.out.println("=".repeat(80));
    }

    private static long runBenchmark(int threadCount, boolean isVirtual) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);

        Runnable task = () -> {
            try {
                simulateIO();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        };

        ThreadFactory threadFactory = isVirtual
                ? Thread.ofVirtual().factory()
                : Thread.ofPlatform().factory();

        Instant start = Instant.now();
        for (int i = 0; i < threadCount; i++) {
            threadFactory.newThread(task).start();
        }

        latch.await(); // Wait for all tasks to complete
        Instant end = Instant.now();

        long durationMillis = Duration.between(start, end).toMillis();
        if (durationMillis == 0)
            durationMillis = 1; // avoid division by zero

        return (threadCount * 1000L) / durationMillis; // r/s = requests / seconds
    }

    // Simulates I/O-bound workload
    private static void simulateIO() throws InterruptedException {
        Thread.sleep(50); // Network delay
        Thread.sleep(100); // Database delay
    }
}
