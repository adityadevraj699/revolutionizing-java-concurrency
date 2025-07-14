# 🚀 Revolutionizing Java Concurrency with Virtual Threads (Project Loom)

Welcome to the official GitHub documentation for the research project **"Revolutionizing Java Concurrency"**, focused on benchmarking and implementing **Virtual Threads** in Java 21+.

🔗 **Live Blog**: [nextgenjavaconcurrency.adityadevraj699.online](https://nextgenjavaconcurrency.adityadevraj699.online)

---

## 📚 Project Summary

This project explores the performance and practical application of **Virtual Threads** introduced by **Project Loom** in Java 21. It includes a comparative analysis between traditional platform threads and virtual threads using various workloads like:

* I/O-bound tasks (e.g., result portals, network-based apps)
* CPU-bound tasks (e.g., computational simulations)
* Mixed workloads (e.g., IRCTC-style ticket booking)

Key focus areas:

* **Benchmarking throughput, memory usage, and latency**
* **Thread pinning and how to avoid it**
* **Structured concurrency with `StructuredTaskScope`**
* **Real-world simulation: Chat system, ERP login flood, ticket booking**

---

## 🆚 Virtual Threads vs Traditional Threads – Execution Flow

```text
[Request 1]       [Request 2]       [Request 3]       [Request 4]        ← Virtual Threads
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
│           Carrier Thread Pool (2–5 platform threads)       │
│ ┌────────────┐ ┌────────────┐ ┌────────────┐               │
│ │ Carrier-1  │ │ Carrier-2  │ │ ...        │               │
│ └────┬───────┘ └────┬───────┘ └────────────┘               │
│      │              │                                      │
│      ▼              ▼                                      │
│   Executes      Executes                                   │
│   V-Thread 1    V-Thread 2                                 │
│   ⏳ I/O wait    ⏳ I/O wait                              │
│   ▼             ▼                                          │
│   Releases     Releases (Unmounts)                         │
│   Carrier      Carrier                                     │
│      │              │                                      │
│      ▼              ▼                                      │
│   Assigned to   Assigned to                                │
│   V-Thread 3    V-Thread 4                                 │
└────────────────────────────────────────────────────────────┘

[Request 1]       [Request 2]       [Request 3]       [Request 4]        ← Traditional Threads
    |                 |                 |                 |
    |                 |                 |                 |
    |                 |                 |                 |
┌────────────┐   ┌────────────┐   ┌────────────┐   ┌────────────┐
│ Platform   │   │ Platform   │   │ Platform   │   │ Platform   │
│ Thread 1   │   │ Thread 2   │   │ Thread 3   │   │ Thread 4   │
└────────────┘   └────────────┘   └────────────┘   └────────────┘
     │                 │                 │                 │
     ▼                 ▼                 ▼                 ▼
 Performs Task   Performs Task    Performs Task     Performs Task
 (I/O or CPU)    (I/O or CPU)     (I/O or CPU)      (I/O or CPU)
     │                 │                 │                 │
     ▼                 ▼                 ▼                 ▼
Blocked during   Blocked during   Blocked during   Blocked during
  I/O wait         I/O wait         I/O wait         I/O wait
```

* One thread per request — direct 1:1 mapping with OS threads
* High memory footprint (\~1MB per thread)
* Expensive context switching and poor scalability under load

---

## 🌀 How Virtual Threads Work (Java 21 – Project Loom)

* Virtual threads are **lightweight (\~4KB)** and fully managed by the **JVM**, not the OS.
* When a virtual thread hits a **blocking operation** (like I/O), it is **unmounted** from its carrier (platform thread).
* The **carrier thread is immediately reused** for another runnable virtual thread — ensuring high throughput.
* Once the I/O completes, the virtual thread is **remounted** and resumes execution.
* This enables **millions of virtual threads** to run efficiently using just **a few hundred platform threads**.

---

## 🧪 Technologies Used

* **Java 21** (Project Loom Preview Features)
* **VisualVM**, **JMeter**, **Java Flight Recorder (JFR)**, **htop**
* **StructuredTaskScope API** for structured concurrency
* **Executors.newVirtualThreadPerTaskExecutor()** for scalable task execution
* **ReentrantLock**, **R2DBC**, and other Loom-compatible libraries

> ⚙️ **Note**:
> To run the code examples in this project, **you only need Java 21 installed**.
> No additional setup is required — just compile and run!

---

## 📝 Key Research Highlights

* Achieved **11× higher throughput** in I/O-intensive scenarios using virtual threads.
* Reduced memory usage by **\~80%** in simulations with 10,000+ threads.
* Successfully simulated **50,000+ concurrent users** in chat systems and ERP login environments.
* Identified and documented causes of **thread pinning** and how to avoid them using modern Java constructs.

📘 For full technical documentation, code samples, benchmarks, and real-world applications, explore the [💻 Project Blog](https://nextgenjavaconcurrency.adityadevraj699.online)

---

## 👨‍💻 Authors

<table>
  <tr>
    <td width="50%" valign="top" style="padding-right: 30px; font-family: sans-serif; line-height: 1.6;">
      <strong style="font-size: 16px;">Aditya Kumar</strong><br>
      Final Year Student – B.Tech in Computer Science & Engineering<br>
      Meerut Institute of Technology, Meerut (Uttar Pradesh, India)<br>
      📧 <a href="mailto:aditya.kumar1.cs.2022@mitmeerut.ac.in" style="color: inherit;">Email</a><br>
      🔗 <a href="https://adityadevraj699.online" target="_blank" style="color: inherit;">Portfolio Website</a>
    </td>
    <td width="50%" valign="top" style="font-family: sans-serif; line-height: 1.6;">
      <strong style="font-size: 16px;">Amol Sharma</strong><br>
      Faculty Mentor<br>
      Department of Computer Science & Engineerng<br>
      Meerut Institute of Technology, Meerut (Uttar Pradesh, India)<br>
      📧 <a href="mailto:amol.sharma@mitmeerut.ac.in" style="color: inherit;">Email</a>
    </td>
  </tr>
</table>

---

## 📄 License

This project and all its code, diagrams, and documentation are made available under the **MIT License**.

---

## ⭐ Support the Project

If you found this project helpful or insightful, please consider ⭐ starring the repository on GitHub to support the research!
