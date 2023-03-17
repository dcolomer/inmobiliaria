package dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import entidades.Factura;
import entidades.Pedido;
import entidades.PedidoPiso;
import entidades.Piso;

/**
 * Clase Singleton que implementa mediante JDBC los métodos de 
 * acceso a datos relacionados con operaciones de cobros/pagos.
 * 
 * Varios metodos requieren comportamiento transccional
 */
public class DaoPagosImpl extends AbstractDao implements DaoPagos {
	
	private static DaoPagos daoPagos;
	
	/**
	 * Constructores (privados)
	 */
	private DaoPagosImpl() {
		if (LOG.isDebugEnabled())
			LOG.debug("OBJETO DaoPagosImpl CONSTRUIDO");		
	}
	
	private DaoPagosImpl(DaoConstantes modoTransacciones) throws DaoException {
		super(modoTransacciones);
		if (LOG.isDebugEnabled())
			LOG.debug("OBJETO DaoPagosImpl CONSTRUIDO (en modo transaccional)");		
	}
	
	/**
	 * Metodo de factoria estatico sincronizado para evitar que otro
	 * hilo pueda simultaneamente llamar al constructor privado
	 */
	public static synchronized DaoPagos getDaoPagos() {
		if (daoPagos==null) 
			daoPagos=new DaoPagosImpl();
			
		return daoPagos;
	}
	
	public static synchronized DaoPagos getDaoPagos(DaoConstantes modoTransacciones) throws DaoException {
		if (daoPagos==null) 
			daoPagos=new DaoPagosImpl(modoTransacciones);
			
		return daoPagos;
	}
	
	/**
	 *  Implementacion de los metodos Dao
	 */
	
