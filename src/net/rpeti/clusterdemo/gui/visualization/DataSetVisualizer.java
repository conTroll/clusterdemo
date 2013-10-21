package net.rpeti.clusterdemo.gui.visualization;

import java.awt.Dimension;

import net.rpeti.clusterdemo.data.spi.DataContainer;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class DataSetVisualizer {
	
	//vertices are represented with the data points ID
	DataContainer data;
	UndirectedSparseGraph<Integer, Integer> representation;
	VisualizationViewer<Integer, Integer> canvas;
	VertexTransformer vertexTransformer;
	//TODO megnézni melyik optimálisabb (FRLayout, FRLayout2, KKLayout, esetleg ISOMLayout)
	FRLayout2<Integer, Integer> layout;
	Dimension size;
	
	public DataSetVisualizer(DataContainer data){
		this.data = data;
		vertexTransformer = new VertexTransformer(data);
		representation = new UndirectedSparseGraph<>();
		size = new Dimension(600, 600);
		layout = new FRLayout2<>(representation, size);
		canvas = new VisualizationViewer<>(layout, size);
		canvas.setVertexToolTipTransformer(vertexTransformer);
		addDataPoints();
	}
	
	/**
	 * Put the vertices on the representation object.
	 */
	private void addDataPoints(){
		for(int i = 0; i < data.getNumberOfRows(); i++){
			representation.addVertex(i);
		}
	}
	
	/**
	 * Draw edges according to the clustering result.
	 * An edge will be drawn between two data points if
	 * and only if they are in the same cluster.
	 * @param clusterResult
	 * 		the array containing the cluster number for every data point
	 */
	public void showClusteringResult(int[] clusterResult){
		if(clusterResult.length != data.getNumberOfRows()){
			throw new IllegalArgumentException("Clustering result doesn't match data set.");
		}
		
		for(Integer edge : representation.getEdges()){
			representation.removeEdge(edge);
		}
		
		for(int i = 0; i < data.getNumberOfRows(); i++){
			for (int j = i + 1; j < data.getNumberOfRows(); j++){
				if(clusterResult[i] == clusterResult[j]){
					representation.addEdge(
							clusterResult[i], new Pair<Integer>(i, j), EdgeType.UNDIRECTED);
				}
			}
		}
		
	}
	
	public VisualizationViewer<Integer, Integer> getCanvas(){
		return canvas;
	}
}
