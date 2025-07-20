import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

public class ScopedValuesDemo {

    // Total number of tasks to run for each benchmark
    static final int TASKS = 10000;

    // Define a scoped value (preview feature in Java 21+)
    @SuppressWarnings("preview")
    static final ScopedValue<String> USER_CONTEXT = ScopedValue.newInstance();

    public static void main(String[] args) throws InterruptedException {
        // Print table header
        System.out.printf("%-12s %-10s %-10s %-10s %-12s %-12s%n", 
            "Thread Type", "Scenario", "Avg (ms)", "P95 (ms)", "Throughput", "Memory (KB)");
        System.out.println("-".repeat(80));

        // Run benchmarks for different thread types and workload scenarios
        benchmark("Platform", "I/O", false, true);   // Platform threads + I/O
        benchmark("Virtual", "I/O", true, true);     // Virtual threads + I/O

        benchmark("Platform", "CPU", false, false);  // Platform threads + CPU
        benchmark("Virtual", "CPU", true, false);    // Virtual threads + CPU
    }

    // Benchmark method that runs tasks and captures performance metrics
    @SuppressWarnings("preview")
    private static void benchmark(String threadType, String scenario, boolean isVirtual, boolean isIO) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(TASKS); // Used to wait for all threads to finish
        List<Long> times = Collections.synchronizedList(new ArrayList<>(TASKS)); // List to store execution times

        // Task logic wrapped inside ScopedValue context
        Runnable task = () -> {
            Callable<Void> scopedTask = () -> {
                try {
                    Instant start = Instant.now(); // Start time
                    if (isIO) simulateIO();        // Simulate I/O workload
                    else simulateCPU();            // Simulate CPU workload
                    Instant end = Instant.now();   // End time
                    times.add(Duration.between(start, end).toMillis()); // Record duration
                } catch (Exception ignored) {
                } finally {
                    latch.countDown(); // Mark task as completed
                }
                return null;
            };

            // Run the scoped task within a context (ScopedValue preview API)
            try {
                ScopedValue.callWhere(USER_CONTEXT, "User-Session", scopedTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Create threads using either virtual or platform thread factory
        ThreadFactory factory = isVirtual ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();
        Instant startAll = Instant.now(); // Overall benchmark start

        // Launch all tasks
        for (int i = 0; i < TASKS; i++) {
            factory.newThread(task).start();
        }

        latch.await(); // Wait for all tasks to complete
        Instant endAll = Instant.now(); // Overall benchmark end

        // Compute average latency
        long total = times.stream().mapToLong(Long::longValue).sum();
        long avg = total / TASKS;

        // Compute 95th percentile latency
        long p95 = percentile(times, 95);

        // Compute total duration and throughput
        long duration = Duration.between(startAll, endAll).toMillis();
        long throughput = (TAXS * 1000L) / Math.max(1, duration); // Tasks per second

        // Estimate memory usage
        long memoryKB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;

        // Output formatted benchmark results
        System.out.printf("%-12s %-10s %-10d %-10d %-12d %-12d%n", 
            threadType, scenario, avg, p95, throughput, memoryKB);
    }

    // Calculate percentile value (e.g., 95th)
    private static long percentile(List<Long> values, int percent) {
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int index = Math.min(sorted.size() - 1, (sorted.size() * percent) / 100);
        return sorted.get(index);
    }

    // Simulate I/O-bound work (sleep to imitate delay)
    private static void simulateIO() throws InterruptedException {
        Thread.sleep(3);
    }

    // Simulate CPU-bound work (lightweight computation)
    private static void simulateCPU() {
        int result = 0;
        for (int i = 1; i < 10000; i++) {
            result += (i ^ 2) % 17;
        }
    }

    // Getter for scoped value (optional for future use)
    public static ScopedValue<String> getUSER_CONTEXT() {
        return USER_CONTEXT;
    }
}
