package utiles;

import gui.gestion.AbstractDaoForm;
import gui.utiles.ExceptionDialog;

import java.awt.Dialog.ModalityType;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Clase miscelanea que contiene metodos estaticos 
 * para diversas utilidades no relacionadas
 */
public class Misc {
	
	private static final Logger LOG=Logger.getLogger(Misc.class);
	
	/**
	 * Pasar de java.util.Date a XMLGregorianCalendar
	 */
	public static XMLGregorianCalendar date2XmlGC(java.util.Date fecha) {
				
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(fecha);
		XMLGregorianCalendar xmlFecha = null;
		
		try {
			xmlFecha = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
			String msg="La conversion de fecha ha producido un error.";	
			LOG.error(e);
			Misc.mostrarError(e, msg, false);					
		}
		
		return xmlFecha;		
	}
	
	/**
	 * Carga el fichero de configuracion de Log4j
	 * y lo inicializa
	 */
	public static void loadConfigLogging(Class<?> clase, String fichero) {
		Properties logProperties = new Properties();

		try { // Cargar la configuracion del log
			
			logProperties.load(clase.getClassLoader()
					.getResourceAsStream(fichero));
			
			PropertyConfigurator.configure(logProperties);
						
			if (LOG.isDebugEnabled())
				LOG.debug("Fichero "+fichero+" cargado.");
		} catch (Exception e) {
			String msg="No se ha podido cargar el fichero de configuracion de logging: "
				+ fichero;			
			Misc.mostrarError(e, msg, false);
		}
	}
	
	
	/**
	 * Cargar cualquier fichero de configuracion
	 */
	public static Properties loadConfig(Class<?> clase, String fichero) {
		Properties config = new Properties();

		try { // Cargar la configuracion del log
			
			config.load(clase.getClassLoader()
					.getResourceAsStream(fichero));
			
			if (LOG.isDebugEnabled())
				LOG.debug("Fichero "+fichero+" cargado.");
			
			return config;
			
		} catch (Exception e) {
			String msg="No se ha podido cargar el fichero de configuracion de host: "
				+ fichero;			
			Misc.mostrarError(e, msg, false);
		}
		return null;
	}
	
	/**
	 * 
	 */
	public static String getBaseDatosURL(String ficheroHost) {
		Properties configHost=Misc.loadConfig(AbstractDaoForm.class, ficheroHost);
		String ipHost=configHost.getProperty("rdbmsip");
		String rdbmsschema=configHost.getProperty("rdbmsschema");
		String rdbmsuser=configHost.getProperty("rdbmsuser");
		String rdbmspwd=configHost.getProperty("rdbmspwd");
		
		String URL=
			"jdbc:mysql://"+ipHost+":3306/"+rdbmsschema+"?user="+rdbmsuser+"&password="+rdbmspwd;
		
		return URL;
	}
	
	/**
	 * Cuadro de dialogo que muestra un error
	 * @param throwable: La excepcion
	 * @param titulo: El mensaje principal
	 * @param detalles: si es true muestra el cuadro de dialo extendido,
	 * es decir, con toda la traza de la excepcion. Si no, muesra el
	 * cuadro con un tamaño pequeño.
	 */
	public static void mostrarError(Throwable throwable, String titulo, boolean detalles) {
				
		ExceptionDialog excepDialog=
			new ExceptionDialog(null, ModalityType.APPLICATION_MODAL);
		
		excepDialog.setLocationRelativeTo(null);
		excepDialog.showForThrowable(titulo, throwable);
		excepDialog.showDetails(detalles);	
	}
	
	/**
	 * Devuelve el directio base de la aplicacion.
	 * Recordemos que dentro de Eclipse 'user.dir' no contempla
	 * el directio 'bin', mientras a nivel de SO sí se contempla.
	 * En cualquier caso, no se devuelve el directorio bin.
	 *
	 */
	public static String getDirBaseApp() {
		
		String strAppPath=System.getProperty("user.dir");
		
		if (strAppPath.endsWith("bin"))
			strAppPath=strAppPath.substring(0, strAppPath.length()-4);
		
		return strAppPath;
	}
}
