## Mixed (I/O + CPU) Threading Models Performance Test

This guide explains how to set up, run, and monitor the performance tests for **Platform Threads**, **Virtual Threads**, and **Hybrid Threads** using different thread counts.

---

## 1️⃣ Prerequisites

Before running the tests, ensure you have:

* **Java 21 or later** (Virtual Threads require Project Loom features included in Java 21+)
* **Maven** (for building and running the project)
* **JDK bin folder added to PATH** (so you can run `jcmd` and `jstat`)
* **Operating System**: Linux / macOS / Windows 10+

To verify installation:

```bash
java -version
mvn -version
```

---

## 2️⃣ Clone the Repository

```bash
git clone https://github.com/yourusername/mixed-threading-test.git
cd mixed-threading-test
```

---

## 3️⃣ Build the Project

```bash
mvn clean package
```

---

## 4️⃣ Run Tests

### 4.1 Using Maven

```bash
mvn exec:java -Dexec.mainClass="com.example.MixedThreadingTest"
```

### 4.2 Using `java -jar`

```bash
java -jar target/mixed-threading-test.jar
```

---

## 5️⃣ Monitoring Memory & CPU Usage

### 5.1 JVM-Level Monitoring

#### With **JCMD**

```bash
jcmd <PID> VM.native_memory summary
```

Replace `<PID>` with the process ID of the running Java program.
Find PID:

```bash
jps -l
```

#### With **JFR** (Java Flight Recorder)

```bash
jcmd <PID> JFR.start name=MixedTest filename=recording.jfr duration=60s
```

---

### 5.2 OS-Level Monitoring

#### Linux/macOS

```bash
top
```

#### Windows

```powershell
tasklist /fi "imagename eq java.exe"
```

---

## 6️⃣ Thread Counts Tested

The code automatically runs with the following thread counts:

```
1000, 2000, 5000, 10000, 20000
```

---

## 7️⃣ launch.json (VS Code Setup)

Add this to `.vscode/launch.json`:

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Run Mixed Threading Test",
            "request": "launch",
            "mainClass": "com.example.MixedThreadingTest",
            "vmArgs": "--enable-preview -Xmx4G -Xms512M -XX:NativeMemoryTracking=detail"
        }
    ]
}
```

---

## 8️⃣ Example Command to Run with NMT & GC Logs

```bash
java --enable-preview -Xmx4G -Xms512M -XX:NativeMemoryTracking=detail -XX:+UnlockDiagnosticVMOptions -XX:+PrintGCDetails -jar target/mixed-threading-test.jar
```
