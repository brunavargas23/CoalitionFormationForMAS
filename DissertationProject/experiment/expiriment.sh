#!/bin/bash

#experiment/experiment.sh > /dev/null

DIR_TEMP="experiment/temp"
DIR_CHART="experiment/chart"
DIR_RESULT="$HOME/Dropbox/DissertationExperiments"
PATH_SCRIPT_FOLDER="experiment/scripts"

# used to run a number of experiments on a specific branch
declare -a NAME_BRANCHES=("TaskAllocation" "CoalitionCLINK" "CoalitionRahwan")

# used to run a number of experiments
declare -a NAME_EXPERIMENTS=("ex_12ag_jobrate_08" "ex_12ag_jobrate_10" "ex_12ag_jobrate_12" "ex_12ag_jobrate_14" "ex_12ag_jobrate_16" "ex_12ag_jobrate_18" "ex_12ag_jobrate_20")

# how many iterations
iterations=5 

if [ ! -d "$DIR_TEMP" ]; then
	echo "Directory $DIR_TEMP does not exist, creating it..." 
	mkdir -p "$DIR_TEMP"
fi
if [ ! -d "$DIR_CHART" ]; then
	echo "Directory $DIR_CHART does not exist, creating it..." 
	mkdir -p "$DIR_CHART"
fi
if [ ! -d "$DIR_RESULT" ]; then
	echo "Directory $DIR_RESULT does not exist, creating it..." 
	mkdir -p "$DIR_RESULT"
fi

echo "Directory $DIR_RESULT"

ExperimentFolder=$DIR_RESULT/experiment_$(date "+%Y-%m-%d_%H-%M")

for BRANCH in "${NAME_BRANCHES[@]}"
do
    echo "Moving to branch $BRANCH to start experiments"   
    git checkout $BRANCH    
    
    for EXPERIMENT in "${NAME_EXPERIMENTS[@]}"
    do
        echo "Running experiment $EXPERIMENT from branch $BRANCH"

        echo "Preparing temp folder..."
                rm -rfv $DIR_TEMP/*
                rm -rfv $DIR_CHART/*

        for ((i=1; i<=$iterations ; i++));
        do
                echo "Running iteration $i of $iterations of experiment $EXPERIMENT from branch $BRANCH"    
                
                gradle execJunit -PjunitFile=pucrs.agentcontest2017.ScenarioRun1simParis_Automatic -PexperimentConf=$EXPERIMENT
                
                echo "Fixing file name..."
                mv $DIR_TEMP/step.csv $DIR_TEMP/step_$i.csv
                mv $DIR_TEMP/roundTeam.csv $DIR_TEMP/roundTeam_$i.csv
                mv $DIR_TEMP/roundIndividual.csv $DIR_TEMP/roundIndividual_$i.csv
		mv $DIR_TEMP/jobInterval.csv $DIR_TEMP/jobInterval_$i.csv
        done

        echo "Generating mean..."
        python3 $PATH_SCRIPT_FOLDER/meanGenerator.py -p $DIR_TEMP/
	python3 $PATH_SCRIPT_FOLDER/meanInterval.py -p $DIR_TEMP/

        echo "Generating charts..."
        #python3 $PATH_SCRIPT_FOLDER/stepChart.py $DIR_TEMP/mean_step.csv $DIR_CHART/step_money.pdf "" "Steps" "Money"
        #python3 $PATH_SCRIPT_FOLDER/team_individual.py $DIR_CHART/team_results.pdf "" "completed jobs,failed jobs,free agents failed,failed coalition"

        echo "Moving to respective folder..."
        mkdir -p $ExperimentFolder/$BRANCH/$EXPERIMENT/chart
        mv $DIR_TEMP/* $ExperimentFolder/$BRANCH/$EXPERIMENT/
        mv $DIR_CHART/* $ExperimentFolder/$BRANCH/$EXPERIMENT/chart/

        echo "experiment $EXPERIMENT is finished on branch $BRANCH!"
    done
    echo "experiments on branch $BRANCH are finished!"
done
echo "All the experiments are done!!! :D"

