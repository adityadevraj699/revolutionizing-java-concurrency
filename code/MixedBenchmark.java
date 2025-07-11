import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MixedBenchmark {
    public static void main(String[] args) throws InterruptedException {
        int taskCount = 10000;

        System.out.println("Mixed Workload Benchmark\n");

        System.out.println("Platform Threads:");
        runBenchmark(Executors.newFixedThreadPool(100), taskCount);

        System.out.println("\nVirtual Threads:");
        runBenchmark(Executors.newVirtualThreadPerTaskExecutor(), taskCount);
    }

    static void runBenchmark(ExecutorService executor, int taskCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(taskCount);
        Instant start = Instant.now();

        for (int i = 0; i < taskCount; i++) {
            executor.submit(() -> {
                try {
                    simulateMixedWorkload();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Instant end = Instant.now();

        long duration = Duration.between(start, end).toMillis();
        long memoryKB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;

        System.out.println("Total Time     : " + duration + " ms");
        System.out.println("Throughput     : " + (taskCount * 1000L / Math.max(1, duration)) + " tasks/sec");
        System.out.println("Memory Used    : " + memoryKB + " KB");

        executor.shutdown();
    }

    static void simulateMixedWorkload() {
        try {
            Thread.sleep(50); // I/O
        } catch (InterruptedException ignored) {}

        // CPU workload
        double sum = 0;
        for (int i = 1; i <= 10000; i++) {
            sum += Math.sqrt(i);
        }
    }
}
