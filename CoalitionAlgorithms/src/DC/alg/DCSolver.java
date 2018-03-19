package DC.alg;

import java.util.LinkedList;
import java.util.List;

import DC.constraints.Subspace;
import DC.constraints.SubspaceIterator;
import DC.generators.DCGenerator;
import DC.generators.DecisionNode;
import DC.generators.Generator;
import DC.solver.Solver;
import characteristicFunction.CharacteristicFunction;

/**
 * The main class that includes the implementation of both algorithms in the constraints paper
 */
public class DCSolver implements Solver
{
	private int numOfAgents; //The number of agents in the system
	
	private int agentsAsMask; //The mask of all available agents, e.g. for 5 agents, allAgentsAsMask=31 (i.e., 11111)

	private List<Integer> aStar; //For more details about A*, see Figure 2 of the constraints paper
	
	private int aStarSize; //The number of agents in "aStar"
	
	private int[] aStarAsMasks_plusZero; //as "aStar", but each agent represented using a mask. Also, the array
	//contains an additional zero in the end of it. So, it's size is bigger than the size of "aStar"
	
	private List<Subspace> subspaces; //The set of all transformed constraints (i.e., the set of all subspaces)
	
	private LinkedList<Subspace>[] lists; //the lists that appear in Figure 2 of the constraints paper.
	
	private double[][] maximumValuesInListsBasedOnAgentsUsedSoFar; //maximumValuesInListsBasedOnAgentsUsedSoFar[i][j] is the
	//maximum of all the values of the coalitions in "lists[i]", and that is when there were already "j" agents used so far
	
	private double[][] maximumValuesInListsBasedOnCoalitionSizes; //maximumValuesInListsBasedOnCoalitionSizes[i][j] is the
	//maximum value of all the coalitions in lists[i] that are of size "j".
	
	private CharacteristicFunction characteristicFunction; //The characteristic function to be used
    
	private long startTime; //The time at which we started searching for the optimal coalition structure

	private boolean keepAnytimeStatistics; //if "true" then the CSG solver keeps result
    //showing how performance improves over time, i.e., it shows the anytime performance
    
	private List<Integer> bestCS; //The best coalition structure found
	
	private double bestValue; //The value of the best coalition structure found
	
	private LinkedList<Pair> anytimeResults; //list to keep track of the anytime statistics.
    //In more detail, the list contains pairs; each pair contains: time in miliseconds from the
	//start of the computation, and a new value of the best coalition structure at that time.
	
	private int numOfExaminedCoalitionStructures; //The number of examined (i.e., searched) coalition structures
	
	private long timeToSolveTheCSGProblem; //The time required by the solver to solve the CSG problem
	
	private Generator generator; //The generator that will be used to generate the feasible coalitions
	
	private long numOfExaminedNodes; //The number of coalitions that were searched by the solver. In other
	//words, this is a counter of the number of times that the solver examined a node in the search tree. This
	//is only meaningful when you have a tree-structured searched (e.g., in the DC, IP, or Naive solver).
	
	private int agentAcumulator; //Here, we store the agents that were chosen so
	//far in the coalition structure
	
	private DecisionNode[] decisionNodesAtTopOfLists; //Consists of every node that is at the top of a list
	
	private int numOfVisitedSubspaces; //the number of times we searched a subspace while solving the CSG problem

	private double[][] upperBoundOfRemainingLists; //upperBoundOfRemainingLists[i][j] is the upper bound 
	//of the coalition-combinations in the lists {i,i+1, i+2, ...}, and that is given that "j" agents 
	//have been used so far in the lists {1,,2, ..., i-1}.
	
	private int numOfFeasibleCoalitions;
	
    //************************************************************************************************
    
