package net.rpeti.clusterdemo;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.rpeti.clusterdemo.algorithms.Algorithms;
import net.rpeti.clusterdemo.algorithms.olary.OlaryAlgo;
import net.rpeti.clusterdemo.data.olary.OlaryDataSet;
import net.rpeti.clusterdemo.gui.MainWindow;
import net.rpeti.clusterdemo.input.CSVReader;

/**
 * Gets an event (like choosing a file to import) from MainWindow
 * instantiates the correct classes for the action, and run them.
 *
 */
public class Controller {
	
	private boolean attributesInFirstLine;
	private String separator;
	private File file;
	private Algorithms algo;
	private MainWindow mainWindow;
	
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
	
	public void runClustering(Algorithms algo){
		if(file == null){
			mainWindow.showErrorMessage("Error", "Please import data first.");
			return;
		}
		
		if(algo == Algorithms.OLARY){
			OlaryDataSet dataSet = new OlaryDataSet();
			CSVReader reader = new CSVReader(dataSet);
			try {
				reader.read(file, attributesInFirstLine, separator);
				OlaryAlgo algorithm = new OlaryAlgo(3, 2, 50, dataSet);
				algorithm.run();
				mainWindow.showMessage("Finished.", "Clustering finished.");
			} catch (IOException e) {
				mainWindow.showErrorMessage("I/O Error", "Couldn't read input file.\nPlease import file, and try again.");
				e.printStackTrace();
			}
		}
	}
}
