package net.sf.juffrou.reflect.error;

public class CannotWrapInterfaceException extends AbstractRuntimeException {

	private static final long serialVersionUID = 8278315532746800212L;

	public CannotWrapInterfaceException() {
		super();
	}

	public CannotWrapInterfaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public CannotWrapInterfaceException(String message) {
		super(message);
	}

	public CannotWrapInterfaceException(Throwable cause) {
		super(cause);
	}

	
}
