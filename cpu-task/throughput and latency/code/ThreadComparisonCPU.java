import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLongArray;

public class ThreadComparisonCPU {

    static final int[] TASK_COUNTS = {1000, 2000, 10000, 50000, 100000};
    static AtomicLongArray taskLatencies;  // Array to store latency per task
    static int loopPerTask = 10_000_000;   // Workload per task to make latency significant

    public static void main(String[] args) throws Exception {
        System.out.printf("%-35s %-10s %-15s %-15s %-22s %-15s%n",
                "Thread Model", "Tasks", "Avg Latency(μs)", "Throughput", "Thread Creation Time(ms)", "Execution Time(ms)");

        for (int count : TASK_COUNTS) {
            // ======== Warm-up Run =========
            warmUpRun(count);

            // ========== Actual Measured Runs ==========
            benchmark("Platform Threads", count, () -> runWithPlatformThreads(count), () -> createPlatformThreads(count));
            benchmark("Virtual Threads", count, () -> runWithVirtualThreads(count), () -> createVirtualThreads(count));
            benchmark("ForkJoin RecursiveTask", count, () -> runWithForkJoinRecursive(count), () -> createForkJoinTasks(count));
            benchmark("Hybrid (50% Platform + 50% Virtual)", count, () -> runWithHybridThreads(count), () -> createHybridThreads(count));

            System.out.println();
        }
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    static void benchmark(String label, int tasks, ThrowingRunnable test, ThrowingRunnable creationMeasure) throws Exception {
        // Thread Creation Time
        long creationStart = System.currentTimeMillis();
        creationMeasure.run();
        long creationEnd = System.currentTimeMillis();
        long creationTime = creationEnd - creationStart;

        taskLatencies = new AtomicLongArray(tasks); // Reset latency array

        // Execution Time (High Precision)
        long start = System.nanoTime();
        test.run();
        long end = System.nanoTime();
        long executionTimeNs = end - start;
        double executionTimeMs = executionTimeNs / 1_000_000.0;

        // Compute Average Latency (in microseconds)
        long totalLatencyNs = 0;
        for (int i = 0; i < tasks; i++) {
            totalLatencyNs += taskLatencies.get(i);
        }
        double avgLatencyUs = totalLatencyNs / 1_000.0 / tasks;  // Convert ns to μs (microseconds)

        // Throughput (tasks per second)
        double throughput = (executionTimeNs == 0) ? 0 : ((double) tasks / (executionTimeNs / 1_000_000_000.0));

        // Output
        System.out.printf("%-35s %-10d %-15.3f %-15.2f %-22d %-15.3f%n",
                label, tasks, avgLatencyUs, throughput, creationTime, executionTimeMs);
    }

    // ======================== CPU Task with Latency ============================

    static void cpuTask(int taskId) {
        long submissionTime = System.nanoTime();

        long result = 0;
        for (int i = 0; i < loopPerTask; i++) {
            result += i * i;
        }

        long completionTime = System.nanoTime();

        // Safe set if taskLatencies is initialized and index is valid
        if (taskLatencies != null && taskId < taskLatencies.length()) {
            taskLatencies.set(taskId, completionTime - submissionTime);
        }
    }

    // ======================== CPU Load Functions ============================

    static void runWithPlatformThreads(int count) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            final int taskId = i;
            executor.execute(() -> {
                cpuTask(taskId);
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();
    }

    static void runWithVirtualThreads(int count) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            final int taskId = i;
            Thread.startVirtualThread(() -> {
                cpuTask(taskId);
                latch.countDown();
            });
        }
        latch.await();
    }

    static void runWithForkJoinRecursive(int count) {
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new RecursiveCpuTask(0, count));
    }

    static class RecursiveCpuTask extends RecursiveAction {
        int start, end;
        static final int THRESHOLD = 1000;

        RecursiveCpuTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            int taskLimit = (taskLatencies != null) ? taskLatencies.length() : Integer.MAX_VALUE;
            int safeEnd = Math.min(end, taskLimit);

            if (safeEnd - start <= THRESHOLD) {
                for (int i = start; i < safeEnd; i++) {
                    cpuTask(i);
                }
            } else {
                int mid = (start + safeEnd) / 2;
                invokeAll(new RecursiveCpuTask(start, mid), new RecursiveCpuTask(mid, safeEnd));
            }
        }
    }

    static void runWithHybridThreads(int count) throws InterruptedException {
        int half = count / 2;
        CountDownLatch latch = new CountDownLatch(count);
        ExecutorService platformPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < half; i++) {
            final int taskId = i;
            platformPool.execute(() -> {
                cpuTask(taskId);
                latch.countDown();
            });
        }

        for (int i = half; i < count; i++) {
            final int taskId = i;
            Thread.startVirtualThread(() -> {
                cpuTask(taskId);
                latch.countDown();
            });
        }

        latch.await();
        platformPool.shutdown();
    }

    // ======================== Thread Creation Timing ============================

    static void createPlatformThreads(int count) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            executor.execute(() -> latch.countDown());
        }
        try {
            latch.await();
        } catch (InterruptedException ignored) {}
        executor.shutdown();
    }

    static void createVirtualThreads(int count) {
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            Thread.startVirtualThread(() -> latch.countDown());
        }
        try {
            latch.await();
        } catch (InterruptedException ignored) {}
    }

    static void createForkJoinTasks(int count) {
        ForkJoinPool.commonPool().invoke(new RecursiveCpuTask(0, count));
    }

    static void createHybridThreads(int count) {
        int half = count / 2;
        CountDownLatch latch = new CountDownLatch(count);
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < half; i++) {
            executor.execute(() -> latch.countDown());
        }
        for (int i = half; i < count; i++) {
            Thread.startVirtualThread(() -> latch.countDown());
        }
        try {
            latch.await();
        } catch (InterruptedException ignored) {}
        executor.shutdown();
    }

    // ======================== Warm-up Run for Stabilization ============================

    static void warmUpRun(int count) throws Exception {
        // No latency measurement in warm-up → taskLatencies remains null

        createPlatformThreads(count);
        runWithPlatformThreads(count);

        createVirtualThreads(count);
        runWithVirtualThreads(count);

        createForkJoinTasks(count);
        runWithForkJoinRecursive(count);

        createHybridThreads(count);
        runWithHybridThreads(count);
    }
}
