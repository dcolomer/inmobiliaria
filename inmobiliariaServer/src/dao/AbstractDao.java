package dao;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.Timer;

import javax.sql.DataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

/**
 * Clase que contiene la informacion y la 
 * funcionalidad comun para cualquier clase Dao.
 */
public abstract class AbstractDao {
	
	// DataSource
	private static DataSource dataSource;
	
	// Para aquellos Dao que requieren comportamiento transaccional
	protected GestorTransacciones gestorTran;		
	
	// Hacemos que el LOG esté disponible en todas las subclases
	protected final Logger LOG = Logger.getLogger(getClass().getName());
	
	// Intervalo en segundos que queremos lanzar el timer
	// que comprueba el estado del pool de conexiones y el
	// numero de procesos bloqueados
	private static final int CADENCIA=60000;
	
	/**
	 * El primer objeto DAO que se instancie creará
	 * la conexión con la BD. Los siguientes ya 
	 * obtendrán el dataSource inicializado.
	 */
	static {		
		dataSource=GestorPersistencia.getDataSource();
		
		// Cada n segundos registrar en el LOG el estado del pool 
		// de conexiones y los procesos bloqueados
		
		lanzarTimer(CADENCIA);
	}
	
	/**
	 * Constructores
	 */
	protected AbstractDao() {
		
	}
	
	/**
	 * Si algún DAO invoca a este constructor es porque
	 * requiere control transaccional, como es el caso
	 * de la clase DaoPagosImpl.
	 * @throws DaoException 
	 */
	protected AbstractDao(DaoConstantes modoTransacciones) {
		if (modoTransacciones==DaoConstantes.TransaccionesON) 
			gestorTran=GestorTransaccionesImpl.getGestorTransacciones();		
	}
	
	/**
	 * Devuelve el dataSource, el cual ya fue inicializado 
	 * en el inicializador estático de la clase
	 */
	protected static DataSource getDataSource() {
		return dataSource;
	}
	
	/* *********************************************************
	 * Cierre de los recursos JDBC
	 * ********************************************************/
	 	
	/**
	 * Cerrar un objeto Statement.
	 * Si el objeto es null entonces evita el intento de cierre.
	 * Oculta cualquier SQLException que puediera ocurrir.
	 */
	protected void closeQuiet(Statement stmt) {		
		DbUtils.closeQuietly(stmt);
	}
	
	/**
	 * Cerrar un objeto ResultSet.
	 * Si el objeto es null entonces evita el intento de cierre.
	 * Oculta cualquier SQLException que puediera ocurrir.
	 */
	protected void closeQuiet(ResultSet rs) {		
		DbUtils.closeQuietly(rs);
	}
	
	/*
	 * Cierre de objetos Connection
	 */
		
	/**
	 * Cerrar un objeto Connection.
	 * Si el objeto es null entonces evita el intento de cierre.
	 * Oculta cualquier SQLException que puediera ocurrir.
	 */		
	protected void closeQuiet(Connection conn) {		
		DbUtils.closeQuietly(conn);		
	}
	

	/**
	 * Metodo que permite cerrar ordenadamente la jerarquia de recursos JDBC abiertos
	 */
	protected void closeQuiet(Connection conn, Statement stmt, ResultSet rs) {		
			closeQuiet(rs);
			closeQuiet(stmt);
			closeQuiet(conn);
	}
	
	/**
	 * Registrar la exception SQLException en el LOG.
	 * Envuelve una SQLException dentro de una DaoException
	 * y la lanza a la capa de negocio. Esto permite que la
	 * capa de negocio no se acople a la tecnologia subyacente
	 * de acceso a datos.
	 */
	protected void relanzarExcepcion(String msg, Exception e) throws DaoException {
		LOG.error(msg,null);						
		throw new DaoException(e);
	}
	
		
	/**
	 * Cada N segundos mostrar el estado del pool de conexiones
	 * y el numero de procesos bloqueados. 
	 */
	private static void lanzarTimer(int cadencia) {		
		if (dataSource!=null) {
			Timer t = new Timer(cadencia, new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						GestorPersistencia.printDriverStats();
						GestorPersistencia.printNumLockedProcesses();
					} catch (Exception e) {
						final Logger LOG = Logger.getLogger(getClass().getName());
						LOG.warn(e);
					}
				}
			});
			t.start();
		}		
	}
	
	
	/**
	 * Extension de QueryRunner para poder obtener la clave primaria
	 * devuelta por el SGBD al hacer un insert.
	 */
	protected class QueryRunnerGeneratedKeys extends QueryRunner {		
		ResultSetHandler<?> rsSh;
		
		public QueryRunnerGeneratedKeys(ResultSetHandler<?> rsSh) {			
			this.rsSh=rsSh;
		}
		
		@Override
		public int update(Connection conn, String sql, Object... params) throws SQLException {
		   return update(conn, sql, rsSh, params);
		}
		
		public int update(Connection conn, String sql, ResultSetHandler<?> rsh,
				Object... params) throws SQLException {
			
			PreparedStatement stmt = null;
			int rows = 0;

			try {
				stmt = this.prepareStatement(conn, sql, 
			    		rsh==null?Statement.NO_GENERATED_KEYS:Statement.RETURN_GENERATED_KEYS);
			    this.fillStatement(stmt, params);
			    rows = stmt.executeUpdate();

			    if(rsh!=null) {			
			    	Long l= (Long) rsh.handle(stmt.getGeneratedKeys());
			    	if (l<Integer.MAX_VALUE)
			    		return (int) l.intValue();
			    }

			  } catch (SQLException e) {
			     this.rethrow(e, sql, params);
			  } finally {
			      close(stmt);
			  }
			 
			 return rows;
			}
		
		protected PreparedStatement prepareStatement(Connection conn, String sql, int autoGeneratedKeys) throws SQLException {
		   return conn.prepareStatement(sql, autoGeneratedKeys);
		}
	}			
}
