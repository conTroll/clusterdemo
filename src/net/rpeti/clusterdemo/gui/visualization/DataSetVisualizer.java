package net.rpeti.clusterdemo.gui.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.functors.MapTransformer;

import net.rpeti.clusterdemo.data.DataContainer;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class DataSetVisualizer {
	
	//vertices are represented with the data points ID
	private DataContainer data;
	private Dimension size;
	private int numberOfClusters;
	private int[] clusterResult;
	private UndirectedSparseGraph<Integer, Integer> representation;
	private VisualizationViewer<Integer, Integer> canvas;
	private LayoutScalingControl scaler;
	private VertexTransformer vertexTransformer;
	private ContextMenuPlugin contextMenu;
	private EditingModalGraphMouse<Integer, Integer> mouse;
	private AggregateLayout<Integer, Integer> layout;
	private Layout<Integer, Integer> groupLayout;
	private Map<Integer, Paint> vertexPaints;
	
	//color RGB codes representing clusters
	private final static Color[] colors = {
			new Color(117, 184, 0),
			new Color(78, 150, 214),
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
		
		this.initialize();
	}
	
	/**
	 * Adds a node to the canvas.
	 * @param vertexId
	 */
	public void addVertex(Integer vertexId){
		representation.addVertex(vertexId);
		canvas.repaint();
	}
	
	/**
	 * @return the JPanel containing the canvas to be drawn.
	 */
	public VisualizationViewer<Integer, Integer> getCanvas(){
		return canvas;
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
				(int)(size.getWidth() * ((double)vertices.size() / (double)data.getNumberOfRows() / 1.0d)),
				(int)(size.getHeight() * ((double)vertices.size() / (double)data.getNumberOfRows() / 1.0d))));

		layout.put(subLayout,center);
	}
	
	/**
	 * Do all the initialization work: create visualization table,
	 * add nodes, run layout algorithms, add plugins, etc
	 */
	private void initialize(){
		
		ArrayList<Integer> nodes;
		if (representation != null){
			nodes = new ArrayList<>(representation.getVertices());
		}
		else{
			nodes = new ArrayList<>();
		}
		
		representation = new UndirectedSparseGraph<>();
		
		groupLayout = new DAGLayout<>(representation);
		groupLayout.setSize(size);
		layout = new AggregateLayout<>(groupLayout);
		layout.setSize(size);
		canvas = new VisualizationViewer<>(layout, size);
		canvas.setSize(size);
		canvas.setPreferredSize(size);
		
		canvas.setVertexToolTipTransformer(vertexTransformer);
		mouse = new EditingModalGraphMouse<>(canvas.getRenderContext(), null, null);
		contextMenu = new ContextMenuPlugin(canvas);
		scaler = new LayoutScalingControl();
		mouse.remove(mouse.getPopupEditingPlugin());
		mouse.add(contextMenu);
		mouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		canvas.setGraphMouse(mouse);
		canvas.getRenderContext().setVertexFillPaintTransformer(MapTransformer.getInstance(vertexPaints));
		canvas.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		canvas.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		if(nodes.size() > 0){
			for (Integer node : nodes){
				representation.addVertex(node);
			}
			this.showClusteringResult(clusterResult, numberOfClusters);
		} else {
			for(Integer i = 0; i < data.getNumberOfRows(); i++){
				representation.addVertex(i);
			}
		}
	}
	
	/**
	 * Rearranges clusters on the canvas.
	 */
	public void rearrange(){
		this.initialize();
	}
	
	/**
	 * Removes a node from the canvas.
	 * @param vertexId
	 */
	public void removeVertex(Integer vertexId){
		representation.removeVertex(vertexId);
		canvas.repaint();
	}
	
	/**
	 * Remove the fill color of a vertex.
	 * @param vertexId
	 */
	public void removeVertexColor(Integer vertexId){
		vertexPaints.remove(vertexId);
		canvas.repaint();
	}
	
	/**
	 * Sets the mouse mode for the canvas.
	 * @param mouseMode
	 */
	public void setMouseMode(ModalGraphMouse.Mode mouseMode){
		mouse.setMode(mouseMode);
	}
	
	/**
	 * Sets size for the canvas.
	 * @param size
	 */
	public void setSize(Dimension size){
		this.size = size;
		canvas.setSize(size);
		canvas.setPreferredSize(size);
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
		this.clusterResult = clusterResult;
		this.numberOfClusters = numberOfClusters;
		
		//clear previous result
		layout.removeAll();
		
		//assign vertices to clusters
		List<Set<Integer>> clusters = new ArrayList<>(numberOfClusters);
		//make one extra cluster for unclustered data
		for(int i = 0; i <= numberOfClusters; i++){
			clusters.add(new HashSet<Integer>());
		}
		
		for(int i = 0; i < data.getNumberOfRows(); i++){
			//if the data is newly added set its assigned cluster to -1 (unclustered)
			int assignedCluster = i < clusterResult.length ? clusterResult[i] : -1;
			//if the data is unclustered (assigned cluster = -1) then assign it to an extra cluster,
			//otherwise assign it to its own cluster
			clusters.get(assignedCluster == -1 ? numberOfClusters : assignedCluster).add(Integer.valueOf(i));
		}
		
		//set color of nodes (vertices in the same cluster will have same colors)
		int i = 0;
		for(Set<Integer> cluster : clusters){
			//don't add color to unclustered data
			if(i == numberOfClusters) continue; 
			
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
	
	/**
	 * Zooms in on the canvas one level based on the center point.
	 */
	public void zoomIn(){
		scaler.scale(canvas, 1.1f, new Point(size.width / 2, size.height / 2));
	}
	
	/**
	 * Zooms out of the canvas one level based on the center point.
	 */
	public void zoomOut(){
		scaler.scale(canvas, 0.9f, new Point(size.width / 2, size.height / 2));
	}
}
