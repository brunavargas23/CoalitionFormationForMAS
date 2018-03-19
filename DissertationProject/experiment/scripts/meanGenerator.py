import glob
import sys
import time
import argparse
import pandas as pd

def readCSV(prefix):
	print("Reading "+prefix+" .csv")
	allFiles = glob.glob(prefix+"_*.csv")
	listCSV = []
	for csv in allFiles:
    		listCSV.append(pd.read_csv(csv,index_col=False))
	return listCSV


def calculateMean(listCSV,groupby=None):
	df_concat = pd.concat(listCSV)
	if groupby is None:
		df = df_concat.mean().to_frame().T
	else:
		df = df_concat.groupby(groupby,as_index=False).mean()
	return df

parser = argparse.ArgumentParser(description='Generate the mean of step, individual and team files.')
parser.add_argument('-p', '--path',dest="path",default="",help='path to where the files are')
arguments = parser.parse_args(sys.argv[1:])

print("Running {}".format(sys.argv[0]))

listCSV_step = readCSV(arguments.path+'step')
df_step = calculateMean(listCSV_step,['steps'])
df_step.to_csv(arguments.path+'mean_step.csv',index=False)

listCSV_team = readCSV(arguments.path+'roundTeam')
df_team = calculateMean(listCSV_team)
df_team.to_csv(arguments.path+'mean_team.csv',index=False)

listCSV_individual = readCSV(arguments.path+'roundIndividual')
df_individual = calculateMean(listCSV_individual,['agent'])
df_individual.to_csv(arguments.path+'mean_individual.csv',index=False)



