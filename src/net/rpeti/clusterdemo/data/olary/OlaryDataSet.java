package net.rpeti.clusterdemo.data.olary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.rpeti.clusterdemo.algorithms.olary.OlaryCodedAttribute;
import net.rpeti.clusterdemo.data.DataSet;


/**
 * A class for storing and accessing data for the Olary algorithm.
 * Usage: first add attributes, then add data rows.
 */
public class OlaryDataSet extends DataSet {

	private List<OlaryCodedAttribute> transformedAttributes;
	private int differentValues[];
	private boolean transformed;
	//the transformation is dirty, when new data rows have been added
	//and no transformation have been made since
	private boolean dirtyTransformation;
	
	public OlaryDataSet(){
		transformedAttributes = new ArrayList<OlaryCodedAttribute>();
		transformed = false;
		dirtyTransformation = false;
	}
	
	/**
	 * Add a row to the data set. All attributes should be represented as not null java.lang.Strings.
	 * For attributes you don't want to supply, you can provide an empty string.
	 * @param row
	 */
	@Override
	public void addData(List<String> row) throws IllegalArgumentException {
		if(row.size() != attributes.size())
			throw new IllegalArgumentException("The data should have " + attributes.size() + 
					" number of attributes. For attributes you don't want to supply,"
					+ " you can provide empty string.");
		
		for(String item : row)
			if (item == null)
				throw new IllegalArgumentException("You shouldn't provide null as data. "
						+ "Use empty string instead.");
		
		dirtyTransformation = true;
		
		//if this is the first row of data, all possible values for attributes should be set to 1
		if(data.size() == 0){
			differentValues = new int[attributes.size()];
			Arrays.fill(differentValues, 1);
		} 
		//else check if the row contains a different data in the attributes from the
		//previously added rows
		else {
			updateDifferentValuesOnInsertion(row);
		}
		
		super.addData(row);
	}
	
	/**
	 * Executes Olary Transformation on the data.
	 */
	public void doTransformation(){
		transformed = true;
		dirtyTransformation = false;
		
		for(int i = 0; i < attributes.size(); i++){
			transformedAttributes.add(new OlaryCodedAttribute(attributes.get(i), differentValues[i], getDataColumn(i)));
		}
	}
	
	@Override
	public void editRow(int rowNumber, List<String> newValues){
		List<String> oldRow = this.getDataRow(rowNumber);
		super.editRow(rowNumber, newValues);
		
		dirtyTransformation = true;
		
		updateDifferentValuesOnRemoval(oldRow);
		updateDifferentValuesOnInsertion(newValues);
	}
	
	/**
	 * @param attributeID
	 * 		the ID of the attribute
	 * @return the length of the Olary Code needed to encode the attribute.
	 */
	public int getCodeLength(int attributeID){
		return this.transformedAttributes.get(attributeID).getLength();
		
	}
	
	/**
	 * 
	 * @param attribute
	 * 		the name of the attribute
	 * @return the length of the Olary Code needed to encode the attribute.
	 */
	public int getCodeLength(String attribute){
		return this.getCodeLength(this.getIndexOfAttr(attribute));
	}
	
	/** 
	 * @param id
	 * 		row number
	 * @param attribute
	 * 		column number
	 * @return The data encoded with Olary Code.
	 */
	public boolean[] getEncodedAttributeValue(int id, String attribute){
		if(!(transformed) || dirtyTransformation)
			throw new UnsupportedOperationException(
					"Transformation doesn't exist, or is dirty. Please transform the data.");
		int index = getIndexOfAttr(attribute);
		String value = data.get(id).get(index);
		return transformedAttributes.get(index).getOlaryCodeForValue(value);
	}
	
	/** 
	 * @param value
	 * 		the original value
	 * @param attribute
	 * 		column number
	 * @return The data encoded with Olary Code.
	 */
	public boolean[] getEncodedAttributeValue(String value, String attribute){
		if(!(transformed) || dirtyTransformation)
			throw new UnsupportedOperationException(
					"Transformation doesn't exist, or is dirty. Please transform the data.");
		int index = getIndexOfAttr(attribute);
		return transformedAttributes.get(index).getOlaryCodeForValue(value);
	}
	
	/**
	 * @param id
	 * 		row number
	 * @return All the encoded values for that data row.
	 */
	public List<boolean[]> getEncodedRow(int id){
		List<boolean[]> result = new ArrayList<boolean[]>(getAttributes().size());
		for(String attribute : getAttributes()){
			result.add(getEncodedAttributeValue(id, attribute));
		}
		return result;
	}
	
	public int getNumberOfDifferentDataValues(String attribute){
		int index = getIndexOfAttr(attribute);
		return differentValues[index];
	}
	
	@Override
	public void removeRow(int rowNumber){
		List<String> row = this.getDataRow(rowNumber);
		super.removeRow(rowNumber);
		
		dirtyTransformation = true;
		
		updateDifferentValuesOnRemoval(row);
	}
	
	private void updateDifferentValuesOnInsertion(List<String> newRow){
		boolean isNewValue[] = new boolean[newRow.size()];
		Arrays.fill(isNewValue, true);
		
		int i = 0;
		for(String value : newRow){
			for(List<String> item : this.data){
				if (item != null && value.equals(item.get(i))){
					isNewValue[i] = false;
					break;
				}
			}
			i++;
		}
		
		for (i = 0; i < isNewValue.length; i++){
			if(isNewValue[i]) (differentValues[i])++;
		}
	}
	
	private void updateDifferentValuesOnRemoval(List<String> removedRow){
		int i = 0;
		for(String value : removedRow){
			for(List<String> item : this.data){
				if (item == null) continue;
				if (value.equals(item.get(i))){
					differentValues[i]--;
					break;
				}
			}
			i++;
		}
	}
}
