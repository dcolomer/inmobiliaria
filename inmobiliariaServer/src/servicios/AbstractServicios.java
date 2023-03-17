package servicios;

import org.apache.log4j.Logger;

import dao.DaoInmobiliaria;
import dao.DaoInmobiliariaImpl;

public abstract class AbstractServicios {

	// Hacemos que el LOG esté disponible en todas las subclases
	protected final Logger LOG = Logger.getLogger(getClass().getName());
	
	protected static final DaoInmobiliaria daoInmobiliaria=
		new DaoInmobiliariaImpl();
	
	protected static final int OPERACION_OK=1;		
	
	private static final String msgDaoException="Se ha recibido un error desde la capa de Acceso a Datos. La causa es:";
	
	protected void relanzarExcepcion(Exception e) throws ServiciosException {		
		LOG.error(msgDaoException,e);
		throw new ServiciosException(e);
	}
	
	protected void relanzarExcepcion(String msg, Exception e) throws ServiciosException {		
		LOG.error(msg,e);
		throw new ServiciosException(e);
	}
}
