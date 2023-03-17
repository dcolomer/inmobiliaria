package servicios;

public class ServiciosException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiciosException() {
		super();
	}

	public ServiciosException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ServiciosException(Throwable cause) {
		super(null,cause);		
	}

	public ServiciosException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
