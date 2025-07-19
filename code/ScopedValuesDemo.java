import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

public class ScopedValuesDemo {

    static final int TASKS = 10000;

    @SuppressWarnings("preview")
    static final ScopedValue<String> USER_CONTEXT = ScopedValue.newInstance();

    public static void main(String[] args) throws InterruptedException {
        System.out.printf("%-12s %-10s %-10s %-10s %-12s %-12s%n", 
            "Thread Type", "Scenario", "Avg (ms)", "P95 (ms)", "Throughput", "Memory (KB)");
        System.out.println("-".repeat(80));

        benchmark("Platform", "I/O", false, true);
        benchmark("Virtual", "I/O", true, true);

        benchmark("Platform", "CPU", false, false);
        benchmark("Virtual", "CPU", true, false);
    }

    @SuppressWarnings("preview")
    private static void benchmark(String threadType, String scenario, boolean isVirtual, boolean isIO) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(TASKS);
        List<Long> times = Collections.synchronizedList(new ArrayList<>(TASKS));

        Runnable task = () -> {
            Callable<Void> scopedTask = () -> {
                try {
                    Instant start = Instant.now();
                    if (isIO) simulateIO();
                    else simulateCPU();
                    Instant end = Instant.now();
                    times.add(Duration.between(start, end).toMillis());
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
                return null;
            };

            try {
                ScopedValue.callWhere(USER_CONTEXT, "User-Session", scopedTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        ThreadFactory factory = isVirtual ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();
        Instant startAll = Instant.now();

        for (int i = 0; i < TASKS; i++) {
            factory.newThread(task).start();
        }

        latch.await();
        Instant endAll = Instant.now();

        // Metrics
        long total = times.stream().mapToLong(Long::longValue).sum();
        long avg = total / TASKS;
        long p95 = percentile(times, 95);
        long duration = Duration.between(startAll, endAll).toMillis();
        long throughput = (TASKS * 1000L) / Math.max(1, duration); // requests/sec
        long memoryKB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;

        System.out.printf("%-12s %-10s %-10d %-10d %-12d %-12d%n", 
            threadType, scenario, avg, p95, throughput, memoryKB);
    }

    private static long percentile(List<Long> values, int percent) {
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int index = Math.min(sorted.size() - 1, (sorted.size() * percent) / 100);
        return sorted.get(index);
    }

    private static void simulateIO() throws InterruptedException {
        Thread.sleep(3); // Simulate I/O latency
    }

    private static void simulateCPU() {
        int result = 0;
        for (int i = 1; i < 10000; i++) {
            result += (i ^ 2) % 17;
        }
    }

    public static ScopedValue<String> getUSER_CONTEXT() {
        return USER_CONTEXT;
    }
}
