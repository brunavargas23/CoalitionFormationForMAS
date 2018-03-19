//!setup_coalition_artefact.

{begin namespace(lCoalition, local)}

+!finish_internal_module
<-
	.abolish(::_);
	.drop_desire(::_);
	.

{end}

{begin namespace(gCoalition, global)}

// Se o agente consegue carregar tools soma 5, soma 1 para cada item que consegue carregar
//mcValueTools([],MyTools,0).
//mcValueTools([item(Tool,Qtd)|ListTool],MyTools,Value+5) :- .member(Tool,MyTools) & mcValueTools(ListTool,MyTools,Value).
//mcValueTools([item(Tool,Qtd)|ListTool],MyTools,Value) :- mcValueTools(ListTool,MyTools,Value).
//mcValueItems([],Myload,MyLimitLoad,0).
//mcValueItems([item(Item,Qtd)|ListItems],Myload,MyLimitLoad,Value+Qtd) :- default::item(Item,VolItem,_,_) & (Myload+(VolItem*Qtd) <= MyLimitLoad) & mcValueItems(ListItems,Myload+VolItem,MyLimitLoad,Value).
//mcValueItems([item(Item,Qtd)|ListItems],Myload,MyLimitLoad,Value) :- mcValueItems(ListItems,Myload,MyLimitLoad,Value).
//calculateContribution(ListItems,ListTools,ValueTools+ValueItems) :- default::role(_,_,Load,_,Tools) & default::load(MyLoad) & mcValueTools(ListTools,Tools,ValueTools) & mcValueItems(ListItems,MyLoad,Load,ValueItems).
mcValueTools([],MyTools,CurrentLoad,MyLimitLoad,PredictedLoad,0) :- PredictedLoad = CurrentLoad.
mcValueTools([item(Tool,Qtd)|ListTool],MyTools,CurrentLoad,MaximumLoad,PredictedLoad,Value+1) 
:- 
	.member(Tool,MyTools) & 
	default::item(Tool,VolTool,_,_) &  
	CurrentLoad+VolTool <= MaximumLoad &
	mcValueTools(ListTool,MyTools,CurrentLoad+VolTool,MaximumLoad,PredictedLoad,Value).
mcValueTools([item(Tool,Qtd)|ListTool],MyTools,CurrentLoad,MaximumLoad,PredictedLoad,Value) :- mcValueTools(ListTool,MyTools,CurrentLoad,MaximumLoad,PredictedLoad,Value).
mcValueItems([],Myload,MyLimitLoad,0).
mcValueItems([item(Item,Qtd)|ListItems],Myload,MyLimitLoad,Value+1) :- default::item(Item,VolItem,_,_) & (Myload+(VolItem*Qtd) <= MyLimitLoad) & mcValueItems(ListItems,Myload+VolItem,MyLimitLoad,Value).
mcValueItems([item(Item,Qtd)|ListItems],Myload,MyLimitLoad,Value) :- mcValueItems(ListItems,Myload,MyLimitLoad,Value).
calculateContribution(ListItems,ListTools,ValueTools+ValueItems) :- default::role(_,_,Load,_,Tools) & default::load(MyLoad) & mcValueTools(ListTools,Tools,ValueTools) & mcValueItems(ListItems,MyLoad,Load,ValueItems).



//calculateContribution(ListItems,ListTools,ValueTools+ValueItems) :- default::role(_,_,Load,_,Tools) & default::load(MyLoad) & mcValueTools(ListTools,Tools,ValueTools) & mcValueItems(ListItems,MyLoad,Load,ValueItems).


convertListTerm2String([],Temp,Result) :- Result = Temp.
convertListTerm2String([Term | ListTerm],Temp,Result) :- .term2string(Term,String) & convertListTerm2String(ListTerm,[String|Temp],Result).

isMyCoalition(Coalition) :- .my_name(Me) & .member(Me,Coalition).

freeTrucksCoalition([],Temp,Result) :- Result=Temp.
freeTrucksCoalition([Agent|Coalition],Temp,Result) :- new::listFreeTrucks(Trucks) & ((.member(Agent,Trucks) & freeTrucksCoalition(Coalition,[Agent|Temp],Result)) | freeTrucksCoalition(Coalition,Temp,Result)).

{end}



@addAgentCFArtefact[atomic]
+!add_agent_to_CFArtefact(Agent,Type)
	: default::role(_,_,_,_,Tools) & lCoalition::artefactId(CFArtId)
