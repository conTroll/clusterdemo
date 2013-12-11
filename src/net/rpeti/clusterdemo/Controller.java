package net.rpeti.clusterdemo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JOptionPane;

import net.rpeti.clusterdemo.algorithms.Algorithms;
import net.rpeti.clusterdemo.algorithms.ClusteringAlgorithm;
import net.rpeti.clusterdemo.algorithms.IllegalClusterNumberException;
import net.rpeti.clusterdemo.algorithms.IllegalSeedException;
import net.rpeti.clusterdemo.algorithms.kmeans.KMeansAlgo;
import net.rpeti.clusterdemo.algorithms.olary.OlaryAlgo;
import net.rpeti.clusterdemo.data.DataSet;
import net.rpeti.clusterdemo.data.olary.OlaryDataSet;
import net.rpeti.clusterdemo.gui.MainWindow;
import net.rpeti.clusterdemo.gui.SidePanel;
import net.rpeti.clusterdemo.gui.dialog.ClusteringProgress;
import net.rpeti.clusterdemo.gui.dialog.NodeEditor;
import net.rpeti.clusterdemo.gui.visualization.DataSetVisualizer;
import net.rpeti.clusterdemo.input.CSVReader;
import net.rpeti.clusterdemo.input.EmptyFileException;
import net.rpeti.clusterdemo.input.InvalidFileException;
import net.rpeti.clusterdemo.output.CSVWriter;
import net.rpeti.clusterdemo.output.HTMLWriter;

/**
 * Gets an event (like choosing a file to import) from MainWindow
 * instantiates the correct classes for the action, and run them.
 *
 */
public final class Controller {

	public static final Controller INSTANCE = new Controller();
	
	private static final String ERROR_UNIMPLEMENTED_ALGORITHM = "Unimplemented algorithm.";
	private static final String EXPORT_FINISHED = "Report has been saved.";
	private static final String BAD_FORMATTING = "<html>An error has occurred processing the file.<br>The CSV file is badly formatted.<br></html>";
	private static final String EMPTY_FILE = "Empty data file.";
	private static final String PLEASE_IMPORT_DATA_FIRST = "Please import data first.";
	private static final String CANT_READ_FILE = "<html>Couldn't read input file.<br>Please import file, and try again.</html>";
	private static final String INVALID_CSV = "Invalid CSV file.";
	private static final String ERROR = "Error";
	private static final String CSV_SELECTED = "CSV data file has been selected. You can now run the algorithm.";
	private static final String READY = "Ready.";
	private static final String CLUSTERING_FINISHED = "Clustering finished.";
	private static final String CLUSTERING_CANCELLED = "Clustering cancelled.";
	private static final String INVALID_CLUSTER_NUMBER = "Invalid cluster number provided.";
	private static final String INVALID_SEED = "Invalid seed ID provided.";
	private static final String IO_ERROR_HTML_REPORT = "<html>IO error happened while saving the HTML report.<br>Check if free space and write privileges are present on the destination drive.</html>";
	private static final String CONFIRMATION_TITLE = "Are you sure?";
	private static final String SAVE_CONFIRMATION_TITLE = "Do you want to save?";
	private static final String DELETE_CONFIRMATION_MESSAGE = "Do you want to delete the node #";
	private static final String MODIFIED = " modified.";
	private static final String ADDED = " added.";
	private static final String ADD_DATA = "Add data";
	private static final String EDIT_DATA = "Edit data";
	private static final String NODE_ID = "Node #";
	private static final String DELETED_SUCCESSFULLY = " deleted successfully.";
	private static final String SAVE_HTML_REPORT = "Save HTML Report";
	private static final String MODIFIED_STATUS = "Modified. Please save.";
	private static final String SAVE_BEFORE_RECLUSTER_QUESTION = "<html>The data set has been modified.<br>" + 
			"If you re-cluster the data now, you'll lose all the changes.<br>Do you want to save the changes before re-clustering?</html>";
	private static final String FILE_WRITTEN_TEXT = "File written successfully.";
	private static final String WRITE_ERROR = "<html>Can't access file for write.<br>May it's in use by another process.</html>";

