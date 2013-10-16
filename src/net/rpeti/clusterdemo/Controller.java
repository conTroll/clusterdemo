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
import net.rpeti.clusterdemo.input.CSVReader;
import net.rpeti.clusterdemo.input.EmptyFileException;
import net.rpeti.clusterdemo.input.InvalidFileException;

/**
 * Gets an event (like choosing a file to import) from MainWindow
 * instantiates the correct classes for the action, and run them.
 *
 */
public class Controller {
	
	//TODO prepare for other algorithms
	//TODO set status bar messages on main window
	
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
					mainWindow.showErrorMessage("Error", "Please import data first.");
					return;
				}

				if(algo == Algorithms.OLARY){
					
					OlaryDataSet dataSet = new OlaryDataSet();
					CSVReader reader = new CSVReader(dataSet);
					try {
						reader.read(file, attributesInFirstLine, separator);
						OlaryAlgo algorithm = new OlaryAlgo(k, seed, maxIterations, dataSet);
						algorithm.setController(Controller.this);
						algorithm.run();
						progressDialog.close();
						if(!shouldStop) mainWindow.showMessage("Finished.", "Clustering finished.");
					} catch (EmptyFileException e){
						progressDialog.close();
						mainWindow.showErrorMessage("Empty file.", "You provided an empty file.\nPlease select a valid file.");
					} catch (InvalidFileException e){
						progressDialog.close();
						mainWindow.showErrorMessage("Invalid file provided.", "You provided an invalid CSV file.");
					} catch (IOException e) {
						progressDialog.close();
						mainWindow.showErrorMessage("I/O Error", 
								"Couldn't read input file.\nPlease import file, and try again.");
						e.printStackTrace(); //TODO
					} catch (IllegalArgumentException e){
						progressDialog.close();
						mainWindow.showErrorMessage("Error processing file.", 
								"An error has occurred processing the file.\n"
								+ "The CSV file is badly formatted (" + e.getMessage() + ")");
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
	}
	
	public void setProgress(int value){
		progressDialog.setProgress(value);
	}
}
