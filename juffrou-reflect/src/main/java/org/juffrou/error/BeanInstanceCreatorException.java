package org.juffrou.error;

public class BeanInstanceCreatorException extends Exception {

	private static final long serialVersionUID = -3799453881689346682L;

	public BeanInstanceCreatorException(Throwable cause) {
		super(cause);
	}

	public BeanInstanceCreatorException(String message, Throwable cause) {
		super(message, cause);
	}
}
