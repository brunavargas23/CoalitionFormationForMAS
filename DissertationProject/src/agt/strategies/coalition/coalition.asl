//!setup_coalition_artefact.
//{include("strategies/coalition/coalitionAlgorithm.asl")}

{begin namespace(lCoalition, local)}

setOfAgentsWithJobs([],Temp,SetAgents) 
:- 	SetAgents = Temp
	.
setOfAgentsWithJobs([job(JobId,_,_,_,_,_)|ListJobs],Temp,SetAgents) 
:- 	.union([JobId],Temp,NewSet) 
&	setOfAgentsWithJobs(ListJobs,NewSet,SetAgents)
	.

{end}

isMyCoalition(Coalition) :- .my_name(Me) & .member(Me,Coalition).

coalitionJob([],Temp,JobIds) 
:-	JobIds = Temp
	.
coalitionJob([Agent|Coalition],Temp,JobId) 
:- 	.substring("job",Agent) 
& 	coalitionJob(Coalition,[Agent|Temp],JobId)
	.
coalitionJob([Agent|Coalition],Temp,JobId) 
:- 	coalitionJob(Coalition,Temp,JobId)
	.

+!setup_coalition_artefact(Id,NumberOfAgents,NumberOfJobs,ListJobs)
	: .my_name(vehicle1) & default::role(Role,_,_,_,_)
<-
	.print("Preparing artefact to ",ListJobs," we have ",NumberOfAgents," agents");
	
	.include("strategies/coalition/coalitionAlgorithm.asl",Id);
	!Id::setup_coalition_artefact(Id,"algorithms.adapterCFSS",NumberOfAgents*NumberOfJobs);	
	
	!Id::put_constraints(ListJobs);
	
	for(.member(job(JobId,_,_,_,_),ListJobs)){
		+action::hold_action(JobId);			
	}
	
	!Id::define_tasks(ListJobs);
	
	!!Id::run_algorithm;
	.
+!setup_coalition_artefact(Id,NumberOfAgents,NumberOfJobs,ListJobs) <- .print("Passou aqui, não deveria").
+!form_coalition(Agents,Id,ListJobs)
	: .my_name(Me) & default::role(Role,_,_,_,_) 
<-
	.print("Forming coalition of agents for ",ListJobs);
	
	if (Me \== vehicle1){
		.include("strategies/coalition/coalitionAlgorithm.asl",Id);
		!Id::setup_coalition_artefact(Id);
	}	
	
	for(.member(job(JobId,_,_,_,Reward),ListJobs)){
//		+gCoalition::coalitionAgents(JobId,[],Reward);
		+Id::coalitionAgents(JobId,Reward);
	}
	
	!Id::add_agent_to_CFArtefact(Me,Role);
	
//	!Id::define_pos_neg_constraints(Agents,ListJobs);
	!Id::define_graph_neighbours_constraint(Agents,ListJobs);
	
	!Id::post_contribution(ListJobs);
	.
	
// ### STRUCTURE GENERATION ###	
	
+ModuleId::coalitionStructure([])
	: .substring("cf_",ModuleId)
<-
	!failed_coalition_structure(ModuleId);
	!finish_coalition_structure(ModuleId);
	.
+ModuleId::coalitionStructure(CS)
	: .substring("cf_",ModuleId)
<-
	.print("CS: ",CS);
	for(.member(coalition(Value,Members),CS)){
		?coalitionJob(Members,[],JobIds)
		if (JobIds \== []){			
			.difference(Members,JobIds,AgentsCoalition);			
			for(.member(JobId,JobIds)){		
				if (Value > 0){
					!continue_coalition_process(ModuleId,AgentsCoalition,JobId);
				} else{
					!failed_coalition_job(JobId);
				}						
			}
		}
	}
	
	!finish_coalition_structure(ModuleId);
	.

+!continue_coalition_process(ModuleId,Coalition,JobId)
	: .my_name(vehicle1)
<-
	?gCoalition::freeTrucksCoalition(Coalition,[],TCoalition);
	+initiator::coalitionAgents(JobId,Coalition);
	+initiator::coalitionTrucks(JobId,TCoalition);
	?initiator::job(JobId,ListItems,ListToolsNew,Items,End,Storage,Reward);
	!!initiator::separate_tasks(JobId, Storage, ListItems, ListToolsNew, Items);
	-initiator::job(JobId,ListItems,ListToolsNew,Items,End,Storage,Reward);

	!check_my_coalition(ModuleId,Coalition,JobId);
	.
+!continue_coalition_process(ModuleId,Coalition,JobId)
<-
	!check_my_coalition(ModuleId,Coalition,JobId);
	.
	
+!failed_coalition_structure(ModuleId)
	: .my_name(vehicle1)
<-
	for ( initiator::job(JobId,_,_,_,_,_,_) ) {
        !failed_coalition_job(JobId);
    }
	.
+!failed_coalition_structure(ModuleId).
+!failed_coalition_job(JobId)
	: .my_name(vehicle1)
<-
    .print("There is no suitable coalition for ",JobId);
	!initiator::update_coalition_failed;
	-initiator::job(JobId,_,_,_,_,_,_);
	-action::hold_action(JobId);
	.
+!failed_coalition_job(JobId).
+!finish_coalition_structure(ModuleId)
	: .my_name(vehicle1)
<-
    !ModuleId::dispose_coalition_artefact;
    !ModuleId::finish_module;
	.
+!finish_coalition_structure(ModuleId)
<-
	!ModuleId::finish_module;
	.

+!check_my_coalition(ModuleId,Coalition,JobId)
	: isMyCoalition(Coalition)
<-
	.print("I'm in the coalition for ",JobId);
	?ModuleId::coalitionAgents(JobId,Reward);
	+gCoalition::coalitionAgents(JobId,Coalition,Reward);
	.
+!check_my_coalition(ModuleId,Coalition,JobId).
	
// ### PAYMENT DIVISION ###	
+!start_division(JobId,JobReward)
	: gCoalition::coalitionAgents(JobId,Coalition,_)
<-
	.send(Coalition,achieve,coalition::payment_division(JobId,JobReward));
	.
+!start_division(JobId,JobReward).
+!payment_division(JobId,JobReward)
	: gCoalition::coalitionAgents(JobId,Coalition,_) & .length(Coalition,NumbMembers)
<-
	.print(JobId," provided the following reward ",JobReward);
	IndividualReward = math.floor(JobReward/NumbMembers);
	!update_money_earned(JobId,IndividualReward);
	-gCoalition::coalitionAgents(JobId,_,_);
	.
@updateMoneyEarned[atomic]
+!update_money_earned(JobId,Reward)
	: metrics::moneyEarned(Money)
<-
	.print("I received ",Reward,"$ for ",JobId);
	-+metrics::moneyEarned(Money+Reward)
	.