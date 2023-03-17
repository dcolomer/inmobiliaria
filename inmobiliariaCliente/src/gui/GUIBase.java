package gui;

import gui.gestion.Conexion;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.net.URL;
import java.util.EventObject;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Application.ExitListener;

import utiles.Misc;

/**
 * Clase que:
 * -Construye los paneles principales, el menu y la barra de botones.
 * -Lee el sistema de ayuda de disco y lo asocia al menu y a la tecla F1
 * -Incluye una clase privada que define el comportamiento deseado cuando
 * el usuario quiere abandonar la aplicacion. 
 *
 */
public class GUIBase {
	
	private final static Logger LOG=Logger.getLogger(GUIBase.class);
	
	public GUIBase() {
		crearPanelesMarco();		
		crearBarraBotones();
		crearMenu();				
		cargarSistemaAyuda();		
				
		// Manejador para cerrar la aplicacion principal
		Application.getInstance().addExitListener(new MiExitListener());
	}
		

	// Menus: Aplicacion, Gestion, Pagos
	private JMenu aplicacionMenu, gestionMenu, pagosMenu;
	private JMenu informesMenu, informesGestMenu, informesPagosMenu;
	private JMenu ayudaMenu;

	/*
	 * Elementos de menu
	 */
	
	// Elementos del menu Aplicacion
	private JMenuItem usuariosItem;
	public JMenuItem getUsuariosItem() { return usuariosItem; }
	private JMenuItem reloginItem;
	private JMenuItem salirItem;

	// Elementos del menu Gestion
	private JMenuItem clientesItem, propietariosItem, pisosItem;

	// Elementos del menu Pagos
	private JMenuItem reservarPisoItem, pagarPisoItem, cancelarPisoItem;
	private JMenuItem pagarPropietarioItem, cerrarCajaItem;

	// Elementos del menu Informes
	private JMenuItem rptClientesItem, rptPropietariosItem, rptPisosItem;
	private JMenuItem rptPagosCajaReservasEntreFechasItem;
		
	// Elementos del menu Ayuda
	private JMenuItem ayudaItem, acercaDeItem;	
	
	private JPanel topPanel; // Panel que contiene la barra de herramientas y el
	public JPanel getTopPanel() { return topPanel; }

	// espacio para los form. childs
	private JPanel toolBarPanel; // Panel para la barra de herramientas
	private JPanel contentPanel; // Panel para los form. childs		
	public JPanel getContentPanel() { return contentPanel; }

	protected JToolBar toolBar; // Barra de herramientas
	public JToolBar getToolBar() { return toolBar; }

	private JButton clientesButton, pisosButton;
	private JButton reservarButton;
	private JButton pagarButton;
	private JButton cierreCajaButton;
	private JButton salirButton; // Cuarto boton

	private JSeparator jSeparator; // Separador (horizontal) entre la barra de
									// herramientas y el espacio de los form.
									// childs

	private JMenuBar menuBar; // Barra de menu
	public JMenuBar getMenuBar() { return menuBar;	}


	/**
	 * Crear el panel base y del contenido
	 */
	private void crearPanelesMarco() {
		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		
		contentPanel = new JPanel();
		topPanel.add(contentPanel, BorderLayout.CENTER);
		contentPanel.setPreferredSize(new java.awt.Dimension(691, 449));
		contentPanel.setName("contentPanel");		
	}
	