	private boolean attributesInFirstLine;
	private String separator;
	private File file;
	private MainWindow mainWindow;
	private DataSet dataSet;
	private int k;
	private int[] clusterResult;
	private Thread backgroundThread;
	private ClusteringProgress progressDialog;
	private DataSetVisualizer visualizer;
	private boolean shouldStop;
	private boolean isModified = false;
	
	/**
	 * The class is singleton. 
	 * Get the instance from the static field <code>INSTANCE</code>.
	 */
	private Controller(){
		//disable instantiation
	}
	
	/**
	 * Handles addition of a node from the context menu. Opens node editor window,
	 * gets the result, makes the change in the data set and sets the modified tag to true.
	 */
	public void addNode(){
		NodeEditor editorDialog = 
				new NodeEditor(ADD_DATA, dataSet.getAttributes());
		
		if(editorDialog.isOk()){
			dataSet.addData(editorDialog.getValues());
			visualizer.addVertex(dataSet.getNumberOfRows() - 1);
			isModified = true;
			mainWindow.updateWindowTitle(true, file.getName());
			mainWindow.getSidePanel().setStatus(MODIFIED_STATUS, SidePanel.STATUS_TYPE_WARNING);
			mainWindow.setStatusBarText(NODE_ID + (dataSet.getNumberOfRows() - 1) + ADDED);
		}
	}
	
