package net.rpeti.clusterdemo;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.rpeti.clusterdemo.algorithms.Algorithms;

/**
 * Gets event (like choosing a file to import) from MainWindow
 * instantiates the correct classes for the action, and run them.
 *
 */
public class Controller {
	
	private boolean attributesInFirstLine;
	private String separator;
	private File file;
	private Algorithms algo;
	
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
}
