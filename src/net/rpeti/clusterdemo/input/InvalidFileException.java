package net.rpeti.clusterdemo.input;

import java.io.IOException;

/**
 * Identifies that the input file couldn't be parsed as it contains
 * illegal, or badly formatted data.
 */
public class InvalidFileException extends IOException {
	
	private static final long serialVersionUID = -4171327617607816602L;
	
	public InvalidFileException(){
		super();
	}
	
	public InvalidFileException(String message){
		super(message);
	}

}
