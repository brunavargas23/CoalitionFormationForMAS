package DC.generators;

import DC.constraints.Subspace;

public class DecisionNode {

	public int branchingAgentAsMask;
	
	public DecisionNode branchingAgent_in;
	
	public DecisionNode branchingAgent_out;
	
	public Subspace subspace;	
	
	public double[] upperBound; //the maximum value of all the coalitions that are hanged from this decision
	//node. For example, while constructing a coalition structure, if we already put a total of 5 agents in 
	//it, and we want to add to it a coalition that is hanged from this node, then we use "upperBound[5]".

	public DecisionNode()
	{
		subspace = null;
		branchingAgent_in  = null;
		branchingAgent_out = null;
	}
}