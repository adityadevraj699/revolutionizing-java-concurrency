import java.time.*;                      // For measuring time (Instant, Duration)
import java.util.concurrent.*;           // For concurrency utilities like ExecutorService, CountDownLatch

public class ThreadBenchmark {
    public static void main(String[] args) throws InterruptedException {
        int taskCount = 10000;           // Total number of tasks to run

        // Run benchmark using traditional (platform) threads
        System.out.println("Traditional Threads Benchmark:");
        runBenchmark(Executors.newFixedThreadPool(100), taskCount); // Uses fixed thread pool with 100 threads

        // Run benchmark using virtual threads
        System.out.println("\nVirtual Threads Benchmark:");
        runBenchmark(Executors.newVirtualThreadPerTaskExecutor(), taskCount); // Uses one virtual thread per task
    }

    // Method to run the benchmark with a given executor and number of tasks
    static void runBenchmark(ExecutorService executor, int tasks) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(tasks); // Used to wait for all threads to complete
        Instant start = Instant.now();                    // Start time of benchmark

        // Submit all tasks to the executor
        for (int i = 0; i < tasks; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(100);                    // Simulate an I/O-bound task by sleeping
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();   // Restore interrupted status
                } finally {
                    latch.countDown();                    // Decrease latch count when task finishes
                }
            });
        }

        latch.await();                                     // Wait until all tasks complete
        Instant end = Instant.now();                       // End time of benchmark

        // Print total time taken for all tasks
        System.out.println("Total time: " + Duration.between(start, end).toMillis() + " ms");

        executor.shutdown();                               // Shutdown the executor service
    }
}
