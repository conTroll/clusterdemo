package net.rpeti.clusterdemo;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.rpeti.clusterdemo.algorithms.Algorithms;
import net.rpeti.clusterdemo.algorithms.olary.IllegalClusterNumberException;
import net.rpeti.clusterdemo.algorithms.olary.IllegalSeedException;
import net.rpeti.clusterdemo.algorithms.olary.OlaryAlgo;
import net.rpeti.clusterdemo.data.olary.OlaryDataSet;
import net.rpeti.clusterdemo.gui.MainWindow;
import net.rpeti.clusterdemo.gui.dialog.ClusteringProgress;
import net.rpeti.clusterdemo.gui.visualization.DataSetVisualizer;
import net.rpeti.clusterdemo.input.CSVReader;
import net.rpeti.clusterdemo.input.EmptyFileException;
import net.rpeti.clusterdemo.input.InvalidFileException;

/**
 * Gets an event (like choosing a file to import) from MainWindow
 * instantiates the correct classes for the action, and run them.
 *
 */
//TODO prepare for other algorithms
public class Controller {
	private static final String NEWLINE = System.getProperty("line.separator");
	
	private static final String BAD_FORMATTING = "An error has occurred processing the file." + NEWLINE + "The CSV file is badly formatted." + NEWLINE;
	private static final String EMPTY_FILE = "You provided an empty file. Please select a valid file.";
	private static final String PLEASE_IMPORT_DATA_FIRST = "Please import data first.";
	private static final String CANT_READ_FILE = "Couldn't read input file." + NEWLINE + "Please import file, and try again.";
	private static final String INVALID_CSV = "You provided an invalid CSV file.";
	private static final String ERROR = "Error";
	private static final String CSV_SELECTED = "CSV data file has been selected. You can now run the algorithm.";
	private static final String CLUSTERING_FINISHED = "Clustering finished.";
	private static final String CLUSTERING_CANCELLED = "Clustering cancelled.";
	private static final String INVALID_CLUSTER_NUMBER = "Invalid cluster number provided." + NEWLINE + "It cannot be greater than the number of data points.";
	private static final String INVALID_SEED = "Invalid seed ID provided." + NEWLINE + "IDs are indexed between 0 and m-1, where m is the number of data points.";

	private boolean attributesInFirstLine;
	private String separator;
	private File file;
	private MainWindow mainWindow;
	private Thread backgroundThread;
	private ClusteringProgress progressDialog;
	private DataSetVisualizer visualizer;
	private boolean shouldStop;
	
	/**
	 * Start importing the CSV file.
	 * @param attributesInFirstLine
	 * 		are the attribute names present in the first row of the file?
	 * @param separator
	 * 		a valid Java Regular Expression, that will be used as separator
	 * @param file
	 * 		the File object containing the absolute path
	 */
	public void importCSV(boolean attributesInFirstLine, String separator, File file){
		if(!(file.isAbsolute() && file.isFile() && file.exists())){
			throw new IllegalArgumentException("Invalid file.");
		}
		try{
			Pattern.compile(separator);
			this.attributesInFirstLine = attributesInFirstLine;
			this.separator = separator;
			this.file = file;
			mainWindow.setStatusBarText(CSV_SELECTED);
		} catch (PatternSyntaxException e){
			throw new IllegalArgumentException("Invalid regular expression for separator.");
		}
	}
	
	public void setMainWindow(MainWindow mainWindow){
		this.mainWindow = mainWindow;
	}
	
