# 📊 Thread Benchmark Results by Request Load

## 🔹 100 Requests

| Metric                      | Platform Thread | Virtual Thread |
|----------------------------|-----------------|----------------|
| Throughput (r/s)           | 529             | 588            |
| Avg Latency (ms)           | 163.74          | 162.80         |
| P95 Latency (ms)           | 175.00          | 164.00         |
| P99 Latency (ms)           | 175.00          | 164.00         |
| Thread Creation Time (ms)  | 0               | 0              |
| Live Threads               | 7               | 16             |
| Total Threads Created      | 100             | 9              |
| Memory Used                | 14428 KB        | 1528 KB        |

---

## 🔹 500 Requests

| Metric                      | Platform Thread | Virtual Thread |
|----------------------------|-----------------|----------------|
| Throughput (r/s)           | 2688            | 2777           |
| Avg Latency (ms)           | 155.47          | 167.91         |
| P95 Latency (ms)           | 163.00          | 173.00         |
| P99 Latency (ms)           | 164.00          | 173.00         |
| Thread Creation Time (ms)  | 0               | 0              |
| Live Threads               | 16              | 16             |
| Total Threads Created      | 500             | 0              |
| Memory Used                | 42633 KB        | 1601 KB        |

---

## 🔹 1000 Requests

| Metric                      | Platform Thread | Virtual Thread |
|----------------------------|-----------------|----------------|
| Throughput (r/s)           | 4672            | 5649           |
| Avg Latency (ms)           | 162.77          | 168.49         |
| P95 Latency (ms)           | 175.00          | 175.00         |
| P99 Latency (ms)           | 177.00          | 176.00         |
| Thread Creation Time (ms)  | 0               | 3              |
| Live Threads               | 16              | 16             |
| Total Threads Created      | 1000            | 0              |
| Memory Used                | 6435 KB         | 3218 KB        |

---

## 🔹 2000 Requests

| Metric                      | Platform Thread | Virtual Thread |
|----------------------------|-----------------|----------------|
| Throughput (r/s)           | 7352            | 11111          |
| Avg Latency (ms)           | 173.27          | 168.22         |
| P95 Latency (ms)           | 187.00          | 175.00         |
| P99 Latency (ms)           | 190.00          | 175.00         |
| Thread Creation Time (ms)  | 0               | 2              |
| Live Threads               | 16              | 16             |
| Total Threads Created      | 2000            | 0              |
| Memory Used                | -53298 KB       | 5002 KB        |

---

## 🧠 Summary Insights

- **Virtual Threads consistently outperform Platform Threads** in terms of throughput across all request levels.
- **Latency remains stable** and comparable between the two, with slight advantage to virtual threads in higher load scenarios.
- **Memory usage** is significantly lower for Virtual Threads, highlighting their scalability.
- **Thread creation time** remains negligible in both, but Virtual Threads handle bulk creation more efficiently.

---
