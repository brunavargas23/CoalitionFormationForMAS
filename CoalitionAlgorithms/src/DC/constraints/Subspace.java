package DC.constraints;


import java.util.Set;

import DC.useful.Combinations;
import DC.useful.General;

/**
 * Represents a subspace with a positive constraint and a set of negative
 * constraints. The individual constraints are represented with binary masks.
 */
public class Subspace
{
    public final int nPrime; //all agents in the single, big, negative constraint
	public final int pStar; //all agents in the positive constraint
    public int nDoublePrime; //all agents in the multiple, singleton, negative constraints 
    public int x; //the remaining agents that are not covered by any constraints
    public boolean isEmpty; //"true" if the subspace is empty
    public double[] upperBound; //the maximum value of all the coalitions in the subspace. For
    //example, while constructing a coalition structure, if we already put 5 agents in the coalition
    //structure, and we want to add to it a coalition from the subspace, we use "upperBound[5]"
    
    //************************************************************************************************
    
    /**
     * The constructor. Sets the parameters of the subspace.
     */
    public Subspace(int pStar, Set<Integer> nStar, int agentsAsMask)
    {
    	//Since "nPrime" is "final", we cannot assign it to a value more than once. Therefore, we
    	//use "temp_nPrime" and then, before exiting the constructor, we set "Prime = temp_nPrime"
        int temp_nPrime = 0;
        
    	//Set the parameters of the subspace
        this.pStar = pStar;
        nDoublePrime = 0;
        for(int coalition : nStar)
        {
        	if( Integer.bitCount( coalition ) == 1)
        		nDoublePrime += coalition;
        	else
        		temp_nPrime = coalition;
        }
        x = agentsAsMask & ( ~( pStar + temp_nPrime + nDoublePrime ) );
        upperBound = new double[ Integer.bitCount(agentsAsMask)+1 ];

        //check whether pStar intersects with nDoublePrime.
        if( (pStar & nDoublePrime) != 0 ) {
        	isEmpty = true;
        	nPrime = temp_nPrime;
        	return;
        }
        //check whether nPrime intersects with nDoublePrime.
        if( (temp_nPrime & nDoublePrime) != 0 ) {
        	temp_nPrime = 0;
        	isEmpty = false;
        	nPrime = temp_nPrime;
        	return;
        }
        //check whether pStar intersects with nPrime
        if( (pStar & temp_nPrime ) != 0 ) {
        	temp_nPrime &= ~ (pStar & temp_nPrime ); //nPrime = nPrime\pStar
        	if( temp_nPrime == 0 ) { //then nPrime is a subset of, or equal to, pStar
        		isEmpty = true;
            	nPrime = temp_nPrime;
            	return;
        	}else{
        		if( Integer.bitCount( temp_nPrime ) == 1 ) {
        			nDoublePrime |= temp_nPrime;
        			temp_nPrime = 0;
        			isEmpty = false;
        			nPrime = temp_nPrime;
        			return;
        		}
        	}
        }
        isEmpty = false;
    	nPrime = temp_nPrime;
    }
    
    //************************************************************************************************
    
    /**
     * Used for returning a human-readable string representing the object
     */
    @Override public String toString()
    {
    	int numOfAgents = Integer.bitCount( pStar & nPrime & nDoublePrime & x );
    	
    	String pStar_string = "[]";
    	if( pStar != 0 )
    		pStar_string = General.convertArrayToString(Combinations.convertCombinationFromBitToByteFormat(  pStar, numOfAgents));

    	String nPrime_string = "[]";
    	if( nPrime != 0 )
    		nPrime_string = General.convertArrayToString(Combinations.convertCombinationFromBitToByteFormat( nPrime, numOfAgents)); 
    	
    	String nDoublePrime_string = "[]";
    	if( nDoublePrime != 0 )
    		nDoublePrime_string = General.convertArrayToString(Combinations.convertCombinationFromBitToByteFormat( nDoublePrime, numOfAgents));
    	
    	return("pStar = "+pStar_string+",  nPrime = "+nPrime_string+",  nDoublePrime = "+nDoublePrime_string);
    }
}