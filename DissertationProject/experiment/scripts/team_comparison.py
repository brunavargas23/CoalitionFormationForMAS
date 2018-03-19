import sys
import glob
import argparse
import matplotlib.pyplot as plt
from matplotlib.ticker import MaxNLocator
import pandas as pd
import seaborn as sns

def readDataframe(path):    
    allFiles = glob.glob(path)
    listCSV = []
    for csv in allFiles:
        listCSV.append(pd.read_csv(csv,index_col=False,usecols=[arguments.baseColumn]))
    return pd.concat(listCSV)

parser = argparse.ArgumentParser(description='Generate the boxplot comparison between approaches based on a column.')
parser.add_argument('baseColumn',help='base column name')
parser.add_argument('outputFile',help='output file name')
parser.add_argument('experimentFolder',help='folder name to where the mean files are')
parser.add_argument('-t', '--title',dest="title",default="Comparison Between Approaches",help='chart title')
parser.add_argument('-x', '--xlabel',dest="xlabel",default="Approaches",help='x-axis label')
parser.add_argument('-y', '--ylabel',dest="ylabel",default="Money",help='y-axis label')
parser.add_argument('-j', '--tickjump',dest="jump",type=int,default=5,help='number of ticks to jump when showing y-label')
parser.add_argument('-s', '--tickstep',dest="step",type=int,default=1,help='number of ticks step when showing y-label')
arguments = parser.parse_args(sys.argv[1:])

print("Running {}".format(sys.argv[0]))

fig, ax = plt.subplots(1,1)
    
df_task_3 = readDataframe("TaskAllocation/"+arguments.experimentFolder+"/roundTeam_*.csv")
df_task_3 = df_task_3.rename(columns=lambda x: str('TA Only (3 Jobs)'))

df_task_1 = readDataframe("TaskAllocation_SMART/"+arguments.experimentFolder+"/roundTeam_*.csv")
df_task_1 = df_task_1.rename(columns=lambda x: str('TA Only (1 Job)'))

df_clink = readDataframe("CoalitionCLINK/"+arguments.experimentFolder+"/roundTeam_*.csv")
df_clink = df_clink.rename(columns=lambda x: str('CLINK'))

df_rahwan = readDataframe("CoalitionRahwan/"+arguments.experimentFolder+"/roundTeam_*.csv")
df_rahwan = df_rahwan.rename(columns=lambda x: str('DC'))

df3 = pd.concat([df_task_3,df_task_1,df_clink,df_rahwan])

#palette = ['#1f77b4','#ff7f0e','']
#sns.boxplot(ax=ax,data=df3,showmeans=True,palette=palette)
sns.boxplot(ax=ax,data=df3,showmeans=True)

ax.set_title(arguments.title)
ax.set_xlabel(arguments.xlabel)
ax.set_ylabel(arguments.ylabel)

yint = range(0, int(df3.max().max())+1, arguments.step)
plt.yticks(yint)
for index, label in enumerate(ax.yaxis.get_ticklabels()):
	if index % arguments.jump != 0:
	    label.set_visible(False)

# configure space to cover labels
plt.tight_layout()

fig.savefig(arguments.outputFile+'.pdf',bbox_inches='tight')
