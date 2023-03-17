package gui.informes;

import gui.FormPrincipal;
import gui.utiles.DateButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JButton;

import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.jdesktop.application.Application;


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
public class FormRptPagosCajaReservasRealizadas extends AbstractFormRpt {

	private static final long serialVersionUID = 1L;
	
	private DateButton fechaIni, fechaFin;
	private Date fIni, fFin;
				
	public static FormRptPagosCajaReservasRealizadas createForm() {			
		return new FormRptPagosCajaReservasRealizadas();
	}
	
	private FormRptPagosCajaReservasRealizadas() {
		super("Informe de reservas realizadas entre fechas", 580, 200,"rptPagosReservas");
		
		/*
		 * Sobrescribimos el metodo iniGUI() de la clase padre
		 */
		initGUI(580,200);
	}
	
	@Override
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
			lblIni.setBounds(62, 19, 140, 19);
		}		
		
		{				
			fIni=new Date();
			fechaIni=new DateButton();
			fechaIni.setBounds(62, 38, 117, 21);
			
			/*
			 * Nos registramos al evento de cambio de fecha del DateButton.
			 * Cuando esto suceda, tomamos la nueva fecha y la volcamos en fIni
			 */
			fechaIni.addPropertyChangeListener("date", new PropertyChangeListener() {					
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					fIni=(Date) evt.getNewValue();						
				}
			});
			getContentPane().add(fechaIni);				
		}
		
		{
			lblFin = new JLabel();
			getContentPane().add(lblFin, "Center");
			lblFin.setLayout(null);
			lblFin.setBounds(385, 19, 140, 18);
			lblFin.setName("lblFin");
		}
								
		{				
			fFin=new Date();
			fechaFin=new DateButton();
			fechaFin.setBounds(385, 38, 117, 21);
			
			/*
			 * Nos registramos al evento de cambio de fecha del DateButton.
			 * Cuando esto suceda, tomamos la nueva fecha y la volcamos en fFin
			 */
			fechaFin.addPropertyChangeListener("date", new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					fFin=(Date) evt.getNewValue();						
				}
			});
			getContentPane().add(fechaFin);
		}
		
		{
			btnInforme = new JButton();				
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
			getContentPane().add(btnInforme);
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
			jSeparator1 = new JSeparator();
			jSeparator1.setBounds(17, 89, 515, 10);
			getContentPane().add(jSeparator1);
		}
				
		Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
	}
	
	@Override
	protected String[] cargarCombo() {	// Para este formulario no necesitamos combos 
		String msg=this.getClass()
			.getName()+" no soporta el metodo cargarCombo()";
		Exception e=new UnsupportedOperationException(msg);
		gestionarError(msg, e);
		return null;
	}
	
	
	@Override
	protected void btnInformeActionPerformed(ActionEvent evt) {		
		generarInforme(fIni, fFin);						 
	}
	
	protected void salir() {
		super.salir();
		FormPrincipal.setFrmInformePagos_ReservasEntreFechasActivo(false);
	}	
}
