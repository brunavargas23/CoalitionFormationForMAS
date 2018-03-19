package CFSS;

import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableValueGraph;

import CFSS.CFSS.eColors;

public class KernighanLin {	
	private MutableValueGraph<Set, eColors> graphOriginal;
	private MutableValueGraph<Set, eColors> graphA;
	private MutableValueGraph<Set, eColors> graphB;
	private Set<EndpointPair<Set>> cutset;
	private Set<Set> unswappedA;
	private Set<Set> unswappedB;
	private double partitionSize; 
	
	private Set<Set> A;
	private Set<Set> B;
	
	public MutableValueGraph<Set, eColors> getGraphA(){
		return graphA; 
	}
	public MutableValueGraph<Set, eColors> getGraphB(){ 
		return graphB; 
	}
	public Set<EndpointPair<Set>> getCutset(){
		return cutset;
	}
	
	  
	public KernighanLin(MutableValueGraph<Set, eColors> graph){
		this.graphOriginal = graph;
		this.partitionSize = graph.nodes().size()/2;
	}
	
	public void cut(){	    
	    A = new HashSet<>();
	    B = new HashSet<>();

	    int index = 0;
	    for (Iterator iterator = graphOriginal.nodes().iterator(); iterator.hasNext();) 
	    	(++index<=partitionSize ? B : A).add((Set) iterator.next());
	   
	    unswappedA = new HashSet(A);
	    unswappedB = new HashSet(B);
	    
//	    System.out.println(A.size()+" "+B.size());
	    
	    doAllSwaps();
	    
	    graphA = Graphs.copyOf(graphOriginal);
	    for (Set set : B)
			graphA.removeNode(set);
	    graphB = Graphs.copyOf(graphOriginal);
	    for (Set set : A)
			graphB.removeNode(set);	   
	    cutset = new HashSet<>();
	    for (EndpointPair<Set> edge : graphOriginal.edges())
			if (!graphA.edges().contains(edge) && !graphB.edges().contains(edge))
				cutset.add(edge);		
	}
	
	private void doAllSwaps() {
		LinkedList<Pair<Set>> swaps = new LinkedList<Pair<Set>>();
		double minCost = Double.POSITIVE_INFINITY;
		int minId = -1;
    
	    for (int i = 0; i < partitionSize; i++) {
	      double cost = doSingleSwap(swaps);
	      if (cost < minCost) {
	        minCost = cost; minId = i; 
	      }
	    }
	    
	    while (swaps.size()-1 > minId) {
	      Pair<Set> pair = swaps.pop();
	      swapVertices(A, pair.second, B, pair.first);
	    }
	}
	  
	private double doSingleSwap(Deque<Pair<Set>> swaps) {    
	    Pair<Set> maxPair = null;
	    double maxGain = Double.NEGATIVE_INFINITY;
	    
	    for (Set v_a : unswappedA) {
	    	for (Set v_b : unswappedB) {    
		        double edge_cost = 0;
		        try {
		        	graphOriginal.edgeValue(v_a, v_a);
		        	edge_cost = 1;
				} catch (Exception e) {	}
		        double gain = getVertexCost(v_a) + getVertexCost(v_b) - 2 * edge_cost;
		        
		        if (gain > maxGain) {
		          maxPair = new Pair<Set>(v_a, v_b);
		          maxGain = gain;
		        }	        
		    }
	    }
	    
	    swapVertices(A, maxPair.first, B, maxPair.second);
	    swaps.push(maxPair);
	    unswappedA.remove(maxPair.first);
	    unswappedB.remove(maxPair.second);
	    
	    return getCutCost();
	}

	  
	private double getVertexCost(Set v) {    
	    double cost = 0;
	
	    boolean v1isInA = A.contains(v);
	    
	    for (Set v2 : graphOriginal.predecessors(v)) {	      
	      boolean v2isInA = A.contains(v2);
	      
	      if (v1isInA != v2isInA) // external
	        cost += 1;
	      else
	        cost -= 1;
	    }
	    return cost;
	}
	
	public double getCutCost() {
	    double cost = 0;
	
	    for (EndpointPair<Set> edge : graphOriginal.edges()) {
	    	Pair<Set> endpoints = new Pair<Set>(edge.nodeU(), edge.nodeV());
	      
	    	boolean firstInA = A.contains(endpoints.first);
	    	boolean secondInA= A.contains(endpoints.second);
	      
	    	if (firstInA != secondInA) // external
	    		cost += 1;
	    }
	    return cost;
	}
	  
	/** Swaps va and vb in groups a and b **/
	private static void swapVertices(Set<Set> a, Set va, Set<Set> b, Set vb) {
		if (!a.contains(va) || a.contains(vb) ||
				!b.contains(vb) || b.contains(va)) 
			throw new RuntimeException("Invalid swap");
		a.remove(va); a.add(vb);
		b.remove(vb); b.add(va);
	}
	  
	public class Pair<T> {
	  final public T first;
	  final public T second;
	
	  public Pair(T first, T second) {
	    this.first = first;
	    this.second = second;
	  }
	}
}
