# 📁 Project Structure – Revolutionizing Java Concurrency
```yml
revolutionizing-java-concurrency/
├── README.md                           # 📘 Project overview, objectives, and summary
├── structure.md                        # 🗂️ Directory and file organization
├── how_to_run_java21_virtual_thread_tests.md   # 🔧 Step-by-step guide to run Java 21 preview features
│
├── code/                               # 📂 Java source code and compiled output
│   ├── benchmark_results.csv           # 📊 Exported benchmark metrics in CSV
│   ├── CpuBoundComparison.java         # 🔥 CPU-bound benchmark: virtual vs platform threads
│   ├── GraphExport.java                # 📈 Utility for exporting results to graphs
│   ├── IoBoundVirtualVsPlatform.java   # 🌐 I/O-bound benchmark simulating DB/API calls
│   ├── MixedBenchmark.java             # 🔄 Mixed workload test combining CPU and I/O
│   ├── ScopedValuesDemo.java           # 🧵 ScopedValue concurrency use case demonstration
│   ├── StructuredConcurrencyDemo.java  # 🌲 Structured concurrency demo with task scoping
│   ├── ThreadBenchmark.java            # 🧪 Basic test comparing thread performance
│   └── out/                            # 📦 Compiled `.class` files (javac output)
│
├── questions/                          # 📂 Analytical markdown answers for report sections
│   ├── benchmark-analysis.md           # 📈 In-depth analysis of performance results
│   ├── conceptual-questions.md         # 📚 Definitions and core Java concurrency concepts
│   ├── decision-making-analysis.md     # 💡 Justification for design/tech stack choices
│   ├── implementation-questions.md     # 💻 Code-level implementation insights
│   ├── industry-comparison-virtual-threads.md  # 🏭 Loom vs Go, Node.js, Kotlin, etc.
│   ├── limitations-future-work.md      # ⚠️ Limitations of study and proposed future scope
│   ├── realworld-usecases.md           # 🌍 Practical use cases of virtual threads
│   └── technical-questions.md          # 🧠 Advanced technical deep-dives
│
├── result/                             # 📂 Benchmark execution result logs and markdowns
│   ├── CpuBoundComparison.md           # Result of CPU-bound test execution
│   ├── GraphExport.md                  # Output log of graph generation
│   ├── IOMetricBenchmark.md            # Result of I/O-bound test execution
│   ├── MixedBenchmark.md               # Mixed workload result
│   ├── ScopedValuesDemo.md             # Output from ScopedValue example run
│   ├── StructuredConcurrencyDemo.md    # Output from StructuredConcurrency example
│   └── ThreadBenchmark.md              # Output from basic thread test
```

## 📁 Related Resources

* 🔗 [GitHub Repo: Java Concurrency Benchmarks](https://github.com/adityadevraj699/revolutionizing-java-concurrency)
* 🔗 [Blog: Java Virtual Threads Simplified](https://nextgenjavaconcurrency.adityadevraj699.online/)
