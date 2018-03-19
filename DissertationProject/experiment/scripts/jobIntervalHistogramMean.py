import glob
import sys
import argparse
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

parser = argparse.ArgumentParser(description='Generate the interval between jobs chart.')
parser.add_argument('outputFile',default="Teste",help='path to chart file')
parser.add_argument('experimentFolder',help='folder name to where the mean files are')
parser.add_argument('-t', '--title',dest="title",default="Job Intervals",help='chart title')
parser.add_argument('-x', '--xlabel',dest="xlabel",default="Interval",help='x label')
parser.add_argument('-y', '--ylabel',dest="ylabel",default="Frequency of Intervals",help='y label')
arguments = parser.parse_args(sys.argv[1:])

print("Running {}".format(sys.argv[0]))

fig, ax = plt.subplots(1,1)

columms = ['frequency','interval']

df_task = pd.read_csv("TaskAllocation/"+arguments.experimentFolder+"/mean_interval.csv",index_col=False,usecols=columms)
df_task['method'] = 'TA Only'

df_clink = pd.read_csv("CoalitionCLINK/"+arguments.experimentFolder+"/mean_interval.csv",index_col=False,usecols=columms)
df_clink['method'] = 'CLINK'

df_rahwan = pd.read_csv("CoalitionRahwan/"+arguments.experimentFolder+"/mean_interval.csv",index_col=False,usecols=columms)
df_rahwan['method'] = 'DC'

df3 = pd.concat([df_clink,df_rahwan,df_task], ignore_index=True)

sns.barplot(data=df3, x='interval', y='frequency', hue='method')

ax.set_xlabel(arguments.xlabel)
ax.set_ylabel(arguments.ylabel)

ax.set_title(arguments.title)

plt.xticks(rotation=-45)

fig.savefig(arguments.outputFile+'.pdf',bbox_inches='tight')