	/**
	 * Calling this function indicates that the algorithm should stop.
	 */
	public void cancelClustering(){
		shouldStop = true;
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
	 * Handles the deletion of a node from the context menu. Makes the change
	 * in the data set, and sets the modified tag to true.
	 * @param id
	 * 		the id of the node being edited
	 */
	public void deleteNode(Integer id){
		int selected = JOptionPane.showConfirmDialog(mainWindow.getFrame(),
				DELETE_CONFIRMATION_MESSAGE + id + "?", CONFIRMATION_TITLE,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if(selected == JOptionPane.YES_OPTION){
			dataSet.removeRow(id);
			visualizer.removeVertex(id);
			isModified = true;
			mainWindow.updateWindowTitle(true, file.getName());
			mainWindow.getSidePanel().setStatus(MODIFIED_STATUS, SidePanel.STATUS_TYPE_WARNING);
			mainWindow.setStatusBarText(NODE_ID + id + DELETED_SUCCESSFULLY);
		}
	}
	
	/**
	 * Handles editing a node from the context menu. Opens node editor window,
	 * gets the result, makes the change in the data set, marks the data as
	 * unclustered, and sets the modified tag to true.
	 * @param id
	 * 		the id of the node being edited
	 */
	public void editNode(Integer id){
		NodeEditor editorDialog =
				new NodeEditor(EDIT_DATA, dataSet.getAttributes(), dataSet.getDataRow(id));
		
		if(editorDialog.isOk()){
			dataSet.editRow(id, editorDialog.getValues());
			clusterResult[id] = -1;
			visualizer.removeVertexColor(id);
			isModified = true;
			mainWindow.updateWindowTitle(true, file.getName());
			mainWindow.getSidePanel().setStatus(MODIFIED_STATUS, SidePanel.STATUS_TYPE_WARNING);
			mainWindow.setStatusBarText(NODE_ID + id + MODIFIED);
		}
	}
	
	/**
	 * Exports the clustering result to an HTML report.
	 */
	public void exportToHtml(){
		File destination = mainWindow.selectSavePath(SAVE_HTML_REPORT, 
				"report_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".html");
		if(destination == null) return;
		HTMLWriter htmlWriter = new HTMLWriter(
				destination, dataSet, clusterResult, k, visualizer.getCanvasAsImage());
		try {
			htmlWriter.write();
			mainWindow.setStatusBarText(EXPORT_FINISHED);
		} catch (IOException e) {
			mainWindow.showErrorMessage(ERROR, IO_ERROR_HTML_REPORT);
		}
	}
	
	public MainWindow getMainWindow(){
		return mainWindow;
	}
	
	/**
	 * If the data set has been modified, warn the user, and ask for action to take
	 * @param question
	 * 		the question to show the user upon asking if a save should occur or not
	 * @return
	 * 		true, if the operation should cancel, false otherwise
	 */
	public boolean handleModification(String question){
		if(isModified){
			int selection = JOptionPane.showConfirmDialog(mainWindow.getFrame(), question,
					SAVE_CONFIRMATION_TITLE, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(selection == JOptionPane.CANCEL_OPTION){
				return true;
			}
			else if(selection == JOptionPane.YES_OPTION){
				this.saveCSV();
			}
		}
		return false;
	}
	
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
		//if the data set has been modified, warn the user, and ask for action to take

		if(!(file.isAbsolute() && file.isFile() && file.exists())){
			throw new IllegalArgumentException("Invalid file.");
		}
		try{
			Pattern.compile(separator);
			this.attributesInFirstLine = attributesInFirstLine;
			this.separator = separator;
			this.file = file;
			mainWindow.updateWindowTitle(false, file.getName());
			mainWindow.setStatusBarText(CSV_SELECTED);
			mainWindow.getSidePanel().setStatus(READY, SidePanel.STATUS_TYPE_READY);
			mainWindow.getSidePanel().setRunEnabled(true);
		} catch (PatternSyntaxException e){
			throw new IllegalArgumentException("Invalid regular expression for separator.");
		}
	}
	
	/**
	 * Rebuilds the visualization without reclustering the data set.
	 */
	public void rearrangeCanvas(){
		visualizer.rearrange();
		mainWindow.setGraphDrawingComponent(visualizer.getCanvas());
		visualizer.setMouseMode(mainWindow.getMouseMode());
	}
	
	/**
	 * Starts the clustering algorithm in a separate thread.
	 * You can add clustering algorithms here.
	 * You'll have to specify the data set implementation being used,
	 * instantiate it, fill it with data, then instantiate your algorithm.
	 * See the currently implemented algorithms for an example.
	 */
	public void runClustering(){
		shouldStop = false;
		
		if(this.handleModification(SAVE_BEFORE_RECLUSTER_QUESTION))
			return;
			
		progressDialog = new ClusteringProgress();
		
		Runnable thread = new Runnable(){

			@Override
			public void run() {

				//get the parameters set on the side panel
				Algorithms algo = Controller.this.mainWindow.getSidePanel().getSelectedAlgorithm();
				int k = Controller.this.mainWindow.getSidePanel().getClusterNumber();
				int maxIterations = Controller.this.mainWindow.getSidePanel().getIterations();
				boolean manualSeed = Controller.this.mainWindow.getSidePanel().isManualSeed();
				int seed = Controller.this.mainWindow.getSidePanel().getSeed();

				//check if an input file path is provided
				if(file == null){
					progressDialog.close();
					mainWindow.showErrorMessage(ERROR, PLEASE_IMPORT_DATA_FIRST);
					return;
				}

				try {
					ClusteringAlgorithm algorithm;

					//here are the handlers for the algorithms
					//you can add new algorithms here
					if(algo == Algorithms.OLARY){
						OlaryDataSet dataSet = new OlaryDataSet();
						Controller.this.dataSet = dataSet;
						CSVReader reader = new CSVReader(dataSet);
						reader.read(file, attributesInFirstLine, separator);
						if(manualSeed)
							algorithm = new OlaryAlgo(k, seed, maxIterations, dataSet);
						else
							algorithm = new OlaryAlgo(k, maxIterations, dataSet);
					} else if (algo == Algorithms.KMEANS){
						OlaryDataSet dataSet = new OlaryDataSet();
						Controller.this.dataSet = dataSet;
						CSVReader reader = new CSVReader(dataSet);
						reader.read(file, attributesInFirstLine, separator);
						algorithm = new KMeansAlgo(k, maxIterations, dataSet);
					} else {
						mainWindow.getSidePanel().setStatus(ERROR_UNIMPLEMENTED_ALGORITHM, SidePanel.STATUS_TYPE_ERROR);
						progressDialog.close();
						return;
					}

					algorithm.run();
					progressDialog.close();
					showClusteringResult(algorithm.getResult(), k);
				} catch (EmptyFileException e){
					progressDialog.close();
					mainWindow.getSidePanel().setStatus(EMPTY_FILE, SidePanel.STATUS_TYPE_ERROR);
				} catch (InvalidFileException e){
					progressDialog.close();
					mainWindow.getSidePanel().setStatus(INVALID_CSV, SidePanel.STATUS_TYPE_ERROR);
				} catch (IOException e) {
					progressDialog.close();
					mainWindow.showErrorMessage(ERROR, CANT_READ_FILE + "<html><br><br></html>" + e.getMessage());
				} catch (IllegalSeedException e) {
					progressDialog.close();
					mainWindow.getSidePanel().setStatus(INVALID_SEED, SidePanel.STATUS_TYPE_ERROR);
				} catch (IllegalClusterNumberException e) {
					progressDialog.close();
					mainWindow.getSidePanel().setStatus(INVALID_CLUSTER_NUMBER, SidePanel.STATUS_TYPE_ERROR);
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
		};
		
		backgroundThread = new Thread(thread);
		backgroundThread.start();
	}
	
	/**
	 * Saves the changes to the current file.
	 */
	public void saveCSV(){
		try {
			CSVWriter.write(dataSet, file);
			this.mainWindow.setStatusBarText(FILE_WRITTEN_TEXT);
			this.mainWindow.getSidePanel().setStatus(READY, SidePanel.STATUS_TYPE_READY);
			this.attributesInFirstLine = true;
			this.separator = ";";
			this.isModified = false;
			this.mainWindow.updateWindowTitle(false, this.file.getName());
		} catch (IOException e) {
			this.mainWindow.showErrorMessage(ERROR, WRITE_ERROR);
		}
	}
	
	/**
	 * Opens a file browser where the user can set a file location.
	 * Saves the changed data set as a CSV file to the selected destination.
	 * Changes the currently selected file to the newly saved file.
	 */
	public void saveCSVAs(){
		File destination = mainWindow.selectSavePath("Save As...", 
				"data_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".csv");
		if(destination == null) return;
		try {
			CSVWriter.write(dataSet, destination);
			this.mainWindow.setStatusBarText(FILE_WRITTEN_TEXT);
			this.mainWindow.getSidePanel().setStatus(READY, SidePanel.STATUS_TYPE_READY);
			this.file = destination;
			this.attributesInFirstLine = true;
			this.separator = ";";
			this.isModified = false;
			this.mainWindow.updateWindowTitle(false, destination.getName());
		} catch (IOException e) {
			this.mainWindow.showErrorMessage(ERROR, "IO error while saving CSV file.");
		}
	}
	
	public void setMainWindow(MainWindow mainWindow){
		this.mainWindow = mainWindow;
	}
	
	/**
	 * Sets the progress on the progress dialog.
	 * @param iterations
	 * 		iterations already done
	 * @param maxIterations
	 * 		maximum number of iterations set by the user
	 */
	public void setProgress(int iterations, int maxIterations){
		progressDialog.setProgress(iterations, maxIterations);
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
	 * Displays the result of a clustering algorithm on the visualizer.
	 * @param result
	 * 		the result achieved from the clustering algo
	 * @param k
	 * 		the number of clusters set by the user
	 */
	public void showClusteringResult(int[] result, int k){
		visualizer = new DataSetVisualizer(dataSet, mainWindow.getSizeForCanvas());
		visualizer.setMouseMode(mainWindow.getMouseMode());
		if(!shouldStop){
			mainWindow.setGraphDrawingComponent(visualizer.getCanvas());
			Controller.this.k = k;
			Controller.this.clusterResult = result;
			visualizer.showClusteringResult(result, k);
			mainWindow.enableSave();
			isModified = false;
			mainWindow.updateWindowTitle(false, file.getName());
			mainWindow.getSidePanel().setStatus(READY, SidePanel.STATUS_TYPE_READY);
			mainWindow.setStatusBarText(CLUSTERING_FINISHED);
		} else {
			mainWindow.getSidePanel().setStatus(READY, SidePanel.STATUS_TYPE_READY);
			mainWindow.setStatusBarText(CLUSTERING_CANCELLED);
		}
	}
	
	/**
	 * Updates the size of the canvas.
	 * It is typically called upon resize of the main window.
	 */
	public void updateCanvasSize(){
		if (visualizer != null)
			visualizer.setSize(mainWindow.getSizeForCanvas());
	}
	
	public void zoomIn(){
		if(visualizer != null)
			visualizer.zoomIn();
	}
	
	public void zoomOut(){
		if(visualizer != null)
			visualizer.zoomOut();
	}
}
