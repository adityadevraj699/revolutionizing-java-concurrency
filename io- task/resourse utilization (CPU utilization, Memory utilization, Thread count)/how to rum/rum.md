### ✅ **`launch.json` Configuration for Visual Studio Code (Java)**

Make sure you place this inside your `.vscode/launch.json` file:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Run ThreadComparisonWithJcmd",
      "request": "launch",
      "mainClass": "ThreadComparisonWithJcmd",
      "vmArgs": "-XX:NativeMemoryTracking=summary -XX:+UnlockDiagnosticVMOptions"
    }
  ]
}
```

> ✅ **Explanation:**

* `"type": "java"`: Specifies the Java debugger.
* `"request": "launch"`: Indicates this configuration is for launching (not attaching).
* `"mainClass"`: The fully qualified name of your main class (with package if any).
* `"vmArgs"`: JVM options to enable **Native Memory Tracking (NMT)** and unlock diagnostic tools.

---

### 🧪 **Manual Compile and Run (if using terminal):**

#### Step 1: Compile your Java class:

```bash
javac ThreadComparisonWithJcmd.java
```

#### Step 2: Run it with required JVM options:

```bash
java -XX:NativeMemoryTracking=summary -XX:+UnlockDiagnosticVMOptions ThreadComparisonWithJcmd
```

>  Make sure your Java version supports Native Memory Tracking (Java 8+).
