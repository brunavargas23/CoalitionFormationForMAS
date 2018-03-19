package DC.constraints;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Represents a set of constraints. The individual constraints are represented
 * with binary masks. The set is backed by an array. It offers operations used
 * by algorithm 1.
 */
public class ConstraintSetForDC implements Iterable<Integer>
{
    public int[] arrayOfConstraints;
    protected int[] constraintsWithoutAgent; // Filled by the method "split"
    protected int[] constraintsWithAgent;    // Filled by the method "split"
    int firstFreeIndexOfConstraintsWithoutAgent;
    int firstFreeIndexOfConstraintsWithAgent;

    //************************************************************************************************
    
    /**
     * the 1st constructor. Constructs the set from one or more constraints provided as a 
     * varargs parameter. If an array is provided it is not cloned, but just referenced.
     * @param setOfMasks : the constraint/constraints to be included in the set.
     */
    public ConstraintSetForDC(int... arrayOfConstraints) {
        this.arrayOfConstraints = Arrays.copyOf( arrayOfConstraints, arrayOfConstraints.length );
    }
    /**
     * the 2nd constructor. Creates a union/merge of two sets of constraints, and that is
     * by copying their elements. Does not do any optimizations, even removing duplicates.
     */
    public ConstraintSetForDC(ConstraintSetForDC cs1, ConstraintSetForDC cs2)
    {
        arrayOfConstraints = Arrays.copyOf(cs1.arrayOfConstraints, cs1.size()+cs2.size());
        int cs1Size = cs1.size();
        for (int i = 0; i < cs2.size(); i++) {
            arrayOfConstraints[cs1Size+i] = cs2.arrayOfConstraints[i];
        }
    }   
    
    //************************************************************************************************
    
    /**
     * Iterates over constraints in the set and fills the
     * constraintsWithoutAgent and constraintsWithAgent arrays. Assumes there
     * are no duplicates.
     * @param agentAsMask
     */
    public void split(int agentAsMask) {
        //initialize
        int length = arrayOfConstraints.length;
        constraintsWithAgent = new int[length];
        constraintsWithoutAgent = new int[length];
        firstFreeIndexOfConstraintsWithAgent = 0;
        firstFreeIndexOfConstraintsWithoutAgent = 0;

        //do the split
        for (int i = 0; i < arrayOfConstraints.length; i++) {
            int constraint = arrayOfConstraints[i];
            if ((constraint & agentAsMask) != 0) {
                constraintsWithAgent[firstFreeIndexOfConstraintsWithAgent++] = constraint;
            } else {
                constraintsWithoutAgent[firstFreeIndexOfConstraintsWithoutAgent++] = constraint;
            }
        }

        //deal with the free space at the end of arrays
        if (firstFreeIndexOfConstraintsWithAgent < length) {
            constraintsWithAgent = Arrays.copyOf(constraintsWithAgent, firstFreeIndexOfConstraintsWithAgent);
        }
        if (firstFreeIndexOfConstraintsWithoutAgent < length) {
            constraintsWithoutAgent = Arrays.copyOf(constraintsWithoutAgent, firstFreeIndexOfConstraintsWithoutAgent);
        }
    }
    
    //************************************************************************************************

    /**
     * Creates a new ConstraintSet with constraints of the original set for
     * which this method is called that contain a given agent.
     * @param agentAsMask a binary mask representation of the agent with which
     * to do the filtering.
     * @return the newly created ConstraintSet.
     */
    public ConstraintSetForDC constraintsWithAgent(int agentAsMask) {
        int length = arrayOfConstraints.length;

        int[] cloned = Arrays.copyOf(arrayOfConstraints, length);
        int indLast = length - 1;
        int i = 0;

        while (indLast >= i) {
            if ((cloned[i] & agentAsMask) == 0) {
                cloned[i] = cloned[indLast--];
            } else {
                i++;
            }
        }
        return new ConstraintSetForDC(Arrays.copyOf(cloned, i));
    }
    
    //************************************************************************************************