<-
	addAgentToSet(Agent,Type,Tools)[artifact_id(CFArtId)];
	.
@removeAgentCFArtefact[atomic]
+!remove_agent_to_CFArtefact(Agent)
	: lCoalition::artefactId(CFArtId)
<-
	removeAgentFromSet(Agent)[artifact_id(CFArtId)];
	.

+!setup_coalition_artefact(ArtefactName,Algorithm,MCRuleSize)
	: default::joined(coalition,CoalitionWs)
<-
	.print("Creating Coalition Artefact ",MCRuleSize);
	
	makeArtifact(ArtefactName,"env.CFArtefact",[vehicle1,Algorithm,false],CFArtId)[wid(CoalitionWs)];
	
	putType("drone")[artifact_id(CFArtId)];
	putType("motorcycle")[artifact_id(CFArtId)];
	putType("car")[artifact_id(CFArtId)];
	putType("truck")[artifact_id(CFArtId)];
	putType("job")[artifact_id(CFArtId)];
	setupInputs(0,0,0,MCRuleSize)[artifact_id(CFArtId)];
	
	!setup_coalition_artefact(ArtefactName);
	.
+!setup_coalition_artefact(ArtefactName)
	: default::joined(coalition,CoalitionWs)
<-
//	.print("Focusing on Coalition Artefact");
//	.concat("cf_",Name,ArtefactName);
//	coalition::focusWhenAvailable(ArtefactName)[wid(CoalitionWs)];
	focusWhenAvailable(ArtefactName)[wid(CoalitionWs)];
	lookupArtifact(ArtefactName,CFArtId)[wid(CoalitionWs)];
	+lCoalition::artefactId(CFArtId);
	.
	
+!dispose_coalition_artefact
	: lCoalition::artefactId(CFArtId)
<-
	remove[artifact_id(CFArtId)];
	.
	
getToolRoles([],Tool,Temp,Result) :- Result = Temp.
getToolRoles([tools(Role,Tools)|Tail],Tool,Temp,Result) :- .member(Tool,Tools) & get_tools(Tools,Tool,[Role|Temp],Result).
getToolRoles(Tool,Temp,Result) :- .findall(tools(Role,Tools),default::tools(Role,Tools),List) & getToolRoles(List,Tool,[],FResult) & default::role(SRole,_,_,_,STools) & ((.member(Tool,STools) & .union(FResult,[SRole],Result)) | Result = FResult).
	
numberVehicleCarryTools(Type,MaxLoad,ListRawTools,Qtd,MissingTools)
:-	default::get_tools([Type],[],MTools) 
&	.intersection(MTools,ListRawTools,MJobTools) 
&	default::totalLoadItems(MJobTools,0,MTotalLoad) 
&	Qtd = math.ceil(MTotalLoad/MaxLoad)
& 	.difference(ListRawTools,MJobTools,MissingTools)
	.
+!put_constraints(ListJobs)
	: lCoalition::artefactId(CFArtId)
<-
	+lCoalition::temp(0,0,0,0);
	for(.member(job(_,_,ListTools,_,_),ListJobs)){
		?default::rawListItems(ListTools,[],RawListTools);
		
		.print("Raw ",RawListTools);
		?::numberVehicleCarryTools(truck,3000,RawListTools,TQtd,ToolsAfterTruck);
		.print("T ",ToolsAfterTruck);
		?::numberVehicleCarryTools(car,550,ToolsAfterTruck,CQtd,ToolsAfterCar);
		.print("C ",ToolsAfterCar);
		?::numberVehicleCarryTools(motorcycle,300,ToolsAfterCar,MQtd,ToolsAfterMoto);
		.print("M ",ToolsAfterMoto);
		?::numberVehicleCarryTools(drone,100,ToolsAfterMoto,DQtd,ToolsAfterDrone);	
		.print("D ",ToolsAfterDrone);
		
		.print(DQtd," ",MQtd," ",CQtd," ",TQtd);
		?lCoalition::temp(TDrone,TMoto,TCar,TTruck);
		-+lCoalition::temp(math.max(DQtd,TDrone),math.max(MQtd,TMoto),math.max(CQtd,TCar),math.max(TQtd,TTruck));		
	}
	
	?lCoalition::temp(QtdDrone,QtdMoto,QtdCar,QtdTruck);
	.print("We need drones: ",QtdDrone);
	if (QtdDrone \== 0){
		setSizeConstraint(QtdDrone, "drone")[artifact_id(CFArtId)];	
	}
	.print("We need motors: ",QtdMoto);
	if (QtdMoto \== 0){
		setSizeConstraint(QtdMoto, "motorcycle")[artifact_id(CFArtId)];
	}
	.print("We need cars: ",QtdCar);
	if (QtdCar \== 0){
		setSizeConstraint(QtdCar, "car")[artifact_id(CFArtId)];
	}
	.print("We need trucks: ",QtdTruck);
	if (QtdTruck == 0){
		setSizeConstraint(1, "truck")[artifact_id(CFArtId)];
	} else{
		setSizeConstraint(QtdTruck, "truck")[artifact_id(CFArtId)];
	}
	.

