package gui;

import gui.utiles.Login;

import java.awt.Dialog.ModalityType;

import org.apache.log4j.Logger;
import org.jdesktop.application.SingleFrameApplication;

import utiles.Misc;

/**
 * Clase abstracta que:
 * -Carga las propiedades de Log4j para la aplicacion cliente
 * -Crea una instancia de la clase GUIBase, la cual lleva a
 * cabo toda la creacion de la GUI de la ventana principal
 * -Muestra el formulario de login 
 *
 */
public class AbstractFormPrincipal extends SingleFrameApplication {
	
	private static final String LOG_PROPERTIES_FILE = "log4j.properties";
	protected final static Logger LOG;
	
	protected GUIBase guiBase;
	
	static {
		LOG = Logger.getLogger(AbstractFormPrincipal.class);
		Misc.loadConfigLogging(AbstractFormPrincipal.class, LOG_PROPERTIES_FILE);			
	}
		
	@Override
	protected void startup() {		
		
		// Crear la interfaz grafica de la ventana principal
		guiBase=new GUIBase();
		
		getMainFrame().setJMenuBar(guiBase.getMenuBar());				
		//getMainFrame().setSize(800, 600);
		
		// Mostrar la ventana principal
		show(guiBase.getTopPanel());
		
		// Mostrar la pantalla de login en primer
		// plano y modal
		formLogin();							
	}
	

	/*
	 * Metodo que se ejecuta cuando finalmente se sale
	 * de la aplicacion.
	 * La llamada a super.shutdown() salva el estado
	 * de la sesion: medida y posicion de la ventana,etc
	 */
	@Override
    protected void shutdown() {        
        super.shutdown();        
        LOG.info("Aplicacion finalizada");
    }

	
	// Mostrar la pantalla de login
	private void formLogin() {
		
		Login login=new Login(getMainFrame(),"Acceso al sistema", ModalityType.APPLICATION_MODAL);
		login.setVisible(true);
		
		// Si el usuario no es root desactivamos
		// el menu 'Usuarios de la aplicacion'
		String tit=getMainFrame().getTitle();
		String slogin=tit.split(":")[1].toString().trim();
		if (!slogin.equals("root"))
			guiBase.getUsuariosItem().setEnabled(false);
		
	}	
		
}
