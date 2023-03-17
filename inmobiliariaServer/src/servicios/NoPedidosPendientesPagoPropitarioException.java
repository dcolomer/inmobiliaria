package servicios;

import org.apache.log4j.Logger;

public class NoPedidosPendientesPagoPropitarioException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(NoPedidosPendientesPagoPropitarioException.class);
	
	public NoPedidosPendientesPagoPropitarioException(String msg) {
		LOG.warn(msg);
	}
	
	public NoPedidosPendientesPagoPropitarioException() {
		LOG.warn("No existen pedidos pendientes de pagar al propietario especificado.");
	}

}
