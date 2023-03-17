package servicios;

import java.util.Collection;

import javax.jws.WebService;

import entidades.Cliente;
import entidades.Propietario;

@WebService
public interface ServiciosInmobiliaria 
	extends ServiciosGestion, ServiciosPagos {

	Collection<Cliente> getClientesCacheados();

	Collection<Propietario> getPropietariosCacheados();

	void refreshClientesCacheados() throws ServiciosException;

	void refreshPropietariosCacheados() throws ServiciosException;

}
