package structures;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class cfRule {
//	Sets hashPositiveRule = null;
//	int[] hashNegativeRule = null;
//	double value = 0;
//	boolean hasNegation = false;
//	
//	public cfRule(String[] positiverule, String[] negativerule, double value){
//		hashPositiveRule = new int[positiverule.length];
//		for (int i = 0; i < positiverule.length; i++)
//			hashPositiveRule[i] = positiverule[i].hashCode();
//		
//		hashNegativeRule = new int[negativerule.length];
//		for (int i = 0; i < negativerule.length; i++)
//			hashNegativeRule[i] = negativerule[i].hashCode();
//		
//		this.value 			= value;
//	}
	public Set<String> positiveRule = null;
	public Set<String> negativeRule = null;
	public double value = 0;
	boolean hasNegation = false;
	
	public cfRule(String[] positiverule, String[] negativerule, double value){
		positiveRule = Sets.newHashSet(positiverule);
		negativeRule = Sets.newHashSet(negativerule);		
		this.value 			= value;
	}
}