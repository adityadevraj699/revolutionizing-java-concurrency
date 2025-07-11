import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

public class GraphExport {

    // Define different task counts to benchmark
    static final int[] TASK_COUNTS = {1000, 5000, 10000, 20000};

    public static void main(String[] args) throws InterruptedException, IOException {
        try ( // Create the CSV file and write headers
                FileWriter writer = new FileWriter("benchmark_results.csv")) {
            writer.write("ThreadType,TaskCount,Duration(ms),Throughput(tasks/sec),Memory(KB)\n");
            // Run for each task count
            for (int count : TASK_COUNTS) {
                // Platform Thread Benchmark
                long platformTime = runBenchmark(
                        Executors.newFixedThreadPool(100), count, writer, "Platform");
                
                // Virtual Thread Benchmark
                long virtualTime = runBenchmark(
                        Executors.newVirtualThreadPerTaskExecutor(), count, writer, "Virtual");
                
                // Show status on console
                System.out.println(count + " tasks benchmarked — Platform: " + platformTime + " ms, Virtual: " + virtualTime + " ms");
            }
            // Close the file
        }
        System.out.println("✅ CSV file generated: benchmark_results.csv");
    }

    static long runBenchmark(ExecutorService executor, int taskCount, FileWriter writer, String threadType)
            throws InterruptedException, IOException {

        CountDownLatch latch = new CountDownLatch(taskCount);
        Instant start = Instant.now();

        // Submit tasks
        for (int i = 0; i < taskCount; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(30); // Simulate I/O
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // Wait for all to complete
        Instant end = Instant.now();
        executor.shutdown();

        // Calculate metrics
        long duration = Duration.between(start, end).toMillis();
        long throughput = (taskCount * 1000L) / Math.max(1, duration);
        long memoryKB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;

        // Write row to CSV
        writer.write(threadType + "," + taskCount + "," + duration + "," + throughput + "," + memoryKB + "\n");

        return duration;
    }
}
