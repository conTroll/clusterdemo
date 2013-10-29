package net.rpeti.clusterdemo.gui.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.functors.MapTransformer;

import net.rpeti.clusterdemo.data.spi.DataContainer;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

//TODO: optimalizáció
//TODO: interakció

public class DataSetVisualizer {
	
	//vertices are represented with the data points ID
	DataContainer data;
	UndirectedSparseGraph<Integer, Integer> representation;
	VisualizationViewer<Integer, Integer> canvas;
	VertexTransformer vertexTransformer;
	AggregateLayout<Integer, Integer> layout;
	KKLayout<Integer, Integer> groupLayout;
	Dimension size;
	Map<Integer, Paint> vertexPaints;
	
	public final Color[] colors =
		{
			new Color(216, 134, 134),
			new Color(135, 137, 211),
			new Color(134, 206, 189),
			new Color(206, 176, 134),
			new Color(194, 204, 134),
			new Color(145, 214, 134),
			new Color(133, 178, 209),
			new Color(103, 148, 255),
			new Color(60, 220, 220),
			new Color(30, 250, 100)
		};

	public DataSetVisualizer(DataContainer data){
		this.data = data;
		vertexTransformer = new VertexTransformer(data);
		vertexPaints = new HashMap<>();
		representation = new UndirectedSparseGraph<>();
		size = new Dimension(600, 600);
		groupLayout = new KKLayout<>(representation);
		groupLayout.setSize(size);
		groupLayout.setDisconnectedDistanceMultiplier(0.1);
		layout = new AggregateLayout<>(groupLayout);
		canvas = new VisualizationViewer<>(layout, size);
		canvas.setVertexToolTipTransformer(vertexTransformer);
		canvas.getRenderContext().setVertexFillPaintTransformer(MapTransformer.getInstance(vertexPaints));
		canvas.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		canvas.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
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
	public void showClusteringResult(int[] clusterResult, int numberOfClusters){
		if(clusterResult.length != data.getNumberOfRows()){
			throw new IllegalArgumentException("Clustering result doesn't match data set.");
		}
		
		//clear previous result
		layout.removeAll();
		
		//assign vertices to clusters
		List<Set<Integer>> clusters = new ArrayList<>(numberOfClusters);
		for(int i = 0; i < numberOfClusters; i++){
			clusters.add(new HashSet<Integer>());
		}
		
		for(int i = 0; i < data.getNumberOfRows(); i++){
			clusters.get(clusterResult[i]).add(Integer.valueOf(i));
		}
		
		//set color of nodes (vertices in the same cluster will have same colors)
		int i = 0;
		for(Set<Integer> cluster : clusters){
			Color color = colors[i % colors.length];
			for(Integer vertex : cluster){
				vertexPaints.put(vertex, color);
			}
			groupCluster(layout, cluster, numberOfClusters);
			i++;
		}
	}
	
	private void groupCluster(AggregateLayout<Integer, Integer> layout, Set<Integer> vertices, int numberOfClusters) {
		if(vertices.size() < layout.getGraph().getVertexCount()) {
			Point2D center = layout.transform(vertices.iterator().next());
			Graph<Integer, Integer> subGraph = 
					UndirectedSparseGraph.<Integer, Integer>getFactory().create();
			for(Integer v : vertices) {
				subGraph.addVertex(v);
			}
			Layout<Integer, Integer> subLayout = 
					new CircleLayout<Integer, Integer>(subGraph);
			subLayout.setInitializer(canvas.getGraphLayout());
			subLayout.setSize(new Dimension(
					(int)(size.width * ((double)vertices.size() / (double)data.getNumberOfRows())),
					(int)(size.height * ((double)vertices.size() / (double)data.getNumberOfRows()))));

			layout.put(subLayout,center);
			canvas.repaint();
		}
	}

	public VisualizationViewer<Integer, Integer> getCanvas(){
		return canvas;
	}
}
