#!/bin/bash

#experiment/experiment.sh > /dev/null

DIR_TEMP="experiment/temp"
DIR_CHART="experiment/chart"
DIR_RESULT="$HOME/Dropbox/DissertationExperiments/experimentos_Certo"
PATH_SCRIPT_FOLDER="experiment/scripts"

# used to run a number of experiments on a specific branch
declare -a DIRETORIOS=("CoalitionCLINK" "CoalitionRahwan" "TaskAllocation")

# used to run a number of experiments
declare -a NAME_EXPERIMENTS=("ex_12ag_jobrate_10" "ex_12ag_jobrate_25" "ex_12ag_jobrate_50" "ex_12ag_jobrate_75" "ex_12ag_jobrate_100")



for DIRETORIO in "${DIRETORIOS[@]}"
do
    echo "Entrando na pasta $DIRETORIO "   
    cd $DIRETORIO   
    
    for EXPERIMENT in "${NAME_EXPERIMENTS[@]}"
    do
        echo "Running experiment $EXPERIMENT da pasta $DIRETORIO"
	cd $EXPERIMENT
	python3 meanGenerator.py
	cd ..    
    done
    cd $DIR_RESULT
done

for EXPERIMENT in "${NAME_EXPERIMENTS[@]}"
do   
   python3 team_comparison.py 'completed jobs' "Jobs_Completos_$EXPERIMENT" $EXPERIMENT -t ""
   python3 team_comparison.py 'received jobs' "Jobs_Recebidos_$EXPERIMENT" $EXPERIMENT -t "" -j 5 -s 7 
   python3 step_comparison.py "Money_Do_$EXPERIMENT" $EXPERIMENT -t ""	
done	
echo "All the experiments are done!!! :D"
