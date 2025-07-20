import java.io.FileWriter; // CSV file likhne ke liye
import java.io.IOException; // IOException handle karne ke liye
import java.time.Duration; // Duration calculate karne ke liye
import java.time.Instant; // Time capture karne ke liye
import java.util.concurrent.*; // Threading-related classes

public class GraphExport {

    // 📊 Task counts jo benchmark ke liye use honge
    static final int[] TASK_COUNTS = {1000, 5000, 10000, 20000};

    public static void main(String[] args) throws InterruptedException, IOException {
        try (
            // ✅ CSV file create karo aur header likho
            FileWriter writer = new FileWriter("benchmark_results.csv")
        ) {
            writer.write("ThreadType,TaskCount,Duration(ms),Throughput(tasks/sec),Memory(KB)\n");

            // 🔁 Har task count ke liye test run karo
            for (int count : TASK_COUNTS) {

                // 🧵 Platform threads ke saath benchmark run karo
                long platformTime = runBenchmark(
                        Executors.newFixedThreadPool(100), // 100 platform threads
                        count, writer, "Platform");

                // 🧵 Virtual threads ke saath benchmark run karo
                long virtualTime = runBenchmark(
                        Executors.newVirtualThreadPerTaskExecutor(), // Loom virtual threads
                        count, writer, "Virtual");

                // 🖥️ Console pe result print karo
                System.out.println(count + " tasks benchmarked — Platform: " + platformTime + " ms, Virtual: " + virtualTime + " ms");
            }
            // 📁 FileWriter try-with-resources se khud close ho jaayega
        }

        // ✅ Completion message
        System.out.println("✅ CSV file generated: benchmark_results.csv");
    }

    // 🔁 Ye method benchmark run karta hai given executor ke saath
    static long runBenchmark(ExecutorService executor, int taskCount, FileWriter writer, String threadType)
            throws InterruptedException, IOException {

        // 🔒 Saare threads complete hone ka wait karne ke liye latch
        CountDownLatch latch = new CountDownLatch(taskCount);

        // ⏱️ Benchmark start time
        Instant start = Instant.now();

        // 🔁 Saare tasks executor ke through submit karo
        for (int i = 0; i < taskCount; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(30); // 💤 30ms ka simulated I/O delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Thread ko interrupt hone par safe exit
                } finally {
                    latch.countDown(); // 🧮 Ek task complete hua
                }
            });
        }

        // 🕰️ Wait till all tasks are done
        latch.await();

        // ⏱️ Benchmark end time
        Instant end = Instant.now();

        // 🔚 Executor shutdown kar do
        executor.shutdown();

        // 📊 Metrics calculate karo
        long duration = Duration.between(start, end).toMillis(); // Total time in ms
        long throughput = (taskCount * 1000L) / Math.max(1, duration); // Tasks per second
        long memoryKB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024; // Memory used

        // 📝 CSV row write karo
        writer.write(threadType + "," + taskCount + "," + duration + "," + throughput + "," + memoryKB + "\n");

        // ⏱️ Duration return karo for console output
        return duration;
    }
}
