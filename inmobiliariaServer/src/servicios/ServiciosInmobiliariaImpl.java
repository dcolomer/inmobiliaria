package servicios;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.Logger;

import entidades.Cliente;
import entidades.Factura;
import entidades.PedidoPiso;
import entidades.Piso;
import entidades.Propietario;
import entidades.Usuario;

@WebService(endpointInterface ="servicios.ServiciosInmobiliaria")
public class ServiciosInmobiliariaImpl implements ServiciosInmobiliaria {

	private static final Logger LOG=
		Logger.getLogger(ServiciosInmobiliariaImpl.class);
	
		
	/* ****************************************
	 * Implementaciones reales de los servicios
	 ******************************************/
	
	private static final ServiciosGestion serviciosGestion=
		ServiciosGestionImpl.getServiciosGestion();
	
	private static final ServiciosPagos serviciosPagos=
		ServiciosPagosImpl.getServiciosPagos();
	
	
	@Override
	public Usuario comprobarCredenciales(String usuario, String pwd) {		
		return serviciosGestion.comprobarCredenciales(usuario, pwd);
	}
	
	
	/* ********************************************************
	 * Colecciones para cachear clientes y propietarios. No es
	 * necesario cachear pisos, ya que en los casos de uso no se
	 * requiere rellenar ningun combo con todos los pisos, sino
	 * rellenar uno con los pisos disponibles para la reserva del
	 * cliente, lo cual se resuelve con un procedimiento almacenado 	 
	 **********************************************************/
	
	// CLIENTES ***************************************
	
	/*
	 * Lista con los clientes. Al ser static se realiza
	 * la consulta a la BD la primera vez que se carga 
	 * la clase. Las siguientes veces se devuelve esta
	 * lista cacheada
	 */
	private static Collection<Cliente> clientesCacheados;
	
	/*
	 * Devolvemos los clientes sin necesidad de consultar
	 * la base de datos
	 */
	@Override
	public Collection<Cliente> getClientesCacheados() {
		return clientesCacheados;
	}
	
	/*
	 * El siguiente metodo renueva el contenido de la
	 * lista de clientes con informaci�n de la BD.
	 * Se llama desde el mantenimiento de clientes despu�s de haber
	 * dado de alta o de baja un cliente 
	 */	
	@Override
	public void refreshClientesCacheados() throws ServiciosException {		
		clientesCacheados=serviciosGestion.getClientes();
	}
	
	
	// PROPIETARIOS ***************************************	
	private static Collection<Propietario> propietariosCacheados;
		
	@Override
	public Collection<Propietario> getPropietariosCacheados() {
		return propietariosCacheados;
	}
		
	@Override
	public void refreshPropietariosCacheados() throws ServiciosException {
		propietariosCacheados=serviciosGestion.getPropietarios();
	}

	/*
	 * Inicializador estatico
	 */
	static {
		try {
			clientesCacheados=
				serviciosGestion.getClientes();
			
			if (LOG.isDebugEnabled()) 
				LOG.debug("Lista de clientes almacenada en cache");
			
		} catch (ServiciosException e) {
			String msg="Se ha recibido un error desde la capa de Acceso a Datos al cachear la lista de clientes. La causa es:";
			LOG.error(msg, e);			
		}
				
		try {
			propietariosCacheados=
				serviciosGestion.getPropietarios();
			
			if (LOG.isDebugEnabled()) 
				LOG.debug("Lista de propietarios almacenada en cache");
			
		} catch (ServiciosException e) {
			String msg="Se ha recibido un error desde la capa de Acceso a Datos al cachear la lista de propietarios. La causa es:";
			LOG.error(msg, e);
		}
		
	}
	
	
	
	/* *****************************************************
	 * Implementaciones "dummy" de los servicios
	 *******************************************************/
	
	@Override
	public boolean grabarCliente(Cliente cliente) throws ServiciosException {		
		return serviciosGestion.grabarCliente(cliente);
	}

	@Override
	public boolean eliminarCliente(String nif) throws ServiciosException {		
		return serviciosGestion.eliminarCliente(nif);
	}

	@Override
	public Cliente getCliente(String nif) throws ServiciosException {		
		return serviciosGestion.getCliente(nif);
	}

	@Override
	public Collection<Cliente> getClientes() throws ServiciosException {		
		return serviciosGestion.getClientes();
	}

