import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd

# Set IEEE-like plot style
sns.set(style="whitegrid", font_scale=1.2, rc={"font.family": "serif", "axes.labelweight": "bold"})

# Updated data from your Mixed (I/O + CPU) table
data = {
    'Thread Count': (
        [1000]*3 + 
        [2000]*3 + 
        [5000]*3 + 
        [10000]*3 + 
        [20000]*3
    ),
    'Thread Model': ['Platform', 'Virtual', 'Hybrid'] * 5,
    'Total Memory (MB)': [
        1.53, 3.93, 2.17,
        2.32, 5.29, 2.17,
        3.51, 8.80, 13.32,
        2.68, 12.40, 17.10,
        2.24, 18.84, 11.64
    ],
    'CPU (%)': [
        25.96, 323.82, 217.17,
        52.99, 472.45, 104.63,
        58.30, 474.00, 115.36,
        51.94, 566.10, 33.51,
        58.83, 591.21, 48.33
    ],
    'Time (s)': [
        0.24, 0.07, 0.06,
        0.24, 0.08, 0.10,
        0.62, 0.14, 0.15,
        1.44, 0.24, 0.23,
        2.63, 0.50, 0.42
    ]
}

df = pd.DataFrame(data)

# Unique thread counts and models
thread_counts = sorted(df['Thread Count'].unique())
models = ['Platform', 'Virtual', 'Hybrid']
colors = ['#1f77b4', '#ff7f0e', '#2ca02c']

# Plot Total Memory, CPU, and Time as subplots
fig, axes = plt.subplots(3, 1, figsize=(10, 14), sharex=True)

metrics = ['Total Memory (MB)', 'CPU (%)', 'Time (s)']
titles = [
    '(a) Total Memory Usage',
    '(b) CPU Utilization',
    '(c) Execution Time'
]

for i, metric in enumerate(metrics):
    ax = axes[i]
    for j, model in enumerate(models):
        subset = df[df['Thread Model'] == model]
        ax.plot(
            subset['Thread Count'],
            subset[metric],
            marker='o',
            label=model,
            color=colors[j],
            linewidth=2
        )
    
    ax.set_ylabel(metric, fontsize=12)
    ax.set_title(titles[i], loc='left', fontsize=13, fontweight='bold')
    ax.grid(True)

axes[2].set_xlabel("Thread Count", fontsize=12)
axes[0].legend(title="Thread Model", loc='upper left', fontsize=10)

plt.suptitle("Figure 4.3.2: Combined Resource Utilization under Mixed (I/O + CPU)-Bound Workload",
             fontsize=14, fontweight='bold')
plt.tight_layout(rect=[0, 0.03, 1, 0.97])
plt.show()