    /**
     * Removes duplicates from the begining of an array.
     * @param tab the array for duplicates removal
     * @param length the index up to which to do duplicate removal (exclusive)
     * @param originalLength the last index in the array
     * @return array withour duplicates;  if after removal the array has the
     * same number of elements, i.e., originalLength, then returns the original
     * array otherwise returns a shorter subcopy.
     */
    protected int[] removeDuplicatesFromArray(int[] tab, int length, int originalLength) {
        if (length == 0) {
            if (originalLength == 0) {
                return tab;
            } else {
                return new int[0];
            }
        }

        //remove duplicates
        Arrays.sort(tab, 0, length);
        int writeIndex = 0;
        for (int readIndex = 0; readIndex < length; readIndex++) {
            if ((writeIndex == 0) || (tab[writeIndex - 1] != tab[readIndex])) {
                tab[writeIndex++] = tab[readIndex];
            }
        }

        //if necessary skip the ending leftover elements
        if (writeIndex < originalLength) {
            return Arrays.copyOf(tab, writeIndex);
        } else {
            return tab;
        }
    }
    
    //************************************************************************************************

    //ignores the constraints that become empty and resulting duplicates
    /**
     * Returns a new constraints set with constraints obtained by removing a
     * given agent from constraint of this set that included it. This is a quick
     * version that relies on preprocessing done by the split method - needs to
     * be called after split. Deals with resulting duplicates, but does not
     * remove empty constraints.
     * @param agentAsMask agent to be removed represented as binary mask
     * @return the newly constructed constraints set
     */
    protected ConstraintSetForDC removeAgentFromAllConstraintsWithAgentAfterSplit(int agentAsMask) {
        int reverseAgentMask = ~agentAsMask;

        //remove the agent from all constraints and DO NOT ignore the resulting empty constraints
        for (int index = 0; index < firstFreeIndexOfConstraintsWithAgent; index++) {
            //if (constraintsWithAgent[readIndex] != agentAsMask) {
                constraintsWithAgent[index] = constraintsWithAgent[index] & reverseAgentMask;
            //}
        }

        constraintsWithAgent = removeDuplicatesFromArray(constraintsWithAgent, firstFreeIndexOfConstraintsWithAgent, firstFreeIndexOfConstraintsWithAgent);
        return new ConstraintSetForDC(constraintsWithAgent);
    }
    
    //************************************************************************************************

    /**
     * Filters out from the constrain set all constraints that are supersets of
     * some other constraints in the set. This will also remove duplicates.
     */
    public void removeRedundantSupersets() {
        //observe that sets that are smaller in number representation can not be
        //supersets (requires that the first bit is not ussed!)
        sortArray();

        //to LinkedList
        LinkedList<Integer> ll = new LinkedList<Integer>();
        for (Integer x : arrayOfConstraints) {
            ll.add(x);
        }

        //remove redundant supersets
        int index = 0;
        while (index < ll.size()) {
            Iterator<Integer> it = ll.listIterator(index);
            Integer currentSubset = it.next(); //ok since index < size()
            while (it.hasNext()) {
                Integer possibleSuperset = it.next();
                if ((currentSubset & possibleSuperset) == currentSubset) {
                    it.remove();
                }
            }
            index++;
        }

        //back to array
        arrayOfConstraints = new int[ll.size()];
        index = 0;
        for (int x : ll) {
            arrayOfConstraints[index++] = x;
        }
    }
    
    //************************************************************************************************

    /**
     * The constraint set on which this method is called is assumed to contain
     * possitive constraints. The method filters out from it all constraints
     * that are disallowed by the provided as a parameter set of negative
     * constraints.
     * @param negatives c.s. of the negative constraints
     */
    public void removePositivesDisallowedByNegatives(ConstraintSetForDC negatives) {
        int[] negMasks = negatives.arrayOfConstraints;
        int lastPosIndex = arrayOfConstraints.length - 1;

        int posIndex = 0;
        outsideLoop:
        while (posIndex <= lastPosIndex) {
            //check if setOfMasks[posIndex] should be removed
            for (int negIndex = 0; negIndex < negMasks.length; negIndex++) {
                int currentNegEl = negMasks[negIndex];
                if ((currentNegEl & arrayOfConstraints[posIndex]) == currentNegEl) {
                    arrayOfConstraints[posIndex] = arrayOfConstraints[lastPosIndex--];
                    continue outsideLoop;
                }
            }
            posIndex++;
        }

        arrayOfConstraints = Arrays.copyOf(arrayOfConstraints, lastPosIndex + 1);
    }
    
    //************************************************************************************************

