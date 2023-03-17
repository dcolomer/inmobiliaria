package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import entidades.Cliente;
import entidades.Piso;
import entidades.Propietario;
import entidades.Usuario;

/**
 * Clase Singleton que implementa mediante JDBC los métodos de acceso a datos
 * relacionados con operaciones de gestion (en nuestro caso, se trata 
 * del mantenimiento de las entidades).
 * 
 *  NOTA IMPORTANTE: Esta clase esta infrautilizada desde el punto de
 *  vista de la aplicacion cliente Swing, ya que ésta utiliza 
 *  'ResultSets updatables' para realizar las tareas de mantenimiento 
 *  de las entidades, en lugar de usar esta clase DAO. La causa de ello
 *  es puramente academica, es decir, para ver dos maneras diferentes
 *  de hacer las cosas. No obstante, el uso de RS updatables no sigue
 *  un buen diseño, que permita separar claramente responsabilidades
 *  dentro de una aplicacion con una arquitectura por capas (¡estamos
 *  acoplando las clases cliente a la capa de integracion!) 
 */
public class DaoGestionImpl extends AbstractDao implements DaoGestion {
	
	private static DaoGestion daoGestion;
	
	private DaoGestionImpl() {		
		if (LOG.isDebugEnabled())
			LOG.debug("OBJETO DaoGestionImpl CONSTRUIDO");
	}

	/**
	 * Metodo de factoria estatico sincronizado para evitar que otro
	 * hilo pueda simultaneamente llamar al constructor privado
	 */
	public static synchronized DaoGestion getDaoGestion() {
		if (daoGestion==null) 
			daoGestion=new DaoGestionImpl();
			
		return daoGestion;
	}
	
	// **********************************************************************
	// ************************** Cliente ***********************************
	// **********************************************************************
	
	/*
	 * Buscamos al cliente a partir de su NIF. Si el cliente existe 
	 * lo modificamos y si no existe lo damos de alta
	 */
	@Override
	public int grabarCliente(Cliente cliente) throws DaoException {
			
		Cliente clienteRecuperado=getCliente(cliente.getNif_cli());
		
		if (clienteRecuperado!=null && clienteRecuperado.equals(cliente)) 
			return modificarCliente(cliente);			
		else
			return nuevoCliente(cliente);
	}
	
	/*
	 * Eliminar un cliente de la BD a partir del NIF
	 */
	@Override
	public int eliminarCliente(String nif) throws DaoException {
		Connection conn=null;
		int eliminadas=0;
		String SQL_ELIMINAR_CLIENTE="DELETE FROM CLI WHERE nif_cli=?";
		
		QueryRunner run = new QueryRunner();
		
		try	{			     
			conn = getDataSource().getConnection();
		    eliminadas = run.update(conn, SQL_ELIMINAR_CLIENTE, nif);
		    
		    if (LOG.isDebugEnabled()) {
		    	if (eliminadas>0) 
		    		LOG.debug("Cliente eliminado (NIF): "+nif );
		    	else	
		    		LOG.debug("NO se ha eliminado ningun cliente para el NIF: "+nif );
		    }		
		    
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.eliminarCliente()'. Parametro: String nif="+nif;
			relanzarExcepcion(msg,e);									 
		} finally {
			closeQuiet(conn);
		}
		
		return eliminadas;
	}
	
		
	
