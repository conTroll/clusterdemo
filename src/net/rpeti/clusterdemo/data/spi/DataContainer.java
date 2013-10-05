package net.rpeti.clusterdemo.data.spi;

import java.util.List;

//TODO documentation
public interface DataContainer {
	public List<String> getAttributes();
	public int getNumberOfRows();
	public int getNumberOfColumns();
	public List<String> getDataRow(int rowNumber);
	public String getDataValue(int rowNumber, String attribute);
}
