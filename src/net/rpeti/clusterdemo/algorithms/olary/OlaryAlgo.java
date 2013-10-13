package net.rpeti.clusterdemo.algorithms.olary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.rpeti.clusterdemo.data.olary.OlaryDataSet;

public class OlaryAlgo {
	
	//TODO finish + javadoc
	
	private OlaryDataSet dataSet;
	private int k;
	private int seed;
	private int maxIterations;
	private int rowCount;
	private List<Integer> centers1;
	private List<Integer> centers2;
	private int[] index;
	
	/**
	 * Initializes Olary Algorithm with a random seed.
	 * @param k
	 * 		the desired number of clusters
	 * @param maxIterations
	 * 		the maximum number of iterations the algorithm can make before terminating
	 * @param dataSet
	 * 		the data set (an object of type OlaryDataSet)
	 */
	public OlaryAlgo(int k, int maxIterations, OlaryDataSet dataSet){
		this(k, -1, maxIterations, dataSet);
	}
	
	/**
	 * Initializes Olary Algorithm with a predefined seed.
	 * @param k
	 * 		the desired number of clusters
	 * @param seed
	 * 		the id of data which will be the center of the first cluster in the first iteration
	 * @param maxIterations
	 * 		the maximum number of iterations the algorithm can make before terminating
	 * @param dataSet
	 * 		the data set (an object of type OlaryDataSet)
	 */
	public OlaryAlgo(int k, int seed, int maxIterations, OlaryDataSet dataSet){
		this.rowCount = dataSet.getNumberOfRows();
		
		if(seed == -1){
			Random random = new Random();
			random.setSeed(System.currentTimeMillis());
			this.seed = random.nextInt(this.rowCount);
		}
		
		if(seed > this.rowCount){
			throw new IllegalArgumentException("Seed must be less than the number of rows in the dataset!");
		}
		if(maxIterations < 1){
			throw new IllegalArgumentException("Maximum iterations must be a positive integer!");
		}
		if(k > this.rowCount || k < 1){
			throw new IllegalArgumentException("0 < k < # of rows in data set");
		}
		
		this.k = k;
		this.seed = seed;
		this.maxIterations = maxIterations;
		this.dataSet = dataSet;
		this.centers1 = new ArrayList<Integer>(k);
		this.index = new int[rowCount];
		Arrays.fill(index, -1);
	}
	
	/**
	 * Must initialize centers1 before calling this.
	 * Will assign the data points to clusters.
	 * Ensures, that the centers will be assigned to their own clusters.
	 * Other data points will be assigned to the cluster, whose center is
	 * the closest to it. The result will be stored in "index".
	 */
	private void assignToClusters(){
		if(rowCount == 1) this.index[0] = 0;
		for (int i = 0; i < rowCount; i++){
			if(centers1.contains(i)){
				index[i] = i;
			}
			else{
				int minDistance = Integer.MAX_VALUE;
				int j = 0;
				for(int centerId : centers1){
					int currentDistance = getDistance(i, centerId);
					if(currentDistance < minDistance){
						minDistance = currentDistance;
						index[i] = j;
					}
					j++;
				}
			}
		}
	}

	/**
	 * @return the distance between 2 binary sequences.
	 */
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
	
	/**
	 * @return
	 * 		the distance between 2 data points (the sum of distances
	 * 		between their appropriate attributes)
	 */
	private int getDistance(int id1, int id2){
		if (id1 >= rowCount || id2 >= rowCount){
			throw new IllegalArgumentException("IDs are indexed between 0 and " + (rowCount - 1));
		}
		
		int distance = 0;
		for(String attribute : dataSet.getAttributes()){
			distance += this.getDistance(
					dataSet.getEncodedAttributeValue(id1, attribute), 
					dataSet.getEncodedAttributeValue(id2, attribute));
		}
		return distance;
	}
	
	/**
	 * Implementation of initCenters() method, as specified in the documentation.
	 * Initializes the centers before the first iteration.
	 */
	private void initCenters(){
		int optimalCenter;
		//the first center will be supplied, or randomly selected
		centers1.add(seed);
		//set up the remaining k-1 centers
		for(int i = 1; i < k; i++){
			int maxDistance = 0;
			optimalCenter = 0;
			//we examine all data points, and select one which will be
			//the best center for the new cluster
			for (int j = 0; j < rowCount; j++){
				if(!centers1.contains(j)){
					int currentDistance = 0;
					//sum the distances between the actual point and the
					//previously selected centers
					for(int s = 0; s < i; s++){
						currentDistance += this.getDistance(j, centers1.get(s));
					}
					//the one with the maximal sum wins
					if(currentDistance > maxDistance){
						maxDistance = currentDistance;
						optimalCenter = j;
					}
				}
			}
			centers1.add(optimalCenter);
		}
	}
	
	private void computeCenters(){
		//TODO implement
	}
	
	/**
	 * Run the Olary algorithm with the supplied parameters on the supplied data set.
	 */
	public void run(){
		dataSet.doTransformation();
		this.initCenters();
		int iterations = 0;
		while(iterations < maxIterations && !(centers1.equals(centers2))){
			this.assignToClusters();
			centers2 = new ArrayList<Integer>(centers1);
			this.computeCenters();
		}
	}

}
