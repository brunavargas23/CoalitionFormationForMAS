package algorithms;

import java.util.Set;

import structures.cfAgent;
import structures.cfCoalitionStructure;
import structures.cfConstraintBasic;
import structures.cfConstraintSize;
import structures.cfRule;
import structures.cfTask;

public interface ICoalitionFormationArtifact {
	public void keepAnyTimeStatistics(boolean keep);

	public void initialization();

	public cfCoalitionStructure solveCoalitionStructureGeneration(Set<cfAgent> agents, Set<cfConstraintBasic> positiveConstraints,
			Set<cfConstraintBasic> negativeConstraints, Set<cfConstraintSize> sizeConstraints, Set<cfTask> tasks, Set<cfRule> rules);
	
	public void getCSNow();
	
	public void clear();
}