import glob
import sys
import time
import argparse
import pandas as pd

parser = argparse.ArgumentParser(description='Generate the mean of step, individual, team and interval files.')
parser.add_argument('-p', '--path',dest="path",default="",help='path to where the files are')
arguments = parser.parse_args(sys.argv[1:])

print("Running {}".format(sys.argv[0]))

allFiles = glob.glob(arguments.path+"jobInterval_*.csv")
dfs=[]
for csv in allFiles:
    temp_df = pd.read_csv(csv,index_col=False)
    dfs.append(temp_df['job interval'].value_counts(ascending=True).reset_index())
df_concat = pd.concat(dfs)
df_concat.columns = ['interval','frequency']
df_team = df_concat.groupby('interval').mean()
df_team.to_csv(arguments.path+'mean_interval.csv')
print("Interval mean csv generated")

