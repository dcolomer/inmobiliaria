package servicios;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dao.DaoException;
import dao.GestorTransacciones;
import entidades.Factura;
import entidades.Pedido;
import entidades.PedidoPiso;
import entidades.Piso;

/**
 * Clase Singleton que implementa los metodos de negocio
 * relacionados con las operacione de cobros y pagos
 */
public class ServiciosPagosImpl extends AbstractServicios 
	implements ServiciosPagos {
			
	private static ServiciosPagos serviciosPagos;
	
	private GestorTransacciones gestorTran;
			
	private ServiciosPagosImpl() {
		if (LOG.isDebugEnabled())
			LOG.debug("OBJETO ServiciosPagosImpl CONSTRUIDO");			
	
			gestorTran=daoInmobiliaria.getGestorTran();		
	}
	
	/**
	 * Metodo de factoria estatico sincronizado para evitar que otro
	 * hilo pueda simultaneamente llamar al constructor privado
	 */
	public static synchronized ServiciosPagos getServiciosPagos() {
		if (serviciosPagos==null) 
			serviciosPagos=new ServiciosPagosImpl();
			
		return serviciosPagos;
	}
	
	/**
	 * Retorna true si el piso se encuentra disponible para el intervalo
	 * de fechas especificado
	 */
	private boolean isPisoDisponible(long n_piso, Date entrada, 
			Date salida) throws ServiciosException {

		// Lista de pisos disponibles
		List<Piso> pisosDisponibles=null;
		
		// Comprobamos si el numero de piso recibido por parametro
		// es uno de los de la lista de pisos disponibles
		Piso piso = null;
		
		try {
			pisosDisponibles=daoInmobiliaria.getPisosDisponibles(entrada, salida);
			piso = daoInmobiliaria.getPiso(n_piso);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		
		return pisosDisponibles.contains(piso);
	}
	
	/**
	 * Consulta que retorna una coleccion de pisos disponibles
	 * segun un intervalo de fechas.
	 * Retorna la lista vacia en caso de no encontrar pisos
	 * disponibles para esas fechas.
	 */
	@Override
	public Collection<Piso> getPisosDisponibles(Date entrada, Date salida) {
		try {
			return daoInmobiliaria.getPisosDisponibles(entrada, salida);
		} catch (DaoException e) {
			// Si se produce una excepcion en la capa Dao, la registramos 
			// en el LOG y la envolvemos en una RuntimeException y asi no 
			// forzamos que la capa cliente deba capturar esta excepcion.
			relanzarExcepcion(e);
		}
		return Collections.emptyList();	
	}
	
	
	/**
	 * Intenta reservar el piso especificado para el intervalo de fechas
	 * que se proporciona.
	 * 
	 * La reserva de un piso implica crear un pedido y generar una factura
	 * con el valor del 50% del precio total (operacion tipo A). Por tanto,
	 * este metodo debe ser transaccional
	 * 
	 * Retorno: Si la reserva es posible se devuelve el numero de factura, 
	 * sino se retorna el numero 0.
	 * 	 
	 * @throws PisoOcupadoException, CajaYaCerradaException 
	 */
	@Override
	public long reservarPiso(String nif_cli, Date entrada, Date salida,
			long n_piso) throws PisoOcupadoException, CajaYaCerradaException {
		
		if (!estaCerradaCaja(new Date())) {
			if (isPisoDisponible(n_piso, entrada, salida)) {				
				try { 					
					gestorTran.start();
					
					/*
					 * Generar el pedido. Se obtiene el nuevo numero de pedido
					 * generado por el SGBD
					*/ 
					long n_pedido=daoInmobiliaria.nuevoPedido(
						new Pedido(nif_cli, n_piso, entrada, salida, false, false));
									
					/*
					 * Generar la factura (se hace el pago del 50% del importe total.
					 * Se obtiene el nuevo numero de factura generado por el SGBD.
					 */
					//int diasReserva=(int) ((salida.getTime()-entrada.getTime())/(1000*60*60*24));
					int diasReserva=InmobiliariaUtilidades.restarFechas(entrada, salida);
					float precio_piso_dia=daoInmobiliaria.getPiso(n_piso).getPrecio();
					float pago_reserva=(float) (precio_piso_dia*diasReserva*0.5);
					
					// obtiene el nuevo num. factura generado por el SGBD
					long n_fact=daoInmobiliaria.nuevaFactura (
						new Factura("A", n_pedido, pago_reserva, false, new Date()));
					
					/*
					 * Validacion de las modificaciones y
					 * cierre de la transaccion 
					 */
					gestorTran.commit();
					
					if (n_fact>0)
						if (LOG.isDebugEnabled())
							LOG.debug("Realizada la reserva de piso correctamente. "+
									"String nif_cli="+nif_cli+", Date entrada="+entrada+", "+
									"Date salida="+salida+", long n_piso="+n_piso);
					
					return n_fact;
					
				} catch (DaoException e) {
					String msg="La reserva de piso no se ha podido llevar a cabo."+
						"String nif_cli="+nif_cli+", Date entrada="+entrada+", "+
						"Date salida="+salida+", long n_piso="+n_piso;
					deshacer(e, msg);
										
				} finally {
					gestorTran.end();
				}
				
			} else {
				throw new PisoOcupadoException(entrada, salida, n_piso);
			}
		} else {
			throw new CajaYaCerradaException();
		}
		return 0L;				
	}

	

	// ***************************************************************************
	
	/**
	 * Consulta que retorna una coleccion de pisos
	 * reservados y no pagados
	 */
	@Override
	public Collection<Piso> getPisosNoPagados() {
		try {
			return daoInmobiliaria.getPisosNoPagados();
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return Collections.emptyList();
	}
	
	@Override	
	public Collection<PedidoPiso> getPedidosNoPagados() {
		try {
			return daoInmobiliaria.getPedidosNoPagados();
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return Collections.emptyList();
	}
	
	@Override	
	public Collection<PedidoPiso> getPedidosNoPagadosByNIF(String nif) {
		try {
			return daoInmobiliaria.getPedidosNoPagados(nif);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return Collections.emptyList();
	}
	
	
	@Override
	public Collection<PedidoPiso> getPedidosCancelables() {
		try {
			return daoInmobiliaria.getPedidosCancelables();
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return Collections.emptyList();
	}
	
	
	@Override
	public Collection<PedidoPiso> getPedidosCancelablesByNIF(String nif) {
		try {
			return daoInmobiliaria.getPedidosCancelables(nif);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return Collections.emptyList();
	}
	
	/**
	 * Intenta pagar el pedido/piso para el numero de pedido
	 * especificado.
	 * El pago de un piso implica poner a true el campo 'pagado'
	 * de la tabla PEDIDO y crear un nuevo registro (nueva factura)
	 * en la tabla CAJA con el valor que restaba por abonar (el 50%
	 * del precio total -operacion tipo B-). Por tanto, este metodo 
	 * debe ser transaccional.
	 * 
	 * Retorno: Si el pago es posible se devuelve el numero de factura, 
	 * sino se retorna el numero 0.
	 * @throws PedidoYaPagadoException, CajaYaCerradaException 
	 */
	@Override
	public long pagarPiso(String nif_cli, 
			long n_pedido) throws PedidoYaPagadoException, CajaYaCerradaException {
				
		long n_fact=0L;
		final String msg="No se ha podido pagar el pedido: "+n_pedido+" para el cliente nif: "+nif_cli;
		
		if (!estaCerradaCaja(new Date())) {
		
			try {
				// Si el pedido NO esta pagado...
				if (!daoInmobiliaria.getPedido(n_pedido).isPagado()) {		
					try { 				
						gestorTran.start();
													
						// Miramos de pagarlo
						if (daoInmobiliaria.pagarPedido(n_pedido)==OPERACION_OK) {
							/*
							 * Se obtiene el nuevo numero de factura generado por el SGBD.
							 */		
							Factura factura=daoInmobiliaria.getFactura(n_pedido, "A");
							
							// Si recuperamos el importe de la operacion 'A' sabremos
							// lo que resta por pagar, ya que se trata del 50%
							float pago_pendiente=factura.getImporte();
							
							// Generar la factura (se hace el pago del 50% restante.
							// Obtiene el nuevo num. factura generado por el SGBD
							n_fact=daoInmobiliaria.nuevaFactura (
									new Factura("B", n_pedido, pago_pendiente, false, new Date()));
						} else {						
							throw new DaoException(msg);
						}
										
						/*
						 * Validacion de las modificaciones y
						 * cierre de la transaccion
						 */
						gestorTran.commit();
											
						if (LOG.isDebugEnabled()) {
							if (n_fact>0)
								LOG.debug("Realizado el pago del pedido correctamente. String nif_cli="+nif_cli+" long n_pedido="+n_pedido);
							else 
								LOG.debug(msg);
						}
						
						return n_fact;
						
					} catch (DaoException e) {					
						deshacer(e, msg);
					} finally {
						gestorTran.end();
					}
				} else {				
					throw new PedidoYaPagadoException(msg);
				}
			} catch (DaoException e) {
				relanzarExcepcion(e);
			}		
		
		} else {
			throw new CajaYaCerradaException();
		}
		return 0L;
		
	}
	
	/**
	 * Cancelacion de piso. Es necesario que el piso este reservado.
	 * Se cambia el estado de pedido a cancelado, luego se calcula si
	 * es necesario devolver parte de la reserva al cliente y, finalmente
	 * se anota una nueva factura interna en la tabla CAJA.
	 * Por tanto, este metodo debe ser transaccional.
	 * @throws PedidoYaPagadoException, PedidoYaCanceladoException, CajaYaCerradaException 
	 */
	@Override
	public long cancelarPiso(String nif_cli, 
			long n_pedido) throws PedidoYaPagadoException, PedidoYaCanceladoException, CajaYaCerradaException {
				
		long n_fact=0L;
		final String msg="NO se ha realizado la cancelacion de piso. String nif_cli="+nif_cli+" long n_pedido="+n_pedido;
		
		boolean pedidoPagado, pedidoAnulado;
		
		if (!estaCerradaCaja(new Date())) {
			try {
				pedidoPagado=daoInmobiliaria.getPedido(n_pedido).isPagado();
				pedidoAnulado=daoInmobiliaria.getPedido(n_pedido).isCancelado();
				
				// Si el pedido NO esta ni pagado ni cancelado...entonces es cancelable
				if (!pedidoPagado && !pedidoAnulado) {
					
					try { 				
						gestorTran.start();
	
						// Miramos de cancelarlo
						if (daoInmobiliaria.cancelarPedido(n_pedido)==OPERACION_OK) {
												
							// Calcular porcentaje del importe a retornar
							float impDev=importeDevolucion(n_pedido);
							
							if (impDev>0) // el importe se debe reflejar en negativo
								impDev=impDev*-1;
							
							// Generar la devolucion (se inserta una linea en CAJA
							// con el immporte en negativo y cuyo codigo de operacion es "C")
							// Obtiene el nuevo num. factura generado por el SGBD
							n_fact=daoInmobiliaria.nuevaFactura (
									new Factura("C", n_pedido, impDev, false, new Date()));
																	
							/*
							 * Validacion de las modificaciones y
							 * cierre de la transaccion
							 */
							gestorTran.commit();
							
							if (LOG.isDebugEnabled()) {
								if (n_fact>0)
									LOG.debug("Realizada la cancelacion de piso correctamente. String nif_cli="+nif_cli+" long n_pedido="+n_pedido);
								else 
									LOG.debug(msg);
							}
							
							return n_fact;
							
						} else {						
							throw new DaoException(msg);						
						}
														
					} catch (DaoException e) {					
						deshacer(e, msg);
					} finally {
						gestorTran.end();
					}
				} else {
					String msg1="No se pudo cambiar el estado del pedido "+n_pedido+" a cancelado=true";
					if (pedidoPagado)
						throw new PedidoYaPagadoException(msg1);
					else if (pedidoAnulado) {
						throw new PedidoYaCanceladoException(msg1);
					}
				}
				
			} catch (DaoException e) {
				relanzarExcepcion(e);
			}
				
		} else {
			throw new CajaYaCerradaException();
		}
		return 0L;		
	}
	
	
	/**
	 * Consulta que retorna una coleccion con pedidos
	 * que aun no hemos pagado a su propietario
	 */
	@Override
	public Collection<PedidoPiso> getPedidosNoPagadosPropietario(String nif) {
		try {
			return daoInmobiliaria.getPedidosNoPagadosPropietario(nif);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return Collections.emptyList();	
	}
	
	/**
	 * Liquidar la deuda que tenemos con los propietarios.
	 * Se deben realizar varias modificaciones de datos, asi como
	 * nuevos registros en la tabla CAJA. Por tanto, este metodo
	 * debe ser transaccional.
	 * @throws NoPedidosPendientesPagoPropitarioException, CajaYaCerradaException 
	 */
	@Override
	public float pagarPedidosPropietario(String nif_prop, 
			List<Long> pedidos) throws NoPedidosPendientesPagoPropitarioException, CajaYaCerradaException {
		
		float importeTotal=0f;
		long n_fact=0;
		
		if (!estaCerradaCaja(new Date())) {		
			try {
				if (daoInmobiliaria.getPedidosNoPagadosPropietario(nif_prop).isEmpty()) {
					throw new NoPedidosPendientesPagoPropitarioException();
				} else {
				
					try { 
						
						gestorTran.start();
					
						/*
						 * Para cada numero de pedido:			 
						 * -Creamos un nuevo registro en la tabla CAJA con el importe
						 * que le corresponde al propietario en negativo (tal importe)
						 * sera el total pagado por el cliente menos nuestra comision
						 * -Establecemos el campo 'pagado' a true en la tabla CAJA para
						 * toda factura cuyo pedido pertenezca al piso del propietario
						 */
						for (Long num_ped:pedidos) {
							
							/*
							 * Generar la factura de pago a propietario.
							 * Se obtiene el nuevo numero de factura generado por el SGBD.
							 */
							Pedido pedido=daoInmobiliaria.getPedido(num_ped);
							int diasReserva=InmobiliariaUtilidades.restarFechas(pedido.getLlegada(), pedido.getPartida());
							
							Piso piso=daoInmobiliaria.getPiso(pedido.getN_piso());
							
							float precio_piso_dia=piso.getPrecio();
							float comision=piso.getComision();
							
							float pago_propietario=(float) (((100-comision)/100)*precio_piso_dia*diasReserva);
							
							importeTotal+=pago_propietario; // sumatorio de importes de cada pedido
							
							// obtiene el nuevo num. factura generado por el SGBD
							n_fact=daoInmobiliaria.nuevaFactura (
								new Factura("D", num_ped, -1*pago_propietario, true, new Date()));
							
							if (LOG.isDebugEnabled()) {
								if (n_fact>0)
									LOG.debug("Factura generada numero "+n_fact+" (operacion D) para el pedido: "+num_ped);
								else
									LOG.debug("Factura NO generada para el pedido: "+num_ped+" y propietario nif: "+nif_prop);
							}
							
							if (daoInmobiliaria.pagarFacturas(num_ped)<OPERACION_OK) 							
								throw new DaoException("No se ha cambiado el estado de pagado a true de las facturas del pedido:"+num_ped);
							
						}
						
						/*
						 * Validacion de las modificaciones y
						 * cierre de la transaccion
						 * 
						 */
						gestorTran.commit();
						
						if (LOG.isDebugEnabled()) 
							LOG.debug("Realizado el pago de pedidos a propietario correctamente. String nif_prop="+nif_prop);
						
						return importeTotal;
					} catch (DaoException e) {
						String msg="El pago de pedidos del propietario no se ha podido llevar a cabo. String nif_prop="+nif_prop;
						deshacer(e, msg);
					} finally {
						gestorTran.end();
					}
				}
			} catch (DaoException e) {
				relanzarExcepcion(e);
			}
		
		} else {
			throw new CajaYaCerradaException();
		}
		
		return 0f;				
	}

	/**
	 * Recuperar una factura a partir de su numero
	 */
	@Override
	public Factura getFactura(long n_factura) {
		try {
			return daoInmobiliaria.getFactura(n_factura);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return null;
	}

	
	/**
	 * Devolver true si la caja esta cerrada para la fecha especificada
	 */
	@Override
	public boolean estaCerradaCaja(Date fecha) {
		try {
			return daoInmobiliaria.estaCerradaCaja(fecha);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return false;
	}                  
	
	/**
	 * Obtener el total de caja para el dia especificado
	 */
	@Override
	public float totalCajaDia(Date fecha) {
		try {
			return daoInmobiliaria.totalCajaDia(fecha);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return 0;
	}
	
	/**
	 * Sumar el total de caja para la fecha especificada.
	 * Se inserta una nueva factura en la tabla CAJA con
	 * el campo operacion='E'.
	 * 
	 * Por tanto, esta operacion debe realizarse de forma
	 * transaccional.
	 * 
	 * Despues de esta operacion ya no se permitira realizar 
	 * ninguna operacion que implique la tabla CAJA
	 * @throws CajaYaCerradaException 
	 * 
	 */
	@Override
	public float cerrarCaja(Date fecha) throws CajaYaCerradaException {
		
		if (!estaCerradaCaja(fecha)) {			
			try { 
				
				gestorTran.start();
				
				/*
				 * Obtener el total de caja de la fecha especificada
				 * 
				 * Nota1: como numero de pedido pasamos el -1 para
				 * indicar que no corresponde a ningun pedido en
				 * concreto, sino a todos aquellos cuyo movimiento
				 * de caja se ha producido en la fecha especificada.
				 * El Dao correspondiente convertira el -1 en null antes
				 * de grabar en la tabla.
				 * 
				 * Nota2: Establecemos el valor false para el campo
				 * 'pagado' (a propietario) ya que no todos los
				 * movimientos de caja tienen que haber sido pagados
				 * a su propietario 			
				*/ 
				float totalCaja=totalCajaDia(fecha);
								
				// Generar la factura tipo 'E'				
				long n_fact=daoInmobiliaria.nuevaFactura (
					new Factura("E", -1L, totalCaja, false, fecha));
				
				/*
				 * Validacion de las modificaciones y
				 * cierre de la transaccion
				 * 
				 */
				gestorTran.commit();
				
				if (LOG.isDebugEnabled()) {
					if (n_fact>0)
						LOG.debug("Realizado el cierre de caja correctamente. Factura generada: "+n_fact+" Total: "+totalCaja);
					else
						LOG.debug("NO se ha realizado el cierre de caja para la fecha: "+fecha);
				}
				
				return totalCaja;
				
			} catch (DaoException e) {
				String msg="El cierre de caja no se ha podido llevar a cabo.";
				deshacer(e, msg);
			} finally {
				gestorTran.end();
			}
		} else {
			throw new CajaYaCerradaException();
		}
		return 0.0f;				
	}
	
	
	/**
	 * Cuando un cliente quiere cancelar un pedido puede recuperar 
	 * una parte de la reserva que pago. La cantidad a devolver depende 
	 * de los dias de antelacion con los que avise.
	 */
	private float importeDevolucion(long n_pedido) {
		Pedido pedido=null;
		Factura factura=null;
		try {
			pedido = daoInmobiliaria.getPedido(n_pedido);
			/*
			 * En la factura que se realizo en su momento tenemos
			 * el importe que pago el cliente
			 */		
			factura=daoInmobiliaria.getFactura(n_pedido, "A");
					
			int dias_preaviso=InmobiliariaUtilidades.
				restarFechas(new Date(), pedido.getLlegada());
			
	        if (dias_preaviso>=30) {
	        	return factura.getImporte()*0.4f;
	        } else if (dias_preaviso>=20) {
	        	return factura.getImporte()*0.2f;
	        } else if (dias_preaviso>=10) {
	        	return factura.getImporte()*0.1f;
	        } 
		} catch (DaoException e) {
			relanzarExcepcion(e);			
		}
		return 0.0f;
	}
	
	
	/**
	 * Se ha producido una excepcion, anulamos las
	 * modificaciones realizadas y cerramos la
	 * transaccion
	 */
	private void deshacer(Exception e, String msg) throws ServiciosException {
		
		try { 												
			LOG.warn(msg,e);
			LOG.warn("La transaccion ha fallado. Se intentan deshacer los cambios en la BD...");
				
			gestorTran.rollback();						
			
		} catch (Exception ex) {
			msg="Imposible anular las modificaciones realizadas por la transaccion: ";
			//relanzarExcepcion(msg1, ex);
		} finally {
			relanzarExcepcion(msg,e);
		}
	}

	/**
	 * Evitar el clonado: un Singleton no debe poder clonarse
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
}
