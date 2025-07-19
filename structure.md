# 📁 Project Structure – Revolutionizing Java Concurrency

```yml
revolutionizing-java-concurrency/
├── README.md                           # Project overview
├── structure.md                        # Directory and file organization
├── how_to_run_java21_virtual_thread_tests.md   # 🔧 Run instructions (NEW)
│
├── code/                               # 📂 Source code + results
│   ├── benchmark_results.csv           # CSV export of benchmark metrics
│   ├── CpuBoundComparison.java         # CPU-bound task benchmark
│   ├── GraphExport.java                # Utility for exporting graphs
│   ├── IoBoundVirtualVsPlatform.java   # I/O benchmark for thread types
│   ├── MixedBenchmark.java             # Mixed workload test
│   ├── ScopedValuesDemo.java           # Scoped value concurrency test
│   ├── StructuredConcurrencyDemo.java  # Structured concurrency demo
│   ├── ThreadBenchmark.java            # Basic thread benchmark test
│   └── out/                            # Compiled `.class` files
│
├── questions/                          # 📂 Analytical markdown responses
│   ├── benchmark-analysis.md           # Interpretation of benchmark results
│   ├── conceptual-questions.md         # Core concept explanations
│   ├── decision-making-analysis.md     # Design/tech decision justification
│   ├── implementation-questions.md     # Coding-specific questions answered
│   ├── industry-comparison-virtual-threads.md  # Loom vs Industry alternatives
│   ├── limitations-future-work.md      # Constraints + future scope
│   ├── realworld-usecases.md           # Practical uses of virtual threads
│   └── technical-questions.md          # Deep technical exploration
│
├── result/                             # 📂 Benchmark execution results
│   └── result.md                       # Output logs and measured performance
```

## 📁 Related Resources

* 🔗 [GitHub Repo: Java Concurrency Benchmarks](https://github.com/adityadevraj699/revolutionizing-java-concurrency)
* 🔗 [Blog: Java Virtual Threads Simplified](https://nextgenjavaconcurrency.adityadevraj699.online/)
* 📄 [Research Paper: Revolutionizing Java Concurrency (PDF)](./paper/Research_Paper.pdf)
* 📂 [Conceptual Questions](./questions/conceptual-questions.md)
* 📂 [Benchmark Analysis](./questions/benchmark-analysis.md)
* 📂 [Code Samples](./code/IoBoundVirtualVsPlatform.java)
