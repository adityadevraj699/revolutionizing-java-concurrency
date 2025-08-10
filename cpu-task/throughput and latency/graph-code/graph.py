import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd

# Data
data = [
    {"Thread Count": 1000, "Model": "Platform", "Latency": 55.779, "Throughput": 98808.37, "Creation": 1},
    {"Thread Count": 1000, "Model": "Virtual", "Latency": 0.061, "Throughput": 825559.32, "Creation": 2},
    {"Thread Count": 1000, "Model": "ForkJoin", "Latency": 0.023, "Throughput": 1688903.90, "Creation": 1},
    {"Thread Count": 1000, "Model": "Hybrid", "Latency": 0.056, "Throughput": 561987.19, "Creation": 2},
    {"Thread Count": 2000, "Model": "Platform", "Latency": 0.039, "Throughput": 383523.82, "Creation": 4},
    {"Thread Count": 2000, "Model": "Virtual", "Latency": 0.068, "Throughput": 838574.42, "Creation": 4},
    {"Thread Count": 2000, "Model": "ForkJoin", "Latency": 0.024, "Throughput": 3591309.03, "Creation": 1},
    {"Thread Count": 2000, "Model": "Hybrid", "Latency": 0.044, "Throughput": 641416.25, "Creation": 3},
    {"Thread Count": 10000, "Model": "Platform", "Latency": 0.031, "Throughput": 1993819.16, "Creation": 3},
    {"Thread Count": 10000, "Model": "Virtual", "Latency": 0.041, "Throughput": 1915598.72, "Creation": 4},
    {"Thread Count": 10000, "Model": "ForkJoin", "Latency": 0.026, "Throughput": 7949125.60, "Creation": 0},
    {"Thread Count": 10000, "Model": "Hybrid", "Latency": 0.044, "Throughput": 2312138.73, "Creation": 3},
    {"Thread Count": 50000, "Model": "Platform", "Latency": 0.037, "Throughput": 3703539.10, "Creation": 18},
    {"Thread Count": 50000, "Model": "Virtual", "Latency": 0.031, "Throughput": 2045157.07, "Creation": 39},
    {"Thread Count": 50000, "Model": "ForkJoin", "Latency": 0.026, "Throughput": 13871549.45, "Creation": 2},
    {"Thread Count": 50000, "Model": "Hybrid", "Latency": 0.031, "Throughput": 2119820.75, "Creation": 21},
    {"Thread Count": 100000, "Model": "Platform", "Latency": 0.032, "Throughput": 4019825.78, "Creation": 17},
    {"Thread Count": 100000, "Model": "Virtual", "Latency": 0.030, "Throughput": 1543617.22, "Creation": 34},
    {"Thread Count": 100000, "Model": "ForkJoin", "Latency": 0.026, "Throughput": 23935469.97, "Creation": 2},
    {"Thread Count": 100000, "Model": "Hybrid", "Latency": 0.032, "Throughput": 3335757.32, "Creation": 23}
]

df = pd.DataFrame(data)

palette = {
    'Platform': '#1f77b4',
    'Virtual': '#ff7f0e',
    'ForkJoin': '#2ca02c',
    'Hybrid': '#d62728'
}

# Plotting
fig, axs = plt.subplots(2, 2, figsize=(16, 10))
fig.suptitle("CPU-Bound Thread Model Benchmarking - Java 21", fontsize=18)

# Execution Time - Log Scale
sns.barplot(data=df, x="Thread Count", y="Latency", hue="Model", palette=palette, ax=axs[0, 0])
axs[0, 0].set_title("Execution Time (Latency - ms, Log Scale)")
axs[0, 0].set_yscale("log")
axs[0, 0].set_ylabel("Latency (ms)")
axs[0, 0].set_xlabel("Thread Count")
axs[0, 0].bar_label(axs[0, 0].containers[0], fmt="%.3f", fontsize=8)
axs[0, 0].bar_label(axs[0, 0].containers[1], fmt="%.3f", fontsize=8)

# Throughput - Log Scale
sns.barplot(data=df, x="Thread Count", y="Throughput", hue="Model", palette=palette, ax=axs[0, 1])
axs[0, 1].set_title("Throughput (ops/sec, Log Scale)")
axs[0, 1].set_yscale("log")
axs[0, 1].set_ylabel("Ops/sec")
axs[0, 1].set_xlabel("Thread Count")
axs[0, 1].bar_label(axs[0, 1].containers[0], fmt="%.0f", fontsize=8)
axs[0, 1].bar_label(axs[0, 1].containers[1], fmt="%.0f", fontsize=8)

# Thread Creation Time
sns.barplot(data=df, x="Thread Count", y="Creation", hue="Model", palette=palette, ax=axs[1, 0])
axs[1, 0].set_title("Thread Creation Time (ms)")
axs[1, 0].set_ylabel("Creation Time (ms)")
axs[1, 0].set_xlabel("Thread Count")
axs[1, 0].bar_label(axs[1, 0].containers[0], fmt="%.0f", fontsize=8)
axs[1, 0].bar_label(axs[1, 0].containers[1], fmt="%.0f", fontsize=8)

# Latency per Model by Thread Count (Log Scale)
sns.barplot(data=df, x="Model", y="Latency", hue="Thread Count", palette="Set2", ax=axs[1, 1])
axs[1, 1].set_title("Latency per Model (by Thread Count, Log Scale)")
axs[1, 1].set_yscale("log")
axs[1, 1].set_ylabel("Latency (ms)")
axs[1, 1].set_xlabel("Model")
axs[1, 1].bar_label(axs[1, 1].containers[0], fmt="%.3f", fontsize=8)

# Legends
for ax in axs.flat:
    ax.legend_.remove()

handles, labels = axs[0, 0].get_legend_handles_labels()
fig.legend(handles, labels, loc='upper center', bbox_to_anchor=(0.5, 0.02), ncol=4, fontsize=10)

plt.tight_layout(rect=[0, 0.05, 1, 0.95])
plt.show()