    /**
     * Used for returning a human-readable string representing the object
     */
    @Override public String toString(){
        return "DC-solver";
    }
	/**
     * @return list of binary masks representing the best coalition structure found
     */
    public List<Integer> getBestCS() {
        return bestCS;
    }
    /**
     * @return value of the best coalition structure found
     */
    public double getBestValue() {
        return bestValue;
    }
    /**
     * returns the number of examined (i.e., searched) coalition structures
     */
    public int getNumOfExaminedCoalitionStructures() {
        return numOfExaminedCoalitionStructures;
    }
    /**
     * Returns the time required by the solver to solve the CSG problem
     */
    public long getTimeToSolveCSGProblem() {
    	return timeToSolveTheCSGProblem;
    }
    /**
     * Returns a list of pairs (time, value) to show the anytime performance.
     * In other words, the list shows how the solution quality improves over time
     */
    public LinkedList<Pair> getAnytimeResults() {
    	return anytimeResults;
    }
    /**
     * Returns the generator that was used to generate the feasible coalitions
     */
    public Generator getGenerator() {
    	return generator;    	
    }   
    /**
     * The number of coalitions that were searched by the solver. In other words,
     * this is a counter of the number of times that the solver examined a node
     * in the search tree. This is only meaningful when you have a tree-structured
     * searched (e.g., in the DC solver, the IP solver, and the Naive solver).
     */
    public long getNumOfExaminedCoalitions() {
    	return numOfExaminedNodes;
    }
    
    //************************************************************************************************

    /**
     * Searches for the optimal solution to the CSG problem with constraints.
     * @param characteristicFunction : the characteristic function to be used
     * @param agentsAsMask : The mask of all available agents, e.g., given 5 agents, allAgentsAsMask=31 (i.e., 11111)
     * @param positiveConstraintsAsMasks : positive constraints as an array of binary masks
     * @param negativeConstraintsAsMasks : negative constraints as an array of binary masks
     * @param keepAnytimeStatistics : If "true", then keep a list of pairs ("time", "value") to show the anytime performance
     */
    public void runCSG(CharacteristicFunction characteristicFunction, int agentsAsMask, int[] positiveConstraintsAsMasks,
    		int[] negativeConstraintsAsMasks, boolean keepAnytimeStatistics)
    {
    	//Initialization
    	this.startTime = System.currentTimeMillis();    	
    	this.anytimeResults = new LinkedList<Pair>();
        this.bestValue = 0;
        this.bestCS = null;
        this.numOfExaminedNodes = 0;
        this.numOfExaminedCoalitionStructures = 0;
        this.characteristicFunction = characteristicFunction; //WARNING: do not remove this line.
        this.agentsAsMask = agentsAsMask; //WARNING: do not remove this line.
        this.keepAnytimeStatistics = keepAnytimeStatistics; //WARNING: do not remove this line.
        this.numOfAgents = Integer.bitCount( agentsAsMask );
        this.numOfVisitedSubspaces=0;

        //Generate the transformed constraints (i.e., the subspaces)
        DCGenerator dcGenerator = new DCGenerator();
        dcGenerator.generateSubspaces( agentsAsMask, positiveConstraintsAsMasks, negativeConstraintsAsMasks );
        this.aStar = dcGenerator.getAStar();
        this.aStarSize = dcGenerator.getAStarSize();
        this.subspaces = dcGenerator.getSubspaces();
        this.generator = dcGenerator;
        
        //fill "decisionNodesAtTopOfList"
        decisionNodesAtTopOfLists = new DecisionNode[ aStarSize+1 ];
        DecisionNode tempNode = dcGenerator.root;
        for(int i=0; i<decisionNodesAtTopOfLists.length; i++){
        	decisionNodesAtTopOfLists[i] = tempNode;
        	tempNode = tempNode.branchingAgent_out;
        }

        //compute aStar as a set of masks, and add a zero at the end
        aStarAsMasks_plusZero = new int[ aStarSize+1];
        int k=0;
        for (int agentInByteFormat_minusOne : aStar) {
            aStarAsMasks_plusZero[k] = (1 << agentInByteFormat_minusOne);
            k++;
        }
        aStarAsMasks_plusZero[k] = 0;
        
        //fill the lists of subspaces, and compute upper bounds (of lists, subspaces, and decision nodes)
        numOfFeasibleCoalitions = computeUpperBoundsAndFillLists( characteristicFunction, dcGenerator );

    	//If there are no feasible coalitions, then there is no point in calling the IP solver
    	if( numOfFeasibleCoalitions == 0 ){
    		timeToSolveTheCSGProblem = 0;
    		return;
    	}
    	//Scan the coalition structures of size 2 to improve the value of the best CS found
    	scanCoalitionStructuresOfSizeTwo( characteristicFunction, agentsAsMask, dcGenerator.root.branchingAgent_out );

    	//Solve the CSG problem using Algorithm2 from the constraints paper
    	for( Subspace subspace : lists[0] ){
    		for( int coalition : new SubspaceIterator(subspace) ){ //For the current coalition in the current subspace
    			List<Integer> csSoFar = new LinkedList<Integer>();
    			csSoFar.add( coalition );
    			agentAcumulator = coalition; //Add the coalition members to the list of agents used so far
    			searchDecisionNode( csSoFar, characteristicFunction.getCoalitionValue( coalition ), 1, decisionNodesAtTopOfLists[1], true);
    		}
    	}
    	//Compute the time required to solve the CSG problem
        timeToSolveTheCSGProblem = System.currentTimeMillis() - startTime;

        //Print the number of subspaces, and the number of times we searched a subspace
        System.out.println("the number of subspaces created by DC is: "+subspaces.size());
        System.out.println("the number of times that DC searched a subspace is: "+numOfVisitedSubspaces);
        
        //Add the very final results to the list in which the anytime results are stored
        if( keepAnytimeStatistics ){
        	Pair pair = new Pair();
        	pair.value = bestValue;
        	pair.time   = timeToSolveTheCSGProblem;
        	anytimeResults.add(pair);
        }
    }
    
