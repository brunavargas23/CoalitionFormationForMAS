package DC.generators;

import java.util.TreeSet;

/**
 * The interface with functionality to generate our representation with subspaces
 */
public interface Generator
{
    /**
     * @return The name of the generator as a string
     */
    @Override public String toString();

    /**
     * Returns the time required by the solver to generate the feasible coalitions
     */
    long getTimeToGenerateFeasibleCoalitions();

    /**
     * Needs to be called after "generateFeasibleCoalitions".
     * @return the list of feasible coalitions
     */
    TreeSet<Integer> getListOfFeasibleCoalitions();
    
    /**
     * Needs to be called after "generateFeasibleCoalitions".
     * @return the number of feasible coalitions
     */
    int getNumOfFeasibleCoalitions();
    
    /**
     * Generates all coalitions that satisfy the constraints.
     * @param agentsAsMask : the mask of all available agents, e.g. given 5 agents, agentsAsMask=31 (i.e., 11111)
     * @param positiveConstraintsAsMasks : positive constraints as an array of binary masks
     * @param negativeConstraintsAsMasks : negative constraints as an array of binary masks
     * retrieved by calling "getListOfFeasibleCoalitions". Otherwise, "getListOfFeasibleCoalitions" returns "null" 
     */
    void generateFeasibleCoalitions(int agentsAsMask, int[] positiveConstraintsAsMasks, int[] negativeConstraintsAsMasks, boolean storeTheList);
}
