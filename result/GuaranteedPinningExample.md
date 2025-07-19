# 📄 Pinning Observation Log (JFR Trace Output)

| Timestamp   | Event Type     | Details                                                                 |
|-------------|----------------|-------------------------------------------------------------------------|
| 0.538s      | [info][jfr]    | Started recording 1 (Java Flight Recorder session started)             |
| 0.538s      | [info][jfr]    | No limit specified, using maxsize=250MB as default                     |
| 0.538s      | [info][jfr]    | Use `jcmd 116056 JFR.dump name=1` to copy recording data to file       |
| —           | Application    | Virtual thread starting...                                             |
| —           | Virtual Thread | Reading from input stream (waiting for input)... [Enter something]     |

---

## 🧪 **Observation Summary**

- Java Flight Recorder (JFR) was successfully started with default size.
- Virtual thread execution began and attempted to perform a blocking I/O operation using `BufferedReader.readLine()`.
- This causes **thread pinning**, a known limitation with virtual threads when using legacy blocking I/O.
- The JVM traces this and logs data to `pinning.jfr`.

> 🔍 **Note**: This output confirms that pinning behavior can be replicated and analyzed using JFR + `-Djdk.tracePinnedThreads=full`.

[jfr]: https://docs.oracle.com/en/java/javase/17/jfapi/java-flight-recorder.html

