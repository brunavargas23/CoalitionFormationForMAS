package DC.constraints;


import java.util.Collection;

/**
 * Used to produce human readable strings with representations of coalitions and for some conversions.
 */
public class BinPrinter {

    /**
     * Produces a human readable String representation of array of coalitions.
     * represented with binary masks.
     * @param binaryMasksArray array of coalitions represented as binary masks that is to be printed
     * @return human readable String of the argument array
     */
    public static String toString(int[] binaryMasksArray) {
        StringBuffer bf = new StringBuffer("[");
        for (int e : binaryMasksArray) {
            bf.append(Integer.toBinaryString(e));
            bf.append(", ");
        }
        //delete the ending comma and space
        if (binaryMasksArray.length > 0) {
            int length = bf.length();
            bf.delete(length - 2, length);
        }
        bf.append("]");
        return bf.toString();
    }

    /**
     * Produces a human readable String represetation of collection of coalitions.
     * represented with binary masks.
     * @param binaryMaskCollection collection of coalitions represented as binary masks that is to be printed
     * @return human readable String of the argument collection
     */
    public static String toStringI(Collection<Integer> binaryMaskCollection) {
        if (binaryMaskCollection == null)
            return "null";
        StringBuffer bf = new StringBuffer("[");
        for (Integer e : binaryMaskCollection) {
            bf.append(Integer.toBinaryString(e));
            bf.append(", ");
        }
        if (binaryMaskCollection.size() > 0) {
            int length = bf.length();
            bf.delete(length - 2, length);
        }
        bf.append("]");
        return bf.toString();
    }

    /**
     * Produces a human readable String representation of constraint set.
     * @param cs constraint set to be printed
     * @return human readable String of the argument constraint set
     */
    public static String toString(ConstraintSetForDC cs) {
        return cs.toBinString();
    }

    /**
     * Produces a human readable String representation of collection of subspaces.
     * @param subspaceCollection collection of subspaces to be printed
     * @return human readable String of the argument collection
     */
    public static String toStringS(Collection<Subspace> subspaceCollection) {
        StringBuffer bf = new StringBuffer("[");
        for (Subspace s : subspaceCollection) {
            bf.append(s.toString());
            bf.append(", ");
        }
        if (subspaceCollection.size() > 0) {
            int length = bf.length();
            bf.delete(length - 2, length);
        }
        bf.append("]");
        return bf.toString();
    }

    /**
     * Computes agent number for binary mask representing only one agent (only one positive bit)
     * @param mask binary mask with only one positive bit
     * @return agent number from 0 to n-1
     */
    public static int oneBitFromMaskToNo(int mask) {
        return Integer.numberOfTrailingZeros(mask);
    }
}