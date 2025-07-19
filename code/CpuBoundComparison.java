import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

public class CpuBoundComparison {

    static final int[] TASKS = {4, 6}; // Adjust task count for experimentation
    static final int ITERATIONS_PER_TASK = 5000; // Workload per task
    static final int PRIME_TO_CHECK = 15485863;  // A known large prime number

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(25) + " CPU Benchmark Summary " + "=".repeat(25));
        System.out.printf("%-10s %-20s %-20s %-20s %-15s%n", 
            "Tasks", "Metric", "Platform Threads", "Virtual Threads", "Difference");
        System.out.println("-".repeat(90));

        for (int taskCount : TASKS) {
            BenchmarkResult platform = runBenchmark(taskCount, false);
            BenchmarkResult virtual = runBenchmark(taskCount, true);

            double diffThroughput = calculateDifference(platform.throughput, virtual.throughput);
            double diffAvgTime = calculateDifference(platform.avgTimeMs, virtual.avgTimeMs);

            System.out.printf("%-10d %-20s %-20.1f %-20.1f %-15s%n", 
                taskCount, "Tasks/sec", platform.throughput, virtual.throughput, formatDiff(diffThroughput));

            System.out.printf("%-10s %-20s %-20d %-20d %-15s%n", "", 
                "Avg Execution (ms)", platform.avgTimeMs, virtual.avgTimeMs, formatDiff(diffAvgTime));
        }

        System.out.println("=".repeat(90));
    }

    private static BenchmarkResult runBenchmark(int taskCount, boolean isVirtual) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(taskCount);
        long[] times = new long[taskCount];

        Runnable[] tasks = new Runnable[taskCount];
        for (int i = 0; i < taskCount; i++) {
            final int index = i;
            tasks[i] = () -> {
                long start = System.nanoTime();
                for (int j = 0; j < ITERATIONS_PER_TASK; j++) {
                    isPrime(PRIME_TO_CHECK);
                }
                long end = System.nanoTime();
                times[index] = (end - start);
                latch.countDown();
            };
        }

        ThreadFactory factory = isVirtual ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();
        for (Runnable task : tasks) {
            factory.newThread(task).start();
        }

        latch.await();

        long totalTimeNanos = 0;
        for (long t : times) totalTimeNanos += t;

        long avgTimeMs = totalTimeNanos / taskCount / 1_000_000;
        double throughput = (taskCount * 1_000_000_000.0) / totalTimeNanos;

        return new BenchmarkResult(avgTimeMs, throughput);
    }

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

    private static double calculateDifference(double platform, double virtual_) {
        if (platform == 0) return 0;
        return ((virtual_ - platform) / platform) * 100;
    }

    private static String formatDiff(double diff) {
        return String.format("%.1f%%", diff);
    }

    private record BenchmarkResult(long avgTimeMs, double throughput) {}
}
