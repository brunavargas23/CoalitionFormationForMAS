package DC.alg;

public class Rule {
	int positiveRule = 0;
	int negativeRule = 0;
	double value = 0;
	boolean hasNegation = false;
	
	public Rule(int positiverule, int negativerule, double value){
		this.positiveRule = positiverule;
		this.value = value;
		this.negativeRule = negativerule;
	}
}
