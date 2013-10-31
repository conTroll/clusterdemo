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
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

//TODO: layout optimalizáció
//TODO: színlista modernizálása
//TODO: kiválasztásnál stroke szín és event fire
//TODO: kontextusmenü
//TODO: bugfix: valami.csv fájlon 3 klaszterre exception (ötlet: üres klaszterre hibás)
//TODO: bugfix: splitpane állításnál, illetve módválasztásnál elromlik a méret

public class DataSetVisualizer {
	
	//vertices are represented with the data points ID
	DataContainer data;
	Dimension size;
	UndirectedSparseGraph<Integer, Integer> representation;
	VisualizationViewer<Integer, Integer> canvas;
	VertexTransformer vertexTransformer;
	DefaultModalGraphMouse<Integer, Integer> mouse;
	AggregateLayout<Integer, Integer> layout;
	KKLayout<Integer, Integer> groupLayout;
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

	public DataSetVisualizer(DataContainer data, Dimension size){
		this.data = data;
		this.size = size;
		vertexTransformer = new VertexTransformer(data);
		vertexPaints = new HashMap<>();
		representation = new UndirectedSparseGraph<>();
		groupLayout = new KKLayout<>(representation);
		groupLayout.setDisconnectedDistanceMultiplier(0.1);
		layout = new AggregateLayout<>(groupLayout);
		layout.setSize(size);
		mouse = new DefaultModalGraphMouse<>();
		canvas = new VisualizationViewer<>(layout, size);
		canvas.setSize(size);
		canvas.setVertexToolTipTransformer(vertexTransformer);
		mouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		canvas.setGraphMouse(mouse);
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
	 * Sets up the canvas filling with vertices according to the data set,
	 * and the clustering result. Data points in the same cluster will
	 * have the same color, and will be close to each other.
	 * @param clusterResult
	 * 		the array containing the cluster number for every data point
	 * @param numberOfClusters
	 * 		the number of clusters 
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
	
	//TODO átgondolni
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
					(int)(size.getWidth() * ((double)vertices.size() / (double)data.getNumberOfRows())),
					(int)(size.getHeight() * ((double)vertices.size() / (double)data.getNumberOfRows()))));

			layout.put(subLayout,center);
		}
		canvas.repaint();
	}

	/**
	 * @return the JPanel containing the canvas to be drawn.
	 */
	public VisualizationViewer<Integer, Integer> getCanvas(){
		return canvas;
	}
	
	/**
	 * Sets size for the canvas.
	 * @param size
	 */
	public void setSize(Dimension size){
		this.size = size;
		layout.setSize(size);
		canvas.setSize(size);
	}
	
	/**
	 * Sets the mouse mode for the canvas.
	 * @param mouseMode
	 */
	public void setMouseMode(ModalGraphMouse.Mode mouseMode){
		mouse.setMode(mouseMode);
	}
}
