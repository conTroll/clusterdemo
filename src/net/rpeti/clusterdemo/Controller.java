package net.rpeti.clusterdemo;

import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JOptionPane;

import net.rpeti.clusterdemo.algorithms.Algorithms;
import net.rpeti.clusterdemo.algorithms.olary.IllegalClusterNumberException;
import net.rpeti.clusterdemo.algorithms.olary.IllegalSeedException;
import net.rpeti.clusterdemo.algorithms.olary.OlaryAlgo;
import net.rpeti.clusterdemo.data.DataSet;
import net.rpeti.clusterdemo.data.olary.OlaryDataSet;
import net.rpeti.clusterdemo.gui.MainWindow;
import net.rpeti.clusterdemo.gui.dialog.ClusteringProgress;
import net.rpeti.clusterdemo.gui.dialog.NodeEditor;
import net.rpeti.clusterdemo.gui.visualization.DataSetVisualizer;
import net.rpeti.clusterdemo.input.CSVReader;
import net.rpeti.clusterdemo.input.EmptyFileException;
import net.rpeti.clusterdemo.input.InvalidFileException;
import net.rpeti.clusterdemo.output.HTMLWriter;

/**
 * Gets an event (like choosing a file to import) from MainWindow
 * instantiates the correct classes for the action, and run them.
 *
 */
public class Controller {

	private static final String NEWLINE = System.getProperty("line.separator");
	
	private static final String EXPORT_FINISHED = "Report has been saved.";
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
	private static final String IO_ERROR_HTML_REPORT = "IO error happened saving the HTML report.";
	private static final String CONFIRMATION_TITLE = "Are you sure?";
	private static final String DELETE_CONFIRMATION_MESSAGE = "Do you want to delete the node #";
	private static final String MODIFIED = " modified.";
	private static final String ADDED = " added.";
	private static final String ADD_DATA = "Add data";
	private static final String EDIT_DATA = "Edit data";
	private static final String NODE_ID = "Node #";
	private static final String SAVE_HTML_REPORT = "Save HTML Report";

	private boolean attributesInFirstLine;
	private String separator;
	private File file;
	private MainWindow mainWindow;
	private DataSet dataSet;
	private int k;
	private int[] clusteringResult;
	private Thread backgroundThread;
	private ClusteringProgress progressDialog;
	private DataSetVisualizer visualizer;
	private boolean shouldStop;
	private boolean isModified = false;
	
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
			mainWindow.getSidePanel().enableRun();
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
	public void runClustering(){
		shouldStop = false;
			
		progressDialog = new ClusteringProgress(mainWindow.getFrame());
		
		Runnable thread = new Runnable(){

			@Override
			public void run() {
				
				Algorithms algo = Controller.this.mainWindow.getSidePanel().getSelectedAlgorithm();
				int k = Controller.this.mainWindow.getSidePanel().getClusterNumber();
				int maxIterations = Controller.this.mainWindow.getSidePanel().getIterations();
				boolean manualSeed = Controller.this.mainWindow.getSidePanel().isManualSeed();
				int seed = Controller.this.mainWindow.getSidePanel().getSeed();

				if(file == null){
					progressDialog.close();
					mainWindow.showErrorMessage(ERROR, PLEASE_IMPORT_DATA_FIRST);
					return;
				}

				if(algo == Algorithms.OLARY){
					OlaryDataSet dataSet = new OlaryDataSet();
					Controller.this.dataSet = dataSet;
					CSVReader reader = new CSVReader(dataSet);
					try {
						reader.read(file, attributesInFirstLine, separator);
						visualizer = new DataSetVisualizer(dataSet, mainWindow.getSizeForCanvas());
						OlaryAlgo algorithm;
						if(manualSeed)
							algorithm = new OlaryAlgo(k, seed, maxIterations, dataSet);
						else
							algorithm = new OlaryAlgo(k, maxIterations, dataSet);
						algorithm.setController(Controller.this);
						algorithm.run();
						progressDialog.close();
						if(!shouldStop){
							mainWindow.setGraphDrawingComponent(visualizer.getCanvas());
							Controller.this.k = k;
							Controller.this.clusteringResult = algorithm.getResult();
							visualizer.showClusteringResult(algorithm.getResult(), k);
							mainWindow.enableSave();
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
	
	/**
	 * Export the clustering result to an HTML report.
	 */
	public void exportToHtml(){
		FileDialog chooser = new FileDialog(mainWindow.getFrame(), SAVE_HTML_REPORT, FileDialog.SAVE);
		mainWindow.getFrame().setEnabled(false);
		chooser.setFile("report_" + new SimpleDateFormat("yy-MM-dd_HH-mm").format(new Date()) + ".html");
		chooser.setVisible(true);
		mainWindow.getFrame().setEnabled(true);
		mainWindow.getFrame().toFront();
		if(chooser.getDirectory() == null || chooser.getFile() == null)
			return;
		String path = chooser.getDirectory() + chooser.getFile();
		HTMLWriter htmlWriter = new HTMLWriter(
				new File(path), dataSet, clusteringResult, k, visualizer.getCanvasAsImage());
		try {
			htmlWriter.write();
			mainWindow.setStatusBarText(EXPORT_FINISHED);
		} catch (IOException e) {
			mainWindow.showErrorMessage(ERROR, IO_ERROR_HTML_REPORT);
		}
	}
	
	public void saveCSV(){
		//TODO 
		isModified = false;
	}

	public void addNode(){
		NodeEditor editorDialog = 
				new NodeEditor(ADD_DATA, dataSet.getAttributes());
		
		if(editorDialog.isOk()){
			dataSet.addData(editorDialog.getValues());
			visualizer.addVertex(dataSet.getNumberOfRows() - 1);
			isModified = true;
			mainWindow.setStatusBarText(NODE_ID + (dataSet.getNumberOfRows() - 1) + ADDED);
		}
	}
	
	public void deleteNode(Integer id){
		int selected = JOptionPane.showConfirmDialog(mainWindow.getFrame(),
				DELETE_CONFIRMATION_MESSAGE + id + "?", CONFIRMATION_TITLE,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if(selected == JOptionPane.YES_OPTION){
			dataSet.removeRow(id);
			visualizer.removeVertex(id);
			isModified = true;
			mainWindow.setStatusBarText(NODE_ID + id + " deleted successfully.");
		}
	}
	
	public void editNode(Integer id){
		NodeEditor editorDialog =
				new NodeEditor(EDIT_DATA, dataSet.getAttributes(), dataSet.getDataRow(id));
		
		if(editorDialog.isOk()){
			dataSet.editRow(id, editorDialog.getValues());
			clusteringResult[id] = -1;
			visualizer.removeVertexColor(id);
			isModified = true;
			mainWindow.setStatusBarText(NODE_ID + id + MODIFIED);
		}
	}
	
	public MainWindow getMainWindow(){
		return mainWindow;
	}
}
