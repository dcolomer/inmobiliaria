package dao;

import java.io.InputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.*;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

/**
 * Clase Singleton que carga las propiedades de 
 * la conexion con la base de datos desde un fichero XML.
 */
public class Configuracion {

	// Almacenar la única instancia de esta clase
	private static Configuracion config;
	
	private static final String CONFIG_FILENAME = "configDb.xml";		
	private static final Logger LOG=Logger.getLogger(Configuracion.class);
	
	// Atributos para almacenar las propiedades del DataSource
	private String dbDriverName, dbUser, dbPassword, dbURI;
	private int dbPoolMinSize, dbPoolMaxSize;
	
	/**
	 * Metodo de factoria estatico.
	 * 
	 * Si aun no existe la instancia de Configuracion la crea,
	 * y si ya existe la devuelve.
	 * 
	 * El método lo hacemos sincronizado para prevenir errores 
	 * con otros threads, ya que podría suceder que dos hilos
	 * lo ejecutasen a la vez y se creasen dos instancias de 
	 * esta clase.
	 */
	public static synchronized Configuracion getConfiguracion() {
		if (config==null) {
			config=new Configuracion();
			if (LOG.isDebugEnabled())
				LOG.debug("OBJETO Configuracion CONSTRUIDO");
		}	
		return config;
	}
	
	/**
	 * Constructor PRIVADO
	 */
	private Configuracion() {
		
		SAXBuilder builder = new SAXBuilder();

		try {
			
			// Cargar el fichero
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream(CONFIG_FILENAME);
			
			/*
			 * Analizarlo con el parser SAX. Obtenemos las propiedades 
			 * y las establecemos en los atributos de la clase. 
			 */
			Document doc = builder.build(is);
			Element root = doc.getRootElement();

			dbDriverName = root.getChild("dbDriverName").getTextTrim();
			dbUser = root.getChild("dbUser").getTextTrim();
			dbPassword = root.getChild("dbPassword").getTextTrim();
			dbURI = root.getChild("dbURI").getTextTrim();
			dbPoolMinSize = Integer.parseInt(root.getChild("dbPoolMinSize")
					.getTextTrim());
			dbPoolMaxSize = Integer.parseInt(root.getChild("dbPoolMaxSize")
					.getTextTrim());

			if (LOG.isDebugEnabled()) {
				LOG.debug("Fichero "+CONFIG_FILENAME+" cargado.");
				LOG.debug(this.toString());
			}
			
		} catch (Exception ex) {
			LOG.error("Error al leer el fichero de configuracion de la BD: "+CONFIG_FILENAME, ex);
			LOG.error("La aplicacion no puede continuar.");
			System.exit(1);
		}

	}

	/**
	 * getters
	 */
	public String getDbDriverName() { return dbDriverName; }
	public String getDbUser() {	return dbUser; }
	public String getDbPassword() {	return dbPassword; }
	public String getDbURI() { return dbURI; }
	public int getDbPoolMinSize() {	return dbPoolMinSize; }
	public int getDbPoolMaxSize() { return dbPoolMaxSize; }
	
	/**
	 * Usamos Apache Commons para imprimir con formato de facil 
	 * lecturalas propiedades de la BD leidas del fichero XML  
	 */
	public String toString() {
		ReflectionToStringBuilder tsb = new ReflectionToStringBuilder(this,
				ToStringStyle.MULTI_LINE_STYLE);
		return tsb.toString();
	}

	/**
	 * Evitar el clonado: un Singleton no debe poder clonarse
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
