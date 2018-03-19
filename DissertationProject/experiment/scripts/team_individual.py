import glob
import sys

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

def readDataframe(path):    
    allFiles = glob.glob(path)
    listCSV = []
    for csv in allFiles:
        listCSV.append(pd.read_csv(csv,index_col=False,usecols=vectColumms))
    return pd.concat(listCSV)

if len(sys.argv) < 4:
   print("doubleBoxplot.py <outputFileName> <title> <VectorColumms>")
   sys.exit(2)

outputFileName = sys.argv[1]
title = sys.argv[2]
vectColumms = sys.argv[3].split(",")

print("Running {}".format(sys.argv[0]))

fig, ax = plt.subplots(1,1)
    
df = readDataframe("roundTeam_*.csv")

#palette = ["#87CEEB"]
palette = ['#1f77b4']
sns.boxplot(ax=ax,data=df,showmeans=True,palette=palette)

ax.set_title(title)

plt.setp(ax.get_xticklabels(), rotation=45)

plt.tight_layout()

fig.savefig(outputFileName)
