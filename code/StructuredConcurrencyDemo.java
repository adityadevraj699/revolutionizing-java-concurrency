import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

public class StructuredConcurrencyDemo {

    // Define an array of different thread counts to test scalability
    static final int[] THREAD_COUNTS = {1000, 10000, 50000, 100000};

    public static void main(String[] args) throws InterruptedException {
        // Print report heading
        System.out.println("=".repeat(20) + " Scalability Trend " + "=".repeat(20));

        // Print column headers
        System.out.printf("%-15s %-15s %-20s%n", "Thread Type", "Thread Count", "Startup Time (ms)");
        System.out.println("-".repeat(55));

        // Loop through each defined thread count
        for (int count : THREAD_COUNTS) {
            // Measure startup time for platform threads
            long platformTime = measureStartupTime(count, false);
            System.out.printf("%-15s %-15d %-20d%n", "Platform", count, platformTime);

            // Measure startup time for virtual threads
            long virtualTime = measureStartupTime(count, true);
            System.out.printf("%-15s %-15d %-20d%n", "Virtual", count, virtualTime);
        }

        // Print footer line
        System.out.println("=".repeat(55));
    }

    // Method to measure thread startup time
    private static long measureStartupTime(int threadCount, boolean isVirtual) throws InterruptedException {
        // Latch to ensure all threads complete before measuring total time
        CountDownLatch latch = new CountDownLatch(threadCount);

        // Task that simply decrements the latch (simulating minimal work)
        Runnable task = () -> {
            latch.countDown();
        };

        // Choose thread type: platform or virtual
        ThreadFactory factory = isVirtual ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();

        // Start time measurement
        Instant start = Instant.now();

        // Launch all threads using the selected factory
        for (int i = 0; i < threadCount; i++) {
            factory.newThread(task).start();
        }

        // Wait for all threads to complete
        latch.await();

        // End time measurement
        Instant end = Instant.now();

        // Return the total duration in milliseconds
        return Duration.between(start, end).toMillis();
    }
}
