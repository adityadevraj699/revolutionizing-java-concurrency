# Benchmark Analysis of Virtual Threads vs Platform Threads

This benchmark analysis is based on controlled experiments conducted in the research paper  
📄 *Revolutionizing Java Concurrency: A Comparative Study of Traditional Threads and Virtual Threads (Project Loom)*.

---

## 🧪 Benchmark Question:
**How do virtual threads compare to platform threads in real-world performance benchmarks under I/O-bound, CPU-bound, and mixed workloads?**

---

## ✅ Benchmark Summary

### 1. I/O-Bound Workload
Simulated using:
```java
Thread.sleep(50);  // Network delay
Thread.sleep(100); // DB delay
````

#### 🔍 Result:

| Metric       | Platform Threads         | Virtual Threads          |
| ------------ | ------------------------ | ------------------------ |
| Throughput   | \~2,000 requests/second  | \~22,000 requests/second |
| Memory Usage | \~1MB per thread         | \~4KB per thread         |
| Latency      | High under load          | Low and stable           |
| Scalability  | Crashed beyond 10K users | Scaled to 50K+ users     |

> 💡 *Conclusion*: Virtual threads dramatically outperformed platform threads in I/O-heavy scenarios with 10x better throughput and 80% lower memory usage.

---

### 2. CPU-Bound Workload

Used a `isPrime()` function to simulate intensive computation.

#### 🔍 Result:

| Metric     | Platform Threads | Virtual Threads                   |
| ---------- | ---------------- | --------------------------------- |
| Throughput | \~2000 ops/sec   | \~250–300 ops/sec                 |
| Latency    | \~1ms            | \~4ms                             |
| CPU Usage  | Maxed out evenly | Slightly higher due to scheduling |

> ⚠️ *Conclusion*: No major advantage for virtual threads. Platform threads performed better for compute-heavy tasks.

---

### 3. Mixed Workload

Combined network latency + prime number check:

```java
Thread.sleep(20);
isPrime(15485863);
```

#### 🔍 Result:

Virtual threads showed better scalability while handling hybrid workloads due to reduced blocking overhead. Ideal for real-world apps like ticket booking (IRCTC) or chatbot backends.

---

## 📈 Memory Footprint Comparison

| Thread Type      | Avg Memory/Thread | Threads per 1 GB RAM |
| ---------------- | ----------------- | -------------------- |
| Platform Threads | \~1MB             | \~1,000              |
| Virtual Threads  | \~4KB             | \~250,000            |

---

## 🔬 Developer Benchmark Program (Java 21)

```java
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
for (int i = 0; i < 10000; i++) {
    executor.submit(() -> {
        Thread.sleep(100); // Simulate I/O
    });
}
```

📊 **Benchmark Result**:

* **Platform Threads**: 15,500 ms total time
* **Virtual Threads**: 1,200 ms total time

> 👉 10x faster execution for I/O-bound workload with virtual threads.

---

## 🔚 Conclusion

Virtual threads show:

* **Massive gains** in throughput and memory efficiency for I/O-heavy workloads
* **Limited benefit** for CPU-bound tasks
* **Excellent scalability** for mixed and real-world concurrent applications

They make Java concurrency **lighter, faster, and easier to write**.

---

## 📁 Related Resources

* 🔗 [GitHub Code: Virtual vs Platform Thread Benchmarks](https://github.com/adityadevraj699/revolutionizing-java-concurrency)
* 🔗 [Blog: Next-Gen Java Concurrency Simplified](https://nextgenjavaconcurrency.adityadevraj699.online/)

