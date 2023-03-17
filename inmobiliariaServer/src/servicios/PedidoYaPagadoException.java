package servicios;

import org.apache.log4j.Logger;

public class PedidoYaPagadoException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(PedidoYaPagadoException.class);
	
	public PedidoYaPagadoException(String msg) {
		super();		
		LOG.warn(msg);
	}
	
	public PedidoYaPagadoException() {
		super();		
		LOG.warn("El pedido ya se encuentra pagado.");
	}

}
