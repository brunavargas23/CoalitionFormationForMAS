import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import CFSS.CFSS;
import CFSS.CFSS.eColors;
import CLINK.CLINK;
import characteristicFunction.CharacteristicFunction;
import characteristicFunction.ICharacteristicFunction;
import characteristicFunction.vTeste;
import characteristicFunction.vTesteClink;
import structures.cfAgent;
import structures.cfCoalitionStructure;
import structures.cfConstraintBasic;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World");
		
		
		/*MutableValueGraph<Set, CFSS.eColors> cs = ValueGraphBuilder.undirected().build();
		Set<String> a = (ImmutableSet.of("A"));
		Set<String> b = (ImmutableSet.of("B"));
		Set<String> c = (ImmutableSet.of("C"));
		Set<String> d = (ImmutableSet.of("D"));
//		Set<String> e = (ImmutableSet.of("E"));
		cs.putEdgeValue(a, b, CFSS.eColors.green);
		cs.putEdgeValue(b, c, CFSS.eColors.green);
		cs.putEdgeValue(c, d, CFSS.eColors.green);
		cs.putEdgeValue(d, a, CFSS.eColors.green);
//		cs.putEdgeValue(e, d, CFSS.eColors.green);
//		System.out.println(cs);		

		CFSS algorithm = new CFSS();
		CharacteristicFunction cf = new vTeste();
		cf.generateValues(4);
//		cfCoalitionStructure optimalCS = algorithm.run(cs,cf);
//		System.out.println(optimalCS);
		algorithm.run(cs, cf);*/
		
		
		
		MutableGraph<Set> cs = GraphBuilder.undirected().build();
		Set<String> a = (ImmutableSet.of("a1"));
		Set<String> b = (ImmutableSet.of("a2"));
		Set<String> c = (ImmutableSet.of("a3"));
		Set<String> d = (ImmutableSet.of("a4"));
//		Set<String> e = (ImmutableSet.of("E"));
		cs.putEdge(a, b);
		cs.putEdge(a, c);
		cs.putEdge(a, d);
		cs.putEdge(b, c);
		cs.putEdge(b, d);
		cs.putEdge(c, d);
//		cs.putEdgeValue(e, d, CFSS.eColors.green);
//		System.out.println(cs);
		CLINK algorithm = new CLINK();
		CharacteristicFunction cf = new vTesteClink();
		cf.generateValues(4);
//		cfCoalitionStructure optimalCS = algorithm.run(cs,cf);
//		System.out.println(optimalCS);
//		algorithm.run(ImmutableSet.of("a1","a2","a3","a4"),cs, cf);
		
		
		/*MutableValueGraph<Set, CFSS.eColors> cs = ValueGraphBuilder.undirected().build();
		Set<String> sefOfAgents = ImmutableSet.of(			"vehicle1", "vehicle2", "vehicle3", "vehicle4", 
															"vehicle5", "vehicle6", "vehicle7", "vehicle8",
															"vehicle9", "vehicle10", "vehicle11", "vehicle12",
															"vehicle13", "vehicle14", "vehicle15", "vehicle16",
															"vehicle17", "vehicle18", "vehicle19", "vehicle20",
															"vehicle21", "vehicle22", "vehicle23", "vehicle24",
															"vehicle25", "vehicle26", "vehicle27", "vehicle28");

		generateGraph(sefOfAgents, Set<cfConstraintBasic> positiveConstraints)

		CFSS algorithm = new CFSS();
		CharacteristicFunction cf = new vTeste();
		cf.generateValues(4);
//		cfCoalitionStructure optimalCS = algorithm.run(cs,cf);
//		System.out.println(optimalCS);
		algorithm.run(cs, cf);*/
	}
	
	
	private MutableValueGraph<Set, eColors> generateGraph(Set<String> agents, Set<cfConstraintBasic> positiveConstraints){
		MutableValueGraph<Set, CFSS.eColors> cs = ValueGraphBuilder.undirected().build();
		
		for (String agent : agents) {
			cs.addNode(ImmutableSet.of(agent));
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
