import glob
import time
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.ticker import MaxNLocator
import seaborn as sns
import sys

def readDataframe(path):    
    allFiles = glob.glob(path)
    listCSV = []
    for csv in allFiles:
        listCSV.append(pd.read_csv(csv,index_col=False,usecols=[baseColumm,valueColumm]))
    return pd.concat(listCSV)

def sortIDAgent(df):
    df['agent'] = df['agent'].astype('category')
    df['agent'] = (df['agent'].cat.reorder_categories(sorted(df['agent'].cat.categories, key = lambda x: int(x[7:]), reverse=False)))
    df.groupby('agent').sum()
    
if len(sys.argv) < 8:
    print("doubleBoxplot.py <baseColumm> <ValueColumm> <outputFileName> <title> <xlabel> <ylabel> <rotation>")
    sys.exit(2)
 
baseColumm = sys.argv[1]
valueColumm = sys.argv[2]
outputFileName = sys.argv[3]
title = sys.argv[4]
xLabel = sys.argv[5]
yLabel = sys.argv[6]
rotation = sys.argv[7]

print("Running {}".format(sys.argv[0]))

fig, ax = plt.subplots(1,1)
    
df_task = readDataframe("task/roundIndividual_*.csv")
df_task['method'] = 'just task allocation'

df_coalition = readDataframe("coalition/roundIndividual_*.csv")
df_coalition['method'] = 'with coalition'

df3 = pd.concat([df_coalition.melt(id_vars=['method',baseColumm], value_vars = [valueColumm]),
		 df_task.melt(id_vars=['method',baseColumm], value_vars = [valueColumm])],
                 ignore_index=True)

if baseColumm == 'agent':
    sortIDAgent(df3)
else: 
    df3.sort_values(baseColumm)

palette = ['#1f77b4','#ff7f0e']
sns.boxplot(ax=ax,x=baseColumm, y='value', hue='method', data=df3, palette=palette)

# configure number of ticks in y axis
yint = range(min(df_task['worked jobs']), max(df_task['worked jobs'])+1)
plt.yticks(yint)

for index, label in enumerate(ax.yaxis.get_ticklabels()):
	if index % 5 != 0:
	    label.set_visible(False)

ax.set_title(title)
ax.set_xlabel(xLabel)
ax.set_ylabel(yLabel)

plt.setp(ax.get_xticklabels(), rotation=rotation)  

# configure space to cover labels and title
plt.tight_layout()

fig.savefig(outputFileName)
