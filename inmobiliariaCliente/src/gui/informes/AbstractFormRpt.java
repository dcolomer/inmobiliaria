package gui.informes;

import gui.gestion.Conexion;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;

import org.apache.log4j.Logger;
import org.jdesktop.application.Application;

import servutiles.ServiciosInmobiliariaFactory;
import utiles.Misc;

import ws.ServiciosInmobiliaria;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public abstract class AbstractFormRpt extends javax.swing.JInternalFrame {

	private static final long serialVersionUID = 1L;
	private static final String HOST_NAME_FILE = "host.properties";
	private static String URL=Misc.getBaseDatosURL(HOST_NAME_FILE);
		
	private String reportName;
		
	protected static ServiciosInmobiliaria serviciosInmobiliaria;

	protected final Logger LOG = Logger.getLogger(getClass().getName());
	
	protected JButton btnSalir, btnInforme;
	protected JComboBox cmbIni, cmbFin;
		
	protected JSeparator jSeparator1;
	protected JLabel lblIni, lblFin;

	static {
		serviciosInmobiliaria=
			ServiciosInmobiliariaFactory.getServicios();
		
		Conexion.setURL(URL);
	}
	
	protected AbstractFormRpt(String titulo, int ancho, int alto, String reportName) {
		super(titulo);	
		this.reportName=reportName;		
		initGUI(ancho, alto);
	}
	
	protected void initGUI(int ancho, int alto) {
		
		setFrameIcon(new javax.swing.ImageIcon(getClass()
				.getResource("resources/icons/rpts16.png")));		
		
		this.setPreferredSize(new java.awt.Dimension(ancho, alto));
		this.setBounds(0, 0, ancho, alto);
		setVisible(true);
		getContentPane().setLayout(null);
		
		{
			lblIni = new JLabel();
			getContentPane().add(lblIni, "Center");
			lblIni.setLayout(null);
			lblIni.setName("lblIni");
			lblIni.setBounds(17, 19, 140, 19);
		}			
		{
			lblFin = new JLabel();
			getContentPane().add(lblFin, "Center");
			lblFin.setLayout(null);
			lblFin.setBounds(314, 19, 140, 18);
			lblFin.setName("lblFin");
		}
		
		{
			btnInforme = new JButton();
			getContentPane().add(btnInforme);
			btnInforme.setLayout(null);
			btnInforme.setBounds(17, 112, 85, 42);
			btnInforme.setName("btnInforme");
			btnInforme.setToolTipText("Generar informe");
			btnInforme.setHorizontalTextPosition(SwingConstants.CENTER);
			btnInforme.setVerticalTextPosition(SwingConstants.BOTTOM);				
			btnInforme.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					btnInformeActionPerformed(evt);
				}					
			});
		}
		{
			btnSalir = new JButton();								
			btnSalir.setLayout(null);
			btnSalir.setBounds(448, 111, 85, 42);
			btnSalir.setName("btnSalir");
			btnSalir.setToolTipText("Cerrar informe");
			btnSalir.setHorizontalTextPosition(SwingConstants.CENTER);
			btnSalir.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnSalir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					salir();
				}
			});
			getContentPane().add(btnSalir);
		}
		{				
			ComboBoxModel cmbIniModel = 
				new DefaultComboBoxModel(cargarCombo());
			cmbIni = new JComboBox();
			cmbIni.setModel(cmbIniModel);
			cmbIni.setBounds(17, 44, 188, 21);
			getContentPane().add(cmbIni);
			
			cmbIni.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					String init=cmbIni.getItemAt(0).toString();
					if (init.equals("Seleccionar"))
						cmbIni.removeItem("Seleccionar");						
				}
			});
		}
		
		{
			ComboBoxModel cmbFinModel = 
				new DefaultComboBoxModel(cargarCombo());
			cmbFin = new JComboBox();
			cmbFin.setModel(cmbFinModel);
			cmbFin.setBounds(314, 44, 188, 21);
			getContentPane().add(cmbFin);
			
			cmbFin.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					String init=cmbFin.getItemAt(0).toString();
					if (init.equals("Seleccionar"))
						cmbFin.removeItem("Seleccionar");						
				}
			});
		}
		
		{
			jSeparator1 = new JSeparator();
			jSeparator1.setBounds(17, 89, 515, 10);
			getContentPane().add(jSeparator1);
		}
				
		Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
		
	}
	
	
	/*
	 * Cargar el combo con los datos. Las clases hijas deben implementar este metodo
	 */
	protected abstract String[] cargarCombo();	

	/*
	 * Manejador para el boton 'Informe'
	 */
	protected void btnInformeActionPerformed(ActionEvent evt) {
		
		// Obtenemos el elemento del combo
		String ini=cmbIni.getSelectedItem().toString();
		
		//int indexFin=cmbFin.getSelectedIndex();
		String fin=cmbFin.getSelectedItem().toString();
		
		if (ini.equals("Seleccionar"))
			ini="1";
		
		if (fin.equals("Seleccionar"))
			fin="9999999999";
		
		generarInforme(ini, fin);
		 
	}
	
	protected void generarInforme(Object ini, Object fin) {
				
		String strRutaOrigenInf=Misc.getDirBaseApp();
							
		strRutaOrigenInf+="\\informes\\";		
		
		Map<String,Object> parametros=new HashMap<String,Object>(); 
				
		parametros.put("parameter1", ini);
		parametros.put("parameter2", fin);
		
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		try {
			
			JasperFillManager.fillReportToFile(strRutaOrigenInf+this.reportName+".jasper", parametros, Conexion.getConexion());			
			JasperExportManager.exportReportToPdfFile(strRutaOrigenInf+this.reportName+".jrprint");
						
			try {
	        	File f=new File(strRutaOrigenInf+this.reportName+".pdf");
	        	Desktop.getDesktop().open(f);	
	        	
	        	// Eliminar el archivo .jrprint (el temporal)
	        	File fJrPrint=new File(strRutaOrigenInf+this.reportName+".jrprint");
	        	fJrPrint.delete();
	        	//final boolean jrpBorrado = !fJrPrint.exists() || fJrPrint.delete();
	        	
	        	String msg="Informe generado.";
				JOptionPane.showMessageDialog(this.getParent(), msg, 
						"Resultado", JOptionPane.INFORMATION_MESSAGE,null);
	        	
			} catch(IOException e2) {				
				this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				String msg="Informe NO generado. Se ha producido un problema al leer el fichero pdf";
				gestionarError(msg, e2);				
			}
			
		} catch (Exception e1) {			
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			String msg="Informe NO generado. Se ha producido un problema durante el proceso de generacion del informe";
			gestionarError(msg, e1);
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	protected void salir() {
		try {			
			setClosed(true);
		} catch (PropertyVetoException e) {						
			gestionarError("Error al cerrar el formulario de informes", e);
		}
	}

	protected void gestionarError(String msg, Exception e) {
		LOG.error(msg, e);			
		Misc.mostrarError(e, msg, false);
	}
}
