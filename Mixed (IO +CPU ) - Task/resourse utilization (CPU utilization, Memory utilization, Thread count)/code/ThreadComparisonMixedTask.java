import java.io.*;
import java.lang.management.*;
import java.util.*;
import java.util.concurrent.*;

public class ThreadComparisonMixedTask {

    public static void main(String[] args) throws Exception {
        int[] threadCounts = {1000, 2000, 5000, 10000, 20000};
        long pid = ProcessHandle.current().pid();

        System.out.printf("| %-7s | %-12s | %-13s | %-16s | %-13s | %-7s | %-8s |\n",
                "Threads", "Thread Model", "Live Threads", "Used Memory (MB)", "Memory/Thread", "CPU (%)", "Time (s)");
        System.out.println("|---------|--------------|--------------|------------------|---------------|---------|----------|");

        for (int count : threadCounts) {
            runTestGroup(count, pid);
        }

        System.out.println("All tests done.");

        // System info
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
        Thread.sleep(200);

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

    // ----------- Models ------------

    private static void runPlatformThreads(int threadCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                simulateIOTask();
                simulateCPUTask();
                latch.countDown();
            }).start();
        }
        latch.await();
    }

    private static void runVirtualThreads(int threadCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread.startVirtualThread(() -> {
                simulateIOTask();
                simulateCPUTask();
                latch.countDown();
            });
        }
        latch.await();
    }

    private static void runHybridThreads(int threadCount) throws Exception {
        // I/O with Virtual
        CountDownLatch ioLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread.startVirtualThread(() -> {
                simulateIOTask();
                ioLatch.countDown();
            });
        }
        ioLatch.await();

        // CPU with Platform (thread pool)
        ExecutorService cpuPool = Executors.newFixedThreadPool(100);
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            futures.add(cpuPool.submit(ThreadComparisonMixedTask::simulateCPUTask));
        }
        for (Future<?> f : futures) f.get();
        cpuPool.shutdown();
    }

    // ----------- Tasks ------------

    private static void simulateIOTask() {
        try {
            Thread.sleep(10); // Simulate I/O delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void simulateCPUTask() {
        // CPU-bound: simple prime calculation
        int count = 0;
        for (int num = 2; num < 5000; num++) {
            if (isPrime(num)) count++;
        }
    }

    private static boolean isPrime(int num) {
        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    // ----------- Utils ------------

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
