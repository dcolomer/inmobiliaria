package dao;

import java.util.Date;
import java.util.List;

import entidades.Factura;
import entidades.Pedido;
import entidades.PedidoPiso;
import entidades.Piso;

public interface DaoPagos {

	List<Piso> getPisosDisponibles(Date entrada, Date salida) throws DaoException;

	long nuevoPedido(Pedido pedido) throws DaoException;

	long nuevaFactura(Factura factura) throws DaoException;

	List<Piso> getPisosNoPagados() throws DaoException;
	List<PedidoPiso> getPedidosNoPagados() throws DaoException;
	List<PedidoPiso> getPedidosNoPagados(String nif) throws DaoException;

	int pagarPedido(long n_pedido) throws DaoException;

	Factura getFactura(long n_pedido, String operacion) throws DaoException;

	Factura getFactura(long n_factura) throws DaoException;

	Pedido getPedido(long n_pedido) throws DaoException;

	int cancelarPedido(long n_pedido) throws DaoException;

	List<PedidoPiso> getPedidosCancelables() throws DaoException;

	List<PedidoPiso> getPedidosCancelables(String nif) throws DaoException;

	List<PedidoPiso> getPedidosNoPagadosPropietario(String nif) throws DaoException;

	int pagarFacturas(long n_pedido) throws DaoException;

	boolean estaCerradaCaja(Date fecha) throws DaoException;

	float totalCajaDia(Date fecha) throws DaoException;

	List<Factura> getFacturasPagadas_y_NoCobradas() throws DaoException;

	List<Factura> getFacturasPagadasSinReserva() throws DaoException;

	List<Factura> getFacturasCanceladasSinReserva() throws DaoException;

	int eliminarFactura(long n_factura) throws DaoException;

	int actualizarCajaDia(Date fecha) throws DaoException;

	GestorTransacciones getGestorTran();
}
