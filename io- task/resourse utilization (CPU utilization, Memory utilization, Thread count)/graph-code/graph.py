import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd

# Set IEEE-like plot style
sns.set(style="whitegrid", font_scale=1.2, rc={"font.family": "serif", "axes.labelweight": "bold"})

# Data from Table 4.1.5
data = {
    'Thread Count': [1000]*3 + [5000]*3 + [10000]*3 + [50000]*3 + [75000]*3 + [100000]*3,
    'Thread Model': ['Platform', 'Virtual', 'Hybrid'] * 6,
    'Total Memory (MB)': [1.37, 4.00, 2.68, 3.38, 11.05, 2.45, 2.59, 19.59, 2.86, 3.92, 49.03, 6.42, 5.37, 69.39, 11.66, 3.18, 93.87, 11.84],
    'CPU (%)': [55.57, 251.06, 0.00, 29.75, 300.40, 1.94, 44.82, 169.59, 7.85, 44.64, 205.38, 0.20, 58.22, 203.49, 0.54, 53.75, 136.86, 0.20],
    'Time (s)': [0.11, 0.04, 0.19, 0.53, 0.07, 0.80, 1.08, 0.07, 1.59, 4.66, 0.21, 7.87, 6.90, 0.27, 11.66, 8.84, 0.32, 15.57]
}

df = pd.DataFrame(data)

# Unique thread counts and models
thread_counts = sorted(df['Thread Count'].unique())
models = ['Platform', 'Virtual', 'Hybrid']
colors = ['#1f77b4', '#ff7f0e', '#2ca02c']

# Plot Total Memory, CPU, and Time as subplots
fig, axes = plt.subplots(3, 1, figsize=(10, 14), sharex=True)

metrics = ['Total Memory (MB)', 'CPU (%)', 'Time (s)']
titles = ['(a) Total Memory Usage', '(b) CPU Utilization', '(c) Execution Time']

for i, metric in enumerate(metrics):
    ax = axes[i]
    for j, model in enumerate(models):
        subset = df[df['Thread Model'] == model]
        ax.plot(subset['Thread Count'], subset[metric], marker='o', label=model, color=colors[j], linewidth=2)
    
    ax.set_ylabel(metric, fontsize=12)
    ax.set_title(titles[i], loc='left', fontsize=13, fontweight='bold')
    ax.grid(True)

axes[2].set_xlabel("Thread Count", fontsize=12)
axes[0].legend(title="Thread Model", loc='upper left', fontsize=10)

plt.suptitle("Figure 4.1.2: Combined Resource Utilization under I/O-Bound Workload", fontsize=14, fontweight='bold')
plt.tight_layout(rect=[0, 0.03, 1, 0.97])
plt.show()
