package algorithms;

import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import CFSS.CFSS;
import CFSS.CFSS.eColors;
import characteristicFunction.CharacteristicFunction;
import structures.cfAgent;
import structures.cfCoalitionStructure;
import structures.cfConstraintBasic;
import structures.cfConstraintSize;
import structures.cfRule;
import structures.cfTask;

public class adapterCFSS implements ICoalitionFormationArtifact{
	private final Logger logger = Logger.getLogger(adapterCFSS.class.getName()); 
	
	private CFSS algorithm;
	private CharacteristicFunction cf;
	
	public adapterCFSS() {
	}
	
	@Override
	public void keepAnyTimeStatistics(boolean keep) {
		// TODO Auto-generated method stub=
	}

	@Override
	public void initialization() {
		this.algorithm 	= new CFSS();	
	}

	@Override
	public cfCoalitionStructure solveCoalitionStructureGeneration(Set<cfAgent> agents,
			Set<cfConstraintBasic> positiveConstraints, Set<cfConstraintBasic> negativeConstraints, Set<cfConstraintSize> sizeConstraints,
			Set<cfTask> tasks, Set<cfRule> rules) {
		this.cf = new maFunction(agents.size(),rules,sizeConstraints); 
		
		this.algorithm.run(generateGraph(agents, positiveConstraints), cf);
		
		return this.algorithm.getBestCS();
	}
	
	@Override
	public void getCSNow() {
		this.algorithm.setStopSearching(true);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	
	private MutableValueGraph<Set, eColors> generateGraph(Set<cfAgent> agents, Set<cfConstraintBasic> positiveConstraints){
		MutableValueGraph<Set, CFSS.eColors> cs = ValueGraphBuilder.undirected().build();
		
		for (cfAgent agent : agents) {
			cs.addNode(ImmutableSet.of(agent.getName()));
		}
		
		for (cfConstraintBasic constraint : positiveConstraints) {
			Set<String> nodeU = (ImmutableSet.of(constraint.getAgentWhoAdded()));
			for (String string : constraint.getConstraint()) {
				cs.putEdgeValue(nodeU, ImmutableSet.of(string), CFSS.eColors.green);
			}			
		}
		
		return cs;
	}
	
}
