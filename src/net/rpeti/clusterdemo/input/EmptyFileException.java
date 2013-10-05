package net.rpeti.clusterdemo.input;

public class EmptyFileException extends InvalidFileException {

	private static final long serialVersionUID = 5171080505730077331L;
	
	public EmptyFileException(){
		super();
	}
	
	public EmptyFileException(String message){
		super(message);
	}

}