//numberVehicleCarryTools(Type,MaxLoad,ListRawTools,Qtd)
//:-	default::get_tools([Type],[],MTools) 
//&	.intersection(MTools,ListRawTools,MJobTools) 
//&	default::totalLoadItems(MJobTools,0,MTotalLoad) 
//&	Qtd = math.ceil(MTotalLoad/MaxLoad)
//	.
//+!put_constraints(ListJobs)
//	: lCoalition::artefactId(CFArtId)
//<-
//	+lCoalition::temp(0,0,0,0);
//	for(.member(job(_,_,ListTools,_,_),ListJobs)){
//		?default::rawListItems(ListTools,[],RawListTools);
//		
//		?::numberVehicleCarryTools(drone,100,RawListTools,DQtd);		
//		?::numberVehicleCarryTools(motorcycle,300,RawListTools,MQtd);
//		?::numberVehicleCarryTools(car,550,RawListTools,CQtd);
//		?::numberVehicleCarryTools(truck,3000,RawListTools,TQtd);
//		
//		.print(DQtd," ",MQtd," ",CQtd," ",TQtd);
//		?lCoalition::temp(TDrone,TMoto,TCar,TTruck);
//		-+lCoalition::temp(math.max(DQtd,TDrone),math.max(MQtd,TMoto),math.max(CQtd,TCar),math.max(TQtd,TTruck));		
//	}
//	
//	?lCoalition::temp(QtdDrone,QtdMoto,QtdCar,QtdTruck);
//	.print("We need drones: ",QtdDrone);
//	if (QtdDrone \== 0){
//		setSizeConstraint(QtdDrone, "drone")[artifact_id(CFArtId)];	
//	}
//	.print("We need motors: ",QtdMoto);
//	if (QtdMoto \== 0){
//		setSizeConstraint(QtdMoto, "motorcycle")[artifact_id(CFArtId)];
//	}
//	.print("We need cars: ",QtdCar);
//	if (QtdCar \== 0){
//		setSizeConstraint(QtdCar, "car")[artifact_id(CFArtId)];
//	}
//	.print("We need trucks: ",QtdTruck);
//	if (QtdTruck == 0){
//		setSizeConstraint(1, "truck")[artifact_id(CFArtId)];
//	} else{
//		setSizeConstraint(QtdTruck, "truck")[artifact_id(CFArtId)];
//	}
//	.

