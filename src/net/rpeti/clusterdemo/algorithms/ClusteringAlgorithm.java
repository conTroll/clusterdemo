package net.rpeti.clusterdemo.algorithms;

import net.rpeti.clusterdemo.Controller;
import net.rpeti.clusterdemo.Main;

public abstract class ClusteringAlgorithm {
	
	protected Controller controller = Main.getController();
	
	/**
	 * Run the Clustering algorithm with the supplied parameters
	 * on the supplied data set.
	 */
	public abstract void run();
	
	/**
	 * Returns the result of the algorithm.
	 * @return
	 * 		an integer array, where the n-th item
	 * 		represents the cluster that was assigned
	 * 		to the data point with ID n
	 */
	public abstract int[] getResult();
}
