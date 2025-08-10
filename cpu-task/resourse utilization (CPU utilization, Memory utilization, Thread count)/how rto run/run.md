### ✅ **`launch.json` Configuration for Visual Studio Code (Java)**

Place this inside your `.vscode/launch.json` file:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Run ThreadComparisonWithJcmd",
      "request": "launch",
      "mainClass": "ThreadComparisonWithJcmd",
      "vmArgs": "--enable-preview -XX:NativeMemoryTracking=summary -XX:+UnlockDiagnosticVMOptions"
    }
  ]
}
```

> ✅ **Explanation:**
>
> * `"type": "java"` → Uses the Java debugger in VS Code.
> * `"request": "launch"` → Starts the program instead of attaching to an existing process.
> * `"mainClass"` → Your Java class name with the `main()` method (include package name if any).
> * `"vmArgs"` → JVM arguments to:
>
>   * Enable **Java 21 preview features** (`--enable-preview`).
>   * Enable **Native Memory Tracking** (`-XX:NativeMemoryTracking=summary`).
>   * Unlock extra diagnostic commands (`-XX:+UnlockDiagnosticVMOptions`).

---

### 🧪 **Manual Compile and Run (Terminal Method)**

#### Step 1: Compile with Java 21 Preview Features

```bash
javac --enable-preview --release 21 ThreadComparisonWithJcmd.java
```

#### Step 2: Run with Preview Features & NMT Enabled

```bash
java --enable-preview -XX:NativeMemoryTracking=summary ThreadComparisonWithJcmd
```

> 💡 **Tip:**
>
> * Use `--enable-preview` for **virtual threads** in Java 21.
> * Make sure `jcmd` is in your `PATH` (it comes with the JDK).
> * Output `.txt` NMT reports will be saved in the same folder as the program.