    //************************************************************************************************
    
    /**
     * Compute the upper bounds of all subspaces. Then, fill each list with the
     * relevant subspaces, and compute the upper bound of each list accordingly
     * 
     * @return the number of feasible coalition structures
     */
    private int computeUpperBoundsAndFillLists( CharacteristicFunction characteristicFunction, DCGenerator dcGenerator )
    {
    	int numOfFeasibleCoalitions = 0;
    	
    	//compute mask of agents in aStar
        int agentsInAStar = 0;
        for (int agentInByteFormat_minusOne : aStar) {
            agentsInAStar |= (1 << agentInByteFormat_minusOne);
        }
    	//Initialize the list of coalitions
        lists = new LinkedList[ aStarSize+1 ];
        for (int i=0; i < aStarSize+1; i++) {
            lists[i] = new LinkedList<Subspace>();
        }
        //Initialize the list of upper bounds of each list (where the bound is based on the No. of agents used so far)
        maximumValuesInListsBasedOnAgentsUsedSoFar = new double[ aStarSize+1 ][ numOfAgents+1 ];
        for(int i=0; i<maximumValuesInListsBasedOnAgentsUsedSoFar.length; i++)
        	for(int numOfAgentsUsedSoFar=0; numOfAgentsUsedSoFar<=numOfAgents; numOfAgentsUsedSoFar++)
        		maximumValuesInListsBasedOnAgentsUsedSoFar[i][numOfAgentsUsedSoFar] = 0;
        
        //Initialize the list of upper bounds of each list (where the bound is based on the coalition sizes)
        maximumValuesInListsBasedOnCoalitionSizes = new double[ aStarSize+1 ][ numOfAgents+1 ];
        for(int i=0; i<maximumValuesInListsBasedOnCoalitionSizes.length; i++)
        	for(int size=0; size<=numOfAgents; size++)
        		maximumValuesInListsBasedOnCoalitionSizes[i][size] = 0;        
        
        for (Subspace subspace : subspaces)
        {
            //Find the index of the relevant list (i.e., the list to which the subspace belongs)
        	int indexOfRelevantList=0;
        	if ((subspace.pStar & agentsInAStar) == 0){
        		indexOfRelevantList = aStar.size(); //put the subspace in this last list
        	}else{
        		for (int agentInByteFormat_minusOne : aStar) {
        			if (( (1 << agentInByteFormat_minusOne) & subspace.pStar) != 0 ) //if the agent is in pStar
        			{
        				indexOfRelevantList = aStar.indexOf(agentInByteFormat_minusOne);
        				break;
        			}
        		}
            }        	
        	//Put the subspace in the relevant list
            lists[indexOfRelevantList].add(subspace);

        	//Initialize the upper bounds of the subspace
        	for( int numOfAgentsUsedSoFar=0; numOfAgentsUsedSoFar <= numOfAgents; numOfAgentsUsedSoFar++ ){
        		subspace.upperBound[numOfAgentsUsedSoFar] = 0;
        	}
        	//Compute the upper bounds of the subspace
            for( int coalition : new SubspaceIterator(subspace) )
            {					
            	//Update the number of feasible coalitions
            	numOfFeasibleCoalitions++;

            	//Get the size of the coalition
            	int coalitionSize = Integer.bitCount( coalition );

            	//Get the value of the coalition
            	double v = characteristicFunction.getCoalitionValue( coalition );

            	//Update the upperbounds of the coalitions
            	for(int numOfAgentsUsedSoFar=0; numOfAgentsUsedSoFar <= numOfAgents-coalitionSize; numOfAgentsUsedSoFar++)
            		if (subspace.upperBound[ numOfAgentsUsedSoFar ] < v)
            			subspace.upperBound[ numOfAgentsUsedSoFar ] = v;
			}
            //update the upper bounds of the list (where the bounds are computed based on the No. of agents used so far)
            for(int numOfAgentsUsedSoFar=0; numOfAgentsUsedSoFar<=numOfAgents; numOfAgentsUsedSoFar++)
            	if (maximumValuesInListsBasedOnAgentsUsedSoFar[indexOfRelevantList][numOfAgentsUsedSoFar] < subspace.upperBound[numOfAgentsUsedSoFar])
            		maximumValuesInListsBasedOnAgentsUsedSoFar[indexOfRelevantList][numOfAgentsUsedSoFar] = subspace.upperBound[numOfAgentsUsedSoFar];
        }
        
        //upperBoundOfRemainingLists[i][j] is the upper bound of the coalition-combinations in the lists {i,i+1, i+2, ...},
        //and that is given that "j" agents have been used so far in the lists {1,,2, ..., i-1}. 
        computeUpperBoundOfRemainingLists();

        //Compute the upper bounds of decision nodes in one half of the tree
        computeUpperBoundsOfDecisionNodes( dcGenerator.root.branchingAgent_out );

        return( numOfFeasibleCoalitions );
    }

