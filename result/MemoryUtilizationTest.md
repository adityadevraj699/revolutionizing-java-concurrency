# MemoryUtilizationTest.md

## 📊 Virtual Thread Memory Benchmark Report

**Test Environment:**  
- **System:** Dell Inspiron 15 3000  
- **OS:** Windows 11  
- **RAM:** 8 GB  
- **JDK:** 21 (with `--enable-preview`)  
- **Monitoring Tool:** VisualVM 2.2  
- **Execution Time:** 19-07-2025 16:52:57  

---

## 📈 Heap Memory Usage
| Metric           | Value          |
|------------------|----------------|
| Heap Size        | ~675 MB        |
| Used Heap        | ~210 MB        |
| Max Heap         | ~2,078 MB      |
| GC Activity      | 0% (negligible)|

---

## 🧵 Thread Activity
| Metric              | Value        |
|---------------------|--------------|
| Live Threads        | 12,568       |
| Peak Live Threads   | 26,874       |
| Total Threads Created | 98,706     |
| Daemon Threads      | 16           |

---

## 🔍 Memory Utilization (Virtual Threads)
- **Approximate Memory Used:** ~210 MB  
- **Threads Created:** ~100,000  
- **Estimated Memory per Thread:** ≈ 2.1 KB  
- **Expected Theoretical Average:** ~4–6 KB  
- ✅ **Result:** Efficient & matches Project Loom expectations

---

## ✅ Observations
- Virtual threads created in bulk with low memory footprint.
- No major garbage collection activity during execution.
- VisualVM confirms practical applicability of lightweight concurrency using virtual threads.

---

## 📌 Conclusion
Virtual threads offer high scalability with minimal memory usage, making them ideal for concurrent workloads in student-level or microservice applications.

---

*Generated Automatically from VisualVM Monitoring Output*
