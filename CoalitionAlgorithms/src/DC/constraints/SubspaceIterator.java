package DC.constraints;

import java.util.Iterator;

/**
 * Iterates over all coalitions in a subspace. Assumes that the subspace is not empty.
 * Therefore, before calling the iterator, make sure that: subspace.isEmpty == false.
 */
public class SubspaceIterator implements Iterator<Integer>, Iterable<Integer>
{
	private boolean hasNext; //"true" if there are still coalition to iterate through.
	
	private CoalitionIterator iterator1; //used to iterate over subsets of a set of agents
	
	private Iterator<Integer> iterator2; //used to iterate over subsets of a set of agents.
	//For every subset in "iterator1", we cycle through all the subsets in "iterator2" 
	
	private int subsetOfIterator1; //the variable that will iterate over "iterator1"
	
	private int subsetOfIterator2; //the variable that will iterate over "iterator2"
	
	private final Subspace subspace; //the subspace through which we are iterating
	
	private final int additionalAgentsToBeAvoided; //additional agents that must be avoided. In other words,
	//any coalition that contains any of these agents must not be generated.
	
	//************************************************************************************************

	/**
	 * the constructors
	 */
	public SubspaceIterator( Subspace subspace ) {
		this.subspace = subspace;
		this.additionalAgentsToBeAvoided = 0; //There are no additional agents to be avoided
		initializeRemainingParameters();
	}
	public SubspaceIterator( Subspace subspace, int additionalAgentsToBeAvoided ) {
		this.subspace = subspace;
		this.additionalAgentsToBeAvoided = additionalAgentsToBeAvoided;
		initializeRemainingParameters();
	}
	
	//************************************************************************************************

	/**
	 * Used by either of the constructors to initialise the remaining parameters, i.e.,
	 * the parameters that were not initialised in the constructors
	 */
	private void initializeRemainingParameters()
	{
		hasNext = true;

		if( (subspace.nPrime & additionalAgentsToBeAvoided) == 0 )
		{
			//Set "iterator1" to be an iterator over the subsets of nPrime
			iterator1 = new CoalitionIterator( subspace.x & (~additionalAgentsToBeAvoided) );

			//Set "iterator2" to be an iterator over the subsets of nPrime
			if( subspace.nPrime != 0 ){
				iterator2 = new CoalitionIterator( subspace.nPrime, true );
			}else{
				iterator2 = new ZeroIterator();
			}
			//take the first subset from each iterator
			subsetOfIterator1 = iterator1.next();
			subsetOfIterator2 = iterator2.next();
		}
		else
		{
			iterator1 =	new CoalitionIterator( (subspace.x + subspace.nPrime) & (~additionalAgentsToBeAvoided) );

			//take the first subset from the iterator
			subsetOfIterator1 = iterator1.next();
			subsetOfIterator2 = 0;
		}		
		//Check the special case where the first coalition in the subspace is empty
		if( subspace.pStar == 0 ) takeOneStep();
	}
	
	//************************************************************************************************
	
	/**
	 * This method updates all the relevant parameters so as to take one step in the subspace
	 */
	private void takeOneStep()
	{
		if( (subspace.nPrime & additionalAgentsToBeAvoided) == 0 )
		{
			if( iterator2.hasNext() == false )
			{
				if( iterator1.hasNext() == false ){
					hasNext = false;
				}else{
					subsetOfIterator1 = iterator1.next();

					if( subspace.nPrime != 0 ){
						iterator2 = new CoalitionIterator( subspace.nPrime, true );
					}else{
						iterator2 = new ZeroIterator();
					}
					subsetOfIterator2 = iterator2.next();
				}
			}else{
				subsetOfIterator2 = iterator2.next();
			}
		}
		else
		{
			if( iterator1.hasNext() == false ){
				hasNext = false;			
			}else{
				subsetOfIterator1 = iterator1.next();				
			}
		}
	}
	
	//************************************************************************************************
	
	/**
	 * The main method of an iterator.
	 */
	public Integer next()
	{
		int coalition;		
		coalition = subspace.pStar + subsetOfIterator1 + subsetOfIterator2;
		takeOneStep();
		return(coalition);
	}
	
	public boolean hasNext() {
		return hasNext;
	}

	public void remove() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public Iterator<Integer> iterator() {
		return this;
	}
	
	//************************************************************************************************

	/**
	 * An iterator that iterates over the set {0}. In other words, it returns a zero
	 */
	protected class ZeroIterator  implements Iterator<Integer>, Iterable<Integer>
	{
		boolean hasNext;

		public ZeroIterator(){
			hasNext = true;
		}
		public boolean hasNext() {
			return hasNext;
		}
		public Integer next() {
			hasNext = false;
			return 0;
		}
		public void remove() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
		public Iterator<Integer> iterator() {
			return this;
		}
	}
}