package net.rpeti.clusterdemo.algorithms.olary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.rpeti.clusterdemo.Controller;
import net.rpeti.clusterdemo.algorithms.ClusteringAlgorithm;
import net.rpeti.clusterdemo.data.olary.OlaryDataSet;

public class OlaryAlgo implements ClusteringAlgorithm {
	
	private OlaryDataSet dataSet;
	private int k;
	private int seed;
	private int maxIterations;
	private int rowCount;
	private int colCount;
	//stores encoded attributes of center data points
	private List<List<boolean[]>> centers1;
	//a copy of centers1 will be made here after each iteration
	//so we can check the convergence criteria after the next iteration
	private List<List<boolean[]>> centers2;
	private int[] index;
	
	private Controller controller;
	
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
		this.colCount = dataSet.getNumberOfColumns();
		
		if(seed == -1){
			Random random = new Random();
			random.setSeed(System.currentTimeMillis());
			this.seed = random.nextInt(this.rowCount);
		} else {
			this.seed = seed;
		}
		
		if(this.seed >= this.rowCount || this.seed < 0){
			throw new IllegalSeedException();
		}
		if(maxIterations < 1){
			throw new IllegalArgumentException("Maximum iterations must be a positive integer!");
		}
		if(k > this.rowCount || k < 1){
			throw new IllegalClusterNumberException();
		}
		
		this.k = k;
		this.maxIterations = maxIterations;
		this.dataSet = dataSet;
		this.centers1 = new ArrayList<List<boolean[]>>(k);
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
				for(int s = 0; s < k; s++){
					int currentDistance = getDistanceFromClusterCenter(s, i);
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
	 * @param clusterID
	 * 		the ID of the cluster 
	 * @param id
	 * 		the ID of the data point
	 * @return the distance between the center of the specified cluster, and some data point
	 */
	private int getDistanceFromClusterCenter(int clusterID, int id){
		if(clusterID >= k || clusterID < 0)
			throw new IllegalArgumentException("clusterID must be at least 0 and less than " + k);
		if(id >= rowCount || id < 0)
			throw new IllegalArgumentException("id must be at least 0 and less than " + rowCount);
		
		int i = 0;
		int distance = 0;
		for(String attribute : dataSet.getAttributes()){
			distance += this.getDistance(
					dataSet.getEncodedAttributeValue(id, attribute),
					centers1.get(clusterID).get(i));
			i++;
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
		centers1.add(dataSet.getEncodedRow(seed));
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
						currentDistance += this.getDistanceFromClusterCenter(s, j);
					}
					//the one with the maximal sum wins
					if(currentDistance > maxDistance){
						maxDistance = currentDistance;
						optimalCenter = j;
					}
				}
			}
			centers1.add(dataSet.getEncodedRow(optimalCenter));
		}
	}
	
	/**
	 * Implementation of the computeCenters() subroutine as specified in the documentation.
	 * We determine the dominant binary attributes for every cluster.
	 */
	private void computeCenters(){
		for(int i = 0; i < k; i++){ //clusters
			for(int attributeID = 0; attributeID < colCount; attributeID++){
				if(controller.shouldStop()) return;
				for(int binaryID = 0; binaryID < dataSet.getCodeLength(attributeID); binaryID++){
					int count0 = 0;
					int count1 = 0;
					for(int j = 0; j < rowCount; j++){ //data points
						if(index[j] == i){
							if(dataSet.getEncodedRow(j).get(attributeID)[binaryID] == false){
								count0++;
							}
							else{
								count1++;
							}
						}
					}
					if(count0 >= count1){
						centers1.get(i).get(attributeID)[binaryID] = false;
					}
					else{
						centers1.get(i).get(attributeID)[binaryID] = true;
					}
				}
			}
		}
	}
	
	/**
	 * Make copy of the centers, using a simple deep-copy algorithm.
	 */
	private void copyCenters(){
		centers2 = new ArrayList<List<boolean[]>>(centers1.size());
		for(List<boolean[]> center: centers1){
			List<boolean[]> newCenter = new ArrayList<boolean[]>();
			for(boolean[] attribute : center){
				newCenter.add(Arrays.copyOf(attribute, attribute.length));
			}
			centers2.add(newCenter);
		}
	}
	
	private boolean isConvergence(){
		int i = 0;
		for(List<boolean[]> attribute : centers1){
			int j = 0;
			for(boolean[] binAttribute : attribute){
				if(!Arrays.equals(binAttribute, centers2.get(i).get(j))){
					return false;
				}
				j++;
			}
			i++;
		}
		return true;
	}
	
	/**
	 * Run the Olary algorithm with the supplied parameters
	 * on the supplied data set.
	 */
	@Override
	public void run(){
		dataSet.doTransformation();
		this.initCenters();
		int iterations = 0;
		while(iterations < maxIterations){
			controller.setProgress(iterations, maxIterations);
			this.assignToClusters();
			this.copyCenters();
			this.computeCenters();
			iterations++;
			
			if(isConvergence()) break;
		}
	}
	
	/**
	 * Sets the application controller for the algorithm to be
	 * able to communicate with it.
	 * @param controller
	 */
	@Override
	public void setController(Controller controller){
		this.controller = controller;
	}
	
	/**
	 * Returns the result of the algorithm.
	 * @return
	 * 		an integer array, where the n-th item
	 * 		represents the cluster that was assigned
	 * 		to the data point with ID n
	 */
	@Override
	public int[] getResult(){
		return this.index;
	}

}
