package gui.gestion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.jdesktop.application.Application;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class FormNuevaFila extends JDialog  {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, lblTitulo;
	private JTextField txtDireccion, txtLocalidad, txtComision, txtPrecio;
	private JButton btnCancelar, btnAceptar;
	private JRadioButton rbtnPiscinaNO, rbtPiscinaSI;
	
	private TablaPisosPropietario tabla;
	private String restriccion;

	public FormNuevaFila(TablaPisosPropietario tabla, String restriccion) {
		this.setAlwaysOnTop(true);
		this.tabla = tabla;
		this.restriccion = restriccion;
		initGUI();
	}

	private void initGUI() {		
									
		setTitle("Datos para el nuevo piso");
		setSize(400, 280);
		getContentPane().setLayout(null);
		// Centrar la venta
		setLocationRelativeTo(null);		

		{
			lblTitulo = new JLabel();
			getContentPane().add(lblTitulo);
			lblTitulo.setLayout(null);
			lblTitulo.setBounds(17, 12, 334, 24);
			lblTitulo.setName("lblTitulo");
		}

		{ // Direccion
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1);
			jLabel1.setLayout(null);
			jLabel1.setBounds(12, 51, 62, 14);
			jLabel1.setName("jLabel1");
		}
		{
			txtDireccion = new JTextField();
			getContentPane().add(txtDireccion);
			txtDireccion.setBounds(74, 48, 254, 21);
			txtDireccion.setName("txtDireccion");
		}

		{ // Localidad
			jLabel2 = new JLabel();
			getContentPane().add(jLabel2);
			jLabel2.setLayout(null);
			jLabel2.setBounds(12, 88, 62, 14);
			jLabel2.setName("jLabel2");
		}
		{
			txtLocalidad = new JTextField();
			getContentPane().add(txtLocalidad);
			txtLocalidad.setBounds(74, 85, 188, 21);
			txtLocalidad.setName("jTextField1");
		}
		{ // Piscina
			jLabel3 = new JLabel();
			getContentPane().add(jLabel3);
			jLabel3.setLayout(null);
			jLabel3.setBounds(12, 124, 44, 14);
			jLabel3.setName("jLabel3");
		}
		{
			rbtPiscinaSI = new JRadioButton("SI");
			getContentPane().add(rbtPiscinaSI);
			rbtPiscinaSI.setLayout(null);
			rbtPiscinaSI.setBounds(74, 122, 36, 18);
			rbtPiscinaSI.setName("rbtPiscinaSI");
		}
		{
			rbtnPiscinaNO = new JRadioButton("NO");
			getContentPane().add(rbtnPiscinaNO);
			rbtnPiscinaNO.setLayout(null);
			rbtnPiscinaNO.setName("rbtnPiscinaNO");
			rbtnPiscinaNO.setBounds(121, 122, 45, 18);
			rbtnPiscinaNO.setSelected(true);
		}
		{
			ButtonGroup group = new ButtonGroup();
			group.add(rbtPiscinaSI);
			group.add(rbtnPiscinaNO);
		}

		{ // Precio
			jLabel4 = new JLabel();
			getContentPane().add(jLabel4);
			jLabel4.setLayout(null);
			jLabel4.setBounds(12, 160, 86, 14);
			jLabel4.setName("jLabel4");
		}

		{
			txtPrecio = new JTextField();
			getContentPane().add(txtPrecio);
			txtPrecio.setBounds(110, 157, 64, 21);
			txtPrecio.setName("jTextField2");
		}

		{ // Comision
			jLabel5 = new JLabel();
			getContentPane().add(jLabel5);
			jLabel5.setLayout(null);
			jLabel5.setBounds(202, 160, 90, 14);
			jLabel5.setName("jLabel5");
		}

		{
			txtComision = new JTextField();
			getContentPane().add(txtComision);
			txtComision.setBounds(289, 157, 62, 21);
			txtComision.setName("jTextField3");
		}

		{
			btnCancelar = new JButton();
			getContentPane().add(btnCancelar);
			btnCancelar.setLayout(null);
			btnCancelar.setBounds(12, 200, 110, 30);
			btnCancelar.setName("btnCancelar");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					FormNuevaFila.this.setVisible(false);
					FormNuevaFila.this.dispose();
				}
			});
		}
		
		{
			btnAceptar = new JButton();
			getContentPane().add(btnAceptar);
			btnAceptar.setLayout(null);
			btnAceptar.setBounds(268, 200, 110, 30);
			btnAceptar.setName("btnAceptar");
			btnAceptar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					
					if (checkFields()) {
						Vector<Object> piso = new Vector<Object>(7);
						piso.add(null);
						piso.add(txtDireccion.getText());
						piso.add(txtLocalidad.getText());
						piso.add(rbtPiscinaSI.isSelected());
						piso.add(Float.parseFloat(txtPrecio.getText()));
						piso.add(Float.parseFloat(txtComision.getText()));
						piso.add(restriccion);
						tabla.getModeloTabla().insertRecord(piso);
						FormNuevaFila.this.setVisible(false);
						FormNuevaFila.this.dispose();
					}
				}
			});
		}				
			    
		Application.getInstance().getContext().getResourceMap(getClass())
			.injectComponents(getContentPane());
		
	}

	/*
	 * Verificar que los campos tienen valor
	 * y el formato adecuado
	 */
	private boolean checkFields() {
		
		// inicializar el array de errores
		ArrayList<String> msg = new ArrayList<String>(0);
		
		// Comprobar si la direccion esta en blanco
		if (txtDireccion.getText() == null
				|| txtDireccion.getText().equals("")) {
			msg.add("La direccion no puede estar en blanco");
		}
		
		if (txtLocalidad.getText() == null
				|| txtLocalidad.getText().equals("")) {
			msg.add("La localidad no puede estar en blanco");
		}
		
		if (txtPrecio.getText() == null
				|| txtPrecio.getText().equals("")) {
			msg.add("El precio no puede estar en blanco");
			// Comprobar que el precio sigue el formato numerico xxx.xx
		} else if (!txtPrecio.getText().matches(
				"[\\d]{1,3}(\\.[\\d]{1,2})?")) {
			msg.add("El precio debe seguir el formato: xxx.xx");
		}
		
		if (txtComision.getText() == null
				|| txtComision.getText().equals("")) {
			msg.add("La comision no puede estar en blanco");
			// Comprobar que el precio sigue el formato numerico xxx.xx
		} else if (!txtComision.getText().matches(
				"[\\d]{1,3}(\\.[\\d]{1,2})?")) {
			msg.add("La comision debe seguir el formato: xxx.xx");
		}
		
		// Mostrar mensaje en caso de error
		if (!msg.isEmpty()) {
			JOptionPane.showMessageDialog(this, msg.toArray());
			return false;
		}
		return true;
	}
}
