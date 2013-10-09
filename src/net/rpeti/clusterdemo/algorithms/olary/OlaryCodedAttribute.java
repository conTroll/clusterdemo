package net.rpeti.clusterdemo.algorithms.olary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO alaposan átnézni + dok/komment ahol kell

/**
 * Implementation of the Olary Transformation
 * for a single nominal attribute.
 * 
 * @author rpeti
 *
 */
public class OlaryCodedAttribute {
	
	String name;
	OlaryCode code;
	Map<String, boolean[]> codesByValues;
	
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
		
		name = this.name;
		codesByValues = new HashMap<String, boolean[]>();
		
		int i = 0;
		for(String value : values){
			if(!(codesByValues.containsKey(value))){
				codesByValues.put(value, code.getSequence(i));
				i++;
			}
		}
	}

	public String getName() {
		return name;
	}
	
	public boolean[] getOlaryCodeForValue(String value){
		return codesByValues.get(value);
	}
	

}
