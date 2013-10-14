package net.rpeti.clusterdemo.data.olary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.rpeti.clusterdemo.algorithms.olary.OlaryCodedAttribute;
import net.rpeti.clusterdemo.data.spi.DataContainer;
import net.rpeti.clusterdemo.data.spi.DataReceiver;


/**
 * A class for storing and accessing data for the Olary algorithm.
 * Usage: first add attributes, then add data rows.
 */
public class OlaryDataSet implements DataReceiver, DataContainer {

	private List<String> attributes;
	private List<OlaryCodedAttribute> transformedAttributes;
	private int differentValues[];
	private List<List<String>> data;
	
	private boolean transformed;
	//the transformation is dirty, when new data rows have been added
	//and no transformation have been made since
	private boolean dirtyTransformation;
	
	public OlaryDataSet(){
		attributes = new ArrayList<String>();
		data = new ArrayList<List<String>>();
		transformedAttributes = new ArrayList<OlaryCodedAttribute>();
		transformed = false;
		dirtyTransformation = false;
	}
	
	@Override
	public int getNumberOfRows(){
		return data.size();
	}
	
	@Override
	public int getNumberOfColumns(){
		return attributes.size();
	}
	
	@Override
	public void addAttribute(String attribute) throws UnsupportedOperationException {
		if(data.size() != 0) throw new UnsupportedOperationException(
				"Cannot add attribute after data has been added.");
		attributes.add(attribute);
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
			boolean isNewValue[] = new boolean[row.size()];
			Arrays.fill(isNewValue, true);
			
			int i = 0;
			for(String value : row){
				for(List<String> item : this.data){
					if (value.equals(item.get(i))){
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
		
		data.add(row);
	}
	
	/**
	 * @return the list of attributes in the data set.
	 */
	@Override
	public List<String> getAttributes(){
		return new ArrayList<String>(attributes);
	}
	
	/**
	 * @return the given data row from the data set
	 * @param id 
	 * 		the row ID
	 */
	@Override
	public List<String> getDataRow(int id){
		return new ArrayList<String>(data.get(id));
	}
	
	/**
	 * @return the given data column from the data set
	 * @param id
	 * 		the column ID
	 */
	@Override
	public List<String> getDataColumn(int id){
		List<String> result = new ArrayList<>();
		for (int i = 0; i < getNumberOfRows(); i++){
			result.add(data.get(i).get(id));
		}
		return result;
	}
	
	private int getIndexOfAttr(String attribute){
		int index = attributes.indexOf(attribute);
		if (index == -1)
			throw new IllegalArgumentException("The specified attribute doesn't exist.");
		return index;
	}
	
	/**
	 * @return a single data value (in table terms the content of a cell)
	 * @param id
	 * 		the row number
	 * @param attribute
	 * 		the attribute name (determines column)
	 */
	@Override
	public String getDataValue(int id, String attribute){
		int index = getIndexOfAttr(attribute);
		return data.get(id).get(index);
	}
		
	public int getNumberOfDifferentDataValues(String attribute){
		int index = getIndexOfAttr(attribute);
		return differentValues[index];
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
}
