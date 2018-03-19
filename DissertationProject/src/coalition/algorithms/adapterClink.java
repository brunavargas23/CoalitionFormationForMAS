package algorithms;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import CFSS.CFSS;
import CFSS.CFSS.eColors;
import CLINK.CLINK;
import characteristicFunction.CharacteristicFunction;
import jade.util.leap.HashMap;
import structures.cfAgent;
import structures.cfCoalitionStructure;
import structures.cfConstraintBasic;
import structures.cfConstraintSize;
import structures.cfRule;
import structures.cfTask;

public class adapterClink implements ICoalitionFormationArtifact{
	private final Logger logger = Logger.getLogger(adapterClink.class.getName()); 
	
	private CLINK algorithm;
	private CharacteristicFunction cf;
	
	public adapterClink() {
	}
	
	@Override
	public void keepAnyTimeStatistics(boolean keep) {
		// TODO Auto-generated method stub=
	}
 
	@Override
	public void initialization() {
		this.algorithm 	= new CLINK();	
	}

	@Override
	public cfCoalitionStructure solveCoalitionStructureGeneration(Set<cfAgent> agents,
			Set<cfConstraintBasic> positiveConstraints, Set<cfConstraintBasic> negativeConstraints, Set<cfConstraintSize> sizeConstraints,
			Set<cfTask> tasks, Set<cfRule> rules) {
		Set<cfAgent> SetAgentTask = new HashSet<>(agents);
		
		for (cfTask cft : tasks) {
			cfAgent t = new cfAgent(cft.getName(), "job");
			SetAgentTask.add(t);
		}
		
		this.cf = new maFunction(SetAgentTask.size(),rules,sizeConstraints);
		
		Map<String,cfAgent>  setAgents = new LinkedHashMap<>();
		Set<String> a = new HashSet<>();
		for (cfAgent cfa : SetAgentTask) {
			a.add(cfa.getName());
			setAgents.put(cfa.getName(), cfa);
		}
		
		this.algorithm.run(a, generateGraph(SetAgentTask, positiveConstraints), cf, setAgents);
		
		return this.algorithm.getBestCS();
	}
	
	@Override
	public void getCSNow() {
//		this.algorithm.setStopSearching(true);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	
	private MutableGraph<Set> generateGraph(Set<cfAgent> agents, Set<cfConstraintBasic> positiveConstraints){
		MutableGraph<Set> cs = GraphBuilder.undirected().build();
		
		for (cfAgent agent : agents) {
			cs.addNode(ImmutableSet.of(agent.getName()));
		}
		
		for (cfConstraintBasic constraint : positiveConstraints) {
			Set<String> nodeU = (ImmutableSet.of(constraint.getAgentWhoAdded()));
			for (String string : constraint.getConstraint()) {
				cs.putEdge(nodeU, ImmutableSet.of(string));
			}			
		}
		
//		System.out.println("Grafo "+cs);
		return cs;
	}
	
}
