package net.rpeti.clusterdemo.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import net.rpeti.clusterdemo.data.DataContainer;

/**
 * Semicolon separated CSV writer module
 */
public class CSVWriter {
	
	private static final String NEWLINE = System.getProperty("line.separator");
	
	/**
	 * Produces similar CSV as Microsoft Excel, from a DataContainer object.
	 * @param data
	 * 		the DataContainer object, holding the new data to be written out
	 * @param destination
	 * 		the destination of the file to be written
	 */
	public static void write(DataContainer data, File destination) throws IOException {
		StringBuilder sb = new StringBuilder();
		int countColumn = data.getAttributes().size();
		int i = 0;
		
		//write attribute names
		for(String attribute : data.getAttributes()){
			if(attribute.contains(";")){
				sb.append("\"" + attribute.replace("\"", "\"\"") + "\"");
			} else {
				sb.append(attribute.replace("\"", "\"\""));
			}
			//if this is not the last element in the row, append semicolon
			if(i != countColumn - 1) sb.append(";");
			i++;
		}
		sb.append(NEWLINE);
		
		//write data
		for(i = 0; i < data.getNumberOfRows(); i++){
			if(data.getDataRow(i) == null) continue;
			int j = 0;
			for(String value : data.getDataRow(i)){
				if(value.contains(";")){
					sb.append("\"" + value.replace("\"", "\"\"") + "\"");
				} else {
					sb.append(value.replace("\"", "\"\""));
				}
				//if this is not the last element in the row, append semicolon
				if (j != countColumn - 1) sb.append(";");
				j++;
			}
			sb.append(NEWLINE);
		}
		
		//write the assembled content to the file
		FileOutputStream fos = new FileOutputStream(destination);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
		osw.write(sb.toString());
		osw.close();
	}

}
