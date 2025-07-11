import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

public class StructuredConcurrencyDemo {

    // Define the different thread counts to test
    static final int[] THREAD_COUNTS = {1000, 10000, 50000, 100000};

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(20) + " Scalability Trend " + "=".repeat(20));
        System.out.printf("%-15s %-15s %-20s%n", "Thread Type", "Thread Count", "Startup Time (ms)");
        System.out.println("-".repeat(55));

        for (int count : THREAD_COUNTS) {
            long platformTime = measureStartupTime(count, false);
            System.out.printf("%-15s %-15d %-20d%n", "Platform", count, platformTime);

            long virtualTime = measureStartupTime(count, true);
            System.out.printf("%-15s %-15d %-20d%n", "Virtual", count, virtualTime);
        }

        System.out.println("=".repeat(55));
    }

    private static long measureStartupTime(int threadCount, boolean isVirtual) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);

        Runnable task = () -> {
            // Simulate short task, e.g., CPU registration
            latch.countDown();
        };

        ThreadFactory factory = isVirtual ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();

        Instant start = Instant.now();
        for (int i = 0; i < threadCount; i++) {
            factory.newThread(task).start();
        }
        latch.await(); // Wait for all threads to finish
        Instant end = Instant.now();

        return Duration.between(start, end).toMillis();
    }
}
