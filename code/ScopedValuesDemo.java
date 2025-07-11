import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

public class ScopedValuesDemo {

    static final int TASKS = 10000;

    // Dummy value carrier using scoped value
    @SuppressWarnings("preview")
    static final ScopedValue<String> USER_CONTEXT = ScopedValue.newInstance();

    public static void main(String[] args) throws InterruptedException {
        System.out.printf("%-12s %-10s %-10s %-10s %-12s%n", "Thread Type", "Scenario", "Avg (ms)", "P95 (ms)", "Throughput", "Memory (KB)");
        System.out.println("-".repeat(70));

        benchmark("Platform", "I/O", false, true);
        benchmark("Virtual", "I/O", true, true);

        benchmark("Platform", "CPU", false, false);
        benchmark("Virtual", "CPU", true, false);
    }

    @SuppressWarnings("preview")
    private static void benchmark(String threadType, String scenario, boolean isVirtual, boolean isIO) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(TASKS);
        List<Long> times = new ArrayList<>(TASKS);

        Runnable task = () -> {
            Callable<Void> scopedTask = () -> {
                try {
                    Instant start = Instant.now();
                    if (isIO) simulateIO(); else simulateCPU();
                    Instant end = Instant.now();
                    synchronized (times) {
                        times.add(Duration.between(start, end).toMillis());
                    }
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
                return null;
            };
            try {
                ScopedValue.callWhere(USER_CONTEXT, "User-Session", scopedTask);
            } catch (Exception e) {
                // Handle exception if needed
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
        long throughput = (TASKS * 1000L) / Math.max(1, duration);  // r/s
        long memoryKB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;

        System.out.printf("%-12s %-10s %-10d %-10d %-12d%n", threadType, scenario, avg, p95, throughput, memoryKB);
    }

    private static long percentile(List<Long> values, int percent) {
        return values.stream().sorted().skip(values.size() * percent / 100).findFirst().orElse(0L);
    }

    private static void simulateIO() throws InterruptedException {
        Thread.sleep(3); // Simulate latency
    }

    private static void simulateCPU() {
        int n = 0;
        for (int i = 1; i < 10000; i++) {
            n += (i ^ 2) % 17;
        }
    }
}
