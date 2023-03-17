package gui.pagos;

import gui.FormPrincipal;
import gui.utiles.DateButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.Collection;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import javax.xml.datatype.XMLGregorianCalendar;

import org.jdesktop.application.Application;

import utiles.Misc;
import ws.CajaYaCerradaException_Exception;
import ws.Piso;
import ws.PisoOcupadoException_Exception;
import ws.ServiciosException;
import ws.ServiciosException_Exception;


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
public class FormReservarPiso extends AbstractFormBasePagos {

	private static final long serialVersionUID = 1L;
	
	private DateButton fechaEntrada, fechaSalida;
	private JButton btnSalir, btnReservar, btnComprobarDisp;
	private JComboBox cmbPisosDisp, cmbNIFCliente;
	private ComboBoxModel cmbPisosDispModel;
	private JSeparator jSeparator1;	
	private JLabel info1, info2, info3, lblPisosDisponibles, lblSalida, lblEntrada;
		
	public static FormReservarPiso createForm() {			
		return new FormReservarPiso();
	}
	
	private FormReservarPiso() {
		super();				
		initGUI();
	}
	
	private void initGUI() {
		
		setFrameIcon(new javax.swing.ImageIcon(getClass()
				.getResource("resources/icons/reserva16.png")));
		
		this.setPreferredSize(new java.awt.Dimension(557, 305));
		this.setBounds(0, 0, 557, 305);
		setVisible(true);
		getContentPane().setLayout(null);
		
		{
			lblEntrada = new JLabel();
			getContentPane().add(lblEntrada, "Center");
			lblEntrada.setLayout(null);
			lblEntrada.setName("lblEntrada");
			lblEntrada.setBounds(20, 39, 59, 19);
		}			
		{
			fechaEntrada=new DateButton();
			fechaEntrada.setBounds(91, 38, 117, 21);
			getContentPane().add(fechaEntrada);				
		}						
		{
			lblSalida = new JLabel();
			getContentPane().add(lblSalida);
			lblSalida.setLayout(null);
			lblSalida.setBounds(335, 39, 59, 18);
			lblSalida.setName("lblSalida");
		}
		{
			fechaSalida=new DateButton();
			fechaSalida.setBounds(418, 38, 117, 21);
			getContentPane().add(fechaSalida);
		}
		{
			lblPisosDisponibles = new JLabel();
			getContentPane().add(lblPisosDisponibles);
			lblPisosDisponibles.setLayout(null);
			lblPisosDisponibles.setBounds(335, 79, 122, 14);
			lblPisosDisponibles.setName("lblPisosDisponibles");
		}
		
		{
			btnComprobarDisp = new JButton();
			btnComprobarDisp.setBounds(20, 105, 188, 25);
			btnComprobarDisp.setName("btnComprobarDisp");
			btnComprobarDisp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					btnComprobarDispActionPerformed(evt);
				}
			});	
			getContentPane().add(btnComprobarDisp);
		}
		
		{
			cmbPisosDispModel = 
				new DefaultComboBoxModel(
						new String[] { "?" });
			cmbPisosDisp = new JComboBox();
			cmbPisosDisp.setModel(cmbPisosDispModel);
			cmbPisosDisp.setBounds(335, 107, 200, 21);
			getContentPane().add(cmbPisosDisp);
		}
		{
			btnReservar = new JButton();
			getContentPane().add(btnReservar);
			btnReservar.setLayout(null);
			btnReservar.setBounds(19, 219, 90, 42);
			btnReservar.setName("btnReservar");
			btnReservar.setToolTipText("Realizar la reserva");
			btnReservar.setHorizontalTextPosition(SwingConstants.CENTER);
			btnReservar.setVerticalTextPosition(SwingConstants.BOTTOM);				
			btnReservar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					btnReservarActionPerformed(evt);
				}
			});
		}
		{
			btnSalir = new JButton();								
			btnSalir.setLayout(null);
			btnSalir.setBounds(450, 218, 85, 42);
			btnSalir.setName("btnSalir");
			btnSalir.setToolTipText("Cerrar la gestion de reserva de pisos");
			btnSalir.setHorizontalTextPosition(SwingConstants.CENTER);
			btnSalir.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnSalir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						FormPrincipal.setFrmReservarPisoActivo(false);
						setClosed(true);
					} catch (PropertyVetoException e) {
						gestionarError("Error en el cierre de formulario", e);						
					}
				}
			});
			getContentPane().add(btnSalir);
		}
		{
			ComboBoxModel cmbNIFClienteModel = 
			new DefaultComboBoxModel(getNIFClientes());
			cmbNIFCliente = new JComboBox();
			cmbNIFCliente.setModel(cmbNIFClienteModel);
			cmbNIFCliente.setBounds(20, 169, 188, 21);
			getContentPane().add(cmbNIFCliente);
		}
		{
			jSeparator1 = new JSeparator();
			jSeparator1.setBounds(20, 202, 515, 10);
			getContentPane().add(jSeparator1);
		}
		{
			info1 = new JLabel();
			info1.setBounds(20, 5, 306, 28);
			info1.setName("info1");
			getContentPane().add(info1);
		}
		{
			info2 = new JLabel();
			info2.setName("info2");
			info2.setBounds(20, 72, 249, 28);
			getContentPane().add(info2);
		}
		{
			info3 = new JLabel();
			info3.setName("info3");
			info3.setBounds(20, 141, 249, 28);
			getContentPane().add(info3);
		}	
				
		setTitle("Reserva de piso");

		Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
		
	}
	
	
	/*
	 * Manejador para el boton 'Comprobar disponibilidad'
	 */
	private void btnComprobarDispActionPerformed(ActionEvent evt) {
		
		// Pasar de java.util.Date a XMLGregorianCalendar
		XMLGregorianCalendar xmlIn=Misc.date2XmlGC(fechaEntrada.getDate());
		XMLGregorianCalendar xmlOut=Misc.date2XmlGC(fechaSalida.getDate());			
		
		Collection<Piso> pisos=serviciosInmobiliaria.getPisosDisponibles(xmlIn, xmlOut);
		
		cmbPisosDisp.removeAllItems();
		
		if (!pisos.isEmpty()) {		
			for (Piso piso:pisos) {
				cmbPisosDisp.addItem(piso.getNPiso() + " - " + piso.getDir());
			}
		} else {
			if (cmbPisosDispModel.getSize()==0) // si no hay pisos mostrar el '?'
				cmbPisosDisp.addItem(crearObj("?"));
			if (LOG.isDebugEnabled())
				LOG.debug("No hay pisos disponibles para las fechas "
					+"especificadas: ["+fechaEntrada.getDate()+" ,"+fechaSalida.getDate());
		}
	}
	
	/*
	 * Manejador para el boton 'Reservar'
	 */
	private void btnReservarActionPerformed(ActionEvent evt) {
		
		// Obtener el indice del combo de pisos disponibles.
		// Si vale -1 significa que no se ha seleccionado nada
		int indexPD=cmbPisosDisp.getSelectedIndex();
		
		// Obtenemos el elemento del combo
		String piso_sel=cmbPisosDisp.getSelectedItem().toString();
		
		// Si efectivamente se ha seleccionado algun piso...
		if (indexPD>-1 && !piso_sel.equals("?"))	{
						
			// Nos quedamos solo con el numero de piso
			piso_sel=piso_sel.split("-")[0].trim();
			if (LOG.isDebugEnabled())
				LOG.debug("Piso numero: "+piso_sel);
			
			// Lo pasamos a numero
			Integer int_piso_sel=Integer.valueOf(piso_sel);
			
			// Ahora obtenemos el nif del cliente que quiere reservarlo
			
			// Obtener el indice del combo de NIF's de clientes.
			// Si vale -1 significa que no se ha seleccionado nada
			int indexNF=cmbNIFCliente.getSelectedIndex();
			
			// Obtenemos el elemento del combo
			String nif_cli=cmbNIFCliente.getSelectedItem().toString();
			
			// Si efectivamente se ha seleccionado algun nif...
			if (indexNF>-1 && !nif_cli.equals("")) {
				if (LOG.isDebugEnabled())
					LOG.debug("NIF del cliente: "+nif_cli);
				
				// Pasar de java.util.Date a XMLGregorianCalendar
				XMLGregorianCalendar xmlIn=Misc.date2XmlGC(fechaEntrada.getDate());
				XMLGregorianCalendar xmlOut=Misc.date2XmlGC(fechaSalida.getDate());
				
				// Procedemos a la reserva
				try {
					long num_factura=serviciosInmobiliaria.reservarPiso(nif_cli, xmlIn, xmlOut, int_piso_sel);
					
					if (num_factura>0) {
						String msg="Reserva realizada correctamente. Factura: "+num_factura+ "\n"
						+"Importe a cobrar: "+serviciosInmobiliaria.getFactura(num_factura).getImporte()+"€";		
						
						JOptionPane.showMessageDialog(this.getParent(), msg, 
								"Resultado de la reserva", JOptionPane.INFORMATION_MESSAGE, null);
						
						limpiarPantalla(indexPD);
						
					} else {
						String msg="Ha ocurrido algun problema al reservar el piso.";
						Exception e=new ServiciosException_Exception(msg, new ServiciosException());
						gestionarError(msg,e);						
					}
					
				} catch (PisoOcupadoException_Exception e) {					
					String msg="Reserva NO realizada. El piso ya se encuentra reservado.";					
					gestionarError(msg,e);										
				} catch (CajaYaCerradaException_Exception e) {
					String msg="Reserva NO realizada. La caja ya se encuentra cerrada.";																			
					gestionarError(msg,e);
				} catch (Exception e) {
					String msg="Reserva NO realizada.";					
					gestionarError(msg,e);
				}
			}			
		}	
		else {
			if (LOG.isDebugEnabled())
				LOG.debug("No hay pisos disponibles para las fechas especificadas");
		}	
		
	}
	
	// Eliminar el piso del combo - limpiar pantalla -
	private void limpiarPantalla(int indexPD) {		
		cmbPisosDisp.removeItemAt(indexPD);
		if (cmbPisosDispModel.getSize()==0) // si ya no quedan pisos
			cmbPisosDisp.addItem(crearObj("?"));
		
	}
	
}
