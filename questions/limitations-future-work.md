# Limitations and Future Work

This document outlines the observed **limitations**, **threats to validity**, and **future exploration areas** based on the findings of the study:  
📄 *Revolutionizing Java Concurrency: A Comparative Study of Traditional Threads and Virtual Threads (Project Loom)*

---

## 1. When Does Thread Pinning Occur in Virtual Threads?

Virtual threads, while lightweight and efficient, can experience **thread pinning** — where the underlying platform thread remains occupied. This reduces scalability.

### 🔍 Common causes:
- Use of `synchronized` blocks/methods
- Native method calls (e.g., JNI)
- Blocking legacy APIs (e.g., `Object.wait()`, `InputStream.read()`)
- Non-Loom-friendly JDBC drivers

> 🛠 *Mitigation*: Use `ReentrantLock`, replace legacy I/O with NIO, and adopt Loom-compatible libraries like R2DBC.

---

## 2. What Is the Impact of Virtual Threads on Garbage Collection?

Virtual threads allocate **heap-based call stacks**, unlike platform threads that use native memory. This introduces:

- **Increased stack churn** → Higher minor GC activity
- Potential **pressure on the young generation heap**
- GC tuning needs (e.g., stack size, heap sizing)

> 🧪 *Observation*: In a simulated NLP API using virtual threads, GC pauses were caused not by object leaks but by excessive stack creation under heavy load.

---

## 3. Were the Benchmarks OS-Specific?

Yes. All experiments were conducted on:
- OS: Ubuntu Linux 22.04 LTS (kernel 5.15)
- Java: OpenJDK 21 (with `--enable-preview`)

### 🔍 Threat:
Thread scheduling, GC behavior, and syscall handling **vary across OSes** (e.g., Windows vs Linux). So results might differ across platforms.

> 💡 *Example*: Thread performance on Windows may vary due to different scheduler behavior and time-slicing.

---

## 4. Are Virtual Threads Suitable for All Workloads?

**No**, virtual threads are **not optimal for every scenario**.

### ✅ Best for:
- I/O-bound tasks
- High-concurrency services
- Applications with long blocking waits

### ⚠️ Not ideal for:
- Pure CPU-bound tasks (e.g., encryption, heavy number crunching)
- Native-integrated systems with blocking external calls
- Real-time systems needing low-latency deterministic execution

> 📊 *Result*: In CPU-only tasks like `isPrime()`, platform threads outperformed virtual threads slightly due to scheduling overhead.

---

## 5. How Does JVM Memory Pressure Affect Virtual Threads?

Virtual threads store their stacks in the **JVM heap**. Under heavy load:

- Thousands of virtual threads = millions of small heap allocations
- Increased **GC cycles**
- Risk of heap fragmentation or long GC pause if poorly tuned

### 🔬 Future tuning areas:
- Heap sizing for stack allocations
- GC behavior tuning (e.g., G1 GC vs ZGC)
- Stack size flags for dense thread workloads

---

## 6. What Were the Study’s Main Limitations?

### 📌 Key constraints:
- Tests were limited to **Java 21** with preview features
- OS and hardware were fixed (no cross-platform benchmarks)
- Only **custom, controlled workloads** were benchmarked — real enterprise workloads may differ
- Framework-level integration (e.g., Spring Boot, Quarkus) not explored
- Observability and profiling tools for virtual threads are still maturing

> 🧪 *Note*: The absence of Loom-aware diagnostics (e.g., stack trace tools) made profiling challenging.

---

## 🔮 Future Work Suggestions

1. **Framework integration tests** (Spring WebFlux, Quarkus, Micronaut)
2. **Long-running system behavior** (e.g., 24-hour stability with 100K threads)
3. **Cloud-native scaling tests** on Kubernetes or AWS Lambda
4. **Advanced observability tooling** for pinned thread tracing and GC analysis
5. **Hybrid architecture tuning** (virtual + platform thread strategies)

---

## 📁 Related Resources

- 🔗 [GitHub Repo: Java Thread Performance Benchmarks](https://github.com/adityadevraj699/revolutionizing-java-concurrency)
- 🔗 [Blog: Java Virtual Threads - Challenges & Limitations](https://nextgenjavaconcurrency.adityadevraj699.online/)

