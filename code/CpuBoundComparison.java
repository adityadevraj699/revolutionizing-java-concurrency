import java.util.concurrent.CountDownLatch; // Used to wait for all tasks to finish
import java.util.concurrent.ThreadFactory;   // Used to create either platform or virtual threads

public class CpuBoundComparison {

    // Number of tasks to test with (can adjust for experimentation)
    static final int[] TASKS = {4, 6};

    // Number of iterations each task will run (simulate CPU work)
    static final int ITERATIONS_PER_TASK = 5000;

    // Prime number used in CPU calculation (large known prime)
    static final int PRIME_TO_CHECK = 15485863;

    public static void main(String[] args) throws InterruptedException {
        // Print header
        System.out.println("=".repeat(25) + " CPU Benchmark Summary " + "=".repeat(25));
        System.out.printf("%-10s %-20s %-20s %-20s %-15s%n",
            "Tasks", "Metric", "Platform Threads", "Virtual Threads", "Difference");
        System.out.println("-".repeat(90));

        // Loop through each task count
        for (int taskCount : TASKS) {
            BenchmarkResult platform = runBenchmark(taskCount, false); // Run with platform threads
            BenchmarkResult virtual = runBenchmark(taskCount, true);  // Run with virtual threads

            // Calculate difference in performance
            double diffThroughput = calculateDifference(platform.throughput, virtual.throughput);
            double diffAvgTime = calculateDifference(platform.avgTimeMs, virtual.avgTimeMs);

            // Print throughput result
            System.out.printf("%-10d %-20s %-20.1f %-20.1f %-15s%n",
                taskCount, "Tasks/sec", platform.throughput, virtual.throughput, formatDiff(diffThroughput));

            // Print average execution time
            System.out.printf("%-10s %-20s %-20d %-20d %-15s%n", "",
                "Avg Execution (ms)", platform.avgTimeMs, virtual.avgTimeMs, formatDiff(diffAvgTime));
        }

        // End divider
        System.out.println("=".repeat(90));
    }

    // Run benchmark for given number of tasks and thread type
    private static BenchmarkResult runBenchmark(int taskCount, boolean isVirtual) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(taskCount); // Waits for all tasks to complete
        long[] times = new long[taskCount]; // Store execution times

        Runnable[] tasks = new Runnable[taskCount]; // Array to store tasks

        // Prepare each task
        for (int i = 0; i < taskCount; i++) {
            final int index = i; // Required because lambda needs effectively final variable
            tasks[i] = () -> {
                long start = System.nanoTime(); // Start timer
                for (int j = 0; j < ITERATIONS_PER_TASK; j++) {
                    isPrime(PRIME_TO_CHECK); // Do CPU-intensive check repeatedly
                }
                long end = System.nanoTime(); // End timer
                times[index] = (end - start); // Save duration in array
                latch.countDown(); // Mark task as completed
            };
        }

        // Create thread factory for virtual or platform threads
        ThreadFactory factory = isVirtual ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();

        // Start all tasks
        for (Runnable task : tasks) {
            factory.newThread(task).start(); // Create and start thread for each task
        }

        latch.await(); // Wait for all tasks to finish

        // Total execution time across all tasks
        long totalTimeNanos = 0;
        for (long t : times) totalTimeNanos += t;

        // Calculate average time per task in milliseconds
        long avgTimeMs = totalTimeNanos / taskCount / 1_000_000;

        // Calculate throughput: tasks per second
        double throughput = (taskCount * 1_000_000_000.0) / totalTimeNanos;

        return new BenchmarkResult(avgTimeMs, throughput); // Return results
    }

    // Prime check method (used for CPU-bound workload)
    private static boolean isPrime(int num) {
        if (num <= 1) return false;
        if (num <= 3) return true;
        if (num % 2 == 0 || num % 3 == 0) return false;
        for (int i = 5; i * i <= num; i += 6) {
            if (num % i == 0 || num % (i + 2) == 0)
                return false;
        }
        return true;
    }

    // Calculate percentage difference between two values
    private static double calculateDifference(double platform, double virtual_) {
        if (platform == 0) return 0;
        return ((virtual_ - platform) / platform) * 100;
    }

    // Format difference to string with %
    private static String formatDiff(double diff) {
        return String.format("%.1f%%", diff);
    }

    // Record to hold benchmark results
    private record BenchmarkResult(long avgTimeMs, double throughput) {}
}
