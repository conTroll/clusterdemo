package net.rpeti.clusterdemo.data;

import java.util.List;

/**
 * Specifies the query methods a data container object should implement.
 */
public interface DataContainer {
	public void editRow(int rowNumber, List<String> newValues);
	
	/**
	 * @return the list of attributes in the data set.
	 */
	public List<String> getAttributes();
	
	/**
	 * @return the given column from the data set
	 * @param colNumber
	 * 		the column number
	 */
	public List<String> getDataColumn(int colNumber);
	
	/**
	 * @return the given data row from the data set
	 * @param rowNumber 
	 * 		the row number
	 */
	public List<String> getDataRow(int rowNumber);
	
	/**
	 * @return a single data value (in table terms the content of a cell).
	 * @param id
	 * 		the number of the row
	 * @param attribute
	 * 		the attribute name (determines column)
	 */
	public String getDataValue(int id, String attribute);
	
	public int getNumberOfColumns();
	
	public int getNumberOfRows();
	
	public void removeRow(int rowNumber);
}