	/*
	 * Recuperar un cliente de la BD a partir del NIF
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Cliente getCliente(String nif) throws DaoException {
		Connection conn=null;		
		Cliente cliente=null;
		String SQL_CLIENTE_POR_NIF="SELECT * FROM CLI WHERE nif_cli=?";		
		
        QueryRunner qr = new QueryRunner();
        Object[] params = new Object[]{nif};
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Cliente> rsh = new BeanHandler(Cliente.class);
        
        try {			        	
        	
        	if (params[0]!=null) { // Si se ha pasado el NIF al metodo
        		conn = getDataSource().getConnection();
        		cliente=qr.query(conn, SQL_CLIENTE_POR_NIF, rsh, params);        		
        	}
        	
        	if (LOG.isDebugEnabled()) {
 				if (cliente!=null)
 					LOG.debug("Cliente recuperado desde NIF: "+nif);
 				else
 					LOG.debug("No se ha recuperado ningun cliente para el NIF: "+nif);
        	 }	
        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.getCliente()'. Parametro: String nif="+nif;					
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return cliente;		
	}
	
	
	/*
	 * Recuperar todos los clientes de la BD 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> getClientes() throws DaoException {
		Connection conn=null;
		String SQL_CLIENTES="SELECT * FROM CLI";		
		List<Cliente> lista=null;
		
        QueryRunner qr = new QueryRunner();
                
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Cliente> rsh = new BeanListHandler(Cliente.class);
        
        try {						        
        	conn = getDataSource().getConnection();
        	lista=(List<Cliente>)qr.query(conn, SQL_CLIENTES, rsh);
        	
        	if (LOG.isDebugEnabled()) {        	
        		if (lista!=null && !lista.isEmpty())
        			LOG.debug("Lista de clientes recuperada.");
        		else
        			LOG.debug("Lista de clientes vacia.");
        	}
        	
		} catch (SQLException e) {			
			String msg="Error en el metodo 'DaoGestionImpl.getClientes()'.";
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<Cliente>(lista);
		
	}
	
	
	/*
	 * método privado para dar de alta un Cliente en la BD
	 */
	private int nuevoCliente(Cliente cliente) throws DaoException {
		Connection conn=null;
		int insertadas=0;
		String SQL_NUEVO_CLIENTE="INSERT INTO CLI (nif_cli, nombre, apel) VALUES (?,?,?)";
		
		QueryRunner run = new QueryRunner();
		
		try	{
			 /*
			  * Ejecutar la instrucci�n de actualizacion y retornar
			  * el n�mero de filas afectadas
			  * 
			  *  Importante: En el metodo update() el segundo paremetro
			  *  es un varargs (y utiliza autoboxing si se requiere) por
			  *  lo que siempre podremos especificar el numero de campos
			  *  que necesitemos
			  *  
			  */
					    
			conn = getDataSource().getConnection();
			insertadas = run.update(conn, SQL_NUEVO_CLIENTE, 
		    		cliente.getNif_cli(), cliente.getNombre(), cliente.getApel());

		    if (LOG.isDebugEnabled()) {
		    	if (insertadas>0)
		    		LOG.debug("Cliente insertado: "+cliente );
		    	else
		    		LOG.debug("Cliente NO insertado: "+cliente );
		    }		    	
		   
		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.nuevoCliente()'. Parametro: Cliente cliente="+cliente;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);;
		}
		
		return insertadas;
	}

	
	/*
	 * método privado para modificar un Cliente en la BD
	 */
	private int modificarCliente(Cliente cliente) throws DaoException {
		Connection conn=null;
		int actualizadas=0;
		String SQL_MODIFICAR_CLIENTE="UPDATE CLI SET nombre=?, apel=? WHERE nif_cli=?";
		
		QueryRunner run = new QueryRunner();
		
		try	{			     
			conn = getDataSource().getConnection();
		    actualizadas = run.update(conn, SQL_MODIFICAR_CLIENTE, 
		    		cliente.getNombre(), cliente.getApel(), cliente.getNif_cli());
		    
		    if (LOG.isDebugEnabled()) {
		    	if (actualizadas>0)
		    		LOG.debug("cliente modificado: "+cliente );
		    	else
		    		LOG.debug("cliente NO modificado: "+cliente );
		    }
		    
		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.modificarCliente()'. Parametro: Cliente cliente="+cliente;
			relanzarExcepcion(msg,e);						 
		} finally {
			closeQuiet(conn);
		}
		
		return actualizadas;
	}
	
	
	
	// **********************************************************************
	// ************************** Propietario *******************************
	// **********************************************************************
	
	
	/*
	 * Si el propietario existe lo modificamos.
	 * Si no existe lo damos de alta
	 */
	@Override
	public int grabarPropietario(Propietario propietario) throws DaoException {
			
		Propietario propietarioRecuperado=getPropietario(propietario.getNif_prop());
		
		if (propietarioRecuperado!=null && propietarioRecuperado.equals(propietario)) 
			return modificarPropietario(propietario);			
		else
			return nuevoPropietario(propietario);
	}
	
	/*
	 * Elimina un propietario de la BD a partir del NIF
	 */
	@Override
	public int eliminarPropietario(String nif) throws DaoException {
		Connection conn=null;
		int eliminadas=0;
		String SQL_ELIMINAR_PROPIETARIO="DELETE FROM PROP WHERE nif_prop=?";
		
		QueryRunner run = new QueryRunner();
		
		try	{			     
			conn = getDataSource().getConnection();
			eliminadas = run.update(conn, SQL_ELIMINAR_PROPIETARIO, nif);
		    		    
		    if (LOG.isDebugEnabled()) {
		    	if (eliminadas>0) 
		    		LOG.debug("Propietario eliminado (NIF): "+nif );
		    	else	
		    		LOG.debug("NO se ha eliminado ningun propietario para el NIF: "+nif );
		    }
		    
		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.eliminarPropietario)'. Parametro: String nif="+nif;
			relanzarExcepcion(msg,e);			
		} finally {
			closeQuiet(conn);
		}
		
