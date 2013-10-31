package net.rpeti.clusterdemo.gui.visualization;

import net.rpeti.clusterdemo.data.spi.DataContainer;

import org.apache.commons.collections15.Transformer;

//TODO ékezetes betűkkel valamit kezdeni
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
		sb.append("<html>");
		
		for(String attribute : data.getAttributes()){
			sb.append("<b>" + attribute);
			sb.append(":</b> ");
			sb.append(data.getDataValue(input, attribute));
			sb.append("<br>");
		}
		
		sb.append("</html>");
		return sb.toString();
	}

}
