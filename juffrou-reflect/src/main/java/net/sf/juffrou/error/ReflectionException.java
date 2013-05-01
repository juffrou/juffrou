package net.sf.juffrou.error;

public class ReflectionException extends AbstractRuntimeException {

	private static final long serialVersionUID = 1671718962449842289L;

	public ReflectionException(String message) {
		super(message);
	}

	public ReflectionException(Throwable reason) {
		super(reason.getClass().getSimpleName(), reason);
	}

	public ReflectionException(String message, Throwable reason) {
		super(message, reason);
	}

}
