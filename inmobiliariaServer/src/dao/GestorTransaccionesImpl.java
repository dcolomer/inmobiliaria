package dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Clase Singleton
 * Implementacion de un Delegado transaccional para JDBC
 */
public class GestorTransaccionesImpl implements GestorTransacciones {

	private static GestorTransacciones gestorTransacciones;
	private Connection conexion;
	private boolean oldStateOfAutoCommit;	
	private int oldStateOfTransactionIsolation, stateOfTransactionIsolation;
	
	private static final Logger LOG=Logger.getLogger(GestorTransaccionesImpl.class);

	/**
	 * Constructores
	 */
	
	private GestorTransaccionesImpl() {
		this(Connection.TRANSACTION_READ_COMMITTED);
	}

	private GestorTransaccionesImpl(int isolation) {		
		stateOfTransactionIsolation=isolation;
		
		if (LOG.isDebugEnabled())
			LOG.debug("OBJETO GestorTransaccionesImpl CONSTRUIDO");		
	}

	/**
	 * Metodo sincronizado para evitar que otro
	 * hilo pueda simultaneamente llamar al constructor privado
	 * 
	 * La primera vez ejecutamos el constructor pasandole
	 * los datos de conexion obtenidos de la clase Configuracion.
	 * 
	 * El método lo hacemos sincronizado para prevenir errores 
	 * con otros threads, ya que podría suceder que dos hilos
	 * lo ejecutasen a la vez y se creasen dos instancias de 
	 * esta clase.
	 */
	public static synchronized GestorTransacciones 
		getGestorTransacciones() {
		
		if (gestorTransacciones==null) 
			gestorTransacciones=new GestorTransaccionesImpl();
			
		return gestorTransacciones;
	}
	
	public static synchronized GestorTransacciones 
		getGestorTransacciones(int isolation) {
		
		if (gestorTransacciones==null) 
			gestorTransacciones=new GestorTransaccionesImpl(isolation);
			
		return gestorTransacciones;
	}
	
	public void start() {
		try {
			conexion=AbstractDao.getDataSource().getConnection();
			if (conexion.getAutoCommit()) {
				// En caso que la conexion se confirme de forma
				// automatica, guardamos el estado anterior e
				// indicamos que no deseamos el commit automatico.
				conexion.setAutoCommit(false);
				oldStateOfAutoCommit = true;
			}
			oldStateOfTransactionIsolation=
				conexion.getTransactionIsolation();
			
			conexion.setTransactionIsolation(stateOfTransactionIsolation);
			
		} catch (SQLException e) {
			String msg="Se ha producido un error en el metodo 'start()'.";
			LOG.error(msg,e);							
		}
	}
	
	public void commit() {
		try {
			conexion.commit();
		} catch (SQLException e) {
			String msg="Se ha producido un error en el metodo 'commit()'.";
			LOG.error(msg,e);	
		}
	}

	public void end() {
		try {
			conexion.setAutoCommit(oldStateOfAutoCommit);
			conexion.setTransactionIsolation(oldStateOfTransactionIsolation);
			
			if (!conexion.isClosed())
				conexion.close();			
			
		} catch (SQLException e) {
			String msg="Se ha producido un error en el metodo 'end()'.";
			LOG.error(msg,e);
		}
	}

	public void rollback() {
		try {
			conexion.rollback();
		} catch (SQLException e) {
			String msg="Se ha producido un error en el metodo 'rollback()'.";
			LOG.error(msg,e);
		}
	}

	
	/**
	 * Evitar el clonado: un Singleton no debe poder clonarse
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}