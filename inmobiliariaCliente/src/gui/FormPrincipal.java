package gui;

import gui.gestion.FormClientes;
import gui.gestion.FormPisos;
import gui.gestion.FormPropietarios;
import gui.gestion.FormUsuarios;
import gui.informes.FormRptClientes;
import gui.informes.FormRptPagosCajaReservasRealizadas;
import gui.informes.FormRptPisos;
import gui.informes.FormRptPropietarios;
import gui.pagos.FormCancelarPiso;
import gui.pagos.FormPagarPiso;
import gui.pagos.FormPagarPropietario;
import gui.pagos.FormReservarPiso;
import gui.utiles.AcercaDe;
import gui.utiles.ReLogin;

import java.awt.Cursor;
import java.awt.Dialog.ModalityType;

import java.text.NumberFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jdesktop.application.Action;

import servutiles.ServiciosInmobiliariaFactory;
import utiles.Misc;

import ws.ServiciosInmobiliaria;

/**
 * Clase que:
 * -Es el punto de entrada a la aplicacion cliente mediante su metodo main()
 * -Controla que no se pueda cargar mas de un formulario hijo a la vez
 * -Implementa cada opcion de menu (las acciones) 
 */
public class FormPrincipal extends AbstractFormPrincipal {
			
	private static boolean frmUsuariosActivo;	
	private static boolean frmClientesActivo, frmPisosActivo, frmPropietariosActivo;
	private static boolean frmReservarPisoActivo, frmPagarPisoActivo, frmCancelarPisoActivo, frmPagarPropietarioActivo;
	private static boolean frmInformeClientesActivo, frmInformePropietariosActivo, frmInformePisosActivo;
	private static boolean frmInformePagos_ReservasEntreFechasActivo;		

	public static void main(String[] args) {				
		launch(FormPrincipal.class, args);
	}

	/*
	 * getters/setters
	 */
	
	public static boolean isFrmUsuariosActivo() { return frmUsuariosActivo;	}

	public static void setFrmUsuariosActivo(boolean frmUsuariosActivo) {
		FormPrincipal.frmUsuariosActivo = frmUsuariosActivo;
	}
	
	/*
	 * Gestion
	 */
	public static boolean isFrmClientesActivo() { return frmClientesActivo;	}
	public static boolean isFrmPropietariosActivo() { return frmPropietariosActivo;	}
	public static boolean isFrmPisosActivo() { return frmPisosActivo; }
	
	public static void setFrmClientesActivo(boolean frmClientesActivo) {
		FormPrincipal.frmClientesActivo = frmClientesActivo;
	}

	public static void setFrmPropietariosActivo(boolean frmPropietariosActivo) {
		FormPrincipal.frmPropietariosActivo = frmPropietariosActivo;
	}
	
	public static void setFrmPisosActivo(boolean frmPisosActivo) {
		FormPrincipal.frmPisosActivo = frmPisosActivo;
	}
	
	/*
	 * Pagos
	 */
	public static boolean isFrmReservarPisoActivo() { return frmReservarPisoActivo;	}
	public static boolean isFrmPagarPisoActivo() { return frmPagarPisoActivo;	}
	public static boolean isFrmCancelarPisoActivo() { return frmCancelarPisoActivo;	}
	public static boolean isFrmPagarPropietarioActivo() { return frmPagarPropietarioActivo;	}	
			
	public static void setFrmReservarPisoActivo(boolean frmReservarPisoActivo) {
		FormPrincipal.frmReservarPisoActivo = frmReservarPisoActivo;
	}
	
	public static void setFrmPagarPisoActivo(boolean frmPagarPisoActivo) {
		FormPrincipal.frmPagarPisoActivo = frmPagarPisoActivo;
	}
	
	public static void setFrmCancelarPisoActivo(boolean frmCancelarPisoActivo) {
		FormPrincipal.frmCancelarPisoActivo = frmCancelarPisoActivo;
	}
	
	public static void setFrmPagarPropietarioActivo(
			boolean frmPagarPropietarioActivo) {
		FormPrincipal.frmPagarPropietarioActivo = frmPagarPropietarioActivo;
	}	

	/*
	 * Informes
	 */
	
	public static boolean isFrmInformeClientesActivo() { return frmInformeClientesActivo; }
	public static boolean isFrmInformePropietariosActivo() { return frmInformePropietariosActivo; }
	public static boolean isFrmInformePisosActivo() { return frmInformePisosActivo;	}
	public static boolean isFrmInformePagos_ReservasEntreFechasActivo() {
		return frmInformePagos_ReservasEntreFechasActivo;
	}
			
	public static void setFrmInformeClientesActivo(boolean frmInformeClientesActivo) {
		FormPrincipal.frmInformeClientesActivo = frmInformeClientesActivo;
	}		

	public static void setFrmInformePropietariosActivo(
			boolean frmInformePropietariosActivo) {
		FormPrincipal.frmInformePropietariosActivo = frmInformePropietariosActivo;
	}
	
	public static void setFrmInformePisosActivo(boolean frmInformePisosActivo) {
		FormPrincipal.frmInformePisosActivo = frmInformePisosActivo;
	}
	
	public static void setFrmInformePagos_ReservasEntreFechasActivo(
			boolean frmInformePagos_ReservasEntreFechasActivo) {
		FormPrincipal.frmInformePagos_ReservasEntreFechasActivo = frmInformePagos_ReservasEntreFechasActivo;
	}
	
	/******************************************************************
	 * Acciones
	 *******************************************************************/
	
	
	
