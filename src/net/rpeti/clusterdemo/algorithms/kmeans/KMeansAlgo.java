package net.rpeti.clusterdemo.algorithms.kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.rpeti.clusterdemo.algorithms.ClusteringAlgorithm;
import net.rpeti.clusterdemo.algorithms.IllegalClusterNumberException;
import net.rpeti.clusterdemo.data.olary.OlaryDataSet;

public class KMeansAlgo extends ClusteringAlgorithm {
	
	private OlaryDataSet dataSet;
	private int rowCount;
	private int colCount;
	private int k;
	private int maxIterations;
	
	//Representation: an encoded attribute value is a binary sequence, represented as a boolean array.
	//A data row is a list of attribute values, so multiple rows (eg. a data set) is a list of lists of boolean arrays.
	//According to the specification of the k-means algorithm, when a new row comes in, we have to adjust the means.
	//So we do a mapping where we map every binary value to an integer. If a new row comes in, we check every bit of the
	//new data. If a bit is 0 (represented as false), we decrement the mapped integer value, if the bit is 1 (represented
	//as true), we increment it. This means that an attribute of a center will be represented as an integer array now, so
	//a center will be represented as a list of integer arrays, so all the centers will be represented by a list of lists of
	//integer arrays, where if an integer value is > 0, then the corresponding average bit is 1, and 0 otherwise. 
	private List<List<int[]>> centers;
	private Random random;
	private int[] result;
	private int[] prevResult;
	
	public KMeansAlgo(int k, int maxIterations, OlaryDataSet dataSet){
		this.dataSet = dataSet;
		this.rowCount = dataSet.getNumberOfRows();
		this.colCount = dataSet.getNumberOfColumns();
		
		if(maxIterations < 1){
			throw new IllegalArgumentException("Maximum iterations must be a positive integer!");
		}
		
		if(k > this.rowCount || k < 1){
			throw new IllegalClusterNumberException();
		}
		
		this.k = k;
		this.maxIterations = maxIterations;
		this.result = new int[rowCount];
		Arrays.fill(result, -1);
		this.prevResult = new int[rowCount];
		this.random = new Random(System.currentTimeMillis());
		centers = new ArrayList<>();
	}

	/**
	 * Assigns the data points to the clusters based on the previous cluster center means.
	 */
	private void assignDataPointsToClusters(){
		for(int i = 0; i < rowCount; i++){
			
			int minDistance = Integer.MAX_VALUE;
			
			for(int s = 0; s < this.k; s++){
				int currentDistance = 0;
				int j = 0;
				for(String attribute : dataSet.getAttributes()){
					currentDistance += this.getDistance(
							dataSet.getEncodedAttributeValue(i, attribute), centers.get(s).get(j));
					j++;
				}
				if (currentDistance < minDistance){
					minDistance = currentDistance;
					this.result[i] = s;
				}
			}
			
			if (this.result[i] != this.prevResult[i])
				this.updateMeans(this.result[i], this.prevResult[i], i);
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
	 * Returns the distance between a cluster center attribute
	 * (based on the average of the data points assigned to the cluster)
	 * and data attribute
	 * @param value1
	 * 		the encoded attribute of the data point (binary sequence)
	 * @param centerValue
	 * 		the attribute value from the center (see: representation)
	 */
	private int getDistance(boolean[] value1, int[] centerValue){
		boolean[] value2 = new boolean[centerValue.length];
		for(int i = 0; i < centerValue.length; i++){
			if(centerValue[i] > 0)
				value2[i] = true;
			else
				value2[i] = false;
		}
		return getDistance(value1, value2);
	}
	
	@Override
	public int[] getResult() {
		return this.result;
	}
	
	/**
	 * Selects random points as cluster centers, and initializes representation.
	 */
	private void initializeCenters(){
		for(int i = 0; i < this.k; i++){
			List<int[]> center = new ArrayList<>();
			int randomlySelected = random.nextInt(rowCount);
			result[randomlySelected] = i;
			for(boolean[] encodedAttribute : dataSet.getEncodedRow(randomlySelected)){
				int[] centerAttribute = new int[encodedAttribute.length];
				for(int j = 0; j < encodedAttribute.length; j++){
					if(encodedAttribute[j]){
						centerAttribute[j] = 1;
					}
					else{
						centerAttribute[j] = -1;
					}
				}
				center.add(centerAttribute);
			}
			this.centers.add(center);
		}
	}
	
	/**
	 * Runs the algorithm.
	 */
	@Override
	public void run() {
		// set the initial center points
		int iterations = 0;
		dataSet.doTransformation();
		this.initializeCenters();
		do{
			controller.setProgress(iterations, this.maxIterations);
			this.prevResult = Arrays.copyOf(result, result.length);
			if(controller.shouldStop()) return;
			this.assignDataPointsToClusters();
			iterations++;
		} while (!Arrays.equals(result, prevResult) && iterations < this.maxIterations);
		
	}
	
	/**
	 * Adjusts the means of a center when a new row is assigned to it.
	 * @param centerNo
	 * 		the id of the center to be adjusted
	 * @param rowNo
	 * 		the id of the new row
	 */
	private void updateMeans(int centerNo, int prevCenterNo, int rowNo){
		
		if(centerNo < 0 || centerNo > this.centers.size()){
			throw new IllegalArgumentException("Invalid center ID.");
		}
		
		if(rowNo < 0 || rowNo > this.rowCount){
			throw new IllegalArgumentException("Invalid row ID.");
		}
		
		for(int i = 0; i < colCount; i++){
			int j = 0;
			for(boolean bit : dataSet.getEncodedRow(rowNo).get(i)){
				if(bit){
					centers.get(centerNo).get(i)[j]++;
					if(prevCenterNo >= 0) centers.get(prevCenterNo).get(i)[j]--;
				}
				else{
					centers.get(centerNo).get(i)[j]--;
					if(prevCenterNo >= 0) centers.get(prevCenterNo).get(i)[j]++;
				}
				j++;
			}
		}
	}
}
