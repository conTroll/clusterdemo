package net.rpeti.clusterdemo.data.spi;

import java.util.List;

/**
 * Specifies the methods that an object should implement for storing data.
 */
public interface DataReceiver {
	
	/**
	 * Adds an attribute to the data set.
	 * @throws UnsupportedOperationException when at least one row has been added.
	 */
	public void addAttribute(String attribute) throws UnsupportedOperationException;
	
	/**
	 * Add a row to the data set. 
	 * All attributes should be represented as not null java.lang.Strings.
	 * For attributes you don't want to supply, you can provide an empty string.
	 */
	public void addData(List<String> row) throws IllegalArgumentException;
}
