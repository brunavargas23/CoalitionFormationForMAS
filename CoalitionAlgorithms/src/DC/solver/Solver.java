package DC.solver;


import java.util.LinkedList;
import java.util.List;

import DC.alg.Pair;
import DC.generators.Generator;
import characteristicFunction.CharacteristicFunction;

/**
 * The interface with functionality to solver the CSG problem with constraints.
 */
public interface Solver
{
    /**
     * @return The name of the solver as a string
     */
    @Override public String toString();
    
    /**
     * @return list of binary masks representing the best coalition structure found
     */
    List<Integer> getBestCS();

    /**
     * @return value of the best coalition structure found
     */
    double getBestValue();

    /**
     * Returns the number of examined (i.e., searched) coalition structures
     */
    int getNumOfExaminedCoalitionStructures();

    /**
     * Returns the time required by the solver to solve the CSG problem
     */
    long getTimeToSolveCSGProblem();

    /**
     * Returns a list of pairs (time, value) to show the anytime performance.
     * In other words, the list shows how the solution quality improves over time
     */
    LinkedList<Pair> getAnytimeResults();

    /**
     * Returns the generator that was used to generate the feasible coalitions
     */
    Generator getGenerator();
 
    /**
     * The number of coalitions that were searched by the solver. In other words,
     * this is a counter of the number of times that the solver examined a node
     * in the search tree. This is only meaningful when you have a tree-structured
     * searched (e.g., in the DC solver, the IP solver, and the Naive solver).
     */
    long getNumOfExaminedCoalitions();

    /**
     * Searches for the optimal solution to the CSG problem with constraints.
     * @param characteristicFunction : the characteristic function to be used
     * @param agentsAsMask : The mask of all available agents, e.g., given 5 agents, allAgentsAsMask=31 (i.e., 11111)
     * @param positiveConstraintsAsMasks : positive constraints
     * @param negativeConstraintsAsMasks : negative constraints
     * @param keepAnytimeStatistics : If "true", then keep a list of pairs ("time", "value") to show the anytime performance
     */
    void runCSG(CharacteristicFunction characteristicFunction, int agentsAsMask, int[] positiveConstraintsAsMasks, int[] negativeConstraintsAsMasks, boolean keepAnytimeStatistics);
}