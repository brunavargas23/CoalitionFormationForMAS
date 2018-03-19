package DC.constraints;

import java.util.Iterator;

/**
 * Allows to iterate over all coalitions of agents from a given set. Returned
 * coalitions are represented as binary masks. Includes empty coalition.
 */
public class CoalitionIterator implements Iterator<Integer>, Iterable<Integer>
{
	int currentValue;
	int nextValue;
	final int maxValue;
	final int agentsMasks;
	final int quant;

	//************************************************************************************************
	
	/**
	 * The 1st constructor
	 * @param agentsMask : binary mask of the set of agents of which we need to iterate over the subsets
	 */
	public CoalitionIterator(int agentsMask)
	{
		maxValue = agentsMask;

		if (agentsMask == 0) {
			quant = 1;
			this.agentsMasks = 1;
		} else {
			int highestOneBitPlusOne = Integer.highestOneBit(agentsMask) << 1;
			quant = highestOneBitPlusOne - agentsMask;
			this.agentsMasks = agentsMask | highestOneBitPlusOne;
		}
	}
	
	//************************************************************************************************

	/**
	 * the 2nd constructor
	 * @param agentsMask : binary mask of the set of agents of which we need to iterate over the subsets
	 * @param shouldExludeGrand : determines if the biggest subset should be included in the iteration.
	 */
	public CoalitionIterator( int agentsMask, boolean shouldExludeGrand )
	{
		if( shouldExludeGrand )
			maxValue = agentsMask - 1;
		else
			maxValue = agentsMask;

		if (agentsMask == 0) {
			quant = 1;
			this.agentsMasks = 1;
		} else {
			int highestOneBitPlusOne = Integer.highestOneBit(agentsMask) << 1;
			quant = highestOneBitPlusOne - agentsMask;
			this.agentsMasks = agentsMask | highestOneBitPlusOne;
		}
	}
	
	//************************************************************************************************
	
	public boolean hasNext() {
		return nextValue <= maxValue;
	}

	public Integer next() {
		currentValue = nextValue;
		nextValue = (nextValue + quant) & agentsMasks;
		return currentValue;
	}

	public void remove() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Iterator<Integer> iterator() {
		return this;
	}
}