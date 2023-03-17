package dao;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.dbcp.*;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Clase Singleton que crea el DataSource y 
 * el pool de conexiones
 */
public class GestorPersistencia {
	
	private static final Logger LOG=Logger.getLogger(GestorPersistencia.class);
	
	// DataSource
	private static DataSource dataSource;	
	
	// Pool de conexiones cacheado
	private static GenericObjectPool genPool;

	/**
	 * La primera vez ejecutamos el constructor pasandole
	 * los datos de conexion obtenidos de la clase Configuracion.
	 * 
	 * El método lo hacemos sincronizado para prevenir errores 
	 * con otros threads, ya que podría suceder que dos hilos
	 * lo ejecutasen a la vez y se creasen dos instancias de 
	 * esta clase.
	 */
	public static synchronized DataSource getDataSource() {		 
		if (dataSource==null) {
			new GestorPersistencia(Configuracion.getConfiguracion());
			if (LOG.isDebugEnabled())
				LOG.debug("OBJETO GestorPersistencia CONSTRUIDO");
		}	
		return dataSource;
	}
	

	/*
	 * Constructor PRIVADO
	 */
	private GestorPersistencia(Configuracion config) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Conectando a la base de datos...");
		
		try {
			dataSource = setupDataSource(config);
			if (LOG.isDebugEnabled())
				LOG.debug("Conexion establecida.");
		} catch (Exception e) {
			LOG.error("Error al conectar a la base de datos", e);
			LOG.error("La aplicacion no puede continuar.");
			System.exit(1);
		}		
	}
	
	
	/**
	 * Metodo que realmente crea el DataSource	 
	 */
	public static DataSource setupDataSource(Configuracion config)
			throws Exception {

		// Creamos la cache para las conexiones		
		GenericObjectPool connectionPool = new GenericObjectPool(null);

		connectionPool.setMinIdle(config.getDbPoolMinSize());
		connectionPool.setMaxActive(config.getDbPoolMaxSize());
		
		// Lo guardamos en el atributo estatico de la clase
		GestorPersistencia.genPool = connectionPool;
		
		/*
		 * Creamos la factoria de conexiones que el pool
		 * usara para dar conexiones a los clientes
		 */
		ConnectionFactory connectionFactory = 
			new DriverManagerConnectionFactory(
					config.getDbURI(), config.getDbUser(), config.getDbPassword());

		/*
		 * Ahora que ya tenemos un connectioPool y una connectionFactory
		 * podemos crear la factoria de conexiones cacheables. Esta clase
		 * envuelve las conexiones fisicas creadas por la connectionFactory
		 * con las clases que implementan la funcionad cache.
		 */
		new PoolableConnectionFactory(
				connectionFactory, connectionPool, null, null, false, true);

		return new PoolingDataSource(connectionPool);
	}

	/**
	 * Mostrar estado de las conexiones
	 */
	public static void printDriverStats() throws Exception {		
		if (LOG.isDebugEnabled()) {
			ObjectPool connectionPool = GestorPersistencia.genPool;
			LOG.debug("Pool: con. activas: " + connectionPool.getNumActive()+ 
				" con. ociosas: " + connectionPool.getNumIdle());
		}	
	}

	/**
	 * Mostrar el numero de procesos bloqueados
	 */
	public static void printNumLockedProcesses() {
		int num_locked_connections = 0;
		Connection con = null;
		PreparedStatement p_stmt = null;
		ResultSet rs = null;
		try {
			con = GestorPersistencia.dataSource.getConnection();
			p_stmt = con.prepareStatement("SHOW PROCESSLIST");
			rs = p_stmt.executeQuery();
			while (rs.next()) {
				if (rs.getString("State") != null
						&& rs.getString("State").equalsIgnoreCase("Locked")) {
					num_locked_connections++;
					LOG.warn("Procesos bloquedos: " + num_locked_connections);
				}
			}
		} catch (Exception e) {
			LOG.error("Error al obtener las conexiones bloqueadas - Excepcion: "
					+ e.toString());
		} finally {
			try {
				rs.close();
				p_stmt.close();
				con.close();
			} catch (java.sql.SQLException ex) {
				LOG.error(ex.toString());
			}
		}		
	}
	

	/**
	 * Evitar el clonado: un Singleton no debe poder clonarse
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
}
