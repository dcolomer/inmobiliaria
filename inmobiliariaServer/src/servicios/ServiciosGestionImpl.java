package servicios;

import java.util.Collection;
import java.util.Collections;

import dao.DaoException;
import entidades.Cliente;
import entidades.Piso;
import entidades.Propietario;
import entidades.Usuario;

/**
 * Clase Singleton que implementa los metodos de negocio
 * relacionados con las operacione CRUD
 */
public class ServiciosGestionImpl extends AbstractServicios 
	implements ServiciosGestion {
	
	private static ServiciosGestion serviciosGestion;
		
	private ServiciosGestionImpl() {
		if (LOG.isDebugEnabled())
			LOG.debug("OBJETO ServiciosGestionImpl CONSTRUIDO");
	}
			
	/**
	 * Metodo de factoria estatico sincronizado para evitar que otro
	 * hilo pueda simultaneamente llamar al constructor privado
	 */
	public static synchronized ServiciosGestion getServiciosGestion() {
		if (serviciosGestion==null) 
			serviciosGestion=new ServiciosGestionImpl();
			
		return serviciosGestion;
	}
	
	@Override
	public Usuario comprobarCredenciales(String usuario, String pwd) {		
		try {
			return daoInmobiliaria.comprobarCredenciales(usuario, pwd);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return null;
	}
	
	/*
	 * CRUD Cliente
	 */
	
	@Override
	public boolean grabarCliente(Cliente cliente) {		
		try {
			if (daoInmobiliaria.grabarCliente(cliente)==OPERACION_OK)
				return true;			
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return false;
	}

	@Override
	public boolean eliminarCliente(String nif) {
		try {
			if (daoInmobiliaria.eliminarCliente(nif)==OPERACION_OK)
				return true;			
		} catch (DaoException e) {						
			relanzarExcepcion(e);					
		}
		return false;
	}
	
	@Override
	public Cliente getCliente(String nif) {
		try {
			return daoInmobiliaria.getCliente(nif);
		} catch (DaoException e) {			
			relanzarExcepcion(e);
		}
		return null;
	}

	@Override
	public Collection<Cliente> getClientes() {		
		try {
			return daoInmobiliaria.getClientes();
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return Collections.emptyList();
	}
	
	/*
	 * CRUD Propietario
	 */
	
	@Override
	public boolean grabarPropietario(Propietario propietario) {		
		try {
			if (daoInmobiliaria.grabarPropietario(propietario)==OPERACION_OK)
				return true;							
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return false;
	}
	
	@Override
	public boolean eliminarPropietario(String nif) {
		try {
			if (daoInmobiliaria.eliminarPropietario(nif)==OPERACION_OK)
				return true;			
		} catch (DaoException e) {
			relanzarExcepcion(e);			
		}
		
		return false;
	}
	
	@Override
	public Propietario getPropietario(String nif) {
		try {
			return daoInmobiliaria.getPropietario(nif);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return null;
	}

	@Override
	public Collection<Propietario> getPropietarios() {		
		try {
			return daoInmobiliaria.getPropietarios();
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return Collections.emptyList();
	}
	
	/*
	 * CRUD Piso
	 */
	
	@Override
	public boolean grabarPiso(Piso piso) {
		try {
			if (daoInmobiliaria.grabarPiso(piso)==OPERACION_OK)
				return true;
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return false;
	}

	@Override
	public boolean eliminarPiso(long n_piso) {
		try {
			if (daoInmobiliaria.eliminarPiso(n_piso)==OPERACION_OK)
				return true;			
		} catch (DaoException e) {
			relanzarExcepcion(e);			
		}
		
		return false;
	}

	@Override
	public Piso getPiso(long n_piso) {
		try {
			return daoInmobiliaria.getPiso(n_piso);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return null;
	}

	@Override
	public Collection<Piso> getPisos() {
		try {
			return daoInmobiliaria.getPisos();
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return Collections.emptyList();
	}

	@Override
	public Collection<Piso> getPisosPropietario(String nif) {
		try {
			return daoInmobiliaria.getPisosPropietario(nif);
		} catch (DaoException e) {
			relanzarExcepcion(e);
		}
		return Collections.emptyList();
	}

	/**
	 * Evitar el clonado: un Singleton no debe poder clonarse
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
}