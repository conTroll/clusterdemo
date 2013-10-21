package net.rpeti.clusterdemo.algorithms;

import net.rpeti.clusterdemo.Controller;

public interface ClusteringAlgorithm {
	
	/**
	 * Run the Clustering algorithm with the supplied parameters
	 * on the supplied data set.
	 */
	public void run();
	
	/**
	 * Sets the application controller for the algorithm to be
	 * able to communicate with it.
	 * @param controller
	 */
	public void setController(Controller controller);
	
	/**
	 * Returns the result of the algorithm.
	 * @return
	 * 		an integer array, where the n-th item
	 * 		represents the cluster that was assigned
	 * 		to the data point with ID n
	 */
	public int[] getResult();
}
