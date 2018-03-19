package CFSS;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableValueGraph;

import characteristicFunction.CharacteristicFunction;
import characteristicFunction.ICharacteristicFunction;
import structures.cfAgent;
import structures.cfCoalition;
import structures.cfCoalitionStructure;

public class CFSS {	
	public enum eColors{
		green,
		red
	}
	int order = 0;
	
	private CharacteristicFunction characteristicFunction;
	private MutableValueGraph<Set, eColors> best;
	private LinkedList<MutableValueGraph<Set, eColors>> frontier;
	private double bound = 0;
	private boolean stopSearching = false;	
	
	public cfCoalitionStructure getBestCS() {
		return convertSetCS(this.best);
	}
	
	public void setStopSearching(boolean stopSearching) {
		this.stopSearching = stopSearching;
	}
	
	public void run(MutableValueGraph<Set, eColors> graph, CharacteristicFunction cf) {
		this.characteristicFunction = cf;		
		this.best 					= graph; 
		this.frontier 				= new LinkedList<>();
		
		this.frontier.push(graph);
		while (this.frontier.size() > 0){
			synchronized (this) {
				if (stopSearching)
					break;
			}
			
			System.out.println("########");
			MutableValueGraph<Set, eColors> node = this.frontier.pop();	
			System.out.println("Order: "+order++);
//			System.out.println(node);
			
			System.out.println("M: "+M(node));
			System.out.println("V: "+V(best));
			
			if (M(node) > V(best)){
				if (V(node) > V(best))
					this.best = node;
				
				MutableValueGraph<Set, eColors>[] chs = children(node);
				for (MutableValueGraph<Set, eColors> ch : chs){
					this.frontier.push(ch);
				}
			}
			else{
				System.out.println("Pruning ");
//				System.out.println("Pruning "+node);
			}
//			System.out.println(F);
//			System.out.println("value "+V(node)+" "+node);
//			generateBound();
//			System.out.println("Node "+evaluateApproximation(node)+"% from optimal");
			
		}		
	}
	/*public void run(MutableValueGraph<Set, eColors> graph, CharacteristicFunction cf) {
		this.characteristicFunction = cf;		
		this.best 					= graph; 
		this.frontier 				= new LinkedList<>();
		
		System.out.println("Bound: "+M(graph));	
	}*/

//	public MutableValueGraph<Set, eColors>[] children(MutableValueGraph<Set, eColors> graph) {
//		MutableValueGraph<Set, eColors> temp = graph;
//		ArrayList<MutableValueGraph<Set, eColors>> ch = new ArrayList<>();
//				
//		for (EndpointPair<Set> e : graph.edges()) {
////			System.out.println(e);
//			if(graph.edgeValue(e.nodeU(), e.nodeV()) == eColors.green){
////				System.out.println("vai aplicar edge contraction");
//				ch.add(greenEdgeContraction(temp, e));
//				graph.putEdgeValue(e.nodeU(), e.nodeV(), eColors.red);// colouring edge e in red colour
////				System.out.println("pos contraction "+e);
//			}
//		}		
//		
//		return ch.toArray(new MutableValueGraph[ch.size()]);
//	}
	public MutableValueGraph<Set, eColors>[] children(MutableValueGraph<Set, eColors> graph) {
		MutableValueGraph<Set, eColors> temp = graph;
		LinkedList<MutableValueGraph<Set, eColors>> ch 		= new LinkedList<>();
		LinkedList<MutableValueGraph<Set, eColors>> queue 	= new LinkedList<>();
			
		queue.push(graph);
		
		while (queue.size() != 0) {
			KernighanLin n = new KernighanLin(queue.pop());
			n.cut();
			
			for (EndpointPair<Set> e : n.getCutset()) {
				if(graph.edgeValue(e.nodeU(), e.nodeV()) == eColors.green){
					ch.push(greenEdgeContraction(temp, e));
					graph.putEdgeValue(e.nodeU(), e.nodeV(), eColors.red);// colouring edge e in red colour
				}
			}		
		
			if (n.getGraphA().nodes().size() > 1)
				queue.push(n.getGraphA());
			
			if (n.getGraphB().nodes().size() > 1)
				queue.push(n.getGraphB());
		}
		
		return ch.toArray(new MutableValueGraph[ch.size()]);
	}
	
	
	private MutableValueGraph<Set, eColors>  greenEdgeContraction(MutableValueGraph<Set, eColors> gPrime, EndpointPair<Set> e) {
		MutableValueGraph<Set, eColors> newNode = Graphs.copyOf(gPrime);
				
		Set union = Sets.union(e.nodeU(), e.nodeV());
		
		Set<Set> neighbour = Sets.union(gPrime.predecessors(e.nodeU()),gPrime.predecessors(e.nodeV()));
		for (Set set : neighbour) {
			if (gPrime.edgeValueOrDefault(e.nodeU(), set, eColors.green)==eColors.green
					&& gPrime.edgeValueOrDefault(e.nodeV(), set, eColors.green)==eColors.green)
				newNode.putEdgeValue(union, set, eColors.green);
			else
				newNode.putEdgeValue(union, set, eColors.red);
		}
		
		newNode.removeNode(e.nodeU());
		newNode.removeNode(e.nodeV());
		
		return newNode;
	}
	
