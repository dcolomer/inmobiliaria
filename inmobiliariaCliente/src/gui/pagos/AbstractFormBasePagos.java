package gui.pagos;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import servutiles.ServiciosInmobiliariaFactory;
import utiles.Misc;

import ws.Cliente;
import ws.ServiciosInmobiliaria;

public abstract class AbstractFormBasePagos 
	extends javax.swing.JInternalFrame {

	private static final long serialVersionUID = 1L;
	
	protected final Logger LOG=Logger.getLogger(this.getClass());
	
	protected static ServiciosInmobiliaria serviciosInmobiliaria;
	
	static {
		serviciosInmobiliaria=ServiciosInmobiliariaFactory.getServicios();
	}
	
	/*
	 * Cargar el combo con los NIF's de los clientes
	 */
	protected String[] getNIFClientes() {
		
		final String[] EMPTY_NIF = new String[0];
		
		//Collection<Cliente> clientes=serviciosInmobiliaria.getClientes();
		
		Collection<Cliente> clientes=serviciosInmobiliaria.getClientesCacheados();
		
		if (!clientes.isEmpty()) {
			Collection<String> nifs=new ArrayList<String>(clientes.size());
			for (Cliente cliente:clientes) {
				nifs.add(cliente.getNifCli());	
			}
			return nifs.toArray(EMPTY_NIF);
		}
			
		return EMPTY_NIF;				
	}
	
	protected void gestionarError(String msg, Exception e) {
		LOG.error(msg, e);			
		Misc.mostrarError(e, msg, false);
	}
	
	/**
	 * Metodo de conveniencia que permite añadir objetos
	 * a un combo cuyo tipo subyacente sea String. Esto
	 * se utiliza cuando un combo se ha quedado vacio y
	 * queremos que el usuario pueda visualizar, por ejemplo,
	 * el simbolo de interrogacion
	 * 
	 * @param 
	 * 		elemento (un '?', por ejemplo)
	 * @return 
	 * 		un Object
	 */
	protected Object crearObj(final String elemento)  {
	    return new Object() { 
	    	 public String toString() { return elemento; } 
	    };
	}
}
