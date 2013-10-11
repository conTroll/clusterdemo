package net.rpeti.clusterdemo.algorithms.olary;

import java.util.List;

import net.rpeti.clusterdemo.data.olary.OlaryDataSet;

public class OlaryAlgo {
	
	//TODO finish + javadoc
	
	private OlaryDataSet dataSet;
	private List<Integer> assignedClusters;
	private int k;
	private int seed;
	private int maxIterations;
	
	public OlaryAlgo(int k, int seed, int maxIterations, OlaryDataSet dataSet){
		this.k = k;
		this.seed = seed;
		this.maxIterations = maxIterations;
		this.dataSet = dataSet;
	}
	
	private int getDistance(boolean[] value1, boolean[] value2){
		if(value1.length != value2.length){
			throw new IllegalArgumentException("The length of the 2 attributes differ. "
					+ "You can only supply values of the same attribute.");
		}
		
		int distance = 0;
		for(int i = 0; i < value1.length; i++){
			if(value1[i] != value2[i])
				distance++;
		}
		
		return distance;
	}
	
	private void initCenters(){
		//TODO implement
	}
	
	public void run(){
		dataSet.doTransformation();
		this.initCenters();
		
	}

}
