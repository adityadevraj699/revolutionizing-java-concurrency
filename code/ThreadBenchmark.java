import java.time.*;
import java.util.concurrent.*;

public class ThreadBenchmark {
    public static void main(String[] args) throws InterruptedException {
        int taskCount = 10000;

        System.out.println("Traditional Threads Benchmark:");
        runBenchmark(Executors.newFixedThreadPool(100), taskCount);

        System.out.println("\nVirtual Threads Benchmark:");
        runBenchmark(Executors.newVirtualThreadPerTaskExecutor(), taskCount);
    }

    static void runBenchmark(ExecutorService executor, int tasks) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(tasks);
        Instant start = Instant.now();

        for (int i = 0; i < tasks; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(100); // Simulate I/O-bound task
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // Wait for all tasks to complete
        Instant end = Instant.now();

        System.out.println("Total time: " + Duration.between(start, end).toMillis() + " ms");
        executor.shutdown();
    }
}