    //************************************************************************************************

    /**
     * Compute upper bounds of all the decision nodes. Note that (depending on the parameter)
     * the computation does not necessarily have to be carried out for all nodes in the tree.
     * 
     * P.S., this method assumes that the upper bounds of the subspaces have been computed.
     * 
     * @param decisionNode : the initial node. The upper bounds will be computed for this node
     * and any other nodes that are hanged by this node.
     */
    private void computeUpperBoundsOfDecisionNodes( DecisionNode decisionNode )
    {
    	decisionNode.upperBound = new double[ Integer.bitCount(agentsAsMask)+1 ];

    	//if we are at a decision node which is a parent
    	if( decisionNode.branchingAgent_in != null )
    	{
    		computeUpperBoundsOfDecisionNodes( decisionNode.branchingAgent_in );
    		computeUpperBoundsOfDecisionNodes( decisionNode.branchingAgent_out);
    		
    		for( int numOfAgentsUsedSoFar=0; numOfAgentsUsedSoFar <= numOfAgents; numOfAgentsUsedSoFar++ )
        	{
        		if( decisionNode.branchingAgent_in.upperBound[numOfAgentsUsedSoFar] > decisionNode.branchingAgent_out.upperBound[numOfAgentsUsedSoFar] )
        			decisionNode.upperBound[numOfAgentsUsedSoFar] = decisionNode.branchingAgent_in.upperBound[numOfAgentsUsedSoFar];
        		else
        			decisionNode.upperBound[numOfAgentsUsedSoFar] = decisionNode.branchingAgent_out.upperBound[numOfAgentsUsedSoFar];
        	}
    	}
    	else //we have reached a leaf, which is either a subspace, or a dead end
    	{
    		if( decisionNode.subspace != null ){
    			for( int numOfAgentsUsedSoFar=0; numOfAgentsUsedSoFar <= numOfAgents; numOfAgentsUsedSoFar++ )
    			{
    				decisionNode.upperBound[numOfAgentsUsedSoFar] = decisionNode.subspace.upperBound[numOfAgentsUsedSoFar];
    			}    		    		
    		}else{
    	    	for( int numOfAgentsUsedSoFar=0; numOfAgentsUsedSoFar <= numOfAgents; numOfAgentsUsedSoFar++ )
    	    	{
    	    		decisionNode.upperBound[numOfAgentsUsedSoFar] = 0;
    	    	}
    		}
    	}
    }
    
