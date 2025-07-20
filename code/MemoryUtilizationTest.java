import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.CountDownLatch;

public class MemoryUtilizationTest {

    public static void main(String[] args) throws InterruptedException {
        // Print heading for clarity
        System.out.println("=== MEMORY UTILIZATION TEST ===");

        // Run memory test using platform threads
        testMemory("Platform", false);

        // Separator
        System.out.println("--------------------------------");

        // Run memory test using virtual threads
        testMemory("Virtual", true);
    }

    // Method to measure memory usage for a given type of thread
    static void testMemory(String label, boolean isVirtual) throws InterruptedException {
        int threadCount = 100_000; // Number of threads to test
        CountDownLatch latch = new CountDownLatch(threadCount); // Used to wait until all threads finish

        // Suggest garbage collection before starting the test
        System.gc();
        Thread.sleep(1000); // Give GC time to run

        // Get heap memory usage before thread creation
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage before = memoryBean.getHeapMemoryUsage();
        long usedBefore = before.getUsed();

        // Create an array to store thread objects
        Thread[] threads = new Thread[threadCount];

        // Initialize each thread with a 10-second sleep
        for (int i = 0; i < threadCount; i++) {
            Runnable task = () -> {
                try {
                    Thread.sleep(10_000); // Keep thread alive
                } catch (InterruptedException ignored) {}
                latch.countDown(); // Mark thread as completed
            };

            // Create either a virtual or platform thread
            threads[i] = isVirtual
                    ? Thread.ofVirtual().unstarted(task)
                    : new Thread(task);
        }

        // Start all threads
        for (Thread t : threads) t.start();

        // Wait 2 seconds to allow all threads to start and consume memory
        Thread.sleep(2000);

        // Get heap memory usage after threads are started
        MemoryUsage after = memoryBean.getHeapMemoryUsage();
        long usedAfter = after.getUsed();

        // Calculate memory used and per-thread memory usage
        long memoryUsed = usedAfter - usedBefore;
        double perThread = memoryUsed / (double) threadCount / 1024.0;

        // Print memory statistics
        System.out.println("Thread Type: " + label);
        System.out.println("Total Memory Used: " + memoryUsed / 1024 + " KB");
        System.out.println("Avg Memory per Thread: " + String.format("%.2f", perThread) + " KB");
        System.out.println("Heap Used Before: " + usedBefore / 1024 + " KB");
        System.out.println("Heap Used After : " + usedAfter / 1024 + " KB");

        // Wait for all threads to finish
        latch.await();
    }
}
