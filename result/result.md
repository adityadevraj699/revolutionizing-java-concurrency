# 🔬 Java Concurrency Benchmark Results (Windows 11)

> **System Setup:**  
> **Device:** Dell Inspiron 15 3000  
> **OS:** Windows 11  
> **Java Version:** OpenJDK 21.0.6 (Temurin)  
> **Editor:** VS Code  
> **Test Mode:** Command-line execution with `--enable-preview`  
> **Compiler Flags:** `javac --enable-preview --release 21 -d out *.java`

---

## 📊 1. CPU-Bound Benchmark (CpuBoundComparison.java)

| Tasks | Metric              | Platform Threads | Virtual Threads | Difference |
|-------|---------------------|------------------|------------------|------------|
| 4     | Tasks/sec           | Infinity         | Infinity         | NaN%       |
|       | Avg Execution (ms)  | 0                | 0                | NaN%       |
| 6     | Tasks/sec           | Infinity         | Infinity         | NaN%       |
|       | Avg Execution (ms)  | 0                | 0                | NaN%       |

> ⚠️ Looks like a logic error or skipped benchmarking calculation (divide by zero?).

---

## 🌐 2. IO-Bound Benchmark (IoBoundVirtualVsPlatform.java)

| Concurrent Requests | Metric            | Platform Threads | Virtual Threads |
|---------------------|-------------------|------------------|-----------------|
| 1000                | Throughput (r/s)  | 4484             | 4854            |
|                     | Avg Latency (ms)  | 0                | 0               |
| 10000               | Throughput (r/s)  | 9157             | 41666           |
|                     | Avg Latency (ms)  | 0                | 0               |
| 50000               | Throughput (r/s)  | 10535            | 166666          |
|                     | Avg Latency (ms)  | 0                | 0               |

✅ **Virtual Threads scale significantly better at higher concurrency**.

---

## 🔁 3. Mixed Benchmark (MixedBenchmark.java)

### ➤ Platform Threads
- Total Time: 5831 ms  
- Throughput: 1714 tasks/sec  
- Memory Used: 28419 KB

### ➤ Virtual Threads
- Total Time: 1273 ms  
- Throughput: 7855 tasks/sec  
- Memory Used: 26074 KB

✅ Virtual threads are **4.5x faster** and more memory-efficient.

---

## 🌲 4. Structured Concurrency (StructuredConcurrencyDemo.java)

| Thread Type | Thread Count | Startup Time (ms) |
|-------------|--------------|--------------------|
| Platform    | 1000         | 75                 |
| Virtual     | 1000         | 22                 |
| Platform    | 10000        | 634                |
| Virtual     | 10000        | 14                 |
| Platform    | 50000        | 3129               |
| Virtual     | 50000        | 28                 |
| Platform    | 100000       | 6535               |
| Virtual     | 100000       | 28                 |

✅ Virtual threads maintain consistent startup time — even at **100,000** threads.

---

## 🧬 5. Scoped Values Benchmark (ScopedValuesDemo.java)

| Thread Type | Scenario | Avg (ms) | P95 (ms) | Throughput |
|-------------|----------|----------|----------|-------------|
| Platform    | I/O      | 4        | 5        | 11933       |
| Virtual     | I/O      | 33       | 46       | 93457       |
| Platform    | CPU      | 0        | 0        | 14044       |
| Virtual     | CPU      | 0        | 0        | 1666666     |

✅ Scoped values work efficiently with **virtual threads**, especially in **I/O-intensive tasks**.

---

## 🧵 6. Thread Benchmark (ThreadBenchmark.java)

### Traditional Threads
- Total Time: 10979 ms

### Virtual Threads
- Total Time: 221 ms

✅ Virtual threads executed the same task in **~2%** of the time compared to traditional threads.

---

## ✅ Summary

| Test Type         | Virtual Threads Win? | Key Advantage                      |
|-------------------|----------------------|------------------------------------|
| CPU-Bound         | ⚠️ Inconclusive       | Logic issue                        |
| IO-Bound          | ✅ Yes                | Scalable throughput                |
| Mixed Workload    | ✅ Yes                | Faster + less memory               |
| Structured Conc.  | ✅ Yes                | Fast thread startup                |
| Scoped Values     | ✅ Yes                | Efficient isolation/context use    |
| Thread Benchmark  | ✅ Yes                | Huge speed gain (over 50x faster)  |

---

🧠 **Conclusion:**  
Virtual Threads in Java 21 (Loom) offer **massive performance improvements**, especially in high-concurrency and I/O-heavy workloads. They consume fewer resources, launch faster, and maintain clean structured concurrency via `StructuredTaskScope` and `ScopedValue`.

---

📁 Generated on: `Windows 11 • Temurin OpenJDK 21.0.6 • July 2025`

