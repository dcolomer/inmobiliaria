package gui.gestion;

import java.sql.*;

import org.apache.log4j.Logger;

import utiles.Misc;


/**
 * Clase Conexion 
 * Tiene un metodo estatico que da una conexion con la BD 
 * Se utiliza el patron SINGLETON: 
 * Si ya existe un objeto se devuelve y si no se crea uno
 */

public class Conexion {
	private static Connection conDB = null;
	private static String DBUrl;
	private static final Logger LOG = Logger.getLogger(Conexion.class);

	// Metodo publico para establecer la cadena de conexion
	public static void setURL(String dburl) {
		DBUrl = dburl;
	}

	/** Devuelve un objeto del tipo Connection con la base de datos */
	public static Connection getConexion() {
		// Si no existe la conexion entonces se crea
		if (conDB == null) {
			try {
				// Registamos el driver
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (Exception e) {
				System.out.println("No se ha encontrado el driver JDBC");
			}

			try {
				// Obtenemos la conexion
				conDB = DriverManager.getConnection(DBUrl);
			} catch (SQLException sqle) {
				System.out.println("SQLException: " + sqle.getMessage());
				System.out.println("SQLState: " + sqle.getSQLState());
				System.out.println("VendorError: " + sqle.getErrorCode());
			}
		}
		// Devolvemos un objeto Connection
		return conDB;
	}

	/** Desconecta de la base de datos */

	public static void desconecta() {
		if (conDB != null) {
			try {
				if (!conDB.isClosed())
					conDB.close();
				conDB=null;
			} catch (SQLException e) {
				LOG.error(e);
				Misc.mostrarError(e, "Error de acceso a datos", false);
			}
		}
	}
		
}