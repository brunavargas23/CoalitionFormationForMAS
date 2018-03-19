#!/bin/bash

PATH_SCRIPT_FOLDER="."
	
echo "Generating charts..."

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate08" "ex_12ag_jobrate_08"  -t "Job Generation Probability per Step of 8%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate08" "ex_12ag_jobrate_08" -t "Received Jobs with Job Generation Probability per Step of 8%" -y "Number of Jobs Sent by the Server"
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate08" "ex_12ag_jobrate_08" -t "Completed Jobs with Job Generation Probability per Step of 8%" -y "Number of Jobs Completed by each Approach"

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate10" "ex_12ag_jobrate_10"  -t "Job Generation Probability per Step of 10%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate10" "ex_12ag_jobrate_10" -t "Received Jobs with Job Generation Probability per Step of 10%" -y "Number of Jobs Sent by the Server"
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate10" "ex_12ag_jobrate_10" -t "Completed Jobs with Job Generation Probability per Step of 10%" -y "Number of Jobs Completed by each Approach"

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate12" "ex_12ag_jobrate_12"  -t "Job Generation Probability per Step of 12%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate12" "ex_12ag_jobrate_12" -t "Received Jobs with Job Generation Probability per Step of 12%" -y "Number of Jobs Sent by the Server"
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate12" "ex_12ag_jobrate_12" -t "Completed Jobs with Job Generation Probability per Step of 12%" -y "Number of Jobs Completed by each Approach"

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate14" "ex_12ag_jobrate_14"  -t "Job Generation Probability per Step of 14%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate14" "ex_12ag_jobrate_14" -t "Received Jobs with Job Generation Probability per Step of 14%" -y "Number of Jobs Sent by the Server" -j 5 -s 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate14" "ex_12ag_jobrate_14" -t "Completed Jobs with Job Generation Probability per Step of 14%" -y "Number of Jobs Completed by each Approach"

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate16" "ex_12ag_jobrate_16"  -t "Job Generation Probability per Step of 16%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate16" "ex_12ag_jobrate_16" -t "Received Jobs with Job Generation Probability per Step of 16%" -y "Number of Jobs Sent by the Server" -j 5 -s 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate16" "ex_12ag_jobrate_16" -t "Completed Jobs with Job Generation Probability per Step of 16%" -y "Number of Jobs Completed by each Approach"

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate18" "ex_12ag_jobrate_18"  -t "Job Generation Probability per Step of 18%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate18" "ex_12ag_jobrate_18" -t "Received Jobs with Job Generation Probability per Step of 18%" -y "Number of Jobs Sent by the Server" -j 5 -s 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate18" "ex_12ag_jobrate_18" -t "Completed Jobs with Job Generation Probability per Step of 18%" -y "Number of Jobs Completed by each Approach"

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate20" "ex_12ag_jobrate_20"  -t "Job Generation Probability per Step of 20%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate20" "ex_12ag_jobrate_20" -t "Received Jobs with Job Generation Probability per Step of 20%" -y "Number of Jobs Sent by the Server" -j 5 -s 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate20" "ex_12ag_jobrate_20" -t "Completed Jobs with Job Generation Probability per Step of 20%" -y "Number of Jobs Completed by each Approach"

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate30" "ex_12ag_jobrate_30"  -t "Job Generation Probability per Step of 30%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate30" "ex_12ag_jobrate_30" -t "Received Jobs with Job Generation Probability per Step of 30%" -y "Number of Jobs Sent by the Server" -j 5 -s 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate30" "ex_12ag_jobrate_30" -t "Completed Jobs with Job Generation Probability per Step of 30%" -y "Number of Jobs Completed by each Approach"

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate40" "ex_12ag_jobrate_40"  -t "Job Generation Probability per Step of 40%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate40" "ex_12ag_jobrate_40" -t "Received Jobs with Job Generation Probability per Step of 40%" -y "Number of Jobs Sent by the Server" -j 5 -s 10
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate40" "ex_12ag_jobrate_40" -t "Completed Jobs with Job Generation Probability per Step of 40%" -y "Number of Jobs Completed by each Approach"

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate50" "ex_12ag_jobrate_50"  -t "Job Generation Probability per Step of 50%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate50" "ex_12ag_jobrate_50" -t "Received Jobs with Job Generation Probability per Step of 50%" -y "Number of Jobs Sent by the Server" -j 5 -s 10
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate50" "ex_12ag_jobrate_50" -t "Completed Jobs with Job Generation Probability per Step of 50%" -y "Number of Jobs Completed by each Approach"

python3 $PATH_SCRIPT_FOLDER/step_comparison.py "Money_Comparison_JobRate90" "ex_12ag_jobrate_90"  -t "Job Generation Probability per Step of 90%" -fw 10 -fh 5
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "received jobs" "ReceivedJob_Comparison_JobRate90" "ex_12ag_jobrate_90" -t "Received Jobs with Job Generation Probability per Step of 90%" -y "Number of Jobs Sent by the Server" -j 5 -s 10
python3 $PATH_SCRIPT_FOLDER/team_comparison.py "completed jobs" "CompletedJob_Comparison_JobRate90" "ex_12ag_jobrate_90" -t "Completed Jobs with Job Generation Probability per Step of 90%" -y "Number of Jobs Completed by each Approach"




echo "All charts were generated!"


