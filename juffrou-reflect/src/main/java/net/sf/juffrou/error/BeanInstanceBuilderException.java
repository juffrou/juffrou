package net.sf.juffrou.error;

public class BeanInstanceBuilderException extends Exception {

	private static final long serialVersionUID = -3799453881689346682L;

	public BeanInstanceBuilderException(Throwable cause) {
		super(cause);
	}

	public BeanInstanceBuilderException(String message, Throwable cause) {
		super(message, cause);
	}
}
