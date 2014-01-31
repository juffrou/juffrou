package net.sf.juffrou.reflect.error;

public abstract class AbstractRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -270201609986216950L;

	public AbstractRuntimeException() {
		super();
	}

	public AbstractRuntimeException(String message, Throwable cause) {
		super(message + (cause != null ? "==>" + cause.getMessage() : ""), cause);
	}

	public AbstractRuntimeException(String message) {
		super(message);
	}

	public AbstractRuntimeException(Throwable cause) {
		super(cause);
	}

}
