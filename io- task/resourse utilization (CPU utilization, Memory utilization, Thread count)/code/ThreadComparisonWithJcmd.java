import java.io.*;
import java.lang.management.*;
import java.util.*;
import java.util.concurrent.*;

public class ThreadComparisonWithJcmd {

    public static void main(String[] args) throws Exception {
        int[] threadCounts = {1000, 5000, 10000, 50000, 75000, 100000};
        long pid = ProcessHandle.current().pid();

        System.out.printf("| %-7s | %-12s | %-13s | %-16s | %-13s | %-7s | %-8s |\n",
                "Threads", "Thread Model", "Live Threads", "Used Memory (MB)", "Memory/Thread", "CPU (%)", "Time (s)");
        System.out.println("|---------|--------------|--------------|------------------|---------------|---------|----------|");

        for (int count : threadCounts) {
            runTestGroup(count, pid);
        }

        System.out.println("All tests done.");

        // 🔽 Add system configuration info here
    System.out.println("\n=== System Configuration ===");
    System.out.println("Operating System : " + System.getProperty("os.name"));
    System.out.println("JVM Version      : " + System.getProperty("java.version"));
    System.out.println("Available Cores  : " + Runtime.getRuntime().availableProcessors());
    }

    private static void runTestGroup(int threadCount, long pid) throws Exception {
        runTest(threadCount, "Platform", () -> runPlatformThreads(threadCount), pid);
        runTest(threadCount, "Virtual", () -> runVirtualThreads(threadCount), pid);
        runTest(threadCount, "Hybrid", () -> runHybridThreads(threadCount), pid);
    }

    private static void runTest(int threadCount, String model, ThrowingRunnable testMethod, long pid) throws Exception {
        System.gc();
        Thread.sleep(200); // Let GC settle

        long beforeMem = getUsedMemory();
        long beforeCpuTime = getCpuTime();
        long start = System.nanoTime();

        testMethod.run();

        long end = System.nanoTime();
        long afterCpuTime = getCpuTime();
        long afterMem = getUsedMemory();

        double usedMemoryMB = (afterMem - beforeMem) / (1024.0 * 1024);
        double timeSec = (end - start) / 1_000_000_000.0;
        double cpuUsage = (afterCpuTime - beforeCpuTime) / 1_000_000.0 / (timeSec * 1000);
        int liveThreads = Thread.activeCount();
        double memoryPerThread = usedMemoryMB / threadCount;

        System.out.printf("| %-7d | %-12s | %-13d | %16.2f | %13.4f | %7.2f | %8.2f |\n",
                threadCount, model, liveThreads, usedMemoryMB, memoryPerThread, cpuUsage * 100, timeSec);

        printNativeMemorySummary(pid, model, threadCount);
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static void runPlatformThreads(int threadCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                simulateTask();
                latch.countDown();
            }).start();
        }
        latch.await();
    }

    private static void runVirtualThreads(int threadCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread.startVirtualThread(() -> {
                simulateTask();
                latch.countDown();
            });
        }
        latch.await();
    }

    private static void runHybridThreads(int threadCount) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(100);
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            futures.add(pool.submit(ThreadComparisonWithJcmd::simulateTask));
        }
        for (Future<?> f : futures) f.get();
        pool.shutdown();
    }

    private static void simulateTask() {
        try {
            Thread.sleep(10); // Simulate I/O
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
            Process process = pb.start();

            Scanner scanner = new Scanner(process.getInputStream());
            String filename = "nmt_output_" + model + "_" + threadCount + ".txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

            System.out.println("===== NMT Report for " + model + " with " + threadCount + " threads =====");
            bw.write("===== NMT Report for " + model + " with " + threadCount + " threads =====\n");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("Thread")) {
                    System.out.println(line);
                    bw.write(line + "\n");
                }
            }

            bw.close();
            scanner.close();
            process.waitFor();
        } catch (Exception e) {
            System.err.println("NMT command failed: " + e.getMessage());
        }
    }
}


