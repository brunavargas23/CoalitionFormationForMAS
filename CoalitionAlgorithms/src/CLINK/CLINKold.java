package CLINK;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;

import CFSS.CFSS.eColors;
import characteristicFunction.CharacteristicFunction;
import structures.cfAgent;
import structures.cfCoalition;
import structures.cfCoalitionStructure;

public class CLINKold {
	/*private CharacteristicFunction characteristicFunction;
	private MutableGraph<Set> best;
	
	public cfCoalitionStructure getBestCS() {
		return convertSetCS(this.best);
	}

	public void run(Set<cfAgent> agents, MutableGraph<Set> interationGraph, CharacteristicFunction cf) {
		this.characteristicFunction = cf;	
		PartitionLinkage pl = new PartitionLinkage(agents, interationGraph);		
		
		pl.initalisePLTable(agents, interationGraph);
		
		MutableGraph<Set> newGraph = Graphs.copyOf(interationGraph);
		
		while((pl.max() >= 0) && (newGraph.nodes().size() > 1)) {	
			EndpointPair<Set> e = pl.argmax();
			
			newGraph = edgeContraction(interationGraph, e);
			
			pl.updatePL();
		}
	}
	
	private MutableGraph<Set>  edgeContraction(MutableGraph<Set> interationGraph, EndpointPair<Set> e) {
		MutableGraph<Set> newGraph = Graphs.copyOf(interationGraph);
				
		Set union = Sets.union(e.nodeU(), e.nodeV());
		
		Set<Set> neighbour = Sets.union(newGraph.predecessors(e.nodeU()),newGraph.predecessors(e.nodeV()));
		for (Set set : neighbour) {
			newGraph.putEdge(union, set);
		}
		
		newGraph.removeNode(e.nodeU());
		newGraph.removeNode(e.nodeV());
		
		return newGraph;
	}

	

	private Double lf(cfCoalition nodeU, cfCoalition nodeV) {
		return gain(nodeU, nodeV);
	}
	private Double lf(Set nodeU, Set nodeV) {
		return gain(nodeU, nodeV);
	}
	
	private double gain(Set c1, Set c2) {
		double gain = 0;
		Set union = Sets.union(c1, c2);
		
		gain = getCoalitionValue(union) - getCoalitionValue(c1) - getCoalitionValue(c2);
		
		return gain;
	}
	private double getCoalitionValue(Set node){
		double value = 0;
		
		cfCoalition coalition = convertSetCoaliton(node);
//		System.out.println(coalition);
		value = characteristicFunction.getCoalitionValue(coalition);
		
		return value;
	}
	
	private cfCoalitionStructure convertSetCS(MutableGraph<Set> graph){
		cfCoalitionStructure cs = new cfCoalitionStructure("cs");
		
		for (Set node : graph.nodes())
			cs.addCoalition(convertSetCoaliton(node));
		
		return cs;
	}
	private cfCoalition convertSetCoaliton(Set node){
		cfCoalition c = new cfCoalition("c");
		
		String strCoalition = node.toString().replaceAll("\\[|\\]| ", "");
//		String strCoalition = node.toString().replace("\\[(.*?)\\]", "$1");
		
		String[] agents = strCoalition.split(",");
		
		for (String string : agents) 
			c.addAgent(new cfAgent(string, "none"));
		
		return c;
	}
	
	private class PartitionLinkage{
		private Table<cfCoalition, cfCoalition, Double> partitionLinkage;
		
		public PartitionLinkage(Set<cfAgent> agents, MutableGraph<cfCoalition> interationGraph){
			initalisePLTable(agents, interationGraph);
		}
		
		public EndpointPair<Set> argmax() {
			return null;
		}
		
		public double max() {
			return 0;
		}
		
		private void initalisePLTable(Set<cfAgent> agents, MutableGraph<cfCoalition> interationGraph) {		
			for (EndpointPair<cfCoalition> e : interationGraph.edges()) {
				this.partitionLinkage.put(e.nodeU(), e.nodeV(), lf(e.nodeU(),e.nodeV()));
			}
			
			updateMax();
		}
		
		public void updatePL() {
			
			updateMax();
		}
		
		private void updateMax() {
			
		}
		
		
	}
	
	private class Pair{
		private cfCoalition c1;
		private cfCoalition c2;
		
		public Pair(cfCoalition c1, cfCoalition c2) {
			this.c1 = c1;
			this.c2 = c2;
		}
		
		public cfCoalition getC1() {
			return c1;
		}
		public cfCoalition getC2() {
			return c2;
		}
	}*/
}
