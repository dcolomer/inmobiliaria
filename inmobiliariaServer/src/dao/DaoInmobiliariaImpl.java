package dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import entidades.Cliente;
import entidades.Factura;
import entidades.Pedido;
import entidades.PedidoPiso;
import entidades.Piso;
import entidades.Propietario;
import entidades.Usuario;

/**
 * Clase envoltorio. Sencillamente reune todos los metodos
 * existentes en DaoGestionImpl y DaoPagosImpl, con el objetivo
 * de permitir a las clases clientes acceder a tales metodos
 * desde un unico objeto
 */
public class DaoInmobiliariaImpl implements DaoInmobiliaria {

	private static DaoGestion daoGestion;
	private static DaoPagos daoPagos;
		
	private static final Logger LOG=Logger.getLogger(DaoInmobiliariaImpl.class);
	
	static {
		daoGestion=DaoGestionImpl.getDaoGestion();
		try {
			daoPagos=DaoPagosImpl.getDaoPagos(DaoConstantes.TransaccionesON);
		} catch (DaoException e) {
			LOG.error("Error creando el objeto DaoPagosImpl en modo transaccional. La aplicacion debe finalizar.");
			System.exit(1);
		}	
	}
	
	@Override
	public Usuario comprobarCredenciales(String usuario, String pwd)
			throws DaoException {
		
		return daoGestion.comprobarCredenciales(usuario, pwd);
	}
	
	@Override
	public int grabarCliente(Cliente cliente) throws DaoException {
		return daoGestion.grabarCliente(cliente);
	}

	@Override
	public List<Cliente> getClientes() throws DaoException {		
		return daoGestion.getClientes();
	}

	@Override
	public int eliminarCliente(String nif) throws DaoException  {		
		return daoGestion.eliminarCliente(nif);
	}

	@Override
	public Cliente getCliente(String nif) throws DaoException {		
		return daoGestion.getCliente(nif);
	}

	@Override
	public int grabarPropietario(Propietario propietario) throws DaoException {		
		return daoGestion.grabarPropietario(propietario);
	}

	@Override
	public List<Propietario> getPropietarios() throws DaoException {		
		return daoGestion.getPropietarios();
	}

	@Override
	public int eliminarPropietario(String nif) throws DaoException {		
		return daoGestion.eliminarPropietario(nif);
	}

	@Override
	public Propietario getPropietario(String nif) throws DaoException {		
		return daoGestion.getPropietario(nif);
	}

	@Override
	public int grabarPiso(Piso piso) throws DaoException {		
		return daoGestion.grabarPiso(piso);
	}

	@Override
	public List<Piso> getPisos() throws DaoException {		
		return daoGestion.getPisos();
	}

	@Override
	public int eliminarPiso(long n_piso) throws DaoException {		
		return daoGestion.eliminarPiso(n_piso);
	}

	@Override
	public Piso getPiso(long n_piso) throws DaoException {		
		return daoGestion.getPiso(n_piso);
	}

	@Override
	public List<Piso> getPisosPropietario(String nif) throws DaoException {		
		return daoGestion.getPisosPropietario(nif);
	}

	/*
	 * 
	 * 
	 * (non-Javadoc)
	 * @see dao.DaoPagos#getPisosDisponibles(java.util.Date, java.util.Date)
	 */
	
	@Override
	public List<Piso> getPisosDisponibles(Date entrada, Date salida) throws DaoException {		
		return daoPagos.getPisosDisponibles(entrada, salida);
	}

	@Override
	public long nuevoPedido(Pedido pedido) throws DaoException {		
		return daoPagos.nuevoPedido(pedido);
	}

	@Override
	public long nuevaFactura(Factura factura) throws DaoException {		
		return daoPagos.nuevaFactura(factura);
	}

	@Override
	public List<Piso> getPisosNoPagados() throws DaoException {		
		return daoPagos.getPisosNoPagados();
	}

	@Override
	public List<PedidoPiso> getPedidosNoPagados() throws DaoException {		
		return daoPagos.getPedidosNoPagados();
	}

	@Override
	public List<PedidoPiso> getPedidosNoPagados(String nif) throws DaoException {		
		return daoPagos.getPedidosNoPagados(nif);
	}

	@Override
	public int pagarPedido(long n_pedido) throws DaoException {		
		return daoPagos.pagarPedido(n_pedido);
	}

	@Override
	public Factura getFactura(long n_pedido, String operacion) throws DaoException {		
		return daoPagos.getFactura(n_pedido, operacion);
	}

	@Override
	public Factura getFactura(long n_factura) throws DaoException {		
		return daoPagos.getFactura(n_factura);
	}

	@Override
	public Pedido getPedido(long n_pedido) throws DaoException {		
		return daoPagos.getPedido(n_pedido);
	}

	@Override
	public int cancelarPedido(long n_pedido) throws DaoException {		
		return daoPagos.cancelarPedido(n_pedido);
	}

	@Override
	public List<PedidoPiso> getPedidosCancelables() throws DaoException {		
		return daoPagos.getPedidosCancelables();
	}

	@Override
	public List<PedidoPiso> getPedidosCancelables(String nif) throws DaoException {
		return daoPagos.getPedidosCancelables(nif);
	}

	@Override
	public List<PedidoPiso> getPedidosNoPagadosPropietario(String nif) throws DaoException {		
		return daoPagos.getPedidosNoPagadosPropietario(nif);
	}

	@Override
	public int pagarFacturas(long n_pedido) throws DaoException {		
		return daoPagos.pagarFacturas(n_pedido);
	}

	@Override
	public boolean estaCerradaCaja(Date fecha) throws DaoException {		
		return daoPagos.estaCerradaCaja(fecha);
	}

	@Override
	public float totalCajaDia(Date fecha) throws DaoException {		
		return daoPagos.totalCajaDia(fecha);
	}

	@Override
	public List<Factura> getFacturasPagadas_y_NoCobradas() throws DaoException {		
		return daoPagos.getFacturasPagadas_y_NoCobradas();		
	}

	@Override
	public List<Factura> getFacturasPagadasSinReserva() throws DaoException {
		return daoPagos.getFacturasPagadasSinReserva();
	}

	@Override
	public List<Factura> getFacturasCanceladasSinReserva() throws DaoException {
		return daoPagos.getFacturasCanceladasSinReserva();
	}

	@Override
	public int eliminarFactura(long n_factura) throws DaoException {
		return daoPagos.eliminarFactura(n_factura);
	}

	@Override
	public int actualizarCajaDia(Date fecha) throws DaoException {		
		return daoPagos.actualizarCajaDia(fecha);
	}

	@Override
	public GestorTransacciones getGestorTran() {
		return daoPagos.getGestorTran();
	}
	
}
