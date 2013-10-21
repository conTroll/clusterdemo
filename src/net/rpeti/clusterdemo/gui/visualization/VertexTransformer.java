package net.rpeti.clusterdemo.gui.visualization;

import net.rpeti.clusterdemo.data.spi.DataContainer;

import org.apache.commons.collections15.Transformer;

public class VertexTransformer implements Transformer<Integer, String> {

	private DataContainer data;
	
	public VertexTransformer(DataContainer data){
		this.data = data;
	}
	
	@Override
	public String transform(Integer input) {
		if(input >= data.getNumberOfRows() || input < 0)
			throw new IllegalArgumentException("Invalid ID.");
		
		StringBuilder sb = new StringBuilder();
		
		for(String attribute : data.getAttributes()){
			sb.append(attribute);
			sb.append(": ");
			sb.append(data.getDataValue(input, attribute));
			sb.append(System.getProperty("line.separator"));
		}
		
		return sb.toString();
	}

}
