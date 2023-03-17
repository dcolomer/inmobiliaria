package servicios;

import org.apache.log4j.Logger;

public class CajaYaCerradaException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(CajaYaCerradaException.class);
	
	public CajaYaCerradaException() {
		LOG.warn("La caja ya esta cerrada.");
	}


}
