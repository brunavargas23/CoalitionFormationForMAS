package DC.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import DC.constraints.BinPrinter;
import DC.constraints.ConstraintSetForDC;
import DC.constraints.Subspace;
import DC.constraints.SubspaceIterator;

/**
 * The main class that includes the implementation of both algorithms in the constraints paper
 */
public class DCGenerator implements Generator
{
	private List<Integer> aStar; //For more details about A*, see Figure 2 of the constraints paper
	
	private int aStarSize; //the number of agents in "aStar"
	
	private List<Subspace> subspaces; //The set of all transformed constraints (i.e., the set of all subspaces)
	
	private int numOfFeasibleCoalitions; //The number of feasible coalitions (i.e., those that satisfy the constraints)
	
	private TreeSet<Integer> listOfFeasibleCoalitions; //The list of feasible coalitions.
	
	private long timeToGenerateFeasibleCoalitions; //The time required to generate the feasible coalitions
	
	private long timeToGenerateSubspaces; //The time to generate the transformed constraints (i.e., the subspaces)
	
	public DecisionNode root;

	//************************************************************************************************
	
    /**
     * Used for returning a human-readable string representing the object
     */
    @Override public String toString(){
        return "DC-generator";
    }
    /**
     * Returns the time required to generate the feasible coalitions
     */
    public long getTimeToGenerateFeasibleCoalitions() {
    	return timeToGenerateFeasibleCoalitions;
    }
    /**
     * Returns the time required to generate the transformed constraints (i.e., the subspaces)
     */
    public long getTimeToGenerateSubspaces() {
    	return timeToGenerateSubspaces;
    }
    /**
     * Needs to be called after "generateFeasibleCoalitions".
     * @return the list of feasible coalitions
     */
    public TreeSet<Integer> getListOfFeasibleCoalitions() {
    	return listOfFeasibleCoalitions;
    }
    /**
     * Needs to be called after "generateFeasibleCoalitions".
     * @return the number of feasible coalitions
     */
    public int getNumOfFeasibleCoalitions() {
    	return numOfFeasibleCoalitions;
    }
    /**
     * Needs to be called after "generateFeasibleCoalitions" or "generateSubspaces"
     * @return aStar (for more details, see A* in the figures of the contstraints paper)
     */
    public List<Integer> getAStar() {
    	return aStar;
    }
    /**
     * Needs to be called after "generateFeasibleCoalitions" or "generateSubspaces"
     * @return the set of tranformed constraints (i.e., the set of subspaces)
     */
    public List<Subspace> getSubspaces() {
    	return subspaces;
    }
    /**
     * returns the number of agents in aStar
     */
    public int getAStarSize() {
    	return aStarSize;
    }
    
    //************************************************************************************************

    /**
     * Generates all feasible coalitions (i.e., those that satisfy the constraints).
     * This version implements Algorithm 1 from the constraints paper.
     * 
     * P.S. "generateSubspaces" only generates the transformed constraints (i.e., the subspaces). On the other
     * hand, "generateFeasibleCoalitions" generates both the transformed constraints and the feasible coalitions. 
     * 
     * @param agentsAsMask : the mask of all available agents, e.g. given 5 agents, agentsAsMask=31 (i.e., 11111)
     * @param positiveConstraintsAsMasks : positive constraints as an array of binary masks
     * @param negativeConstraintsAsMasks : negative constraints as an array of binary masks
     * @param storeTheList : if "false", then we generate but not store the list of feasible coalitions 
     * retrieved by calling "getListOfFeasibleCoalitions". Otherwise, "getListOfFeasibleCoalitions" returns "null" 
     */
    public void generateFeasibleCoalitions(int agentsAsMask, int[] positiveConstraintsAsMasks,
    		int[] negativeConstraintsAsMasks, boolean storeTheList)
    {
    	//Initialization
    	long startTime = System.currentTimeMillis();
        
        //Generate the transformed constraints (i.e., the subspaces)
        generateSubspaces(agentsAsMask, positiveConstraintsAsMasks, negativeConstraintsAsMasks);

        if( storeTheList ){
        	//Using the transformed constraints (i.e., the subspaces), generate the feasible coalitions 
        	listOfFeasibleCoalitions = new TreeSet<Integer>();
        	for (Subspace subspace : subspaces){
        		for( int coalition : new SubspaceIterator(subspace) ){
        			listOfFeasibleCoalitions.add( coalition );
        		}
        	}
            numOfFeasibleCoalitions = listOfFeasibleCoalitions.size();
        }else{
        	numOfFeasibleCoalitions = 0;
        	for (Subspace subspace : subspaces){
        		for( int coalition : new SubspaceIterator(subspace) ){
        			numOfFeasibleCoalitions++;
        		}
        	}
        }        	
        //compute the time required to generate the feasible coalitions
        timeToGenerateFeasibleCoalitions = System.currentTimeMillis() - startTime;
    }

    //************************************************************************************************
    
