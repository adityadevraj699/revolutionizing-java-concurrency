import java.io.*;
import java.lang.management.*;
import java.util.*;
import java.util.concurrent.*;

public class ThreadComparisonWithJcmd {

    public static void main(String[] args) throws Exception {
        int[] threadCounts = {1000,2000, 5000, 10000, 50000,75000,100000}; // You can go up to 100000
        long pid = ProcessHandle.current().pid();

        System.out.printf("| %-7s | %-15s | %-13s | %-16s | %-13s | %-7s | %-8s |\n",
                "Threads", "Thread Model", "Live Threads", "Used Memory (MB)", "Memory/Thread", "CPU (%)", "Time (s)");
        System.out.println("|---------|-----------------|--------------|------------------|---------------|---------|----------|");

        for (int count : threadCounts) {
            runTestGroup(count, pid);
        }

        System.out.println("\n✅ All tests done.");
        System.out.println("\n=== System Configuration ===");
        System.out.println("OS             : " + System.getProperty("os.name"));
        System.out.println("JVM Version    : " + System.getProperty("java.version"));
        System.out.println("CPU Cores      : " + Runtime.getRuntime().availableProcessors());
        System.out.println("Java PID       : " + pid);
    }

    private static void runTestGroup(int count, long pid) throws Exception {
        runTest(count, "Platform", () -> runPlatformThreads(count), pid);
        runTest(count, "Virtual", () -> runVirtualThreads(count), pid);
        runTest(count, "ForkJoin", () -> runForkJoinThreads(count), pid);
        runTest(count, "Hybrid", () -> runHybridThreads(count), pid);
    }

    private static void runTest(int threadCount, String model, ThrowingRunnable task, long pid) throws Exception {
        System.gc();
        Thread.sleep(300);

        long beforeMem = getUsedMemory();
        long beforeCpuTime = getCpuTime();
        long start = System.nanoTime();

        task.run();

        long end = System.nanoTime();
        long afterCpuTime = getCpuTime();
        long afterMem = getUsedMemory();

        double usedMemoryMB = (afterMem - beforeMem) / (1024.0 * 1024);
        double timeSec = (end - start) / 1_000_000_000.0;
        double cpuUsage = (afterCpuTime - beforeCpuTime) / 1_000_000.0 / (timeSec * 1000);
        int liveThreads = Thread.activeCount();
        double memoryPerThread = usedMemoryMB / threadCount;

        System.out.printf("| %-7d | %-15s | %-13d | %16.2f | %13.4f | %7.2f | %8.2f |\n",
                threadCount, model, liveThreads, usedMemoryMB, memoryPerThread, cpuUsage * 100, timeSec);

        printNativeMemorySummary(pid, model, threadCount);
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static void runPlatformThreads(int count) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                simulateTask();
                latch.countDown();
            }).start();
        }
        latch.await();
    }

    private static void runVirtualThreads(int count) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            Thread.startVirtualThread(() -> {
                simulateTask();
                latch.countDown();
            });
        }
        latch.await();
    }

    private static void runForkJoinThreads(int count) throws InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            pool.execute(() -> {
                simulateTask();
                latch.countDown();
            });
        }
        latch.await();
        pool.shutdown();
    }

    private static void runHybridThreads(int count) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(100); // Fixed platform threads
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            pool.submit(() -> {
                simulateTask();
                latch.countDown();
            });
        }
        latch.await();
        pool.shutdown();
    }

    private static void simulateTask() {
        long dummy = 0;
        for (int i = 0; i < 10_000_000; i++) {
            dummy += Math.sqrt(i);
        }
    }

    private static long getUsedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    private static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return Arrays.stream(bean.getAllThreadIds())
                .map(bean::getThreadCpuTime)
                .filter(t -> t != -1)
                .reduce(0L, Long::sum);
    }

    private static void printNativeMemorySummary(long pid, String model, int threadCount) {
        try {
            ProcessBuilder pb = new ProcessBuilder("jcmd", String.valueOf(pid), "VM.native_memory", "summary");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String filename = "nmt_output_" + model + "_" + threadCount + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            writer.write("===== NMT Report for " + model + " with " + threadCount + " threads =====\n");
            boolean capturing = false;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Native Memory Tracking")) {
                    capturing = true;
                }

                if (capturing) {
                    writer.write(line + "\n");

                    if (line.contains("Total")) {
                        System.out.println("🧠 NMT → " + line.trim());
                    }
                }
            }

            writer.close();
            reader.close();
            process.waitFor();

            System.out.println("📂 NMT Report → " + filename);
        } catch (Exception e) {
            System.err.println("❌ NMT failed: " + e.getMessage());
        }
    }
}
