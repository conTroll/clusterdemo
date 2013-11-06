package net.rpeti.clusterdemo.algorithms.olary;

/**
 * Identifies that the seed value provided was invalid.
 */
public class IllegalSeedException extends IllegalArgumentException {

	private static final long serialVersionUID = 5423105987670912941L;

	public IllegalSeedException() {
	}

	public IllegalSeedException(String arg0) {
		super(arg0);
	}

	public IllegalSeedException(Throwable arg0) {
		super(arg0);
	}

	public IllegalSeedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