		return eliminadas;
	}
	
	/*
	 * Recupera un propietario de la BD a partir del NIF
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Propietario getPropietario(String nif) throws DaoException {
		Connection conn=null;		
		Propietario propietario=null;
		String SQL_PROPIETARIO_POR_NIF="SELECT * FROM PROP WHERE nif_prop=?";		
		
        QueryRunner qr = new QueryRunner();
        Object[] params = new Object[]{nif};
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Propietario> rsh = new BeanHandler(Propietario.class);
        
        try {						
        	if (params[0]!=null) { // Si se ha pasado el NIF al método
        		conn = getDataSource().getConnection();
        		propietario=qr.query(conn, SQL_PROPIETARIO_POR_NIF, rsh, params);        		
        	}
        	if (LOG.isDebugEnabled()) {
        		if (propietario!=null)
        			LOG.debug("Propietario recuperado desde NIF: "+nif);
        		else
        			LOG.debug("No se ha recuperado ningun propietario para el NIF: "+nif);
        	}
        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.getPropietario()'. Parametro: String nif="+nif;					
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return propietario;		
	}
	
	
	/*
	 * Recupera todos los propietarios de la BD 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Propietario> getPropietarios() throws DaoException {
		Connection conn=null;
		String SQL_PROPIETARIOS="SELECT * FROM PROP";		
		List<Propietario> lista=null;
		
        QueryRunner qr = new QueryRunner();        
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Propietario> rsh = new BeanListHandler(Propietario.class);
        
        try {			
        	conn = getDataSource().getConnection();        	
        	lista=(List<Propietario>)qr.query(conn, SQL_PROPIETARIOS, rsh);
        	
        	if (LOG.isDebugEnabled()) {        	
        		if (lista!=null && !lista.isEmpty())
        			LOG.debug("Lista de propietarios recuperada.");
        		else
        			LOG.debug("Lista de propietarios vacia.");
        	}
        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.getPropietarios()'.";
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<Propietario>(lista);
		
	}
	
	
	/*
	 * metodo privado para dar de alta un Propietario en la BD
	 */
	private int nuevoPropietario(Propietario propietario) throws DaoException {
		Connection conn=null;
		int insertadas=0;		
		String SQL_NUEVO_PROPIETARIO="INSERT INTO PROP (nif_prop, nombre, apel, dir, loc) VALUES (?,?,?,?,?)";
		
		QueryRunner run = new QueryRunner();
		
		try	{			 					    
			conn = getDataSource().getConnection();
			insertadas = run.update(conn, SQL_NUEVO_PROPIETARIO, 
		    		propietario.getNif_prop(), propietario.getNombre(), propietario.getApel(),
		    		propietario.getDir(), propietario.getLoc());

		    if (LOG.isDebugEnabled()) {
		    	if (insertadas>0)
		    		LOG.debug("Propietario insertado: "+propietario );
		    	else
		    		LOG.debug("Propietario NO insertado: "+propietario );
		    }
		   
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.nuevoPropietario()'. Parametro: Propietario propietario="+propietario;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return insertadas;
	}

	
	/*
	 * metodo privado para modificar un Prop�etario en la BD
	 */
	private int modificarPropietario(Propietario propietario) throws DaoException {
		Connection conn=null;
		int actualizadas=0;
		String SQL_MODIFICAR_PROPIETARIO="UPDATE PROP SET nombre=?, apel=?, dir=?, loc=? WHERE nif_prop=?";
		
		QueryRunner run = new QueryRunner();
		
		try	{			     
			conn = getDataSource().getConnection();
		    actualizadas = run.update(conn, SQL_MODIFICAR_PROPIETARIO, 
		    		propietario.getNombre(), propietario.getApel(), propietario.getDir(), 
		    		propietario.getLoc(),propietario.getNif_prop());
		    
		    if (LOG.isDebugEnabled()) {
		    	if (actualizadas>0)
		    		LOG.debug("Propietario modificado: "+propietario );
		    	else
		    		LOG.debug("Propietario NO modificado: "+propietario );
		    }
		    
		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.modificarPropietario()'. Parametro: Propietario propietario="+propietario;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return actualizadas;
	}
	
	
	
	// **********************************************************************
	// ***************************** Piso ***********************************
	// **********************************************************************
	
	
	/*
	 * Si el piso existe lo modificamos.
	 * Si no existe lo damos de alta
	 */
	@Override
	public int grabarPiso(Piso piso) throws DaoException {
		Piso pisoRecuperado=getPiso(piso.getN_piso());
		
		if (pisoRecuperado!=null && pisoRecuperado.equals(piso)) 
			return modificarPiso(piso);			
		else
			return (nuevoPiso(piso)>0) ? 1 : 0;
	}
	
	/*
	 * Elimina un piso de la BD a partir del n_piso
	 */
	@Override
	public int eliminarPiso(long n_piso) throws DaoException {
		Connection conn=null;
		int eliminadas=0;
		String SQL_ELIMINAR_PISO="DELETE FROM PISO WHERE n_piso=?";
		
		QueryRunner run = new QueryRunner();
		
		try	{			     
			conn = getDataSource().getConnection();
			eliminadas = run.update(conn, SQL_ELIMINAR_PISO, n_piso);
		    
		    if (LOG.isDebugEnabled()) {
		    	if (eliminadas>0) 
		    		LOG.debug("Piso eliminado (n_piso): "+n_piso);
		    	else	
		    		LOG.debug("NO se ha eliminado ningun piso para el numero: "+n_piso);
		    }
		    
		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.eliminarPiso)'. Parametro: long n_piso="+n_piso;
			relanzarExcepcion(msg,e); 
		} finally {
			closeQuiet(conn);
		}
		
		return eliminadas;
	}
	
	/*
	 * Recupera un piso de la BD a partir del n_piso
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Piso getPiso(long n_piso) throws DaoException {
		Connection conn=null;		
		Piso piso=null;
		String SQL_PISO="SELECT * FROM PISO WHERE n_piso=?";		
		
        QueryRunner qr = new QueryRunner();
        Object[] params = new Object[]{n_piso};
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Piso> rsh = new BeanHandler(Piso.class);
        
        try {			
			
        	if (params[0]!=null) { // Si se ha pasado el n_piso diferente de null al metodo 
        		conn = getDataSource().getConnection();
        		piso=qr.query(conn, SQL_PISO, rsh, params);
        		
            	if (LOG.isDebugEnabled()) {
            		if (piso!=null)
            			LOG.debug("Piso recuperado desde n_piso: "+n_piso);
            		else
            			LOG.debug("No se ha recuperado ningun piso para el n_piso: "+n_piso);
            	}
        	} else {
        		if (LOG.isDebugEnabled())
        			LOG.debug("No se ha proporcionado un numero de piso para poder recuperarlo de la BD.");
        	}
        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.getPiso()'. Parametro: long n_piso="+n_piso;					
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return piso;		
	}
	
	
	/*
	 * Recupera todos los pisos de la BD 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Piso> getPisos() throws DaoException {
		Connection conn=null;
		String SQL_PISOS="SELECT * FROM PISO";		
		List<Piso> lista=null;
		
        QueryRunner qr = new QueryRunner();        
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Piso> rsh = new BeanListHandler(Piso.class);
        
        try {			
        	conn = getDataSource().getConnection();        	
        	lista=(List<Piso>)qr.query(conn, SQL_PISOS, rsh);
        	        	
        	if (LOG.isDebugEnabled()) {        	
        		if (lista!=null && !lista.isEmpty())
        			LOG.debug("Lista de pisos recuperada.");
        		else
        			LOG.debug("Lista de pisos vacia.");
        	}
        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.getPisos()'.";
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<Piso>(lista);
		
	}
	
	
	/*
	 * Recupera todos los pisos de un propietario de la BD 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Piso> getPisosPropietario(String nif) throws DaoException {
		Connection conn=null;
		String SQL_PISOS_PROPIETARIO="SELECT * FROM PISO WHERE nif_prop=?";		
		List<Piso> lista=null;
		
        QueryRunner qr = new QueryRunner();        
        Object[] params = new Object[]{nif};
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Piso> rsh = new BeanListHandler(Piso.class);
        
        try {			
			        	
        	if (params[0]!=null) {
        		conn = getDataSource().getConnection();
        		lista=(List<Piso>)qr.query(conn, SQL_PISOS_PROPIETARIO, rsh, params);
        		
        		if (LOG.isDebugEnabled()) {        	
            		if (lista!=null && !lista.isEmpty())
            			LOG.debug("Lista de pisos recuperada del propietario con NIF: "+nif);
            		else
            			LOG.debug("Lista de pisos del propietario vacia.");
            	}
        		
        	} else {
        		if (LOG.isDebugEnabled())
        			LOG.info("No se ha especificado el NIF del propietario. No se ha recuperado la lista de sus pisos.");
        	}
        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.getPisosPropietario()'.";
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<Piso>(lista);
		
	}
	
	
	/*
	 * metodo privado para dar de alta un Piso en la BD
	 * Atencion: Este metodo no retorna el numero de filas
	 * insertadas sino la clave primaria (el identificador que 
	 * el SGBD ha proporcionado al nuevo piso)
	 */
	private long nuevoPiso(Piso piso) throws DaoException {
		Connection conn=null;
		long pk=0;		
		String SQL_NUEVO_PISO="INSERT INTO PISO (dir, loc, piscina, nif_prop, precio, comision, foto) VALUES (?,?,?,?,?,?,?)";
				
		// Queremos que el resultado sea un escalar, no un bean,
		// ni tampoco una lista de beans
		final ResultSetHandler<?> rsSh=new ScalarHandler();
		
		// Definido como protegido en DaoGeneral
		QueryRunnerGeneratedKeys run=new QueryRunnerGeneratedKeys(rsSh);
		
		try	{			 
			conn = getDataSource().getConnection();		    
			pk = run.update(conn, SQL_NUEVO_PISO, 
		    		piso.getDir(), piso.getLoc(), piso.isPiscina(),
		    		piso.getNif_prop(), piso.getPrecio(), piso.getComision(),
		    		piso.getFoto());

			// Actualizamos el bean con la clave (n_piso)
			piso.setN_piso(pk); 
			
		    if (LOG.isDebugEnabled()) 
		    	LOG.debug("Piso insertado: "+piso);
		    		   
		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.nuevoPiso()'. Parametro: Piso piso="+piso;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return pk;
	}

	
	/*
	 * metodo privado para modificar un Piso en la BD
	 */
	private int modificarPiso(Piso piso) throws DaoException {
		Connection conn=null;
		int insertadas=0;
		
		String SQL_MODIFICAR_PISO="UPDATE PISO SET dir=?, loc=?, piscina=?, "
			+ "nif_prop=?, precio=?, comision=?, foto=? WHERE n_piso=?";
				
		QueryRunner run = new QueryRunner();
		
		try	{	
			conn = getDataSource().getConnection();			
			insertadas = run.update(conn, SQL_MODIFICAR_PISO, 
		    		piso.getDir(), piso.getLoc(), piso.isPiscina(),
		    		piso.getNif_prop(), piso.getPrecio(), piso.getComision(),
		    		piso.getFoto(), piso.getN_piso());
		    
		    if (LOG.isDebugEnabled()) {
		    	if (insertadas>0)
		    		LOG.debug("Piso modificado: "+piso );
		    	else
		    		LOG.debug("Piso NO modificado: "+piso );
		    }
		    
		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.modificarPiso()'. Parametro: Piso piso="+piso;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return insertadas;
	}

	/**
	 * Evitar el clonado: un Singleton no debe poder clonarse
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	@Override
	public Usuario comprobarCredenciales(String usuario, String pwd) throws DaoException {
		Connection conn=null;		
		Usuario user=null;
		
		String SQL_CREDENCIALES="SELECT login, pwd FROM USUARIOS WHERE login=? AND pwd=?";		
		
        QueryRunner qr = new QueryRunner();
        Object[] params = new Object[]{usuario, pwd};
        
        @SuppressWarnings({ "rawtypes", "unchecked" })
		ResultSetHandler<Usuario> rsh = new BeanHandler(Usuario.class);
        
        try {			        	
        	
        	if (params[0]!=null) { 
        		conn = getDataSource().getConnection();
        		user=qr.query(conn, SQL_CREDENCIALES, rsh, params);        		
        	}
        	
        	if (LOG.isInfoEnabled()) {
 				if (user!=null)
 					LOG.info("Usuario VALIDADO con las credenciales: "+usuario+ "/"+pwd);
 				else
 					LOG.info("Usuario NO VALIDADO con las credenciales: "+usuario+ "/"+pwd);
        	 }	
        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoGestionImpl.comprobarCredenciales()'. Parametros: String usuario="+usuario+ " y pwd:"+pwd;					
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return user;		
	}	
}
