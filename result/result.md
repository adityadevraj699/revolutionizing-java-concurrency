# 🔬 Java Concurrency Benchmark Results (Windows 11)

> **System Setup:**  
> **Device:** Dell Inspiron 15 3000  
> **OS:** Windows 11  
> **Java Version:** OpenJDK 21.0.6 (Temurin)  
> **Editor:** VS Code  
> **Test Mode:** Command-line execution with `--enable-preview`  
> **Compiler Flags:** `javac --enable-preview --release 21 -d out *.java`

---

## 📊 1. CPU-Bound Benchmark (`CpuBoundComparison.java`)

| Tasks | Metric              | Platform Threads | Virtual Threads  | Difference |
|-------|---------------------|------------------|------------------|------------|
| 4     | Tasks/sec           | 45.7             | 62.6             | +36.8%     |
|       | Avg Execution (ms)  | 21               | 15               | +36.8%     |
| 6     | Tasks/sec           | 48.9             | 50.9             | +4.0%      |
|       | Avg Execution (ms)  | 20               | 19               | +4.0%      |

✅ **Virtual Threads show slight gains** in CPU-bound workloads, especially with fewer threads.

---

## 🌐 2. IO-Bound Benchmark (`IoBoundVirtualVsPlatform.java`)

| Concurrent Requests | Metric            | Platform Threads | Virtual Threads |
|---------------------|-------------------|------------------|-----------------|
| 1,000               | Throughput (r/s)  | 4,484            | 4,854           |
|                     | Avg Latency (ms)  | 0                | 0               |
| 10,000              | Throughput (r/s)  | 9,157            | 41,666          |
|                     | Avg Latency (ms)  | 0                | 0               |
| 50,000              | Throughput (r/s)  | 10,535           | 166,666         |
|                     | Avg Latency (ms)  | 0                | 0               |

✅ **Virtual Threads scale significantly better** as concurrency increases.

---

## 🔁 3. Mixed Benchmark (`MixedBenchmark.java`)

### ➤ Platform Threads
- ⏱️ Total Time: `5,831 ms`  
- ⚡ Throughput: `1,714 tasks/sec`  
- 💾 Memory Used: `28,419 KB`

### ➤ Virtual Threads
- ⏱️ Total Time: `1,273 ms`  
- ⚡ Throughput: `7,855 tasks/sec`  
- 💾 Memory Used: `26,074 KB`

✅ Virtual threads are **~4.5x faster** and use **~8% less memory**.

---

## 🌲 4. Structured Concurrency (`StructuredConcurrencyDemo.java`)

| Threads     | Platform Threads (ms) | Virtual Threads (ms) |
|-------------|------------------------|-----------------------|
| 1,000       | 75                     | 22                    |
| 10,000      | 634                    | 14                    |
| 50,000      | 3,129                  | 28                    |
| 100,000     | 6,535                  | 28                    |

✅ Virtual threads show **constant startup time**, even at **100,000 threads**.

---

## 🧬 5. Scoped Values Benchmark (`ScopedValuesDemo.java`)

| Workload | Thread Type | Avg (ms) | P95 (ms) | Throughput |
|----------|-------------|----------|----------|------------|
| I/O      | Platform    | 4        | 5        | 11,933     |
|          | Virtual     | 33       | 46       | 93,457     |
| CPU      | Platform    | 0        | 0        | 14,044     |
|          | Virtual     | 0        | 0        | 1,666,666  |

✅ Scoped values are highly efficient with virtual threads, especially in I/O tasks.

---

## 🧵 6. Thread Benchmark (`ThreadBenchmark.java`)

| Thread Type       | Total Execution Time |
|-------------------|----------------------|
| Platform Threads  | 10,979 ms            |
| Virtual Threads   | 221 ms               |

✅ Virtual Threads are **~50x faster** for thread startup and completion.

---

## ✅ Summary

| Test Type         | Virtual Threads Win? | Key Advantage                      |
|-------------------|----------------------|------------------------------------|
| CPU-Bound         | ✅ Slight            | Lower overhead, mild gain          |
| IO-Bound          | ✅ Yes               | Scalable throughput                |
| Mixed Workload    | ✅ Yes               | Faster + less memory               |
| Structured Conc.  | ✅ Yes               | Fast thread startup                |
| Scoped Values     | ✅ Yes               | Efficient isolation/context use    |
| Thread Benchmark  | ✅ Yes               | Huge speed gain (over 50x faster)  |

---

🧠 **Conclusion:**  
Java 21's Virtual Threads (Project Loom) bring **game-changing concurrency** to the JVM. They are ideal for:
- High-throughput web servers
- Massive I/O concurrency
- Lightweight task parallelism  
They simplify threading, avoid callback hell, and make structured concurrency both **clean and performant**.

---

📁 _Generated on: `Windows 11 • Temurin OpenJDK 21.0.6 • July 2025`_
