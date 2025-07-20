# 📁 Project Structure – Revolutionizing Java Concurrency

```yml
├── README.md                          # Project overview and instructions
├── how_to_run_java21_virtual_thread_tests.md  # Guide to running benchmarks
├── structure.md                       # Project structure and organization
├── FUTURE.md                         # 🔮 Advanced industry topics and future directions
│
├───Analysis VirtualVM/               # 🔍 Visual analysis from VisualVM
│   ├── CpuBoundComparison.png
│   ├── IOMetricBenchmark.png
│   ├── MemoryUtilizationTest.png
│   ├── ScopedValuesDemo.png
│   ├── StructuredConcurrencyDemo.png
│   └── ThreadBenchmark.png
│
├───code/                             # 💻 Source code and outputs
│   ├── benchmark_results.csv         # CSV data used in plotting
│   ├── CpuBoundComparison.java
│   ├── GraphExport.java
│   ├── GuaranteedPinningExample.java
│   ├── IOMetricBenchmark.java
│   ├── MemoryUtilizationTest.java
│   ├── MixedBenchmark.java
│   ├── ScopedValuesDemo.java
│   ├── StructuredConcurrencyDemo.java
│   ├── ThreadBenchmark.java
│   ├── pinning.jfr                   # Java Flight Recorder data
│   └───out/                          # (Compiled outputs or classes)
│
├───questions/                        # ❓ Q&A and theoretical discussions
│   ├── benchmark-analysis.md
│   ├── conceptual-questions.md
│   ├── decision-making-analysis.md
│   ├── implementation-questions.md
│   ├── industry-comparison-virtual-threads.md
│   ├── limitations-future-work.md
│   ├── realworld-usecases.md
│   └── technical-questions.md
│
└───result/                           # 📊 Result summaries from benchmarks
    ├── CpuBoundComparison.md
    ├── GraphExport.md
    ├── GuaranteedPinningExample.md
    ├── IOMetricBenchmark.md
    ├── MemoryUtilizationTest.md
    ├── MixedBenchmark.md
    ├── ScopedValuesDemo.md
    ├── StructuredConcurrencyDemo.md
    └── ThreadBenchmark.md
```

## 📁 Related Resources

* 🔗 [GitHub Repo: Java Concurrency Benchmarks](https://github.com/adityadevraj699/revolutionizing-java-concurrency)
* 🔗 [Blog: Java Virtual Threads Simplified](https://nextgenjavaconcurrency.adityadevraj699.online/)
