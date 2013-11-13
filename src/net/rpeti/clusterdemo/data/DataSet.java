package net.rpeti.clusterdemo.data;

import java.util.ArrayList;
import java.util.List;

public class DataSet implements DataContainer, DataReceiver {
	
	protected List<String> attributes;
	protected List<List<String>> data;

	public DataSet() {
		attributes = new ArrayList<String>();
		data = new ArrayList<List<String>>();
	}

	@Override
	public void addAttribute(String attribute) throws UnsupportedOperationException {
		if(data.size() != 0) throw new UnsupportedOperationException(
				"Cannot add attribute after data has been added.");
		attributes.add(attribute);
	}

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
		if(id >= data.size() || id < 0){
			throw new IllegalArgumentException("Wrong index.");
		}
		return data.get(id) == null ? new ArrayList<String>() : new ArrayList<String>(data.get(id));
	}

	@Override
	public int getNumberOfRows(){
		return data.size();
	}
	
	@Override
	public int getNumberOfColumns(){
		return attributes.size();
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
	
	@Override
	public void removeRow(int rowNumber) {
		if(rowNumber >= data.size() || rowNumber < 0){
			throw new IllegalArgumentException("Wrong index.");
		}
		data.set(rowNumber, null);
	}
	
	protected int getIndexOfAttr(String attribute){
		int index = attributes.indexOf(attribute);
		if (index == -1)
			throw new IllegalArgumentException("The specified attribute doesn't exist.");
		return index;
	}

}
