import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.CountDownLatch;

public class MemoryUtilizationTest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== MEMORY UTILIZATION TEST ===");
        testMemory("Platform", false);
        System.out.println("--------------------------------");
        testMemory("Virtual", true);
    }

    static void testMemory(String label, boolean isVirtual) throws InterruptedException {
        int threadCount = 100_000;
        CountDownLatch latch = new CountDownLatch(threadCount);

        // Trigger GC before starting
        System.gc();
        Thread.sleep(1000);

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage before = memoryBean.getHeapMemoryUsage();
        long usedBefore = before.getUsed();

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            Runnable task = () -> {
                try {
                    Thread.sleep(10_000); // keep thread alive
                } catch (InterruptedException ignored) {}
                latch.countDown();
            };
            threads[i] = isVirtual
                    ? Thread.ofVirtual().unstarted(task)
                    : new Thread(task);
        }

        for (Thread t : threads) t.start();

        Thread.sleep(2000); // Let all threads start and consume memory

        MemoryUsage after = memoryBean.getHeapMemoryUsage();
        long usedAfter = after.getUsed();

        long memoryUsed = usedAfter - usedBefore;
        double perThread = memoryUsed / (double) threadCount / 1024.0;

        System.out.println("Thread Type: " + label);
        System.out.println("Total Memory Used: " + memoryUsed / 1024 + " KB");
        System.out.println("Avg Memory per Thread: " + String.format("%.2f", perThread) + " KB");
        System.out.println("Heap Used Before: " + usedBefore / 1024 + " KB");
        System.out.println("Heap Used After : " + usedAfter / 1024 + " KB");

        latch.await(); // Wait for threads to die
    }
}
