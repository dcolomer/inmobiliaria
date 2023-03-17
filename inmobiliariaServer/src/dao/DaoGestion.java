package dao;

import java.util.List;
import entidades.Cliente;
import entidades.Piso;
import entidades.Propietario;
import entidades.Usuario;

public interface DaoGestion {

	/*
	 * Usuarios - Credenciales
	 */
	Usuario comprobarCredenciales(String usuario, String pwd) throws DaoException;
	
	/*
	 * CRUD Cliente
	 */
	int grabarCliente(Cliente cliente) throws DaoException;	
	List<Cliente> getClientes() throws DaoException;
	int eliminarCliente(String nif) throws DaoException;
	Cliente getCliente(String nif) throws DaoException;
	
	/*
	 * CRUD Propietario
	 */
	int grabarPropietario(Propietario propietario) throws DaoException;	
	List<Propietario> getPropietarios() throws DaoException;
	int eliminarPropietario(String nif) throws DaoException;
	Propietario getPropietario(String nif) throws DaoException;
	
	/*
	 * CRUD Piso
	 */
	int grabarPiso(Piso piso) throws DaoException;
	List<Piso> getPisos() throws DaoException;
	int eliminarPiso(long n_piso) throws DaoException;
	Piso getPiso(long n_piso) throws DaoException;
	List<Piso> getPisosPropietario(String nif) throws DaoException;
	
}
