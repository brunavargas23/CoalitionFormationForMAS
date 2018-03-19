package characteristicFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DC.useful.Combinations;
import structures.cfAgent;
import structures.cfCoalition;

public abstract class CharacteristicFunction
{    
	private List<cfAgent> setOfAgents;
	private Map<Integer, cfCoalition> maskToCoalition;
	
	public void setAgents(List<cfAgent> setOfAgents) {
		this.setOfAgents 		= setOfAgents;
		this.maskToCoalition 	= new HashMap<Integer, cfCoalition>();
	}
	
    public abstract void putAdditionalInformation(Object...information);
	public abstract void removeAdditionalInformation(Object information);
    
    public abstract void generateValues(int numOfAgents);
    
    public abstract void clear();
    
    
    public double getCoalitionValue(int coalitionInBitFormat) {
    	if (!this.maskToCoalition.containsKey(coalitionInBitFormat)) {
    		cfCoalition c = new cfCoalition();
    		
    		int[] curCoalition = Combinations.convertCombinationFromBitToByteFormat(coalitionInBitFormat, setOfAgents.size());
			
			for(int j=0; j<curCoalition.length; j++)
				c.addAgent(setOfAgents.get(curCoalition[j]-1));
    		
    		this.maskToCoalition.put(coalitionInBitFormat, c);
    	}
    	
    	return getCoalitionValue(this.maskToCoalition.get(coalitionInBitFormat));
    }
    public abstract double getCoalitionValue(cfCoalition coalition);  
    
	public abstract double[] getCoalitionValues();

	public void storeToFile(String fileName){
		getCoalitionValues();
	}
	public void readFromFile(String fileName){
		
	}
}