package servicios;

import org.apache.log4j.Logger;

public class PedidoYaCanceladoException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(PedidoYaCanceladoException.class);

	public PedidoYaCanceladoException(String msg) {
		super();		
		LOG.warn(msg);
	}
	
	public PedidoYaCanceladoException() {
		super();		
		LOG.warn("El pedido ya se encuentra cancelado.");
	}

}
