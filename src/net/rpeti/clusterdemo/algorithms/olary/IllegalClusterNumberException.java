package net.rpeti.clusterdemo.algorithms.olary;

/**
 * Indicates that the number of clusters provided was illegal.
 */
public class IllegalClusterNumberException extends IllegalArgumentException {

	private static final long serialVersionUID = -6577419579230990428L;

	public IllegalClusterNumberException() {
	}

	public IllegalClusterNumberException(String s) {
		super(s);
	}

	public IllegalClusterNumberException(Throwable cause) {
		super(cause);
	}

	public IllegalClusterNumberException(String message, Throwable cause) {
		super(message, cause);
	}

}
