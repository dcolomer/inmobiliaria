package gui;


import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.*;

import servicios.ServiciosInmobiliariaServer;

/**
 * Clase con dos finalidades:
 * 
 * -Por un lado pone en marcha el programa servidor, 
 * lanzando el Servicio Web y permitiendo que se puedan 
 * conectar a él las aplicaciones cliente.
 *   
 * -Por otro lado crea un icono en la bandeja del sistema
 * para permitir que el usuario del programa servidor
 * pueda interactuar con la aplicacion a traves de
 * de un menu grafico.
 * 
 * Las opciones posibles son:
 * --Acerca de...
 * --Consulta del fichero de LOG del servidor
 * --Cerrar la aplicacion
 *
 */
public class Servidor {
	
	private static final String LOG_PROPERTIES_FILE = "log4j.properties";
	static Properties logProperties;
	static String rutaLogServidor, nomLogServidor;	
	
	/**
	 * En el inicializador estatico cargamos el fichero de propiedades
	 * de Log4j del classpath y leemos cuál es la ruta y el nombre del 
	 * fichero de log del servidor 
	 */
	static {
		logProperties = new Properties();
		try {
			// Lectura
			logProperties.load(Servidor.class.getClassLoader()
					.getResourceAsStream(LOG_PROPERTIES_FILE));
			// Obtencion de la ruta
			rutaLogServidor=logProperties.getProperty("log.dir");
			// Obtencion del nombre especifico para el log del servidor
			nomLogServidor=logProperties.getProperty("log4j.appender.defaultLog.File");
			// Nos quedamos con la cadena despues de la barra
			int pos=nomLogServidor.lastIndexOf("/");
			nomLogServidor=nomLogServidor.substring(pos);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	}
    public static void main(String[] args) {        
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");            
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        
        // Invocamos al metodo main de la clase que lanza el Servicio Web
        ServiciosInmobiliariaServer.main(null);
        
        // Creamos el icono en la bandeja del sistema. Este icono presenta
        // un menu con cierta opciones para el usuario
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    /**
     * Creacion de la interfaz del menu
     */
    private static void createAndShowGUI() {
        // comprobar si el SO soporta iconos en la bandeja del sistema
        if (!SystemTray.isSupported()) {
            System.out.println("El Sistema Operativo no admite iconos en la bandeja del sistema.");
            return;
        }
        
        // Definimos el icono que sera agregado a la bandeja del sistema
        final TrayIcon trayIcon =
                new TrayIcon(crearImagen("inmob.png", "INMOB 10"));
        
        // Definimos la bandeja del sistema
        final SystemTray tray = SystemTray.getSystemTray();
        
        // Definir un menu tipo pop-up
        final PopupMenu popup = new PopupMenu();
        
        // Componentes del menu
        MenuItem tituloItem = new MenuItem("INMOB 10");
        tituloItem.setFont(new Font("SansSerif", Font.BOLD, 14));
        tituloItem.setEnabled(false);
        MenuItem acercaDeItem = new MenuItem("Acerca de...");               
        MenuItem logItem = new MenuItem("Ver log");        
        MenuItem salirItem = new MenuItem("Salir");
        
        // Anadir los elementos de menu al mismo
        popup.add(tituloItem);
        popup.addSeparator();
        popup.add(acercaDeItem);        
        popup.add(logItem);                    
        popup.add(salirItem);
        
        // Vinculamos el menu al icono de la bandeja del sistema
        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip("INMOB 10 - Servidor");
        
        try { // Relacionamos el icono a la bandeja del sistema
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("Error al agregar la aplicacion a la bandeja del sistema.");
            return;
        }
                
        /**
         * Al hacer doble clic sobre el icono en la bandeja del sistema
         * mostramos un cuadro de dialogo informando que el servidor
         * esta en marcha
         */
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "INMOB 1.0 - Programa servidor en funcionamiento!");
            }
        });
        
        /**
         * Al seleccionar la opcion de menu 'Acerca de...'
         * mostramos un cuadro de dialogo con los creditos
         */
        acercaDeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "INMOB 1.0 - (c) 2010 DCT -Programa servidor-");
            }
        });
        
        /**
         * Al seleccionar la opcion de menu 'Ver log'
         * cargamos la clase VisorLog que muestra en una
         * caja de texto multilinea el fichero de log del servidor
         */
        logItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	new VisorLog(rutaLogServidor, 
            			nomLogServidor).setVisible(true);
            }
        });
        
        /**
         * Al seleccionar la opcion de menu 'Salir'
         * preguntamos al usuario que confirme el cierre
         * del servidor
         */
        salirItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int respuesta = JOptionPane.showConfirmDialog(null,
            			"Esta acción cerrará el programa servidor, ¿desea continuar?",
            			"Atención",
            			JOptionPane.YES_NO_OPTION);

            			if (respuesta != JOptionPane.YES_OPTION) {
            				return;
            			}
            			tray.remove(trayIcon); // quitar el icono de la bandeja del sistema
            			System.exit(0);
            }
        });
    }
    
    // Leer el icono desde el fichero en disco
    protected static Image crearImagen(String path, String description) {
        URL imageURL = Servidor.class.getResource(path);
        
        if (imageURL == null) {
            System.err.println("Recurso grafico no encontrado: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
    
}