	@Override
	public boolean grabarPropietario(Propietario propietario) throws ServiciosException {		
		return serviciosGestion.grabarPropietario(propietario);
	}

	@Override
	public boolean eliminarPropietario(String nif) throws ServiciosException {		
		return serviciosGestion.eliminarPropietario(nif);
	}

	@Override
	public Propietario getPropietario(String nif) throws ServiciosException {		
		return serviciosGestion.getPropietario(nif);
	}

	@Override
	public Collection<Propietario> getPropietarios() throws ServiciosException {		
		return serviciosGestion.getPropietarios();
	}

	@Override
	public boolean grabarPiso(Piso piso) throws ServiciosException {		
		return serviciosGestion.grabarPiso(piso);
	}

	@Override
	public boolean eliminarPiso(long n_piso) throws ServiciosException {		
		return serviciosGestion.eliminarPiso(n_piso);
	}

	@Override
	public Piso getPiso(long n_piso) throws ServiciosException  {		
		return serviciosGestion.getPiso(n_piso);
	}

	@Override
	public Collection<Piso> getPisos() throws ServiciosException {		
		return serviciosGestion.getPisos();
	}

	@Override
	public Collection<Piso> getPisosPropietario(String nif) throws ServiciosException {		
		return serviciosGestion.getPisosPropietario(nif);
	}

	/*
	 * 
	 * (non-Javadoc)
	 * @see servicios.ServiciosPagos#getPisosDisponibles(java.util.Date, java.util.Date)
	 */
	
	@Override
	public Collection<Piso> getPisosDisponibles(Date entrada, Date salida) {		
		return serviciosPagos.getPisosDisponibles(entrada, salida);
	}

	@Override
	public long reservarPiso(String nif_cli, Date entrada, Date salida,
			long n_piso) throws PisoOcupadoException, CajaYaCerradaException {
		
		return serviciosPagos.reservarPiso(nif_cli, entrada, salida, n_piso);
	}

	@Override
	public Collection<Piso> getPisosNoPagados() {		
		return serviciosPagos.getPisosNoPagados();		
	}

	@Override
	public Collection<PedidoPiso> getPedidosNoPagados() {		
		return serviciosPagos.getPedidosNoPagados();
	}

	@Override
	public Collection<PedidoPiso> getPedidosNoPagadosByNIF(String nif) {		
		return serviciosPagos.getPedidosNoPagadosByNIF(nif);
	}
	
	@Override
	public long pagarPiso(String nif_cli, long n_pedido) throws PedidoYaPagadoException, 
		CajaYaCerradaException 
	{		
		return serviciosPagos.pagarPiso(nif_cli, n_pedido);
	}

	@Override
	public Factura getFactura(long n_factura) {		
		return serviciosPagos.getFactura(n_factura);
	}

	@Override
	public long cancelarPiso(String nif_cli, long n_pedido) throws PedidoYaPagadoException, 
		PedidoYaCanceladoException, CajaYaCerradaException 
	{		
		return serviciosPagos.cancelarPiso(nif_cli, n_pedido);
	}
	
	@Override
	public Collection<PedidoPiso> getPedidosCancelables() {		
		return serviciosPagos.getPedidosCancelables();
	}
	
	@Override
	public Collection<PedidoPiso> getPedidosCancelablesByNIF(String nif) {		
		return serviciosPagos.getPedidosCancelablesByNIF(nif);
	}

	@Override
	public Collection<PedidoPiso> getPedidosNoPagadosPropietario(String nif) {		
		return serviciosPagos.getPedidosNoPagadosPropietario(nif);
	}

	@Override
	public float pagarPedidosPropietario(String nif_prop, List<Long> pedidos) 
		throws NoPedidosPendientesPagoPropitarioException, CajaYaCerradaException
	{
				
		return serviciosPagos.pagarPedidosPropietario(nif_prop, pedidos);
	}

	@Override
	public boolean estaCerradaCaja(Date fecha) {		
		return serviciosPagos.estaCerradaCaja(fecha);
	}

	@Override
	public float totalCajaDia(Date fecha) {		
		return serviciosPagos.totalCajaDia(fecha);
	}

	@Override
	public float cerrarCaja(Date fecha) throws CajaYaCerradaException {		
		return serviciosPagos.cerrarCaja(fecha);
	}	

}
