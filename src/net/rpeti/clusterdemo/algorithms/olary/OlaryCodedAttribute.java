package net.rpeti.clusterdemo.algorithms.olary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Implementation of the Olary Transformation
 * for a single nominal attribute.
 */
public class OlaryCodedAttribute {
	
	private String name;
	private OlaryCode code;
	private Random random;
	private List<Integer> indices;
	private Map<String, boolean[]> codesByValues;
	
	
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
			this.code = new OlaryCode(1);
		else
			this.code = new OlaryCode(numberOfDifferentValues - 1);
		
		this.name = name;
		this.random = new Random(System.currentTimeMillis());
		this.indices = new ArrayList<Integer>(numberOfDifferentValues);
		this.codesByValues = new HashMap<String, boolean[]>();
		
		for (int i = 0; i < numberOfDifferentValues; i++){
			indices.add(i);
		}
		
		for(String value : values){
			if(!(codesByValues.containsKey(value))){
				int pickFromIndices = this.random.nextInt(this.indices.size());
				int index = this.indices.get(pickFromIndices);
				this.indices.remove(pickFromIndices);
				codesByValues.put(value, code.getSequence(index));
			}
		}
	}

	public int getLength(){
		return this.code.getLength();
	}
	
	/**
	 * @return the original name of the attribute.
	 */
	public String getName() {
		return this.name;
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
