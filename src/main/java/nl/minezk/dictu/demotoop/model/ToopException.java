package nl.minezk.dictu.demotoop.model;

public class ToopException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4854169279572508163L;
	
	public ToopException(String message, Throwable t) {
		super(message, t);
	}
	
	public ToopException(String message) {
		super(message);
	}

}