	private double V(MutableValueGraph<Set, eColors> graph) {
		double value = 0;
		
		for (Set<Set> node : graph.nodes()) 
			value += getCoalitionValue(node);
		
		return value;
	}
	private double M(MutableValueGraph<Set, eColors> graph) {
		return functionMonotonic(Graphs.copyOf(graph))+functionAntiMonotonic(Graphs.copyOf(graph));
	}
	
	private double functionAntiMonotonic(MutableValueGraph<Set, eColors> graph){
		return V(graph);
	}
	private double functionMonotonic(MutableValueGraph<Set, eColors> graph){	
		removeAllRedEdges(graph);
			
		unionAllGreenEdges(graph);
		
		return V(graph);
	}
	
	private void removeAllRedEdges(MutableValueGraph<Set, eColors> graph){
		boolean hasRedEdge = false;
		
		for (EndpointPair<Set> e : graph.edges()) 
			if(graph.edgeValue(e.nodeU(), e.nodeV()) == eColors.red){
				graph.removeEdge(e.nodeU(), e.nodeV());	
				hasRedEdge = true;
				break;
			}
	
		if (hasRedEdge)
			removeAllRedEdges(graph);
	}
	private void unionAllGreenEdges(MutableValueGraph<Set, eColors> graph){
		if (!graph.edges().iterator().hasNext())
			return;
		
		verticesUnion(graph,graph.edges().iterator().next());
		
		unionAllGreenEdges(graph);
	}
	private void verticesUnion(MutableValueGraph<Set, eColors> graph, EndpointPair<Set> edge){
		Set union = Sets.union(edge.nodeU(), edge.nodeV());
		
		edgeUnion(graph,edge.nodeU(),union);
		edgeUnion(graph,edge.nodeV(),union);
		
		graph.removeEdge(edge.nodeU(), edge.nodeV());	
		graph.removeNode(edge.nodeU());
		graph.removeNode(edge.nodeV());
	}
	private void edgeUnion(MutableValueGraph<Set, eColors> graph, Set oldNode, Set unionNode){
		Set<Set> neighbours = graph.predecessors(oldNode);
		for (Set neighbour : neighbours) 
			if (!neighbour.equals(unionNode))
				graph.putEdgeValue(unionNode, neighbour, graph.edgeValue(oldNode, neighbour));		
	}
	
	/*private cfCoalitionStructure convertSetCS(Set node){
		cfCoalitionStructure cs = new cfCoalitionStructure("cs");
//		String agents = node.toString().replace("\\[(.*?)\\]", "$1");
//		String agents = node.toString().replaceAll("\\[|\\]", "");
		
		String strCoalitions = node.toString().replaceAll("\\[|\\]| ", "");
//		String coalitions = node.toString().replace("\\[(.*?)\\]", "$1");
		
		String[] coalitions = strCoalitions.split(",");
		System.out.println("CoalitionS: "+coalitions);
		
		for (String string : coalitions) {
			String strAgents = string.replaceAll("\\[|\\]| ", "");
			String[] agents = strAgents.split(",");
			cfCoalition c = new cfCoalition("c");			
			for (String string2 : agents) 
				c.addAgent(new cfAgent(string2, ""));
			
			cs.addCoalition(c);
		}
		
		
		return cs;
	}*/
	private double getCoalitionValue(Set node){
		double value = 0;
		
		cfCoalition coalition = convertSetCoaliton(node);
//		System.out.println(coalition);
		value = characteristicFunction.getCoalitionValue(coalition);
		
		return value;
	}
	
	private cfCoalitionStructure convertSetCS(MutableValueGraph<Set, eColors> graph){
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
	
	/*Anytime Properties*/
	private void generateBound(){
		double bound = M(this.best);
		double value = 0;
		
		for (MutableValueGraph<Set, eColors> mutableValueGraph : this.frontier) {
			value = M(mutableValueGraph);
			if (value > bound) 
				bound = value;
		}
		
		this.bound = bound;
		System.out.println("Bound: "+this.bound);
	}
	private double evaluateApproximation(MutableValueGraph<Set, eColors> cs){
		double value = V(cs);
		
		return (new BigDecimal(((100*value)/this.bound)).setScale(2, RoundingMode.HALF_EVEN)).doubleValue();
	}
}


