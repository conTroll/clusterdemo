package net.rpeti.clusterdemo.algorithms.olary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Implementation of the Olary Transformation
 * for a single nominal attribute.
 */
public class OlaryCodedAttribute {
	
	private String name;
	private OlaryCode code;
	public Map<String, boolean[]> codesByValues;
	
	
	/**
	 * Generates the Olary Code with the appropriate length, 
	 * and assigns a binary sequence for every possible value
	 * @param name
	 * 		the name of the attribute
	 * @param numberOfDifferentValues
	 * 		the number of unique values that the attribute than take
	 * @param values
	 * 		the actual values
	 */
	public OlaryCodedAttribute(String name, int numberOfDifferentValues, List<String> values){
		if(values == null || values.isEmpty())
			throw new IllegalArgumentException("Values must be provided.");
		
		if(name == null || name.trim().length() == 0)
			throw new IllegalArgumentException("The name of the attribute must be provided.");
		
		if(numberOfDifferentValues < 1)
			throw new IllegalArgumentException("The number of different values must be greater than zero.");
		else if(numberOfDifferentValues == 1)
			code = new OlaryCode(1);
		else
			code = new OlaryCode(numberOfDifferentValues - 1);
		
		this.name = name;
		codesByValues = new HashMap<String, boolean[]>();
		
		int i = 0;
		for(String value : values){
			if(!(codesByValues.containsKey(value))){
				codesByValues.put(value, code.getSequence(i));
				i++;
			}
		}
	}

	/**
	 * @return the original name of the attribute.
	 */
	public String getName() {
		return this.name;
	}
	
	public int getLength(){
		return this.code.getLength();
	}
	
	/**
	 * 
	 * @param value 
	 * 		a possible value for the attribute
	 * @return the binary sequence assigned to the specified value
	 */
	public boolean[] getOlaryCodeForValue(String value){
		return codesByValues.get(value);
	}
	

}