    /**
     * Generates the subspaces by calling the main recursive function
     */
    public void generateSubspaces(int agentsAsMask, int[] positiveConstraintsAsMasks, int[] negativeConstraintsAsMasks)
    {
    	long startTime = System.currentTimeMillis();
    	aStar = new ArrayList<Integer>();
    	aStarSize=0;
        subspaces = new ArrayList<Subspace>();
        Set<Integer> a = convertAgentsFromMaskToSetOfMasks(agentsAsMask);
        ConstraintSetForDC positiveConstraintsForDC = new ConstraintSetForDC( positiveConstraintsAsMasks );
        ConstraintSetForDC negativeConstraintsForDC = new ConstraintSetForDC( negativeConstraintsAsMasks );
        root = new DecisionNode();
        generateSubspaces(a, agentsAsMask, positiveConstraintsForDC, negativeConstraintsForDC, 0, new HashSet<Integer>(),root);
        timeToGenerateSubspaces = System.currentTimeMillis() - startTime;
        listOfFeasibleCoalitions = null;
    }
    
    //************************************************************************************************
    
    /**
     * Generates the set of all transformed constraints (i.e., the set of all subspaces).
     * This is done by implementing Algorithm 1 from the constraints paper.
     * 
     * P.S. "generateSubspaces" only generates the transformed constraints (i.e., the subspaces). On the other
     * hand, "generateFeasibleCoalitions" generates both the transformed constraints and the feasible coalitions.
     *  
     * @param a : The set of agents, with each agent represented with a mask (e.g. for 6 agents, a={1,2,4,8,16,32})
     * @param p : positive constraints as an array of binary masks
     * @param n : negative constraints as an array of binary masks
     * @param pStar : This is used as in Algorithm 1 of the constraints paper
     * @param nStar : This is used as in Algorithm 1 of the constraints paper 
     */
    private void generateSubspaces(Set<Integer> a, int agentsAsMask, ConstraintSetForDC p, ConstraintSetForDC n, int pStar, Set<Integer> nStar, DecisionNode decisionNode)
    {
        //remove redundant constraints
        p.removeRedundantSupersets();
        n.removeRedundantSupersets();
        p.removePositivesDisallowedByNegatives(n);

        //deal with special cases
        Collection<Integer> toRemoveFromN = n.removeSingletonConstraints();
        a.removeAll(toRemoveFromN);
        nStar.addAll(toRemoveFromN);

        if (p.doesContainEmptySet() && (n.size() == 1)) {
            nStar.add(n.getSingletonValue());
            n = new ConstraintSetForDC();
        }

        if (p.size() == 1 && n.size() == 0) {
            pStar = pStar | p.getSingletonValue();
            p = new ConstraintSetForDC(0);
        }

        if (p.doesContainEmptySet() && (n.size() == 0)) {
            Set<Integer> nStarClone = cloneSet(nStar);
            Subspace subspace = new Subspace( pStar, nStarClone, agentsAsMask );
            if( subspace.isEmpty == false ){
            	subspaces.add( subspace );
            	decisionNode.subspace = subspace;
            }
            return; //we have satisfied all the membership constraints
        }

        if ((p.size() == 0) || n.doesContainEmptySet()) {
            return; //the membership constraints cannot be satisfied
        }

        Integer aiMask = p.selectAnAgent();
        if (aiMask == null) {
            aiMask = n.selectAnAgent();
        }

        if (Integer.bitCount(pStar) == 0) {
            aStar.add(BinPrinter.oneBitFromMaskToNo(aiMask));
            aStarSize++;
        }

        p.split(aiMask);
        n.split(aiMask);
        ConstraintSetForDC pSmallDash = p.constraintsWithoutAgentAfterSplit(aiMask);
        ConstraintSetForDC pBigDash = p.subconstraintsWithAgentAfterSplit(aiMask);
        ConstraintSetForDC nSmallDash = n.constraintsWithoutAgentAfterSplit(aiMask);
        ConstraintSetForDC nBigDash = n.subconstraintsWithAgentAfterSplit(aiMask);

        Set<Integer> nStarClone = cloneSet(nStar);
        nStarClone.add(aiMask);
        a.remove(aiMask);
        Set<Integer> aClone1 = cloneSet(a);
        Set<Integer> aClone2 = cloneSet(a);
        
        decisionNode.branchingAgentAsMask = aiMask;
        decisionNode.branchingAgent_in  = new DecisionNode();
        decisionNode.branchingAgent_out = new DecisionNode();
        
        generateSubspaces(aClone1, agentsAsMask, new ConstraintSetForDC(pSmallDash, pBigDash), new ConstraintSetForDC(nSmallDash, nBigDash), (pStar | aiMask), nStar, decisionNode.branchingAgent_in);
        generateSubspaces(aClone2, agentsAsMask, pSmallDash, nSmallDash, pStar, nStarClone, decisionNode.branchingAgent_out);
        a.add(aiMask);//restore ai so that its ok after this call returns from the recursion
    }
    
    //************************************************************************************************
    
    /**
     * Converts the agents from a mask to a set of masks, where every mask represents an agent.
     * For example, given 6 agents, we have: "agentsAsMask=63" (i.e., agentsAsMask=111111). In
     * this case, this method returns a set containing: 1, 2, 4, 8, 16, 32.
     */
    private static Set<Integer> convertAgentsFromMaskToSetOfMasks(int agentsAsMask)
    {
        Set<Integer> a = new HashSet<Integer>();
        for (int i = 0; i < 32; i++)//assumes only 32 bites are used
        	if ((agentsAsMask & (1 << i)) != 0)
                a.add(1 << i);
        return a;
    }

    //************************************************************************************************

    /**
     * This method returns a copy of the parameter "originalSet"
     */
    private static Set<Integer> cloneSet(Set<Integer> original)
    {
        HashSet<Integer> copy = new HashSet<Integer>();
        copy.addAll(original);
        return copy;
    }
}