	/**
	 * Crear la barra de botones
	 */
	private void crearBarraBotones() {
		toolBarPanel = new JPanel();
		topPanel.add(toolBarPanel, BorderLayout.NORTH);
		BorderLayout jPanel1Layout = new BorderLayout();
		toolBarPanel.setLayout(jPanel1Layout);
		{
			toolBar = new JToolBar();
			toolBarPanel.add(toolBar, BorderLayout.CENTER);
			
			{
				clientesButton = new JButton();
				toolBar.add(clientesButton);
				clientesButton.setAction(getAppActionMap().get("clientes"));
				clientesButton.setName("clientesButton");
				clientesButton.setText(null);
				clientesButton.setFocusable(false);						
			}
			
			{
				pisosButton = new JButton();
				toolBar.add(pisosButton);
				pisosButton.setAction(getAppActionMap().get("pisos"));
				pisosButton.setName("pisosButton");
				pisosButton.setText(null);
				pisosButton.setFocusable(false);						
			}
			
			{
				toolBar.addSeparator();
			}
			
			{
				reservarButton = new JButton();
				toolBar.add(reservarButton);
				reservarButton.setAction(getAppActionMap().get("reservarpiso"));
				reservarButton.setName("reservarButton");
				reservarButton.setText(null);
				reservarButton.setFocusable(false);						
			}
			{
				pagarButton = new JButton();
				toolBar.add(pagarButton);
				pagarButton.setAction(getAppActionMap().get("pagarpiso"));
				pagarButton.setName("pagarButton");
				pagarButton.setText(null);
				pagarButton.setFocusable(false);						
			}
			
			{
				cierreCajaButton = new JButton();
				toolBar.add(cierreCajaButton);
				cierreCajaButton.setAction(getAppActionMap().get("cerrarcaja"));
				cierreCajaButton.setName("cierreCajaButton");
				cierreCajaButton.setText(null);
				cierreCajaButton.setFocusable(false);						
			}
			
			{
				toolBar.addSeparator();
			}
			{
				salirButton = new JButton();
				toolBar.add(salirButton);
				salirButton.setAction(getAppActionMap().get("salir"));
				salirButton.setName("salirButton");
				salirButton.setText(null);
				salirButton.setFocusable(true);
			}
		}
		{
			jSeparator = new JSeparator();
			toolBarPanel.add(jSeparator, BorderLayout.SOUTH);
		}
		
	}
	
	/**
	 * Crear el menu 
	 */
	private void crearMenu() {
		menuBar = new JMenuBar();
		menuBar.setName("menuBar");
		
		{ // APLICACION
			aplicacionMenu = new JMenu();
			menuBar.add(aplicacionMenu);
			aplicacionMenu.setName("aplicacionMenu");	
			{
				usuariosItem = new JMenuItem();
				aplicacionMenu.add(usuariosItem);
				usuariosItem.setAction(getAppActionMap().get("usuarios"));
			}
			{
				aplicacionMenu.insertSeparator(1);
			}
				
			{
				reloginItem = new JMenuItem();
				aplicacionMenu.add(reloginItem);
				reloginItem.setAction(getAppActionMap().get("relogin"));
			}
			{
				aplicacionMenu.insertSeparator(3);
			}
			{
				salirItem = new JMenuItem();
				aplicacionMenu.add(salirItem);
				salirItem.setAction(getAppActionMap().get("salir"));
			}
		} // Fin APLICACION
		
		{ // GESTION
			gestionMenu = new JMenu();
			menuBar.add(gestionMenu);
			gestionMenu.setName("gestionMenu");
			{
				clientesItem = new JMenuItem();
				gestionMenu.add(clientesItem);
				clientesItem.setAction(getAppActionMap().get("clientes"));
			}
			{
				propietariosItem = new JMenuItem();
				gestionMenu.add(propietariosItem);
				propietariosItem.setAction(getAppActionMap().get("propietarios"));
			}				
			{
				pisosItem = new JMenuItem();
				gestionMenu.add(pisosItem);
				pisosItem.setAction(getAppActionMap().get("pisos"));
			}
		} // Fin GESTION
		
		{ // PAGOS
			pagosMenu = new JMenu();
			menuBar.add(pagosMenu);
			pagosMenu.setName("pagosMenu");
			{
				reservarPisoItem = new JMenuItem();
				pagosMenu.add(reservarPisoItem);
				reservarPisoItem.setAction(getAppActionMap().get("reservarpiso"));
			}
			{
				pagarPisoItem = new JMenuItem();
				pagosMenu.add(pagarPisoItem);
				pagarPisoItem.setAction(getAppActionMap().get("pagarpiso"));
			}
			{
				cancelarPisoItem = new JMenuItem();
				pagosMenu.add(cancelarPisoItem);
				cancelarPisoItem.setAction(getAppActionMap().get("cancelarpiso"));
			}				
			{
				pagosMenu.insertSeparator(4);
			}				
			{
				pagarPropietarioItem = new JMenuItem();
				pagosMenu.add(pagarPropietarioItem);
				pagarPropietarioItem.setAction(getAppActionMap().get("pagarpropietariopiso"));
			}				
			{
				pagosMenu.insertSeparator(6);
			}				
			{
				cerrarCajaItem = new JMenuItem();
				pagosMenu.add(cerrarCajaItem);
				cerrarCajaItem.setAction(getAppActionMap().get("cerrarcaja"));
			}
		} // Fin PAGOS
		
		{ // INFORMES
			informesMenu = new JMenu();
			menuBar.add(informesMenu);
			informesMenu.setName("informesMenu");	
			
			informesGestMenu=new JMenu();
			informesMenu.add(informesGestMenu);
			informesGestMenu.setName("informesGestMenu");
			
			informesPagosMenu=new JMenu();
			informesMenu.add(informesPagosMenu);
			informesPagosMenu.setName("informesPagosMenu");
			
				// Informes de gestion
			
				{ 
					rptClientesItem = new JMenuItem();
					informesGestMenu.add(rptClientesItem);
					rptClientesItem.setAction(getAppActionMap().get("rptclientes"));
				}				
				{
					rptPropietariosItem = new JMenuItem();
					informesGestMenu.add(rptPropietariosItem);
					rptPropietariosItem.setAction(getAppActionMap().get("rptpropietarios"));
				}				
				{
					rptPisosItem = new JMenuItem();
					informesGestMenu.add(rptPisosItem);
					rptPisosItem.setAction(getAppActionMap().get("rptpisos"));
				}
			
				// Informes de pagos
				{ 
					rptPagosCajaReservasEntreFechasItem = new JMenuItem();
					informesPagosMenu.add(rptPagosCajaReservasEntreFechasItem);
					rptPagosCajaReservasEntreFechasItem.setAction(getAppActionMap().get("rptpagosreservasentrefechas"));
				}								
		}  // Fin INFORMES
		
		{ // MENU DE AYUDA
			ayudaMenu = new JMenu();
			menuBar.add(ayudaMenu);
			ayudaMenu.setName("ayudaMenu");				
			{
				ayudaItem = new JMenuItem();
				ayudaMenu.add(ayudaItem);
				ayudaItem.setAction(getAppActionMap().get("ayuda"));
			}
			{					
				acercaDeItem = new JMenuItem();
				ayudaMenu.add(acercaDeItem);
				acercaDeItem.setAction(getAppActionMap().get("acercade"));
			}
		}
		
	}

