package servicios;

import java.io.IOException;
import java.util.Properties;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ServiciosInmobiliariaServer {
	
	private static final String LOG_PROPERTIES_FILE = "log4j.properties";
	private static final String HOST_NAME_FILE = "host.properties";
	
	private static final Logger LOG=Logger.getLogger(ServiciosInmobiliariaServer.class);
		
	/*
	 * Publicar el WS
	 */
	public static void main(String[] args) {
		
		loadConfigLogging(); //Cargar el fichero de configuracion de Log4j
		
		/*
		 * Leemos el fichero que describe el URL del endpoint y 
		 * publicamos el servicio Web 
		 */
		try {
			
			Properties hostProperties = new Properties();
			hostProperties.load(ServiciosInmobiliariaServer.class.getClassLoader()
					.getResourceAsStream(HOST_NAME_FILE));
			
			if (LOG.isDebugEnabled())
				LOG.debug("Fichero "+HOST_NAME_FILE+" cargado.");
			
			//Endpoint.publish(hostProperties.getProperty("endpoint"), new ServiciosInmobiliariaImpl());
			Endpoint.publish(
					hostProperties.getProperty("endpoint"), 
					ServiciosInmobiliariaFactory.getServiciosInmobiliaria()
					);
			
			String msg="El Servicio Web 'ServiciosInmobiliariaImpl' publicado y listo para atender peticiones.";
			if (LOG.isInfoEnabled())
				LOG.info(msg);
			System.out.println(msg);
		} catch (IOException e) {			
				String msg="No se ha podido cargar el fichero con las propiedades del endpoint "
					+ HOST_NAME_FILE;
				LOG.error(msg);
				LOG.error(e);
				throw new RuntimeException(e);							
		}						
	}
	
	/*
	 * Cargar el fichero de configuracion de Log4j
	 */
	private static void loadConfigLogging() {
		Properties logProperties = new Properties();

		try { // Cargar la configuracion del log
			
			logProperties.load(ServiciosInmobiliariaServer.class.getClassLoader()
					.getResourceAsStream(LOG_PROPERTIES_FILE));
			
			PropertyConfigurator.configure(logProperties);
						
			if (LOG.isDebugEnabled())
				LOG.debug("Fichero "+LOG_PROPERTIES_FILE+" cargado.");
		} catch (Exception e) {
			String msg="No se ha podido cargar el fichero de configuracion de logging: "
				+ LOG_PROPERTIES_FILE;
			System.out.println(msg);
			e.printStackTrace();
		}
	}
		
}
