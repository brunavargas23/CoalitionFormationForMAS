import glob
import sys
import matplotlib.pyplot as plt
import pandas as pd

if len(sys.argv) < 6:
    print("stepChart.py <absolutPathToFile> <outputFileName> <title> <xlabel> <ylabel>")
    sys.exit(2)

path = sys.argv[1]
outputFileName = sys.argv[2]
title = sys.argv[3]
xLabel = sys.argv[4]
yLabel = sys.argv[5]

print("Running {}".format(sys.argv[0]))


df = pd.read_csv(path,index_col=False,usecols=['steps','money'])

fig, ax = plt.subplots(1,1)

df.plot(ax=ax,x='steps',linestyle='-')

ax.set_xlabel(xLabel)
ax.set_ylabel(yLabel)

ax.set_title(title)

fig.savefig(outputFileName)