    //************************************************************************************************
    
    /**
     * upperBoundOfRemainingLists[i][j] is the upper bound of the coalition-combinations in the lists {i,i+1, i+2, ...},
     * and that is given that "j" agents have been used so far in the lists {1,,2, ..., i-1}. 
     */
    private void computeUpperBoundOfRemainingLists()
    {
    	//Compute the upper bound by simply summing the upper bounds of the lists
        upperBoundOfRemainingLists = new double[ aStarSize+2 ][];
        for(int k=0; k <= aStarSize+1; k++)
        {
        	upperBoundOfRemainingLists[k] = new double[ numOfAgents+1 ];
        	for( int numOfAgentsUsedSoFar=0; numOfAgentsUsedSoFar<=numOfAgents; numOfAgentsUsedSoFar++)
        	{
        		upperBoundOfRemainingLists[k][numOfAgentsUsedSoFar] = 0;        		
        		for( int j=k; j <= aStarSize; j++)
        			upperBoundOfRemainingLists[k][numOfAgentsUsedSoFar] += maximumValuesInListsBasedOnAgentsUsedSoFar[j][numOfAgentsUsedSoFar];
        	}
        }
    	
    	//Compute upper bounds based on integer partitions just like IP
//    	upperBoundOfRemainingLists = new double[ aStarSize+2 ][];
    }
    
    //************************************************************************************************
    
    /**
     * Check if the current coalition structure is better than the best one found so far,
     * and if so then update the best one found so far, and update the anytime results
     */
    private void checkIfBetterThanCurrentMax( List<Integer> cs, double v )
    {
    	if (bestValue < v)
    	{
    		bestValue = v;
    		bestCS = cs;

    		if (keepAnytimeStatistics)
    		{
    			Pair p = new Pair();
    			p.time = System.currentTimeMillis() - startTime;
    			p.value = v;
    			anytimeResults.add(p);
    		}
    	}
    }

    //************************************************************************************************
    
    /**
     * Scan the coalition structures of size 2 to improve the value of the best CS found.
     * @param agentsAsMask : The mask of all available agents, e.g., given 5 agents, allAgentsAsMask=31 (i.e., 11111)
     */
    private void scanCoalitionStructuresOfSizeTwo( CharacteristicFunction characteristicFunction, int agentsAsMask, DecisionNode originalDecisionNode )
    {
    	long start = System.currentTimeMillis();
    	
    	//For every subspace in the first list
    	for( Subspace subspace : lists[0] ){
            for( int coalition : new SubspaceIterator(subspace) ){
            	DecisionNode decisionNode = originalDecisionNode;
            	while( decisionNode.branchingAgent_in != null )
            	{
            		if( (coalition & decisionNode.branchingAgentAsMask) == 0 ){
            			decisionNode = decisionNode.branchingAgent_in;
            		}else{
            			decisionNode = decisionNode.branchingAgent_out;
            		}
            	}
            	if( decisionNode.subspace != null )
            	{
            		int matchingCoalition = agentsAsMask - coalition;
            		Subspace curSubspace = decisionNode.subspace;
            		
            		//Check if the matching coalition belongs to the subspace. Note that this will work correctly even if
            		//the matching coalition was the empty set (i.e., if the original coalition was the grand coalition
            		//Also note that our characteristic function returns a zero if asked for the value of the empty set.
            		if(( (curSubspace.pStar & matchingCoalition)==curSubspace.pStar )
            				&&( (curSubspace.nDoublePrime & matchingCoalition)==0 )
            				&&( (( curSubspace.nPrime==0 )||( curSubspace.nPrime & matchingCoalition)!=curSubspace.nPrime ) ))
            		{
            			double currentValue = characteristicFunction.getCoalitionValue( coalition ) + characteristicFunction.getCoalitionValue( matchingCoalition );
            			
            			//Update the current best coalition structure found
            			List<Integer> currentCS = new LinkedList<Integer>();
            			currentCS.add(coalition);
            			currentCS.add(matchingCoalition);
            			checkIfBetterThanCurrentMax( currentCS, currentValue );
            		}
            	}
            }
    	}
    	System.out.println("The time required for DC to search the coalition structures of size 2 is: "+(System.currentTimeMillis()-start));
    }
    
