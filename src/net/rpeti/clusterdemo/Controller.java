package net.rpeti.clusterdemo;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.rpeti.clusterdemo.algorithms.Algorithms;
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
	private static final String FINISHED = "Finished.";
	private static final String CANT_READ_FILE = "Couldn't read input file." + NEWLINE + "Please import file, and try again.";
	private static final String INVALID_CSV = "You provided an invalid CSV file.";
	private static final String ERROR = "Error";
	private static final String CSV_SELECTED = "CSV data file has been selected. You can now run the algorithm.";
	private static final String CLUSTERING_FINISHED = "Clustering finished.";
	private static final String CLUSTERING_CANCELLED = "Clustering cancelled.";

	private boolean attributesInFirstLine;
	private String separator;
	private File file;
	private MainWindow mainWindow;
	private Thread backgroundThread;
	private ClusteringProgress progressDialog;
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
		mainWindow.getFrame().setEnabled(false);
		
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
						DataSetVisualizer visualizer = new DataSetVisualizer(dataSet);
						OlaryAlgo algorithm = new OlaryAlgo(k, seed, maxIterations, dataSet);
						algorithm.setController(Controller.this);
						algorithm.run();
						progressDialog.close();
						if(!shouldStop){
							mainWindow.setStatusBarText(CLUSTERING_FINISHED);
							visualizer.showClusteringResult(algorithm.getResult(), k);
							mainWindow.setGraphDrawingComponent(visualizer.getCanvas());
							mainWindow.showMessage(FINISHED, CLUSTERING_FINISHED);
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
					} catch (IllegalArgumentException e){
						progressDialog.close();
						mainWindow.showErrorMessage(ERROR, BAD_FORMATTING + e.getMessage());
					}
				}
			}
		};
		
		backgroundThread = new Thread(thread);
		backgroundThread.start();
	}
	
	public boolean shouldStop(){
		return shouldStop;
	}
	
	public void cancelClustering(){
		shouldStop = true;
		mainWindow.setStatusBarText(CLUSTERING_CANCELLED);
	}
	
	public void setProgress(int value){
		progressDialog.setProgress(value);
	}
}