	/**
	 * Cargar sistema de ayuda de la aplicacion
	 */
	private void cargarSistemaAyuda() {
		String strAyuda=Misc.getDirBaseApp();
		
		strAyuda+="\\ayuda\\help_set.hs";		
					
		try {
			// Carga el fichero de ayuda
			File fichero = new File(strAyuda);
			URL hsURL = fichero.toURI().toURL();

			if (hsURL!=null)
				if (LOG.isDebugEnabled())
					LOG.debug("Fichero con el sistema de ayuda cargado: "+strAyuda);
			
			// Crea el HelpSet y el HelpBroker
			HelpSet helpset = new HelpSet(AbstractFormPrincipal.class.getClassLoader(), hsURL);
			HelpBroker hb = helpset.createHelpBroker();

			// Pone ayuda a item de menu al pulsarlo y a F1 en ventana
			// principal y secundaria.
			hb.enableHelpOnButton(ayudaItem, "intro", helpset);
			hb.enableHelpKey(contentPanel.getParent(), "intro",
					helpset);
			/*hb.enableHelpKey(secundaria.getContentPane(), "ventana_secundaria",
					helpset);*/

		} catch (Exception e) {
			LOG.error(e);												
			Misc.mostrarError(e, "Error al cargar el sistema de Ayuda.", false);
		}
	}
	
	private ActionMap getAppActionMap() {
		return Application.getInstance().getContext().getActionMap(this);
	}

	/**
	 * Clase privada que implementa el comportamiento
	 * de la aplicacion principal a la hora de cerrar
	 * el programa
	 */
	private class MiExitListener implements ExitListener {

		@Override
		public boolean canExit(EventObject e) {
			Object source = (e != null) ? e.getSource() : null;
			Component owner = (source instanceof Component) ? (Component)source : null;
							
			/*
			 * Obtener el texto para el mensaje de salida desde el 
			 * fichero de recursos (FormPrincipal.properties)
			 */
			ApplicationContext ac = Application.getInstance().getContext();
		    String msg=ac.getResourceMap(getClass()).getString("msg_salir");
			
			int option = JOptionPane.showConfirmDialog(owner, msg, 
					"Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			return option == JOptionPane.YES_NO_OPTION;
		}

		@Override
		public void willExit(EventObject e) { 
	    	// Cerramos la conexion a la BD utilizada por los
	    	// formularios de Gestion y los reports, en el caso de que haya
	    	// sido utilizada
	    	Conexion.desconecta();
	    }
		
	}	
		
}