//+!post_contribution(ListItems,ListTools)
//	: gCoalition::calculateContribution(ListItems,ListTools,Contribution) & .my_name(Me) & lCoalition::artefactId(CFArtId)
//<-
//	.print("My contribution: ",Contribution);
//	setMCRule([Me],[],Contribution)[artifact_id(CFArtId)];
//	.
//+!post_contribution(Storage,ListItems,ListTools)
//	: default::role(Role,Speed,MaximumLoad,_,Tools) & default::load(MyLoad) & new::shopList(ShList) & new::storageList(StList) & new::workshopList(WList) & .my_name(Me) & lCoalition::artefactId(CFArtId)
//<-
//	.print("T ",ListTools);
//	?gCoalition::mcValueTools(ListTools,Tools,MyLoad,MaximumLoad,PredictedLoad,ValueTools);
////	ContrTool = math.ceil((ValueTools*10)/.length(ListTools));
//	ContrTool = (ValueTools*10)/.length(ListTools);
//	
//	.print("My value for tools: ",ContrTool," predicted load: ",PredictedLoad);
//	?gCoalition::mcValueItems(ListItems,PredictedLoad,MaximumLoad,ValueItems);
////	ContrItem = math.ceil((ValueItems*10)/.length(ListItems));
//	ContrItem = (ValueItems*5)/.length(ListItems);
//	.print("My value for items: ",ContrItem);
//
//	actions.closest(Role,WList,Storage,ClosestWorkshop);
//	.union(ShList,StList,FList);
////	actions.closest(Role,FList,ClosestWorkshop,ClosestFacility);	
//	actions.farthest(Role,FList,ClosestWorkshop,ClosestFacility);	
//	actions.route(Role, Speed, ClosestFacility, RouteFacility);
//	actions.route(Role,Speed,ClosestFacility,ClosestWorkshop,RouteWorkshop);
//	ContrRoute = (100 - (RouteFacility + RouteWorkshop));
////	ContrRoute = (10 - ((RouteWorkshop*10)/100));
//	.print("My value for route: ",ContrRoute);
//
////	Contribution = (200 - (RouteFacility + RouteWorkshop));
////	Contribution = ContrTool+ContrItem+ContrRoute;
//	Contribution = ContrRoute;
////	Contribution = math.floor((ContrTool) + (0.2*ContrItem) + (1*ContrRoute));
//
//	.print("My contribution: ",Contribution);	
//	setMCRule([Me],[],Contribution)[artifact_id(CFArtId)];
//	.	
+!post_contribution(Who,Contribution)
	: lCoalition::artefactId(CFArtId)
<-
	setMCRule([Who],[],Contribution)[artifact_id(CFArtId)];
	.
+!post_contribution(Storage,ListItems,ListTools,fast)
	: default::role(Role,Speed,MaximumLoad,_,Tools) & default::load(MyLoad) & new::shopList(ShList) & new::storageList(StList) & new::workshopList(WList) & .my_name(Me) & lCoalition::artefactId(CFArtId)
<-
	.print("T ",ListTools);
	?gCoalition::mcValueTools(ListTools,Tools,MyLoad,MaximumLoad,PredictedLoad,ValueTools);
//	ContrTool = math.ceil((ValueTools*10)/.length(ListTools));
	ContrTool = (ValueTools*10)/.length(ListTools);
	
	.print("My value for tools: ",ContrTool," predicted load: ",PredictedLoad);
	?gCoalition::mcValueItems(ListItems,PredictedLoad,MaximumLoad,ValueItems);
//	ContrItem = math.ceil((ValueItems*10)/.length(ListItems));
	ContrItem = (ValueItems*5)/.length(ListItems);
	.print("My value for items: ",ContrItem);

	actions.closest(Role,WList,Storage,ClosestWorkshop);
	.union(ShList,StList,FList);
	actions.closest(Role,ShList,ClosestFacility);	
//	actions.farthest(Role,ShList,ClosestFacility);
//	actions.closest(Role,ShList,ClosestWorkshop,ClosestFacility);	
//	actions.farthest(Role,FList,ClosestWorkshop,ClosestFacility);	
	actions.route(Role, Speed, ClosestFacility, RouteFacility);
	actions.route(Role,Speed,ClosestFacility,ClosestWorkshop,RouteWorkshop);
	.print("Route: ",RouteFacility + RouteWorkshop);
	ContrRoute = (100 - ((RouteFacility + RouteWorkshop)/Speed));
//	ContrRoute = (100 - ((RouteFacility + RouteWorkshop)));
//	ContrRoute = (10 - ((RouteWorkshop*10)/100));
	.print("My value for route: ",ContrRoute);
	
//	ContrSpeed = (Speed*10)/5;

//	Contribution = (200 - (RouteFacility + RouteWorkshop));
//	Contribution = ContrTool+ContrItem+ContrRoute;
	Contribution = ContrRoute;
//	Contribution = math.floor((ContrTool) + (0.2*ContrItem) + (1*ContrRoute));

	.print("My contribution: ",Contribution);	
	setMCRule([Me],[],Contribution)[artifact_id(CFArtId)];
	.	
+!post_contribution(ListJobs)
	: default::role(Role,Speed,_,_,_) & new::shopList(ShList) & new::storageList(StList) & new::workshopList(WList) & .my_name(Me) & lCoalition::artefactId(CFArtId)
