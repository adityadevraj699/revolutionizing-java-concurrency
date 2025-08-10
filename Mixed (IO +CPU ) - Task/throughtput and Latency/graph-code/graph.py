import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd

# Set Seaborn style
sns.set(style="whitegrid")

# Updated data
data = [
    {"Thread Count": 1000, "Model": "Platform", "Time": 0.86, "Throughput": 1162.79, "Latency": 169.86, "Creation": 12},
    {"Thread Count": 1000, "Model": "Virtual", "Time": 0.18, "Throughput": 5681.82, "Latency": 174.41, "Creation": 18},
    {"Thread Count": 1000, "Model": "Hybrid", "Time": 0.16, "Throughput": 6250.00, "Latency": 79.79, "Creation": 14},

    {"Thread Count": 5000, "Model": "Platform", "Time": 3.95, "Throughput": 1265.82, "Latency": 156.63, "Creation": 18},
    {"Thread Count": 5000, "Model": "Virtual", "Time": 0.21, "Throughput": 24154.59, "Latency": 185.76, "Creation": 8},
    {"Thread Count": 5000, "Model": "Hybrid", "Time": 0.16, "Throughput": 30487.80, "Latency": 79.29, "Creation": 10},

    {"Thread Count": 10000, "Model": "Platform", "Time": 8.14, "Throughput": 1228.20, "Latency": 161.94, "Creation": 20},
    {"Thread Count": 10000, "Model": "Virtual", "Time": 0.22, "Throughput": 44642.86, "Latency": 195.15, "Creation": 6},
    {"Thread Count": 10000, "Model": "Hybrid", "Time": 0.17, "Throughput": 60606.06, "Latency": 79.85, "Creation": 11},

    {"Thread Count": 20000, "Model": "Platform", "Time": 15.62, "Throughput": 1280.49, "Latency": 155.40, "Creation": 24},
    {"Thread Count": 20000, "Model": "Virtual", "Time": 0.28, "Throughput": 71428.57, "Latency": 223.36, "Creation": 20},
    {"Thread Count": 20000, "Model": "Hybrid", "Time": 0.18, "Throughput": 109289.62, "Latency": 86.71, "Creation": 8},
]

# Convert to DataFrame
df = pd.DataFrame(data)

# Set up subplots
fig, axs = plt.subplots(2, 2, figsize=(14, 10))
fig.suptitle("Thread Model Comparison at Various Thread Counts (Mixed I/O + CPU)", fontsize=16)

# Plot configurations
metrics = ["Time", "Throughput", "Latency", "Creation"]
titles = ["Execution Time (s)", "Throughput (req/sec)", "Average Latency (ms)", "Thread Creation Time (ms)"]

# Loop through each subplot
for ax, metric, title in zip(axs.flat, metrics, titles):
    sns.barplot(data=df, x="Thread Count", y=metric, hue="Model", ax=ax)
    ax.set_title(title, fontsize=13)
    ax.set_xlabel("Thread Count")
    ax.set_ylabel(title)
    ax.legend(title="Model")

plt.tight_layout(rect=[0, 0, 1, 0.95])
plt.show()
