package net.rpeti.clusterdemo.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

//TODO kezelni az idézőjeles védőblokkot

import net.rpeti.clusterdemo.data.DataReceiver;

public class CSVReader {
	private final static String DEFAULT_SEPARATOR_REGEX = ";";
	private final static String UTF8_BOM = "\uFEFF";

	private DataReceiver dataSet;

	public CSVReader(DataReceiver dataSet){
		this.dataSet = dataSet;
	}

	public DataReceiver getDataReceiver(){
		return dataSet;
	}

	/**
	 * @param file a Java File object specifying the location of the file to be read
	 * @return the DataReceiver object passed in the constructor after feeding all the data in it.
	 * @throws FileNotFoundException if the file can't be located
	 * @throws InvalidFileException if the CSV file is invalid
	 */
	public DataReceiver read(File file) throws IOException{
		return this.read(file, true, DEFAULT_SEPARATOR_REGEX);
	}

	/**
	 * @param file a Java File object specifying the location of the file to be read
	 * @param attributesInFirstLine is the attribute names present in the first line of the CSV?
	 * @return the DataReceiver object passed in the constructor after feeding all the data in it.
	 * @throws FileNotFoundException if the file can't be located
	 * @throws InvalidFileException if the CSV file is invalid
	 */
	public DataReceiver read(File file, boolean attributesInFirstLine) throws IOException{
		return this.read(file, attributesInFirstLine, DEFAULT_SEPARATOR_REGEX);
	}

	/**
	 * @param file a Java File object specifying the location of the file to be read
	 * @param attributesInFirstLine is the attribute names present in the first line of the CSV?
	 * @param separator regular expression separating the values in the file from each other
	 * @return the DataReceiver object passed in the constructor after feeding all the data in it.
	 * @throws FileNotFoundException if the file can't be located
	 * @throws InvalidFileException if the CSV file is invalid
	 */
	public DataReceiver read(File file, boolean attributesInFirstLine, String separator) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		String line = "";

		while("".equals(line)){
			line = reader.readLine().trim();
			//remove UTF-8 Byte Order Mark
			if(line.startsWith(UTF8_BOM)) 
				line = line.substring(1);
		}

		//read or generate the attributes
		if(line != null){
			if(attributesInFirstLine){
				line = line.trim();
				String[] attributes = line.split(separator);
				for(String attribute : attributes){
					dataSet.addAttribute(attribute);
				}
			} else {
				//when attributes aren't specified we'll generate them based
				//on the first line of data (how many attributes do we need)
				String[] firstLine = line.split(separator);
				for(int i = 0; i < firstLine.length; i++){
					dataSet.addAttribute("A" + Integer.toString(i));
				}
				//since the first line has been read out we need to store
				//it before proceeding
				dataSet.addData(Arrays.asList(firstLine));
			}
		} else {
			reader.close();
			throw new EmptyFileException();
		}

		//add data to the receiver
		try{
			while((line = reader.readLine()) != null){
				line = line.trim();
				if(!"".equals(line)){
					String[] row = line.split(separator, -1);
					dataSet.addData(Arrays.asList(row));
				}
			}
		} catch (IllegalArgumentException e){
			throw new InvalidFileException(e.getMessage());
		} finally {
			reader.close();
		}

		return dataSet;
	}
}

