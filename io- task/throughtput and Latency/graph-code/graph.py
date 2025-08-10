import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd

# Set Seaborn style
sns.set(style="whitegrid")

# Data as list of dictionaries
data = [
    {"Thread Count": 1000, "Model": "Platform", "Time": 0.84, "Throughput": 1190.48, "Latency": 168.72, "Creation": 45},
    {"Thread Count": 1000, "Model": "Virtual", "Time": 0.29, "Throughput": 3434.48, "Latency": 106.54, "Creation": 30},
    {"Thread Count": 1000, "Model": "Hybrid", "Time": 0.19, "Throughput": 5131.57, "Latency": 163.04, "Creation": 11},
    
    {"Thread Count": 10000, "Model": "Platform", "Time": 8.21, "Throughput": 1217.77, "Latency": 161.02, "Creation": 42},
    {"Thread Count": 10000, "Model": "Virtual", "Time": 2.91, "Throughput": 3441.81, "Latency": 161.51, "Creation": 69},
    {"Thread Count": 10000, "Model": "Hybrid", "Time": 7.56, "Throughput": 1324.75, "Latency": 154.51, "Creation": 22},

    {"Thread Count": 50000, "Model": "Platform", "Time": 37.75, "Throughput": 1324.26, "Latency": 150.48, "Creation": 20},
    {"Thread Count": 50000, "Model": "Virtual", "Time": 0.26, "Throughput": 19393.94, "Latency": 210.17, "Creation": 20},
    {"Thread Count": 50000, "Model": "Hybrid", "Time": 7.55, "Throughput": 1324.83, "Latency": 158.63, "Creation": 32},

    {"Thread Count": 100000, "Model": "Platform", "Time": 75.50, "Throughput": 1324.45, "Latency": 150.48, "Creation": 20},
    {"Thread Count": 100000, "Model": "Virtual", "Time": 0.56, "Throughput": 17919.89, "Latency": 412.25, "Creation": 88},
    {"Thread Count": 100000, "Model": "Hybrid", "Time": 75.43, "Throughput": 1325.79, "Latency": 186.09, "Creation": 77},
]

# Convert to DataFrame
df = pd.DataFrame(data)

# Set up subplots
fig, axs = plt.subplots(2, 2, figsize=(14, 10))
fig.suptitle("Thread Model Comparison at Various Thread Counts", fontsize=16)

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

