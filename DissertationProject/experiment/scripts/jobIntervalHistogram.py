import glob
import sys
import argparse
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

parser = argparse.ArgumentParser(description='Generate the interval between jobs chart.')
parser.add_argument('outputFile',default="Teste",help='absolut path to file')
parser.add_argument('-t', '--title',dest="title",default="Job Intervals",help='chart title')
parser.add_argument('-x', '--xlabel',dest="xlabel",default="Interval",help='x label')
parser.add_argument('-y', '--ylabel',dest="ylabel",default="Frequency of Intervals",help='y label')
arguments = parser.parse_args(sys.argv[1:])

print("Running {}".format(sys.argv[0]))

fig, ax = plt.subplots(1,1)

columms = ['job interval']

df_task = pd.read_csv("jobInterval1.csv",index_col=False,usecols=columms)
df_task['method'] = 'TA Only'

df_clink = pd.read_csv("jobInterval2.csv",index_col=False,usecols=columms)
df_clink['method'] = 'CLINK'

df_rahwan = pd.read_csv("jobInterval3.csv",index_col=False,usecols=columms)
df_rahwan['method'] = 'DC'

df3 = pd.concat([   df_clink.melt(id_vars=['method'], value_vars = columms),
                    df_rahwan.melt(id_vars=['method'], value_vars=columms),
                    df_task.melt(id_vars=['method'], value_vars=columms)],
                    ignore_index=True)

sns.countplot(ax=ax, x='value', hue='method', data=df3)

ax.set_xlabel(arguments.xlabel)
ax.set_ylabel(arguments.ylabel)

ax.set_title(arguments.title)

fig.savefig(arguments.outputFile+'.pdf',bbox_inches='tight')
