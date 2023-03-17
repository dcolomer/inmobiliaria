package servicios;

import java.util.Collection;

import entidades.Cliente;
import entidades.Piso;
import entidades.Propietario;
import entidades.Usuario;

public interface ServiciosGestion {
	
	/*
	 * Usuarios - Credenciales
	 */
	Usuario comprobarCredenciales(String usuario, String pwd);
	
	
	/*
	 * CRUD Cliente
	 */
	boolean grabarCliente(Cliente cliente);
	boolean eliminarCliente(String nif);
	Cliente getCliente(String nif);
	Collection<Cliente> getClientes();
	
	/*
	 * CRUD Propietario
	 */
	boolean grabarPropietario(Propietario propietario);
	boolean eliminarPropietario(String nif);
	Propietario getPropietario(String nif);
	Collection<Propietario> getPropietarios();
	
	/*
	 * CRUD Piso
	 */
	boolean grabarPiso(Piso piso);
	boolean eliminarPiso(long n_piso);
	Piso getPiso(long n_piso);
	Collection<Piso> getPisos();
	Collection<Piso> getPisosPropietario(String nif);
		
}
