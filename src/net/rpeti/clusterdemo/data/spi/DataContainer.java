package net.rpeti.clusterdemo.data.spi;

import java.util.List;

/**
 * Specifies the query methods a data container object should implement.
 */
public interface DataContainer {
	/**
	 * @return the list of attributes in the data set.
	 */
	public List<String> getAttributes();
	
	public int getNumberOfRows();
	
	public int getNumberOfColumns();
	
	/**
	 * @return the given data row from the data set
	 * @param rowNumber 
	 * 		the row number
	 */
	public List<String> getDataRow(int rowNumber);
	
	/**
	 * @return a single data value (in table terms the content of a cell).
	 * @param id
	 * 		the row number
	 * @param attribute
	 * 		the attribute name (determines column)
	 */
	public String getDataValue(int rowNumber, String attribute);
}