	/**
	 * Nueva linea de pedido
	 */
	@Override
	public long nuevoPedido(Pedido pedido) throws DaoException {
		
		long pk=0;
		Connection conn=null;
		final String SQL_NUEVO_PEDIDO="INSERT INTO PEDIDO (nif_cli, n_piso, llegada, partida, pagado, cancelado) VALUES (?,?,?,?,?,?)";
		final ResultSetHandler<?> rsSh=new ScalarHandler();
			
		QueryRunnerGeneratedKeys run=new QueryRunnerGeneratedKeys(rsSh);
				
		try	{					
			java.sql.Date fechaLlegada=new java.sql.Date(pedido.getLlegada().getTime());
			java.sql.Date fechaPartida=new java.sql.Date(pedido.getPartida().getTime());
			
			conn=getDataSource().getConnection();
			pk = run.update(conn, SQL_NUEVO_PEDIDO, 
		    		pedido.getNif_cli(), pedido.getN_piso(), fechaLlegada,
		    		fechaPartida, pedido.isPagado(), pedido.isCancelado());
			
			if (LOG.isDebugEnabled())
				LOG.debug("Generado un nuevo pedido. n_pedido: "+pk);		
		}
		catch (SQLException e) {		
			String msg="Error en el metodo 'DaoPagosImpl.nuevoPedido()'. Parametro: Pedido pedido="+pedido;
			relanzarExcepcion(msg,e);	
		} finally {
			closeQuiet(conn);
		}
		
		return pk;
		
	}
	
	
	/**
	 * Pagar un pedido pendiente de pago
	 */
	@Override
	public int pagarPedido(long n_pedido) throws DaoException {
		int actualizadas=0;
		Connection conn=null;
		String SQL_PAGAR_PEDIDO="UPDATE pedido SET pagado=true WHERE n_pedido=?";
				
		QueryRunner run = new QueryRunner();
		Object[] params = new Object[]{n_pedido};
		 
		try	{	
						
			conn=getDataSource().getConnection();
			actualizadas = run.update(conn, SQL_PAGAR_PEDIDO, params[0]);
		    		    
		    if (LOG.isDebugEnabled()) {
		    	if (actualizadas>0)
		    		LOG.debug("Pedido pagado: "+n_pedido);
		    	else
		    		LOG.debug("Pedido NO pagado: "+n_pedido);
		    }
		    		   
		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.pagarPedido()'. long n_pedido="+n_pedido;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return actualizadas;
	}
	
	
	/*
	 * Cancelar un pedido pendiente de pago
	 */
	@Override
	public int cancelarPedido(long n_pedido) throws DaoException {
		int actualizadas=0;
		Connection conn=null;
		String SQL_CANCELAR_PEDIDO="UPDATE pedido SET cancelado=true WHERE n_pedido=?";
				
		QueryRunner run = new QueryRunner();
		Object[] params = new Object[]{n_pedido};
		 
		try	{	
			conn=getDataSource().getConnection();			
			actualizadas = run.update(conn, SQL_CANCELAR_PEDIDO, params[0]);
		    
			if (LOG.isDebugEnabled()) {
		    	if (actualizadas>0)
		    		LOG.debug("Pedido cancelado: "+n_pedido);
		    	else
		    		LOG.debug("Pedido NO cancelado: "+n_pedido);
		    }

		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.cancelarPedido()'. long n_pedido="+n_pedido;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return actualizadas;
	}
	
	
	@Override
	public long nuevaFactura(Factura factura) throws DaoException {
		Connection conn=null;
		long pk=0;
		final String SQL_NUEVA_FACTURA="INSERT INTO CAJA (operacion, n_pedido, importe, pagado, dia) VALUES (?,?,?,?,?)";
		final ResultSetHandler<?> rsSh=new ScalarHandler();
		
		/*
		 * Si se trata del pedido -1 sabemos que tenemos
		 * que grabar el valor null tanto en el campo 'n_pedido'
		 * como en el campo 'pagado' (a propietario), ya que estos 
		 * campos no aplican para el tipo de factura 'E'
		 */
		long n_pedido=factura.getN_pedido();
		
		QueryRunnerGeneratedKeys run=new QueryRunnerGeneratedKeys(rsSh);
		
		try	{		
			// No podemos grabar un objeto java.util.Date en MySQL
			// asi que realizamos la conversion
			java.sql.Date diaFactura=new java.sql.Date(factura.getDia().getTime());
			
			conn=getDataSource().getConnection();
		    pk = run.update(conn, SQL_NUEVA_FACTURA, 
		    		factura.getOperacion(), n_pedido==-1L?null:n_pedido, 
		    		factura.getImporte(), n_pedido==-1L?null:factura.isPagado(), diaFactura);
		    
		    if (LOG.isDebugEnabled()) 
		    	LOG.debug("Generada nueva factura: "+pk);
		    		 
		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.nuevaFactura()'. Factura factura="+factura;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return pk;
	}
	
	
	
	/*
	 * Obtiene la lista de pisos disponibles para el intervalo
	 * de fechas especificado.
	 * Para obtener la lista hay que unir los pisos que no est�n
	 * en la tabla de pedidos junto aquellos que s� est�n pero
	 * quedan libres para el intervalo de fechas especificado 
	 */	
	@Override
	public List<Piso> getPisosDisponibles(Date llegada, Date partida) throws DaoException {

		List<Piso> lista=new ArrayList<Piso>();		
		Connection conn=null;
		CallableStatement call=null;
		ResultSet rs=null;
		
		try {
			conn = getDataSource().getConnection();
			
			// El primer '?' es la fecha de llegada y el segundo la de partida
			call = conn.prepareCall (" {call pisos_disponibles (?,?)}");
			call.setDate (1, new java.sql.Date(llegada.getTime()));
			call.setDate (2, new java.sql.Date(partida.getTime()));
			   
			call.execute();
					
			rs = call.getResultSet();
			while (rs.next()) {										
				long n_piso=rs.getLong(1);
				String dir=rs.getString(2)==null?"":rs.getString("dir");
				String loc=rs.getString(3)==null?"":rs.getString("loc");
				boolean piscina=rs.getBoolean(4);
				String nif_prop=rs.getString(5);
				float precio=rs.getFloat(6);
				float comision=rs.getFloat(7);
				
				Piso pisoTmp=new Piso(dir, loc, piscina, nif_prop, precio, comision);
				// El numero de piso no forma parte del constructor porque es una clave artificial
				pisoTmp.setN_piso(n_piso); 
							
				lista.add(pisoTmp);			
			}
						
			if (LOG.isDebugEnabled()) {
				if (!lista.isEmpty())
					LOG.debug("Lista de <pisos disponibles para el intervalo de fechas> recuperada.");
				else
					LOG.debug("Lista VACIA de <pisos disponibles para el intervalo de fechas>.");
			}		
			
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.getPisosDisponibles()'. Date llegada="+llegada+", Date partida="+partida;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn, call, rs);
		}
		
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<Piso>(lista);
		
	}
	
	// ****************************************************************
	
	
	/*
	 * Recupera los pisos de la tabla pedido que aun 
	 * no han sido pagados en su totalidad 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Piso> getPisosNoPagados() throws DaoException {
		Connection conn=null;
		String SQL_PISOS_NO_PAGADOS="SELECT DISTINCT n_piso FROM pedido WHERE pagado=false";		
		List<Piso> lista=null;
		
        QueryRunner qr = new QueryRunner();        
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Piso> rsh = new BeanListHandler(Piso.class);
        
        try {			
        	conn = getDataSource().getConnection();        	
        	lista=(List<Piso>)qr.query(conn, SQL_PISOS_NO_PAGADOS, rsh);
        	        	
        	if (LOG.isDebugEnabled()) {
				if (lista!=null && !lista.isEmpty())
					LOG.debug("Lista de pisos no pagados recuperada.");
				else
					LOG.debug("Lista de pisos no pagados vacia.");
			}        	
        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.getPisosNoPagados()'.";
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
	 * Recupera todos los pedidos que aun no han sido 
	 * pagados en su totalidad 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PedidoPiso> getPedidosNoPagados() throws DaoException {
		Connection conn=null;
		String SQL_PEDIDOS_PENDIENTES_PAGO="SELECT pe.n_pedido, pi.dir, pi.loc, pe.llegada, pe.partida" 
			+ "FROM pedido pe INNER JOIN piso pi ON pe.n_piso=pi.n_piso AND pe.pagado=false";
		
		List<PedidoPiso> lista=null;
		
        QueryRunner qr = new QueryRunner();        
                
        @SuppressWarnings("rawtypes")
		ResultSetHandler<PedidoPiso> rsh = new BeanListHandler(PedidoPiso.class);
        
        try {			
        	conn = getDataSource().getConnection();
        	lista=(List<PedidoPiso>)qr.query(conn, SQL_PEDIDOS_PENDIENTES_PAGO, rsh);
        				        	
			if (LOG.isDebugEnabled()) {
				if (lista!=null && !lista.isEmpty())
					LOG.debug("Lista de pedidos no pagados recuperada.");
				else
					LOG.debug("Lista de pedidos no pagados recuperada vacia.");
			}
			
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.getPedidosNoPagados()'.";
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<PedidoPiso>(lista);
		
	}
	
	
	/*
	 * Recupera todos los pedidos que aun no han sido 
	 * pagados en su totalidad para UN CLIENTE DETERMINADO
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PedidoPiso> getPedidosNoPagados(String nif) throws DaoException {
		Connection conn=null;
		String SQL_PEDIDOS_PENDIENTES_PAGO_POR_CLIENTE="SELECT pe.n_pedido, pi.dir, pi.loc, pe.llegada, " 
			+ "pe.partida FROM pedido pe INNER JOIN piso pi ON pe.n_piso=pi.n_piso AND " 
			+ "pe.pagado=false AND pe.nif_cli=?";
		
		List<PedidoPiso> lista=null;
		
        QueryRunner qr = new QueryRunner();        
        Object[] params = new Object[]{nif};
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<PedidoPiso> rsh = new BeanListHandler(PedidoPiso.class);
        
        try {			
			        	
        	if (params[0]!=null) {
        		conn = getDataSource().getConnection();
        		lista=(List<PedidoPiso>)qr.query(conn, SQL_PEDIDOS_PENDIENTES_PAGO_POR_CLIENTE, rsh, params);
        		        		
        		if (LOG.isDebugEnabled()) {
    				if (lista!=null && !lista.isEmpty())
    					LOG.debug("Lista de pedidos no pagados recuperada del cliente con NIF: "+nif);
    				else
    					LOG.debug("Lista VACIA de pedidos no pagados del cliente con NIF: "+nif);
    			}
        		
        	} else {
        		if (LOG.isDebugEnabled())
        			LOG.debug("No se ha especificado el NIF del cliente. No se ha recuperado la lista de sus pisos no pagados.");        	        		
        	}
        	
        } catch (SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.getPedidosNoPagados()'. String nif="+nif;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<PedidoPiso>(lista);
		
	}
	
	
	/*
	 * Recupera todos los pedidos que aun no han sido 
	 * pagados en su totalidad y que cancelado=false
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PedidoPiso> getPedidosCancelables() throws DaoException {
		Connection conn = null;
		String SQL_PEDIDOS_CANCELABLES="SELECT pe.n_pedido, pi.dir, pi.loc, pe.llegada, pe.partida" 
			+ "FROM pedido pe INNER JOIN piso pi ON pe.n_piso=pi.n_piso AND pe.pagado=false AND pe.cancelado=false";
		
		List<PedidoPiso> lista=null;
		
        QueryRunner qr = new QueryRunner();        
                
        @SuppressWarnings("rawtypes")
		ResultSetHandler<PedidoPiso> rsh = new BeanListHandler(PedidoPiso.class);
        
        try {			
        	conn = getDataSource().getConnection();
        	lista=(List<PedidoPiso>)qr.query(conn, SQL_PEDIDOS_CANCELABLES, rsh);
        				       	
			if (LOG.isDebugEnabled()) {
				if (lista!=null && !lista.isEmpty())
					LOG.debug("Lista de pedidos cancelables recuperada.");
				else
					LOG.debug("Lista de pedidos cancelables VACIA.");
			}
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.getPedidosCancelables()'.";
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<PedidoPiso>(lista);
		
	}
	
	
	/*
	 * Recupera todos los pedidos que aun no han sido 
	 * pagados en su totalidad y son CANCELABLES 
	 * para UN CLIENTE DETERMINADO
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PedidoPiso> getPedidosCancelables(String nif) throws DaoException {
		Connection conn=null;
		String SQL_PEDIDOS_CANCELABLES_POR_CLIENTE="SELECT pe.n_pedido, pi.dir, pi.loc, pe.llegada, " 
			+ "pe.partida FROM pedido pe INNER JOIN piso pi ON pe.n_piso=pi.n_piso AND " 
			+ "pe.pagado=false AND pe.cancelado=false AND pe.nif_cli=?";
		
		List<PedidoPiso> lista=null;
		
        QueryRunner qr = new QueryRunner();        
        Object[] params = new Object[]{nif};
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<PedidoPiso> rsh = new BeanListHandler(PedidoPiso.class);
        
        try {			
			        	
        	if (params[0]!=null) {
        		conn = getDataSource().getConnection();
        		lista=(List<PedidoPiso>)qr.query(conn, SQL_PEDIDOS_CANCELABLES_POR_CLIENTE, rsh, params);        		
        		
        		if (LOG.isDebugEnabled()) {
    				if (lista!=null && !lista.isEmpty())
    					LOG.debug("Lista de pedidos cancelables recuperada del cliente con NIF: "+nif);
    				else
    					LOG.debug("Lista de pedidos cancelables VACIA para el cliente con NIF: "+nif);
    			}
        		
        	} else {        		
        		if (LOG.isDebugEnabled())
        			LOG.debug("No se ha especificado el NIF del cliente. No se ha recuperado la lista de sus pedidos cancelables.");
        	}
        	        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.getPedidosCancelables()'. String nif="+nif;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<PedidoPiso>(lista);
		
	}
	
	
	/*
	 * Recupera todos los pedidos que aun no han sido 
	 * pagados A SU PROPIETARIO por parte nuestra
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PedidoPiso> getPedidosNoPagadosPropietario(String nif) throws DaoException {
		Connection conn=null;
		String SQL_PEDIDOS_PENDIENTES_PAGO_PROPIETARIO_POR_NIF="SELECT pe.n_pedido, pi.dir, pi.loc, "
			+ "pe.llegada, pe.partida FROM pedido pe INNER JOIN piso pi ON pe.n_piso=pi.n_piso AND "
			+ "pi.nif_prop=? AND pe.pagado=true INNER JOIN caja c ON c.n_pedido=pe.n_pedido AND "
			+ "c.pagado=false AND c.operacion='B'";
		
		List<PedidoPiso> lista=null;
		
        QueryRunner qr = new QueryRunner();        
        Object[] params = new Object[]{nif};
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<PedidoPiso> rsh = new BeanListHandler(PedidoPiso.class);
        
        try {			
			        	
        	if (params[0]!=null) {
        		conn = getDataSource().getConnection();
        		lista=(List<PedidoPiso>)qr.query(conn, SQL_PEDIDOS_PENDIENTES_PAGO_PROPIETARIO_POR_NIF, rsh, params);
        		        		
        		if (LOG.isDebugEnabled()) {
    				if (lista!=null && !lista.isEmpty())
    					LOG.debug("Lista de pedidos no pagados a propietario recuperada del propietario con NIF: "+nif);
    				else
    					LOG.debug("Lista de pedidos no pagados a propietario VACIA para el propietario con NIF: "+nif);
    			}
        	} else {
        		if (LOG.isDebugEnabled())
        			LOG.debug("No se ha especificado el NIF del propietario. No se ha recuperado la lista de sus pisos pendientes de pago.");
        	}
        	        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.getPedidosNoPagadosPropietario()'. String nif="+nif;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<PedidoPiso>(lista);
		
	}
	
	
	
	
	/*
	 * Recuperar un pedido de la BD a partir 
	 * del numero de pedido
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Pedido getPedido(long n_pedido) throws DaoException {
		Connection conn=null;		
		Pedido pedido=null;
		String SQL_PEDIDO="SELECT n_pedido, nif_cli, n_piso, llegada, partida, pagado, cancelado "
			+"FROM pedido WHERE n_pedido=?";		
		
        QueryRunner qr = new QueryRunner();
        Object[] params = new Object[]{n_pedido};
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Pedido> rsh = new BeanHandler(Pedido.class);
        
        try {			
        	conn = getDataSource().getConnection();
        	pedido=qr.query(conn, SQL_PEDIDO, rsh, params);        	
        	        	
        	if (LOG.isDebugEnabled()) {
				if (pedido!=null)
					LOG.debug("Pedido recuperado a partir del numero de pedido "+n_pedido);
				else
					LOG.debug("Pedido NO recuperado a partir del numero de pedido "+n_pedido);
			}
        	
		} catch (SQLException e) {		
			String msg="Error en el metodo 'DaoPagosImpl.getPedido()'. long n_pedido="+n_pedido;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return pedido;		
	}
	
	
	/*
	 * Recuperar una factura de la BD a partir del numero
	 * de pedido y del codigo de la operacion
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Factura getFactura(long n_pedido, String operacion) throws DaoException {
		Connection conn=null;		
		Factura factura=null;
		String SQL_FACTURA="SELECT n_factura, operacion, n_pedido, importe, pagado, dia "
			+"FROM caja WHERE n_pedido=? AND operacion=?";		
		
        QueryRunner qr = new QueryRunner();
        Object[] params = new Object[]{n_pedido, operacion};
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Factura> rsh = new BeanHandler(Factura.class);
        
        try {			
        	conn = getDataSource().getConnection();
        	factura=qr.query(conn, SQL_FACTURA, rsh, params);        	
        	
			if (LOG.isDebugEnabled()) {
				if (factura!=null)
					LOG.debug("Factura recuperada del pedido "+n_pedido+" y la operacion "+operacion);
				else
					LOG.debug("Factura NO recuperada del pedido "+n_pedido+" y la operacion "+operacion);
			}
			        	
		} catch (SQLException e) {		
			String msg="Error en el metodo 'DaoPagosImpl.getFactura()'. long n_pedido="+n_pedido+" String operacion="+operacion;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return factura;		
	}
	
	/*
	 * Recuperar una factura de la BD a partir del 
	 * numero de factura
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Factura getFactura(long n_factura) throws DaoException {
		Connection conn=null;		
		Factura factura=null;
		String SQL_FACTURA="SELECT n_factura, operacion, n_pedido, importe, pagado, dia "
			+"FROM caja WHERE n_factura=?";		
		
        QueryRunner qr = new QueryRunner();
        Object[] params = new Object[]{n_factura};
        
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Factura> rsh = new BeanHandler(Factura.class);
        
        try {
        	conn = getDataSource().getConnection();
			factura=qr.query(conn, SQL_FACTURA, rsh, params);        	
        	        	
        	if (LOG.isDebugEnabled()) {
				if (factura!=null)
					LOG.debug("Factura recuperada a partir de su numero: "+n_factura);
				else
					LOG.debug("Factura NO recuperada a partir de su numero: "+n_factura);
			}
        	
        } catch (SQLException e) {		
			String msg="Error en el metodo 'DaoPagosImpl.getFactura()'. long n_factura="+n_factura;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return factura;		
	}
	
	/*
	 * Establecer las facturas correspondientes a un determinado pedido
	 * como pagadas al propietario
	 */
	@Override
	public int pagarFacturas(long n_pedido) throws DaoException {
		Connection conn=null;
		int actualizadas=0;
		
		String SQL_PAGAR_FACTURAS="UPDATE caja SET pagado=true WHERE n_pedido=?";
				
		QueryRunner run = new QueryRunner();
		Object[] params = new Object[]{n_pedido};
		 
		try	{	
			conn = getDataSource().getConnection();			
			actualizadas = run.update(conn, SQL_PAGAR_FACTURAS, params[0]);
		    		    
		    if (LOG.isDebugEnabled()) {
				if (actualizadas>0)
					LOG.debug("Pagadas todas las facturas del pedido: "+n_pedido);
				else
					LOG.debug("NO se ha pagado las facturas del pedido: "+n_pedido);
			}
		    
		} catch(SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.pagarFacturas()'. long n_pedido="+n_pedido;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return actualizadas;
	}

	/*
	 * Comprobar si existe una operacion tipo 'E' (total facturado del dia)
	 * en la tabla CAJA para la fecha especificada.
	 */
	@Override
	public boolean estaCerradaCaja(Date fecha) throws DaoException {
		Connection conn=null;	
		long resultado=0L;
		String SQL="SELECT COUNT(n_factura) FROM caja WHERE dia=? AND operacion='E'";		
		
        QueryRunner qr = new QueryRunner();
        Object[] params = new Object[]{new java.sql.Date(fecha.getTime())};
        
		ScalarHandler rsh = new ScalarHandler();
        
        try {	        	
        	conn = getDataSource().getConnection();
        	resultado=(Long) qr.query(conn, SQL, rsh, params);        	
        	        	        	
        	if (LOG.isDebugEnabled()) 
				if (resultado>0)
					LOG.debug(resultado+" filas existen en CAJA con operacion tipo 'E' para el dia "+fecha);
				else
					LOG.debug("La CAJA permanece abierta en fecha: "+fecha);
				        	       
		} catch (SQLException e) {		
			String msg="Error en el metodo 'DaoPagosImpl.estaCerradaCaja()'. Date fecha="+fecha;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return resultado==0L?false:true;		
	}
	
	/*
	 * Sumar el total facturado del dia	 
	 */
	@Override
	public float totalCajaDia(Date fecha) throws DaoException {
		Connection conn=null;	
		BigDecimal resultado=null;
		String SQL="SELECT SUM(importe) FROM caja WHERE dia=? AND operacion!='E'";		
		
        QueryRunner qr = new QueryRunner();
        Object[] params = new Object[]{new java.sql.Date(fecha.getTime())};
        
		ScalarHandler rsh = new ScalarHandler();
        
        try {			
        	conn = getDataSource().getConnection();
        	resultado=(BigDecimal) qr.query(conn, SQL, rsh, params);        	
        	        	
        	if (LOG.isDebugEnabled()) 
        		LOG.debug("Total facturado el dia "+fecha+":"+resultado);
        	        	
		} catch (SQLException e) {		
			String msg="Error en el metodo 'DaoPagosImpl.totalCajaDia()'. Date fecha="+fecha;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return Float.parseFloat(resultado.toString());		
	}

	
	/*
	 * Recupera todas las facturas con operacion 'D' (pago a propietario) 
	 * que no tienen asociada un cobro total o parcial del piso (operaciones 'B' y 'A')
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Factura> getFacturasPagadas_y_NoCobradas() throws DaoException {
		Connection conn=null;
		String SQL_FACTURAS_PAGADAS_Y_NO_COBRADAS="SELECT c1.n_factura, c1.operacion, c1.n_pedido, "
			+ "c1.importe, c1.pagado, c1.dia FROM caja c1 WHERE c1.operacion='D' AND c1.n_pedido NOT IN ("
			+ "SELECT c2.n_pedido FROM caja c2 WHERE (c2.operacion='B' AND c1.n_pedido=c2.n_pedido) OR "
			+ "(c2.operacion='A' AND c1.n_pedido=c2.n_pedido))";	
		
		List<Factura> lista=null;
		
        QueryRunner qr = new QueryRunner();        
                
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Factura> rsh = new BeanListHandler(Factura.class);
        
        try {			
        	conn = getDataSource().getConnection();
        	lista=(List<Factura>)qr.query(conn, SQL_FACTURAS_PAGADAS_Y_NO_COBRADAS, rsh);
        	        	
        	if (LOG.isDebugEnabled()) {
				if (lista!=null && !lista.isEmpty())
					LOG.debug("Recuperada la lista de facturas tipo 'D' que no tienen su correspondiente tipo 'B'.");
				else
					LOG.debug("NO Recuperada la lista de facturas tipo 'D' que no tienen su correspondiente tipo 'B'.");
			}
        	        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.getFacturasPagadas_y_NoCobradas()'.";
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<Factura>(lista);
		
	}
	
	
	/*
	 * Recupera todas las facturas con operacion 'B' (cobro total de pedido) 
	 * que no tienen asociada la reserva (operacion 'A')
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Factura> getFacturasPagadasSinReserva() throws DaoException {
		Connection conn=null;
		String SQL_FACTURAS_COBRADAS_SIN_RESERVA="SELECT c1.n_factura, c1.operacion, c1.n_pedido, "
			+ "c1.importe, c1.pagado, c1.dia FROM caja c1 WHERE c1.operacion='B' AND c1.n_pedido NOT IN ("
			+ "SELECT c2.n_pedido FROM caja c2 WHERE c2.operacion='A' AND c1.n_pedido=c2.n_pedido)";	
		
		List<Factura> lista=null;
		
        QueryRunner qr = new QueryRunner();        
                
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Factura> rsh = new BeanListHandler(Factura.class);
        
        try {			
        	conn = getDataSource().getConnection();
        	lista=(List<Factura>)qr.query(conn, SQL_FACTURAS_COBRADAS_SIN_RESERVA, rsh);
        				        	
			if (LOG.isDebugEnabled()) {
				if (lista!=null && !lista.isEmpty())
					LOG.debug("Recuperada la lista de facturas tipo 'B' que no tienen su correspondiente tipo 'A'.");
				else
					LOG.debug("NO Recuperada la lista de facturas tipo 'B' que no tienen su correspondiente tipo 'A'.");
			}
			        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.getFacturasPagadas_y_NoCobradas()'.";
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<Factura>(lista);
		
	}
	
	/*
	 * Recupera todas las facturas con operacion 'C' (pedidos cancelados) 
	 * que no tienen asociada la reserva (operacion 'A')
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Factura> getFacturasCanceladasSinReserva() throws DaoException {
		Connection conn=null;
		String SQL_FACTURAS_CANCELADAS_SIN_RESERVA="SELECT c1.n_factura, c1.operacion, c1.n_pedido, "
			+ "c1.importe, c1.pagado, c1.dia FROM caja c1 WHERE c1.operacion='C' AND c1.n_pedido NOT IN ("
			+ "SELECT c2.n_pedido FROM caja c2 WHERE c2.operacion='A' AND c1.n_pedido=c2.n_pedido)";	
		
		List<Factura> lista=null;
		
        QueryRunner qr = new QueryRunner();        
                
        @SuppressWarnings("rawtypes")
		ResultSetHandler<Factura> rsh = new BeanListHandler(Factura.class);
        
        try {			
        	conn = getDataSource().getConnection();
        	lista=(List<Factura>)qr.query(conn, SQL_FACTURAS_CANCELADAS_SIN_RESERVA, rsh);
        				        
			if (LOG.isDebugEnabled()) {
				if (lista!=null && !lista.isEmpty())
					LOG.debug("Recuperada la lista de facturas tipo 'C' que no tienen su correspondiente tipo 'A'.");
				else
					LOG.debug("NO Recuperada la lista de facturas tipo 'C' que no tienen su correspondiente tipo 'A'.");
			}
        	        	
		} catch (SQLException e) {
			String msg="Error en el metodo 'DaoPagosImpl.getFacturasCanceladasSinReserva()'.";
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
						
		if (lista.isEmpty())
			return Collections.emptyList(); // Siempre devolver la misma lista		
		else
			return new ArrayList<Factura>(lista);
		
	}

	/*
	 * Eliminar una factura. Realmente se trata de eliminar un 
	 * apunte de caja incoherente
	 */
	@Override
	public int eliminarFactura(long n_factura) throws DaoException {
		Connection conn=null;
		int eliminadas=0;
		
		String SQL_ELIMINAR_FACTURA="DELETE FROM caja WHERE n_factura=?";
				
		QueryRunner run = new QueryRunner();
		Object[] params = new Object[]{n_factura};
		 
		try	{	
			conn = getDataSource().getConnection();			
			eliminadas = run.update(conn, SQL_ELIMINAR_FACTURA, params[0]);
		    		    
		    if (LOG.isDebugEnabled()) {
				if (eliminadas>0)
					LOG.debug("Eliminada la factura numero: "+n_factura);
				else
					LOG.debug("NO eliminada la factura numero: "+n_factura);
			}
		    		    
		} catch(SQLException e) {
			String msg="Error en el metodo 'eliminarFactura()'. long n_factura="+n_factura;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return eliminadas;
	}
	
	/*
	 * Actualizar el importe de la factura tipo 'E' para la fecha especificada
	 */
	@Override
	public int actualizarCajaDia(Date fecha) throws DaoException {
		int actualizadas=0;
		Connection conn=null;
		String SQL_ACTUALIZAR_FACTURA="UPDATE caja c1 INNER JOIN (SELECT sum(importe) as importe, dia "
			+ "FROM caja WHERE operacion!='E') AS c2 ON c1.dia=c2.dia AND c1.dia=? "
			+ "SET c1.importe=c2.importe WHERE c1.operacion='E'";
				
		QueryRunner run = new QueryRunner();
		Object[] params = new Object[]{new java.sql.Date(fecha.getTime())};
		 
		try	{	
			conn = getDataSource().getConnection();			
			actualizadas = run.update(conn, SQL_ACTUALIZAR_FACTURA, params[0]);
		    		    
		    if (LOG.isDebugEnabled()) {
				if (actualizadas>0)
					LOG.debug("Actualizada la caja para la fecha: "+fecha);
				else
					LOG.debug("NO actualizada la caja para la fecha: "+fecha);
			}
		    
		} catch(SQLException e) {
			String msg="Error en el metodo 'actualizarCajaDia()'. Date fecha="+fecha;
			relanzarExcepcion(msg,e);
		} finally {
			closeQuiet(conn);
		}
		
		return actualizadas;
	}

	@Override
	public GestorTransacciones getGestorTran() {		
		return gestorTran;
	}
	
	/**
	 * Evitar el clonado: un Singleton no debe poder clonarse
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
