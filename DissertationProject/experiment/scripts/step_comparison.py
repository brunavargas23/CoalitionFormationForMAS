import sys
import argparse
import pandas as pd
import matplotlib.pyplot as plt

parser = argparse.ArgumentParser(description='Generate the money per step comparison between approaches.')
parser.add_argument('outputFile',help='output file name')
parser.add_argument('experimentFolder',help='folder name to where the mean files are')
parser.add_argument('-t', '--title',dest="title",default="Money per Step Comparison Between Approaches",help='chart title')
parser.add_argument('-x', '--xlabel',dest="xlabel",default="Steps",help='x-axis label')
parser.add_argument('-y', '--ylabel',dest="ylabel",default="Money",help='y-axis label')
parser.add_argument('-fw', '--figurewidth',dest="figwidth",type=float,default=6.4,help='figure width size')
parser.add_argument('-fh', '--figureheight',dest="figheight",type=float,default=4.8,help='figure height size')
parser.add_argument('-z', '--zoom',dest="zoom",nargs='?',const=True,default=False,help='zoom at the final steps')
arguments = parser.parse_args(sys.argv[1:])

print("Running {}".format(sys.argv[0]))

df_clink = pd.read_csv('CoalitionCLINK/'+arguments.experimentFolder+'/mean_step.csv',index_col=False)
df_rahwan = pd.read_csv('CoalitionRahwan/'+arguments.experimentFolder+'/mean_step.csv',index_col=False)
df_task_3 = pd.read_csv('TaskAllocation/'+arguments.experimentFolder+'/mean_step.csv',index_col=False)
df_task_1 = pd.read_csv('TaskAllocation_SMART/'+arguments.experimentFolder+'/mean_step.csv',index_col=False)
 
#print(plt.rcParams.get('figure.figsize'))
fig, axes = plt.subplots(1,1,figsize=(arguments.figwidth, arguments.figheight))

df_clink.plot(ax=axes,x='steps',y='money')
df_rahwan.plot(ax=axes,x='steps',y='money')
df_task_1.plot(ax=axes,x='steps',y='money')
df_task_3.plot(ax=axes,x='steps',y='money')

axes.legend(["CLINK", "DC", "TA Only (1 Job)", "TA Only (3 Jobs)"]);

xint = range(0, 1001, 200)
plt.xticks(xint)

axes.set_xlabel(arguments.xlabel)
axes.set_ylabel(arguments.ylabel)

axes.set_title(arguments.title)
plt.tight_layout()

if (arguments.zoom):
	a = plt.axes([.65, .165, .3, .3])
	df_clink[980:].plot(ax=a,y='money')
	df_rahwan[980:].plot(ax=a,y='money')
	df_task_1[980:].plot(ax=a,y='money')
	df_task_3[980:].plot(ax=a,y='money')
	a.set_title("Zoom at the Final Steps")
	a.legend_.remove()

fig.savefig(arguments.outputFile+'.pdf',bbox_inches='tight')

