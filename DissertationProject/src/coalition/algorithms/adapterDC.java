package algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Objects;

import DC.alg.DCSolver;
import DC.alg.Pair;
import DC.generators.Generator;
import DC.useful.Combinations;
import DC.useful.General;
import characteristicFunction.CharacteristicFunction;
import structures.cfAgent;
import structures.cfCoalition;
import structures.cfCoalitionStructure;
import structures.cfConstraintBasic;
import structures.cfConstraintSize;
import structures.cfRule;
import structures.cfTask;

public class adapterDC implements ICoalitionFormationArtifact{
	private boolean keepAnyTimeStatistics = false;
	
	private DCSolver algorithm;	
	
	@Override
	public void keepAnyTimeStatistics(boolean keep) {
		this.keepAnyTimeStatistics = keep;		
	}

	@Override
	public void initialization() {
		this.algorithm = new DCSolver();
	}

	@Override
	public cfCoalitionStructure solveCoalitionStructureGeneration(Set<cfAgent> agents,
			Set<cfConstraintBasic> positiveConstraints, Set<cfConstraintBasic> negativeConstraints,
			Set<cfConstraintSize> sizeConstraints, Set<cfTask> tasks, Set<cfRule> rules) {
		
		List<cfAgent> listOfAgents = new ArrayList<cfAgent>(agents);		
		for (cfTask cft : tasks) {
			listOfAgents.add(new cfAgent(cft.getName(), "job"));
		}
		
		CharacteristicFunction cf = new maFunction(listOfAgents.size(),rules,sizeConstraints);
		cf.setAgents(listOfAgents);
		
//		System.out.println("===============================================\nNo. of agents = "+listOfAgents.size());

		int agentsAsMask = (1 << listOfAgents.size()) - 1;
		
		
		int[] positiveConstraintsAsMasks = convertSetConstraintIntoMaskInt(listOfAgents,positiveConstraints, true);
		int[] negativeConstraintsAsMasks = convertSetConstraintIntoMaskInt(listOfAgents,negativeConstraints, false);
  
//		printConstraints(listOfAgents.size(), positiveConstraintsAsMasks, negativeConstraintsAsMasks); 
//		System.err.println(characteristicFunction.getCoalitionValue(0)+" "+positiveConstraintsAsMasks.length+" "+negativeConstraintsAsMasks.length + " "+/*keepAnyTimeStatistics*/true);
		this.algorithm.runCSG(cf, agentsAsMask, positiveConstraintsAsMasks, negativeConstraintsAsMasks, this.keepAnyTimeStatistics);
  
//		printSolverResults(listOfAgents.size(), this.algorithm, /*keepAnyTimeStatistics*/true, agentsAsMask, positiveConstraintsAsMasks, negativeConstraintsAsMasks, cf);
      
		System.gc();
		
		return convertListIntoCoalitionStructure(listOfAgents, cf, this.algorithm.getBestCS());
	}

