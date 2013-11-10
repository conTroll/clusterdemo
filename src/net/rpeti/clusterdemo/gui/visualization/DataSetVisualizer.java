package net.rpeti.clusterdemo.gui.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.functors.MapTransformer;

import net.rpeti.clusterdemo.data.spi.DataContainer;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

//TODO: layout optimalizáció: saját layout groupLayout-nak (ami egyenlően felosztja a canvast)
//TODO: kiválasztásnál stroke szín és event fire
//TODO: kontextusmenü

public class DataSetVisualizer {
	
	//vertices are represented with the data points ID
	DataContainer data;
	Dimension size;
	UndirectedSparseGraph<Integer, Integer> representation;
	VisualizationViewer<Integer, Integer> canvas;
	VertexTransformer vertexTransformer;
	DefaultModalGraphMouse<Integer, Integer> mouse;
	AggregateLayout<Integer, Integer> layout;
	Layout<Integer, Integer> groupLayout;
	Map<Integer, Paint> vertexPaints;
	
	//color RGB codes representing clusters
	private final static Color[] colors = {
			new Color(117, 184, 0),
			new Color(78, 0, 214),
			new Color(222, 141, 0),
			new Color(0, 93, 255),
			new Color(223, 230, 32),
			new Color(25, 84, 156),
			new Color(82, 81, 61),
			new Color(254, 219, 7),
			new Color(137, 65, 41),
			new Color(18, 106, 118)
	};

	public DataSetVisualizer(DataContainer data, Dimension size){
		this.data = data;
		this.size = size;
		vertexTransformer = new VertexTransformer(data);
		vertexPaints = new HashMap<>();
		representation = new UndirectedSparseGraph<>();
		groupLayout = new KKLayout<>(representation);
		groupLayout.setSize(size);
		layout = new AggregateLayout<>(groupLayout);
		layout.setSize(size);
		mouse = new DefaultModalGraphMouse<>();
		canvas = new VisualizationViewer<>(layout, size);
		canvas.setSize(size);
		canvas.setPreferredSize(size);
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
			
			if(!cluster.isEmpty())
				groupCluster(layout, cluster, numberOfClusters);
			
			i++;
		}
		
		canvas.repaint();
	}

	private void groupCluster(AggregateLayout<Integer, Integer> layout, Set<Integer> vertices, int numberOfClusters) {
		Point2D center = layout.transform(vertices.iterator().next());
		Graph<Integer, Integer> subGraph = 
				UndirectedSparseGraph.<Integer, Integer>getFactory().create();
		for(Integer v : vertices) {
			subGraph.addVertex(v);
		}
		Layout<Integer, Integer> subLayout = 
				new DAGLayout<Integer, Integer>(subGraph);
		subLayout.setInitializer(canvas.getGraphLayout());
		subLayout.setSize(new Dimension(
				(int)(size.getWidth() * ((double)vertices.size() / (double)data.getNumberOfRows() / 2.0d)),
				(int)(size.getHeight() * ((double)vertices.size() / (double)data.getNumberOfRows() / 2.0d))));

		layout.put(subLayout,center);
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
		//TODO tesztelni hogy megéri-e?
		//layout.setSize(size);
		canvas.setSize(size);
		canvas.setPreferredSize(size);
	}
	
	/**
	 * Sets the mouse mode for the canvas.
	 * @param mouseMode
	 */
	public void setMouseMode(ModalGraphMouse.Mode mouseMode){
		mouse.setMode(mouseMode);
	}
	
	/**
	 * @return the contents of the canvas as an image which can be saved later on
	 * 		(for eg. with javax.imageio.ImageIO class)
	 */
	public BufferedImage getCanvasAsImage(){
		canvas.setDoubleBuffered(false);
		BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		canvas.paint(bi.createGraphics());
		canvas.setDoubleBuffered(true);
		return bi;
	}
}