    //returns Collection with removed constraints
    /**
     * Constructs a collection of constraints that includes all the original
     * constraints from the c.s. on which this method was called except
     * the singletons.
     * @return the constructed collection
     */
    public Collection<Integer> removeSingletonConstraints() {
        LinkedList<Integer> ll = new LinkedList<Integer>();

        int writeIndex = 0;
        for (int readIndex = 0; readIndex < arrayOfConstraints.length; readIndex++) {
            if (Integer.bitCount(arrayOfConstraints[readIndex]) == 1) {
                ll.add(arrayOfConstraints[readIndex]);
            } else {//retain only nonsingleton constraints
                arrayOfConstraints[writeIndex++] = arrayOfConstraints[readIndex];
            }
        }

        //skip the ending leftover elements
        if (writeIndex <= arrayOfConstraints.length) {
            arrayOfConstraints = Arrays.copyOf(arrayOfConstraints, writeIndex);
        }

        return ll;
    }
    
    //************************************************************************************************

    /**
     * 
     * @return a human readable string representation of the set where the
     * constraints are represented as binary masks
     */
    public String toBinString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i : arrayOfConstraints) {
            sb.append(Integer.toBinaryString(i));
            sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
    
    //************************************************************************************************

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        ConstraintSetForDC other = (ConstraintSetForDC) obj;
        this.sortArray();
        other.sortArray();

        return Arrays.equals(this.arrayOfConstraints, other.arrayOfConstraints);
    }
    
    //************************************************************************************************

    /**
     *@return true if c.s. contains an empty constraint, false otherwise
     */
    public boolean doesContainEmptySet() {
        for (int i = 0; i < arrayOfConstraints.length; i++) {
            if (arrayOfConstraints[i] == 0)
                return true;
        }
        return false;
    }
    
    //************************************************************************************************

    /**
     * @return a binary mask representing one of the agents appearing in any of
     * the constraints in the set or null if such does not exist
     */
    /*public Integer selectAnAgent() {
        for (int c : arrayOfConstraints) {
            if (Integer.bitCount(c) != 0) {
                return Integer.lowestOneBit(c);
            }
        }
        return null;
    }*/
    public Integer selectAnAgent() {
//    	System.out.print("Imprimir constraints ");
//    	for (int c : arrayOfConstraints) {
//            System.out.print(c+", ");
//        }
        for (int c : arrayOfConstraints) {
            if (Integer.bitCount(c) != 0) {
//            	System.out.println("Escolhido: "+Integer.lowestOneBit(c));
                return Integer.lowestOneBit(c);
            }
        }
        return null;
    }
    
    //************************************************************************************************
    
    /**
     * A very quick version of subconstraintsWithAgent that relies on the
     * preprocessing done by the split method - needs to be called after split.
     */
    public ConstraintSetForDC subconstraintsWithAgentAfterSplit(int agentAsMask) {
        removeAgentFromAllConstraintsWithAgentAfterSplit(agentAsMask);
        return new ConstraintSetForDC(constraintsWithAgent);
    }

    /**
     * A very quick version of constraintsWithoutAgent that relies on the
     * preprocessing done by the split method - needs to be called after split.
     */
    public ConstraintSetForDC constraintsWithoutAgentAfterSplit(int agentAsMask) {
        return new ConstraintSetForDC(Arrays.copyOf(constraintsWithoutAgent, firstFreeIndexOfConstraintsWithoutAgent));
    }

    /**
     * Assumes that this set contains only one value and returns it.
     * @return binary mask representing the singleton constraint
     */
    public int getSingletonValue() {
        return arrayOfConstraints[0];
    }
    
    /**
     * Returns the number of constraints
     */
    public int getNumOfConstraints() {
        return arrayOfConstraints.length;
    }

    /**
     * Sorts the backing integer array using standard integer comparison
     */
    protected void sortArray() {
        Arrays.sort(arrayOfConstraints);
    }
    
    /**
     * @return the size of the constraints set
     */
    public int size() {
        return arrayOfConstraints.length;
    }
    
    @Override
    protected ConstraintSetForDC clone() {
        return new ConstraintSetForDC(arrayOfConstraints.clone());
    }
    
    @Override
    public String toString() {
        return Arrays.toString(arrayOfConstraints);
    }
	@Override
	public Iterator<Integer> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

//    public Iterator<Integer> iterator() {
//        return new ArrayIterator(arrayOfConstraints);
//    }
}