	@Override
	public void getCSNow() {
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	
	private cfCoalitionStructure convertListIntoCoalitionStructure(List<cfAgent> agents, CharacteristicFunction cf, List<Integer> listCS){
		cfCoalitionStructure cs = new cfCoalitionStructure();
		
		if (listCS != null){			
			for(int i=0; i<listCS.size(); i++){
				cfCoalition c = new cfCoalition();
				int[] curCoalition = Combinations.convertCombinationFromBitToByteFormat(listCS.get(i), agents.size());
				
				for(int j=0; j<curCoalition.length; j++) 
					c.addAgent(agents.get(curCoalition[j]-1));				
				c.setValue(cf.getCoalitionValue(c));
				
				cs.addCoalition(c);	
			}
		}
		
		return cs;
	}
	
	private int[] convertSetConstraintIntoMaskInt(List<cfAgent> agents, Set<cfConstraintBasic> constraint, boolean includeAgentWhoAdded){
		int[] maskInt = new int[constraint.size()];
		System.out.println(maskInt.length);
		int i = 0;
		
		if (includeAgentWhoAdded) {
			for (cfConstraintBasic ctr : constraint) {
				maskInt[i] = convertSetIntoMaskInt(agents, ctr.getTotalConstraint());
				i++;
			}
		}else {
			for (cfConstraintBasic ctr : constraint) {
				maskInt[i] = convertSetIntoMaskInt(agents, ctr.getConstraint());
				i++;
			}
		}						
		
		return maskInt;
	}
	private int convertSetIntoMaskInt(List<cfAgent> agents, String[] ctr){
		int maskInt = 0;
				
		for(String agent : ctr) 
			maskInt = (maskInt | (1 << (getIndexByProperty(agents,agent))));			
		
		return maskInt;
	}
	
	private int getIndexByProperty(List<cfAgent> agents, String agentName) {
        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).getName().equals(agentName)) {
                return i;
            }
        }
        return -1;
    }
	
	private void printConstraints( int numOfAgents, int[] positiveConstraintsAsMasks, int[] negativeConstraintsAsMasks  )
	{
		//Print the positive constraints
		if( positiveConstraintsAsMasks != null ){
			System.out.println("The positive constraints are:");
			for(int i=0; i<positiveConstraintsAsMasks.length; i++)
			{
				int[] curConstraintInByteFormat = Combinations.convertCombinationFromBitToByteFormat( positiveConstraintsAsMasks[i], numOfAgents );
				System.out.println( General.convertArrayToString( curConstraintInByteFormat ) );
			}
			System.out.println("");
		}
		//Print the negative constraints
		if( negativeConstraintsAsMasks != null ){
			System.out.println("The negative constraints are:");
			for(int i=0; i<negativeConstraintsAsMasks.length; i++)
			{
				int[] curConstraintInByteFormat = Combinations.convertCombinationFromBitToByteFormat( negativeConstraintsAsMasks[i], numOfAgents );
				System.out.println( General.convertArrayToString( curConstraintInByteFormat ) );
			}
			System.out.println("");
		}
	}    
    private void printSolverResults( int numOfAgents, DCSolver solver, boolean printAnytimeStatistics,
    		int agentsAsMask, int[] positiveConstraintsAsMasks, int[] negativeConstraintsAsMasks, CharacteristicFunction characteristicFunction )
    {
    	System.out.println("Vai criar caracteristca");
		//Initialization
		Generator generator = solver.getGenerator();
		
		System.out.println("acabou");
		
		//When using the DC solver, the DC generate only generates subspaces, and so it will have an empty list
		//of feasible coalitions (even if there were some feasible coalitions). Note, however, that the DC solver
		//iterates over them when computing the bounds of subspaces. So, to get the time for this iteration, we
		//call the following method (the parameters ensure that we generate, but not store, the list):
	   	if( generator.getNumOfFeasibleCoalitions() == 0 )
//        		generator.generateFeasibleCoalitions(agentsAsMask, positiveConstraintsAsMasks, negativeConstraintsAsMasks, false);
	   		generator.generateFeasibleCoalitions(agentsAsMask, positiveConstraintsAsMasks, negativeConstraintsAsMasks, /*storeListFeasibleCoalitions*/true);

	   	//Print the list of feasible coalitions if required
		System.out.println("   ("+solver.toString()+") The feasible coalitions are as follows:");
		TreeSet<Integer> list = generator.getListOfFeasibleCoalitions();
		for (Integer coalitionInBitFormat : list)
		{
			int[] coalitionInByteFormat = Combinations.convertCombinationFromBitToByteFormat( coalitionInBitFormat, numOfAgents);
			String coalitionAsString = General.convertArrayToString( coalitionInByteFormat );
			System.out.printf("   * ("+solver.toString()+")  "+coalitionAsString + " Funcao Caracteristica: " + characteristicFunction.getCoalitionValue(coalitionInBitFormat) + "\n");
		}
	   	
	   	
    	System.out.println("Printing the CSG results for the solver: "+solver.toString()+"\n");
    		
    	if( generator.getNumOfFeasibleCoalitions() == 0 ){
    		System.out.println("   ("+solver.toString()+") No feasible coalitions were found");
    		System.out.println("   ("+solver.toString()+") The time to determine that there were no feasible coalitions is "+generator.getTimeToGenerateFeasibleCoalitions());
    	}else{
    		System.out.println("   ("+solver.toString()+") The number of feasible coalitions is "+generator.getNumOfFeasibleCoalitions());
    		System.out.println("   ("+solver.toString()+") The time to generate the feasible coalitions is "+generator.getTimeToGenerateFeasibleCoalitions());

    		if( solver.getBestCS() == null ){
        		System.out.println("   ("+solver.toString()+") No feasible coalition structures were found");
        		System.out.println("   ("+solver.toString()+") The time to determine that there were no feasible coalition structures is "+solver.getTimeToSolveCSGProblem());
    		}else{
    			//convert the best CS to a string
    			String bestCS_asString = "[";
    			for(int i=0; i<solver.getBestCS().size(); i++)
    			{
    				int[] curCoalition = Combinations.convertCombinationFromBitToByteFormat( solver.getBestCS().get(i), numOfAgents);
    				bestCS_asString += General.convertArrayToString(curCoalition);
    				if( i == solver.getBestCS().size()-1 )
    					bestCS_asString += "]";
    				else
    					bestCS_asString += ", ";    			
    			}    		
    			//Print the CSG results
    			System.out.println("   ("+solver.toString()+") The time to solve the CSG problem is "+solver.getTimeToSolveCSGProblem());
    			System.out.println("   ("+solver.toString()+") The best CS found is "+bestCS_asString);
    			System.out.println("   ("+solver.toString()+") The value of the best CS found is "+solver.getBestValue());
    			System.out.println("   ("+solver.toString()+") The number of examined CS is "+solver.getNumOfExaminedCoalitionStructures());
   				System.out.println("   ("+solver.toString()+") The number of examined coalitions is "+solver.getNumOfExaminedCoalitions());

    			//If there are anytime results that need to be printed
    			if(( printAnytimeStatistics )&&( solver.getBestCS() != null )&&( solver.getAnytimeResults() != null ))
    			{
    				System.out.println("   ("+solver.toString()+") The anytime results are as follows:");
    				for (Pair pair : solver.getAnytimeResults())
    					System.out.printf("   * ("+solver.toString()+")  time="+pair.time+", value="+pair.value+"\n");
    			}
    		}
    	}
    	System.out.println("");
    }
}