	/*
	 * Metodo privado que abre los formularios
	 */
	private void cargarForm(JInternalFrame formulario) {
		guiBase.getToolBar().setEnabled(false);
		guiBase.getMenuBar().setEnabled(false);
		this.getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));								
		guiBase.getContentPanel().add(formulario).setVisible(true);		
		this.getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		guiBase.getMenuBar().setEnabled(true);
		guiBase.getToolBar().setEnabled(true);
	}

	// Aplicacion
	
	@Action
	public void usuarios() {
		if (!frmUsuariosActivo) {
			frmUsuariosActivo=true;					
			cargarForm(FormUsuarios.createForm());								
		}
	}
	
	@Action
	public void relogin() {
		
		ReLogin relogin=new ReLogin(getMainFrame(),"Cambio de credenciales", ModalityType.APPLICATION_MODAL);
		relogin.setVisible(true);
		
		// Si el usuario no es root desactivamos
		// el menu 'Usuarios de la aplicacion'
		String tit=getMainFrame().getTitle();
		String slogin=tit.split(":")[1].toString().trim();
		if (!slogin.equals("root"))
			guiBase.getUsuariosItem().setEnabled(false);
		else
			guiBase.getUsuariosItem().setEnabled(true);
		
	}

	@Action
	public void salir() {
		exit();
	}
	
	
	// Gestion
	
	@Action
	public void clientes() {
		if (!frmClientesActivo) {
			frmClientesActivo=true;					
			cargarForm(FormClientes.createForm());								
		}	
	}
	
	@Action
	public void propietarios() {	
		if (!frmPropietariosActivo) {
			frmPropietariosActivo=true;
			cargarForm(FormPropietarios.createForm());				
		}
	}
	
	@Action
	public void pisos() {	
		if (!frmPisosActivo) {
			frmPisosActivo=true;
			cargarForm(FormPisos.createForm());				
		}
	}
	
	// Pagos
	
	@Action
	public void reservarpiso() {	
		if (!frmReservarPisoActivo) {
			frmReservarPisoActivo=true;
			//reservarButton.setEnabled(false);
			cargarForm(FormReservarPiso.createForm());			
			//reservarButton.setEnabled(true);			
		}
	}
	
	@Action
	public void pagarpiso() {	
		if (!frmPagarPisoActivo) {
			frmPagarPisoActivo=true;
			//pagarButton.setEnabled(false);
			cargarForm(FormPagarPiso.createForm());			
			//pagarButton.setEnabled(true);			
		}
	}
	
	@Action
	public void cancelarpiso() {	
		if (!frmCancelarPisoActivo) {
			frmCancelarPisoActivo=true;
			cargarForm(FormCancelarPiso.createForm());
		}
	}
	
	@Action
	public void pagarpropietariopiso() {	
		if (!frmPagarPropietarioActivo) {
			frmPagarPropietarioActivo=true;
			cargarForm(FormPagarPropietario.createForm());			
		}
	}
	
	@Action
	public void cerrarcaja() {	
		int opcion = JOptionPane.showConfirmDialog(null, "¿Cerrar la caja de hoy?", 
				"Cerrar caja diaria", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (opcion == JOptionPane.YES_NO_OPTION) {
			cerrarCaja();
		}
	}
	
	// Informes
	
	@Action
	public void rptclientes() {	
		if (!frmInformeClientesActivo) {
			frmInformeClientesActivo=true;
			cargarForm(FormRptClientes.createForm());				
		}
	}
	
	@Action
	public void rptpropietarios() {	
		if (!frmInformePropietariosActivo) {
			frmInformePropietariosActivo=true;
			cargarForm(FormRptPropietarios.createForm());			
		}
	}
	
	@Action
	public void rptpisos() {	
		if (!frmInformePisosActivo) {
			frmInformePisosActivo=true;
			cargarForm(FormRptPisos.createForm());			
		}
	}
	
	@Action
	public void rptpagosreservasentrefechas() {	
		if (!frmInformePagos_ReservasEntreFechasActivo) {
			frmInformePagos_ReservasEntreFechasActivo=true;
			cargarForm(FormRptPagosCajaReservasRealizadas.createForm());			
		}
	}
	
	
	// AYUDA
	
	@Action
	public void ayuda() {	
		// No aplica: Se soluciona en la clase base en cargarSistemaAyuda()
	}
	
	@Action
	public void acercade() {
		JFrame padre=(JFrame)this.getMainView().getFrame();
		new AcercaDe(padre);
	}
	
	/*
	 * 
	 */
	private void cerrarCaja() {
		
		ServiciosInmobiliaria serviciosInmobiliaria=
			ServiciosInmobiliariaFactory.getServicios();
		
		// Procedemos al cierre
		try {
			
			// Pasar de java.util.Date a XMLGregorianCalendar
			XMLGregorianCalendar xmlHoy=Misc.date2XmlGC(new Date());					
			
			float totalCaja=serviciosInmobiliaria.cerrarCaja(xmlHoy);
			String msg="Caja cerrada correctamente. Importe facturado: "+
				NumberFormat.getNumberInstance().format(totalCaja)+"€";		
			
			JOptionPane.showMessageDialog(null, msg, 
					"Resultado del cierre de caja", JOptionPane.INFORMATION_MESSAGE, null);	
			
		} catch (Exception e) {					
			String msg="Caja no cerrada. Comprobar que la caja no se encuentre ya cerrada.";
			JOptionPane.showMessageDialog(null, msg, 
					"Resultado del cierre de caja", JOptionPane.ERROR_MESSAGE,null);
			LOG.error(e);
			Misc.mostrarError(e, "Error al cerrar la caja.", false);
		}
					
	}			
		
}