<-
	for(.member(job(Id,_,_,Storage,_),ListJobs)){
		actions.closest(Role,WList,Storage,ClosestWorkshop);
		.union(ShList,StList,FList);
		actions.closest(Role,ShList,ClosestFacility);		
		actions.route(Role, Speed, ClosestFacility, RouteFacility);
		actions.route(Role,Speed,ClosestFacility,ClosestWorkshop,RouteWorkshop);
		Contribution = (100 - ((RouteFacility + RouteWorkshop)/Speed));
		.print("My contribution: ",Contribution);	
		setMCRule([Me,Id],[],Contribution)[artifact_id(CFArtId)];
	}
	.

+!define_tasks(ListJobs)
	: lCoalition::artefactId(CFArtId)
<-
	for(.member(job(Id,_,ListTools,_,_),ListJobs)){
		?default::rawListItems(ListTools,[],RawListTools);
		
		addTask(Id,RawListTools)[artifact_id(CFArtId)];
	}
	.

+!define_graph_neighbours_constraint(Agents,ListJobs)
	: .my_name(Me) & .delete(Me,Agents,Temp) & gCoalition::convertListTerm2String(Temp,[],MyNeighbours) & lCoalition::artefactId(CFArtId)
<-
	+lCoalition::temp([]);
	for(.member(job(Id,_,_,_,_),ListJobs)){
		?lCoalition::temp(L);
		.union(L,[Id],TempFor);
		-+lCoalition::temp(TempFor);
	}
	?lCoalition::temp(Jobs);
	.union(Jobs,MyNeighbours,NewMyNeighbours);

	setPositiveConstraint(NewMyNeighbours)[artifact_id(CFArtId)];
	.
+!define_pos_neg_constraints(Agents,ListJobs)
	: .my_name(Me) & .delete(Me,Agents,Temp) & gCoalition::convertListTerm2String(Temp,[],MyNeighbours) & lCoalition::artefactId(CFArtId)
<-
	JobsLength = .length(ListJobs);
	if (JobsLength > 1){
		for ( .range(I,0,JobsLength-2) ) {
			.nth(I,ListJobs,job(IdI,_,_,_,_))
			for ( .range(J,I+1,JobsLength-1) ) {
				.nth(J,ListJobs,job(IdJ,_,_,_,_))
//				.print("Teste Negative ",IdI," ",IdJ);
				setNegativeConstraint([IdI,IdJ])[artifact_id(CFArtId)];
			}
		}
		setPositiveConstraint([Me])[artifact_id(CFArtId)];
	} else {
		setNegativeConstraint(Agents)[artifact_id(CFArtId)];		
		setPositiveConstraint([Me])[artifact_id(CFArtId)];
	}
	.
	
+!run_algorithm
	: lCoalition::artefactId(CFArtId)
<-
	.print("Running coalition formation algorithm");
	runAlgorithm[artifact_id(CFArtId)];
	.

//+::coalitionStructure
//<-
//	.print("There is no feasible coalition structure");
//	.
//+::coalitionStructure(CS)
//<-
//	.print("The coalition structure is ",CS);
//	.

//+::coalition
//	: .my_name(vehicle1) & ::jobInformation(JobId)
//<-
//	.print("There is no good coalition");
//	!initiator::update_coalition_failed;
//	-initiator::jobCoalition(JobId,_,_,_,_);
//	-action::hold_action(Id);
//	!evaluation_auction::has_set_to_free;
//	
//	!finish_module;
//	.
//+::coalition
//<-
//	!finish_module;
//	.
//+::coalition(Value, Coalition)
//	: .my_name(vehicle1) & ::jobInformation(JobId)
//<-	
//	+initiator::coalitionAgents(JobId,Coalition);
//	.print("The best coalition is ",Coalition);
//	?initiator::jobCoalition(JobId, Storage, ListItems, ListToolsNew, Items);
//	!!initiator::separate_tasks(Id, Storage, ListItems, ListToolsNew, Items);
//	-initiator::jobCoalition(JobId, Storage, ListItems, ListToolsNew, Items);
//	
//	
//	!finish_module;
//	.
//+::coalition(Value, Coalition)
//<-
//	!finish_module;
//	.

+!finish_module
<-
	!lCoalition::finish_internal_module;
	.abolish(::_);
	.drop_desire(::_);
	.
//+!finish_module
//	: ::jobId(Id)
//<-
//	.abolish(Id::_);
//	.drop_desire(Id::_);
//	.