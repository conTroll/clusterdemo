package net.rpeti.clusterdemo.input;

import java.io.IOException;

public class InvalidFileException extends IOException {
	
	private static final long serialVersionUID = -4171327617607816602L;
	
	public InvalidFileException(){
		super();
	}
	
	public InvalidFileException(String message){
		super(message);
	}

}
