package servicios;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import entidades.Factura;
import entidades.PedidoPiso;
import entidades.Piso;

public interface ServiciosPagos {
		
	Collection<Piso> getPisosDisponibles(Date entrada, Date salida);
	
	
	/**
	 * Intenta reservar el piso para las fechas especificadas
	 *  
	 * @param nif_cli: cliente que realiza la reserva del piso
	 * @param entrada: fecha inicio reserva
	 * @param salida: fecha fin reserva
	 * @param n_piso: piso objeto de la reserva
	 * 
	 * @return El numero de factura generado al reservar el piso
	 * o 0 si no ha sido posible.	
	 * @throws PisoOcupadoException 
	 * @throws CajaYaCerradaException 
	 */
	long reservarPiso(String nif_cli, Date entrada, Date salida, 
			long n_piso) throws PisoOcupadoException, CajaYaCerradaException;

	
	/**
	 * Obtener una colecci�n tipo Piso con los
	 * pisos pendientes de pagar	
	 */
	Collection<Piso> getPisosNoPagados();
	
	/**
	 * Obtener una coleccion tipo PedidoPiso con los
	 * pisos pendientes de pagar	
	 */	
	Collection<PedidoPiso> getPedidosNoPagados();
	
	/**
	 * Obtener una coleccion tipo PedidoPiso con los
	 * pisos pendientes de pagar de un cliente	
	 */		
	Collection<PedidoPiso> getPedidosNoPagadosByNIF(String nif);	
	
	
	
	/**
	 * Intenta pagar el pedido/piso para el numero de pedido
	 * especificado.
	 * 
	 * @param nif_cli: el numero de cliente
	 * @param n_pedido: el numero de pedido
	 * @return Si el pago es posible se devuelve el numero de 
	 * factura obtenido, sino se retorna el numero 0.
	 * @throws PedidoYaPagadoException 
	 * @throws CajaYaCerradaException 
	 */
	
	long pagarPiso(String nif_cli, long n_pedido) 
		throws PedidoYaPagadoException, CajaYaCerradaException;

	
	/**
	 * Obtener una factura a partir de su numero
	 */
	Factura getFactura(long n_factura);


	/**
	 * Intenta cancelar el pedido/piso para el numero de pedido
	 * especificado.
	 * 
	 * @param nif_cli: el numero de cliente
	 * @param n_pedido: el numero de pedido
	 * @return Si la cancelacion es posible se devuelve el numero de 
	 * factura (abono) obtenida, sino se retorna el numero 0.
	 * @throws PedidoYaCanceladoException 
	 * @throws PedidoYaPagadoException 
	 * @throws CajaYaCerradaException 
	 */
	long cancelarPiso(String nif_cli, long n_pedido) 
		throws PedidoYaPagadoException, PedidoYaCanceladoException, CajaYaCerradaException;

	Collection<PedidoPiso> getPedidosCancelables();

	Collection<PedidoPiso> getPedidosCancelablesByNIF(String nif);

	Collection<PedidoPiso> getPedidosNoPagadosPropietario(String nif);


	float pagarPedidosPropietario(String nif_prop, List<Long> pedidos) 
		throws NoPedidosPendientesPagoPropitarioException, CajaYaCerradaException;


	boolean estaCerradaCaja(Date fecha);


	float totalCajaDia(Date fecha);


	float cerrarCaja(Date fecha) throws CajaYaCerradaException;

}