    //************************************************************************************************

    /**
     * Solve the CSG problem using Algorithm2 from the constraints paper
     */
    private void searchDecisionNode( List<Integer> csSoFar, double vOfCSSoFar, int k, DecisionNode decisionNode, boolean decisionNodeIsAtTopOfList )
    {
    	//if we are at a decision node which is a parent
    	if( decisionNode.branchingAgent_in != null )
    	{
    		int numOfAgentsInAcumulator = Integer.bitCount(agentAcumulator);

    		//-----------------------Deal with the part that contains the branching agent-----------------------//

    		//branch-and-bound based on agent membership
    		if( (agentAcumulator & decisionNode.branchingAgentAsMask) == 0 )
    		{
    			//branch-and-bound based on coalition values
    			if( vOfCSSoFar + decisionNode.upperBound[numOfAgentsInAcumulator] + upperBoundOfRemainingLists[k+1][numOfAgentsInAcumulator] > bestValue ){
    				searchDecisionNode(csSoFar, vOfCSSoFar, k, decisionNode.branchingAgent_in, false);    				
    			}    			
    		}

    		//-------------------Deal with the part that does not contain the branching agent-------------------//

    		if( decisionNodeIsAtTopOfList )
    		{
    			//branch-and-bound based on agent membership
    			if( (agentAcumulator & decisionNode.branchingAgentAsMask) != 0 )
    			{
        			//branch-and-bound based on coalition values
        			if( vOfCSSoFar + upperBoundOfRemainingLists[k+1][numOfAgentsInAcumulator] > bestValue ){
        				searchDecisionNode(csSoFar, vOfCSSoFar, k+1, decisionNode.branchingAgent_out, true);
        			}
    			}
    		}else{
    			//branch-and-bound based on coalition values
    			if( vOfCSSoFar + decisionNode.upperBound[numOfAgentsInAcumulator] + upperBoundOfRemainingLists[k+1][numOfAgentsInAcumulator] > bestValue ){
    				searchDecisionNode(csSoFar, vOfCSSoFar, k, decisionNode.branchingAgent_out, false);
    			}
    		}
    	}
    	else //-------------------Deal with the case when you have reached the end of a path--------------------//
    	{
    		if( decisionNode.subspace != null )
    		{
    			numOfVisitedSubspaces++;
    			Subspace curSubspace = decisionNode.subspace;        	
    			int remainingAgents = agentsAsMask - agentAcumulator;
    			if(( (curSubspace.pStar & agentAcumulator) == 0 )&&( (curSubspace.nDoublePrime & remainingAgents) != remainingAgents ))
    			{
    				//branch-and-bound (check whether it is promising to enter the subspace)
    				if( vOfCSSoFar + curSubspace.upperBound[ Integer.bitCount(agentAcumulator) ] + upperBoundOfRemainingLists[k+1][ Integer.bitCount(agentAcumulator) ] > bestValue )
    				{
    					for( int coalition : new SubspaceIterator( curSubspace, agentAcumulator ) )
    					{
    						numOfExaminedNodes++;
    						double newVOfCS = vOfCSSoFar + characteristicFunction.getCoalitionValue( coalition );
    						List<Integer> newCS = new LinkedList<Integer>( csSoFar );
    						newCS.add( coalition );
    						agentAcumulator += coalition;
    						if (agentAcumulator == agentsAsMask) {
    							numOfExaminedCoalitionStructures++;
    							checkIfBetterThanCurrentMax( newCS, newVOfCS );
    						} else {
    							if( k+1 <= aStarSize )
    								//branch-and-bound (check whether it is promising to move forward to the next list)
    								if( newVOfCS + upperBoundOfRemainingLists[k+1][Integer.bitCount(agentAcumulator)] > bestValue )
    									searchDecisionNode( newCS, newVOfCS, k+1, decisionNodesAtTopOfLists[k+1], true);
    						}
    						agentAcumulator -= coalition;
    					}
    				}
    			}
    		}
    	}
    }
}