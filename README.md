# 🚀 Revolutionizing Java Concurrency with Virtual Threads (Project Loom)

Welcome to the official GitHub documentation for the research project **"Revolutionizing Java Concurrency"**, focused on benchmarking and implementing **Virtual Threads** in Java 21+.

🔗 **Live Blog**: [nextgenjavaconcurrency.adityadevraj699.online](https://nextgenjavaconcurrency.adityadevraj699.online)

---

## 📚 Project Summary

This project explores the performance and practical application of **Virtual Threads** introduced by **Project Loom** in Java 21. It includes a comparative analysis between traditional platform threads and virtual threads using various workloads like:

- I/O-bound tasks (e.g., result portals, network-based apps)
- CPU-bound tasks (e.g., computational simulations)
- Mixed workloads (e.g., IRCTC-style ticket booking)

Key focus areas:
- **Benchmarking throughput, memory usage, and latency**
- **Thread pinning and how to avoid it**
- **Structured concurrency with `StructuredTaskScope`**
- **Real-world simulation: Chat system, ERP login flood, ticket booking**

---

## 🧩 Virtual Thread Execution Flow – Project Loom (Java 21)

> A visual flow of how Java 21’s virtual threads scale massively with fewer carrier threads.

```text
[Request 1]       [Request 2]       [Request 3]       [Request 4]
    |                 |                 |                 |
    |                 |                 |                 |
    |                 |                 |                 |
┌────────┐       ┌────────┐       ┌────────┐       ┌────────┐
│Virtual │       │Virtual │       │Virtual │       │Virtual │
│Thread 1│       │Thread 2│       │Thread 3│       │Thread 4│
└────┬───┘       └────┬───┘       └────┬───┘       └────┬───┘
     │                │                │                │
     ▼                ▼                ▼                ▼
┌────────────────────────────────────────────────────────────┐
│           Carrier Thread Pool (2–5 platform threads)        │
│ ┌────────────┐ ┌────────────┐ ┌────────────┐                │
│ │ Carrier-1  │ │ Carrier-2  │ │ ...        │                │
│ └────┬───────┘ └────┬───────┘ └────────────┘                │
│      │              │                                        │
│      ▼              ▼                                        │
│   Executes      Executes                                     │
│   V-Thread 1    V-Thread 2                                   │
│   ⏳ I/O wait    ⏳ I/O wait                                  │
│   ▼             ▼                                           │
│   Releases     Releases (Unmounts)                          │
│   Carrier      Carrier                                       │
│      │              │                                        │
│      ▼              ▼                                        │
│   Assigned to   Assigned to                                  │
│   V-Thread 3    V-Thread 4                                   │
└────────────────────────────────────────────────────────────┘
```
## 🌀 How It Works

- Virtual threads are lightweight (~4KB) and scheduled by the JVM.  
- When I/O is encountered, they **unmount** from the carrier thread.  
- Carriers are then reassigned to other ready virtual threads.  
- This allows **millions of virtual threads** to run with just a **few platform (carrier) threads**.

---

## 🧪 Technologies Used

- **Java 21 (Project Loom Preview)**
- **VisualVM**, **JMeter**, **JFR**, **htop**
- **StructuredTaskScope API**
- **Executors.newVirtualThreadPerTaskExecutor()**
- **ReentrantLock**, **R2DBC**, and Loom-compatible constructs

---

## 📝 Research Highlights

- Achieved **11x higher throughput** in I/O-heavy use cases using virtual threads.
- Demonstrated **~80% memory savings** in large-scale simulations.
- Simulated **50,000+ concurrent users** in chat and ERP testbeds.
- Identified thread pinning pitfalls and Loom optimization strategies.

For complete documentation, code walkthroughs, and live examples, visit the [💻 Blog](https://nextgenjavaconcurrency.adityadevraj699.online)

---

## 🙋 Author

**Aditya Devraj**  
Final Year Student, B.Tech CSE  
Meerut Institute of Technology  
📧 [aditya.kumar1.cs.2022@mitmeerut.ac.in](mailto:aditya.kumar1.cs.2022@mitmeerut.ac.in)  
🌐 [Portfolio Website](https://adityadevraj699.online)

---

## 📌 License

This research and source code are licensed under the **MIT License**.

---

## ⭐ If this helped you understand Java concurrency better, please consider starring the repo!
