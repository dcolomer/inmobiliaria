package servutiles;

import org.apache.log4j.Logger;

import utiles.Misc;
import ws.ServiciosInmobiliaria;
import ws.ServiciosInmobiliariaImplService;

public class ServiciosInmobiliariaFactory {
	
	private final static Logger LOG=
		Logger.getLogger(ServiciosInmobiliariaFactory.class);
	
	private static ServiciosInmobiliaria serviciosInmobiliaria;
	
	private ServiciosInmobiliariaFactory() {
		
		try {
			ServiciosInmobiliariaImplService servicios=
				new ServiciosInmobiliariaImplService();
		
			serviciosInmobiliaria=servicios.getPort(ServiciosInmobiliaria.class);
		} catch (Exception e) {
			String msg="Error accedediendo a los servicios remotos. ¿Esta en marcha el servidor?";
			LOG.error(msg, e);			
			Misc.mostrarError(e, msg, false);
		}
	}
	
	/*
     * Metodo publico de factoria estatico para obtener
     * un objeto ServiciosInmobiliaria
     */  
	public static ServiciosInmobiliaria getServicios() {
		if (serviciosInmobiliaria==null)
			new ServiciosInmobiliariaFactory();
		
		return serviciosInmobiliaria;
	}
	
	
}
