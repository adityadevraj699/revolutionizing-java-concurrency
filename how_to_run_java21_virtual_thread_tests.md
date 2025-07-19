# 🚀 How to Compile and Run Java 21 Virtual Thread Benchmarks

This guide explains how to **compile and execute Java 21 concurrency benchmarks** using **Virtual Threads (Project Loom)** on **Windows 11** from the command line. It covers memory, CPU, and I/O benchmarks using the `--enable-preview` flag.

---

## 🛠️ Step 1: Compile All Java Files

Run the following command from your project root directory to compile all `.java` files using Java 21 preview features:

```bash
javac --enable-preview --release 21 -d out *.java
````

> **Explanation**:
>
> * `--enable-preview`: Enables preview features like Virtual Threads
> * `--release 21`: Specifies Java 21 version
> * `-d out`: Compiles all `.class` files to the `out/` directory

---

## ✅ Step 2: Run Any Benchmark Program

Use the following commands to run different benchmarks. All require the preview flag.

---

### ▶️ CPU-bound Benchmark

Tests performance under compute-intensive tasks.

```bash
java --enable-preview -cp out CpuBoundComparison
```

---

### ▶️ IO-bound Benchmark

Simulates many blocking I/O operations to test virtual thread scaling.

```bash
java --enable-preview -cp out IoBoundVirtualVsPlatform
```

---

### ▶️ Mixed Workload Benchmark

Combines CPU-bound and IO-bound tasks for a real-world scenario.

```bash
java --enable-preview -cp out MixedBenchmark
```

---

### ▶️ Structured Concurrency Demo

Demonstrates structured concurrency with various thread counts.

```bash
java --enable-preview -cp out StructuredConcurrencyDemo
```

---

### ▶️ Scoped Values Test

Tests how `ScopedValue` behaves with virtual threads in scoped concurrency.

```bash
java --enable-preview -cp out ScopedValuesDemo
```

---

### ▶️ Thread Benchmark

Compares raw thread creation overhead and execution time between Platform and Virtual Threads.

```bash
java --enable-preview -cp out ThreadBenchmark
```

---

### ▶️ 🧠 Memory Utilization Test

Compares memory usage between Platform and Virtual Threads with 100,000 sleeping threads.

```bash
java --enable-preview -cp out MemoryUtilizationTest
```

---

### ▶️ 📊 CSV Export Benchmark (`GraphExport`)

Benchmarks Platform vs Virtual Threads across task sizes and exports the results to a `CSV` file for graph plotting.

```bash
java --enable-preview -cp out GraphExport
```

> 📁 Output file: `benchmark_results.csv`

---

### ▶️ 🔍 Pinning Analysis (Java Flight Recorder)

Analyze thread pinning using `jdk.tracePinnedThreads` and Java Flight Recorder (JFR):

```bash
java --enable-preview ^
     -Djdk.tracePinnedThreads=full ^
     -XX:StartFlightRecording=filename=pinning.jfr,dumponexit=true ^
     -cp out GuaranteedPinningExample
```

> 🧠 This will generate a `.jfr` file (`pinning.jfr`) that you can open in **JDK Mission Control** or **VisualVM** to analyze pinning behavior.

---

## 💡 Notes & Tips

* 📁 Run all commands from the root directory where `.java` files and `out/` directory exist.
* 🧪 Make sure you're using **Java 21+** with **Project Loom support** (e.g., [Temurin](https://adoptium.net/) OpenJDK 21).
* ❗ If your `.java` files use `package`, make sure the folder structure (e.g., `src/com/aditya/...`) matches.
* 🖥️ Use tools like **VisualVM**, **JFR**, or **Task Manager** to monitor memory, threads, and CPU usage.
* ⚠️ Avoid running too many platform threads on low-RAM systems—they're heavier than virtual threads.

---

## 🧪 Tested On

| Property    | Details                      |
| ----------- | ---------------------------- |
| 💻 System   | Dell Inspiron 15 3000        |
| 🖥️ OS      | Windows 11                   |
| ☕ Java      | OpenJDK 21.0.6 (Temurin)     |
| 🛠️ Editor  | Visual Studio Code (VS Code) |
| 🔧 Terminal | PowerShell / Command Prompt  |
