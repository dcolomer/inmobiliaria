package test;

import java.util.Collection;

import entidades.Cliente;
import servicios.ServiciosException;
import servicios.ServiciosInmobiliaria;
import servicios.ServiciosInmobiliariaFactory;

public class TestCliente {

	public static void main(String[] args) {

		ServiciosInmobiliaria servicios;
		servicios=ServiciosInmobiliariaFactory.getServiciosInmobiliaria();
		
		try {
			// ***Probado: eliminar cliente
			// servicios.eliminarCliente("12211221-G");
			
			// ***Probado: modificar/buscar cliente
			/*Cliente cli=servicios.getCliente("46695124-H");
			System.out.println("Cliente: "+cli);
			cli.setNombre("DANIEL");
			servicios.grabarCliente(cli);*/
			
			// ***Probado: nuevo cliente
			//Cliente cli=new Cliente("12211221-G","LUCAS","PIRUETO");
			//servicios.grabarCliente(cli);
			
			// ***Probado: lista de clientes
			Collection<Cliente> clientes=servicios.getClientes();
			
			for (Cliente cliente:clientes)
				System.out.println(cliente);
			
		} catch (ServiciosException e) {			
			System.out.println(e);
		}

	}

}