	/**
	 * Start the clustering algorithm.
	 * @param algo
	 * 		the type of the algorithm
	 * @param k
	 * 		the number of desired clusters
	 * @param seed
	 * 		the ID of data that will be used as seed (if you pass -1, one will be selected randomly)
	 * @param maxIterations
	 * 		the maximal number of iterations before the algorithm will terminate
	 */
	public void runClustering(final Algorithms algo, final int k, final int seed, final int maxIterations){
		shouldStop = false;
			
		progressDialog = new ClusteringProgress(mainWindow.getFrame());
		
		Runnable thread = new Runnable(){

			@Override
			public void run() {

				if(file == null){
					progressDialog.close();
					mainWindow.showErrorMessage(ERROR, PLEASE_IMPORT_DATA_FIRST);
					return;
				}

				if(algo == Algorithms.OLARY){
					
					OlaryDataSet dataSet = new OlaryDataSet();
					CSVReader reader = new CSVReader(dataSet);
					try {
						reader.read(file, attributesInFirstLine, separator);
						visualizer = new DataSetVisualizer(dataSet, mainWindow.getSizeForCanvas());
						OlaryAlgo algorithm = new OlaryAlgo(k, seed, maxIterations, dataSet);
						algorithm.setController(Controller.this);
						algorithm.run();
						progressDialog.close();
						if(!shouldStop){
							mainWindow.setGraphDrawingComponent(visualizer.getCanvas());
							visualizer.showClusteringResult(algorithm.getResult(), k);
							mainWindow.setStatusBarText(CLUSTERING_FINISHED);
						} else {
							mainWindow.setStatusBarText(CLUSTERING_CANCELLED);
						}
					} catch (EmptyFileException e){
						progressDialog.close();
						mainWindow.showErrorMessage(ERROR, EMPTY_FILE);
					} catch (InvalidFileException e){
						progressDialog.close();
						mainWindow.showErrorMessage(ERROR, INVALID_CSV);
					} catch (IOException e) {
						progressDialog.close();
						mainWindow.showErrorMessage(ERROR, CANT_READ_FILE + NEWLINE + NEWLINE + e.getMessage());
					} catch (IllegalSeedException e) {
						progressDialog.close();
						mainWindow.showErrorMessage(ERROR, INVALID_SEED);
					} catch (IllegalClusterNumberException e) {
						progressDialog.close();
						mainWindow.showErrorMessage(ERROR, INVALID_CLUSTER_NUMBER);
					} catch (IllegalArgumentException e){
						e.printStackTrace();
						progressDialog.close();
						mainWindow.showErrorMessage(ERROR, BAD_FORMATTING + e.getMessage());
					} catch (Exception e) {
						progressDialog.close();
						e.printStackTrace();
						mainWindow.showUnhandledException(e);
					}
				}
			}
		};
		
		backgroundThread = new Thread(thread);
		backgroundThread.start();
	}
	
	/**
	 * Updates the size of the canvas.
	 * It is typically called upon resize of the main window.
	 */
	public void updateCanvasSize(){
		if (visualizer != null)
			visualizer.setSize(mainWindow.getSizeForCanvas());
	}
	
	/**
	 * Changes the mouse mode on the canvas according to the
	 * selected mouse mode on the toolbar.
	 */
	public void changeMouseMode(){
		if (visualizer != null)
			visualizer.setMouseMode(mainWindow.getMouseMode());
		updateCanvasSize();
		
	}
	
	/**
	 * Indicates whether the algorithm should stop
	 * @return
	 * 		true if the user requested a cancellation,
	 * 		false otherwise
	 */
	public boolean shouldStop(){
		return shouldStop;
	}
	
	/**
	 * Calling this function indicates that the algorithm should stop.
	 */
	public void cancelClustering(){
		shouldStop = true;
	}
	
	/**
	 * Sets the progress on the progress dialog.
	 * @param value
	 * 		percentage of progress completion
	 */
	public void setProgress(int iterations, int maxIterations){
		progressDialog.setProgress(iterations, maxIterations);
	}
	
	//TODO actually implement
	public void addNode(){
		mainWindow.showMessage("Add", "User wants to add a node.");
	}
	
	//TODO actually implement
	public void deleteNode(Integer id){
		mainWindow.showMessage("Delete", "User wants to delete node ID #" + id);
	}
	
	//TODO actually implement
	public void editNode(Integer id){
		mainWindow.showMessage("Edit", "User wants to edit node ID #" + id);
	}
	
	public MainWindow getMainWindow(){
		return mainWindow;
	}
}
