# 🚀 How to Compile and Run Java 21 Virtual Thread Benchmarks

This guide explains how to compile and execute Java 21 concurrency benchmarks using **Virtual Threads** (Project Loom) on Windows 11 using the command line and `--enable-preview` flag.

---

## 🛠️ Step 1: Compile All Java Files

Use this command in your project directory to compile all `.java` files with preview features enabled:

```java
javac --enable-preview --release 21 -d out *.java
````

* `--enable-preview`: Enables Java preview features (required for virtual threads)
* `--release 21`: Specifies the Java version
* `-d out`: Compiled `.class` files will go into the `out/` directory

---

## ✅ Step 2: Run the Program (Choose Your Benchmark)

Each of the following commands will execute a different benchmark or demo class.

---

### ▶️ CPU-bound Benchmark

Tests performance under compute-intensive load.

```java
java --enable-preview -cp out CpuBoundComparison
```

---

### ▶️ IO-bound Benchmark

Simulates multiple concurrent blocking I/O operations.

```java
java --enable-preview -cp out IoBoundVirtualVsPlatform
```

---

### ▶️ Mixed Workload Test

Simulates a blend of CPU-bound and IO-bound tasks.

```java
java --enable-preview -cp out MixedBenchmark
```

---

### ▶️ Structured Concurrency Demo

Demonstrates the performance of structured concurrency with different thread counts.

```java
java --enable-preview -cp out StructuredConcurrencyDemo
```

---

### ▶️ Scoped Values Test

Benchmarks how `ScopedValue` behaves with virtual threads in different scenarios.

```java
java --enable-preview -cp out ScopedValuesDemo
```

---

### ▶️ Thread Benchmark

Compares raw thread creation and execution time between platform and virtual threads.

```java
java --enable-preview -cp out ThreadBenchmark
```

---

### 🧪 Pinning Analysis using Java Flight Recorder (JFR)

```bash
java --enable-preview \
     -Djdk.tracePinnedThreads=full \
     -XX:StartFlightRecording=filename=pinning.jfr,dumponexit=true \
     -cp out GuaranteedPinningExample
```

## 💡 Notes

* These commands should be run from the root directory where your `.java` files and `out/` directory exist.
* Ensure you are using **Java 21+ (Temurin/OpenJDK)** with virtual thread support.
* Use `VisualVM` or Task Manager to monitor threads, memory, and CPU usage if needed.
* Make sure none of your `.java` files use a `package` declaration unless your folder structure matches it.

---

🖥️ **Tested On:**

* **System:** Dell Inspiron 15 3000
* **OS:** Windows 11
* **Java:** OpenJDK 21.0.6 (Temurin)
* **Editor:** VS Code
* **Shell:** PowerShell or Command Prompt

---
