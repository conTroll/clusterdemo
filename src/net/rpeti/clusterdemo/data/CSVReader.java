package net.rpeti.clusterdemo.data;

import net.rpeti.clusterdemo.data.spi.DataReceiver;

public class CSVReader {
	private DataReceiver dataSet;
	
	public CSVReader(DataReceiver dataSet){
		this.dataSet = dataSet;
	}
	
	public DataReceiver getDataReceiver(){
		return dataSet;
	}
	
	public DataReceiver read(String filename){
		return this.read(filename, true, ",");
	}
	
	public DataReceiver read(String filename, boolean attributesInFirstLine){
		return this.read(filename, attributesInFirstLine, ",");
	}
	
	public DataReceiver read(String filename, boolean attributesInFirstLine, String separator){
		//TODO implement
		return null;
	}
}
