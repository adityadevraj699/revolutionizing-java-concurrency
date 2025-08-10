import pandas as pd
import matplotlib.pyplot as plt

# --- New Updated Data (from your latest input) ---
data = [
    [1000, "Platform", 85.25172, 26.32, 0.12],
    [1000, "Virtual", 86.20203, 253.02, 0.02],
    [1000, "ForkJoin", 85.76203, 0, 0],
    [1000, "Hybrid", 87.41438, 51.15, 0.03],
    [2000, "Platform", 86.46414, 60.30, 0.23],
    [2000, "Virtual", 86.53789, 0, 0.01],
    [2000, "ForkJoin", 85.93223, 71.10, 0.02],
    [2000, "Hybrid", 88.15699, 13.73, 0.23],
    [5000, "Platform", 94.07119, 43.21, 1.05],
    [5000, "Virtual", 89.23246, 1367.09, 0.01],
    [5000, "ForkJoin", 88.09023, 0, 0],
    [5000, "Hybrid", 90.47773, 105.55, 0.01],
    [10000, "Platform", 89.14262, 65.27, 1.20],
    [10000, "Virtual", 93.99070, 0, 0.01],
    [10000, "ForkJoin", 91.13035, 0, 0],
    [10000, "Hybrid", 93.2925, 18.25, 0.09],
    [50000, "Platform", 87.17305, 66.40, 5.95],
    [50000, "Virtual", 158.9862, 444.64, 0.01],
    [50000, "ForkJoin", 92.26539, 287.34, 0.01],
    [50000, "Hybrid", 90.86109, 65.40, 0.02],
    [75000, "Platform", 87.42758, 64.32, 8.36],
    [75000, "Virtual", 153.8104, 336.79, 0.03],
    [75000, "ForkJoin", 94.26855, 87.87, 0.02],
    [75000, "Hybrid", 153.2623, 53.20, 0.06],
    [100000, "Platform", 91.6732, 65.18, 11.19],
    [100000, "Virtual", 167.323, 213.74, 0.06],
    [100000, "ForkJoin", 90.6502, 145.94, 0.01],
    [100000, "Hybrid", 154.4, 41.95, 0.04],
]

# Create DataFrame
df = pd.DataFrame(data, columns=["Thread Count", "Thread Model", "Total Memory (MB)", "CPU (%)", "Time (s)"])

# --- Set up 3 subplots in one figure ---
fig, axs = plt.subplots(3, 1, figsize=(12, 10), sharex=True)
thread_models = df["Thread Model"].unique()

# Plot A: Total Memory Usage
for model in thread_models:
    subset = df[df["Thread Model"] == model]
    axs[0].plot(subset["Thread Count"], subset["Total Memory (MB)"], marker='o', label=model)
axs[0].set_ylabel("Total Memory (MB)")
axs[0].set_title("(a) Total Memory Usage")
axs[0].grid(True)
axs[0].legend(title="Thread Model")

# Plot B: CPU Utilization
for model in thread_models:
    subset = df[df["Thread Model"] == model]
    axs[1].plot(subset["Thread Count"], subset["CPU (%)"], marker='o', label=model)
axs[1].set_ylabel("CPU (%)")
axs[1].set_title("(b) CPU Utilization")
axs[1].grid(True)

# Plot C: Execution Time
for model in thread_models:
    subset = df[df["Thread Model"] == model]
    axs[2].plot(subset["Thread Count"], subset["Time (s)"], marker='o', label=model)
axs[2].set_ylabel("Time (s)")
axs[2].set_xlabel("Thread Count")
axs[2].set_title("(c) Execution Time")
axs[2].grid(True)

# Super title
fig.suptitle("Figure 4.2.2: Combined Resource Utilization under CPU-Bound Workload", fontsize=14, fontweight='bold')

# Layout and show
plt.tight_layout(rect=[0, 0.03, 1, 0.97])
plt.show()
