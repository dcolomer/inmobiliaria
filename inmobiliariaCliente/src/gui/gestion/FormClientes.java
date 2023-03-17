package gui.gestion;

import gui.FormPrincipal;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import utiles.Misc;
import ws.ServiciosException_Exception;


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


public class FormClientes extends AbstractDaoForm {

	private static final long serialVersionUID = 1L;
	
	// Util para recuperar el cliente actual cuando el usuario hace
	// Nuevo o Modificar y despues se cancela, ya que como borramos 
	// el contenido del formulario no podriamos restaurarlo 
	private static String[] BUFFER_fields=new String[3];
	
	private JLabel jLabel1, jLabel2, jLabel3;	
	private JTextField jTextField1, jTextField2, jTextField3;
	private JToggleButton btnBuscar;
	
	/*
	 * 
	 */
	public static FormClientes createForm() {
		String SQL="SELECT * FROM cli ORDER BY nif_cli";		
		return new FormClientes(SQL);
	}
	
	/*
	 * Constructor
	 */

	private FormClientes(String SQL) {
		super(SQL, "Gestion de clientes", 515, 235);	
	}

	/*
	 * Eliminar cliente
	 */
	@Override protected void eliminar() {
		super.eliminar();		
		
		// Hay que actualizar la lista cacheada de clientes
		// en la capa de negocio		
		try {
			serviciosInmobiliaria.refreshClientesCacheados();
		} catch (ServiciosException_Exception e) {
			gestionarError(e);
		}
	}
	
	/*
	 * Grabar un cliente
	 */
	@Override protected void grabar() {
		int regActual=0;
		String nif = jTextField1.getText();
		String nombre = jTextField2.getText();
		String apel = jTextField3.getText();
		
		if (!nif.equals("") && !nombre.equals("")
				&& !apel.equals("")) {
			try {
				regActual=rs.getRow();
				
				// Si en la tabla no hab�a registros entonces se trata 
				// de la grabacion del primer registro
				if (regActual==0) 
					rs.moveToInsertRow();
				
				rs.updateString(2, nombre);
				rs.updateString(3, apel);
				
				if (estadoCRUD==EstadoCRUD.NUEVO) {				
					rs.updateString(1, nif);					

					rs.insertRow();
					rs.last();
					// Refrescamos el ResultSet para que se cargue
					// de nuevo con este registro añadido
					if (regActual>0) 
						rs.refreshRow();
															
					regActual=++regUltimo;
					
					// Hay que actualizar la lista cacheada de clientes
					// en la capa de negocio					
					try {
						serviciosInmobiliaria.refreshClientesCacheados();
					} catch (ServiciosException_Exception e) {
						String msg="Error al refrescar la cache de clientes";
						LOG.error(msg, e);			
						Misc.mostrarError(e, msg, false);
					}
					
				} else if (estadoCRUD==EstadoCRUD.MODIFICAR) {
					rs.updateRow();
					regActual=rs.getRow();
				} 
			} catch (SQLException e) {							
				gestionarError(e);
			} finally {
				estadoCRUD=EstadoCRUD.LECTURA;
				setEstadoBotones(estadoCRUD);
				
				lblRecordNumber.setText(regActual + " de "
						+ regUltimo);							
			}

		} else {
			JOptionPane.showMessageDialog(null,"Valores incompletos o nulos para poder grabar al cliente.");
		}

	}

	/*
	 * Cancelar la operacion actual de inserci�n o modificaci�n
	 */
	@Override protected void cancelar() {
		int regActual=0;
		try {
			rs.moveToCurrentRow();
			rs.cancelRowUpdates();
			
			jTextField1.setText(BUFFER_fields[0]);
			jTextField2.setText(BUFFER_fields[1]);
			jTextField3.setText(BUFFER_fields[2]);
			
			regActual=rs.getRow();
		} catch (SQLException e) {			
			gestionarError(e);
		} finally {
			estadoCRUD=EstadoCRUD.LECTURA;
			setEstadoBotones(estadoCRUD);
			
			lblRecordNumber.setText(regActual + " de "
					+ regUltimo);							
		}
	}
	
	@Override
	protected void logicaPresentacion() throws SQLException {
		jTextField1.setText(rs.getString(1));
		jTextField2.setText(rs.getString(2));
		jTextField3.setText(rs.getString(3));					
		lblRecordNumber.setText(rs.getRow() + " de "
				+ regUltimo);		
	}
	
	@Override
	protected void setEstadoControles(EstadoCRUD estado) {
		switch (estado) {
			case LECTURA:
			{
				jTextField1.setEditable(false);
				jTextField2.setEditable(false);
				jTextField3.setEditable(false);
				break;
			}
			case NUEVO: case MODIFICAR:
			{
				BUFFER_fields[0]=jTextField1.getText();
				BUFFER_fields[1]=jTextField2.getText();
				BUFFER_fields[2]=jTextField3.getText();
				
				jTextField1.setEditable(false);
				jTextField2.setEditable(true);
				jTextField3.setEditable(true);
				
				if (estado==EstadoCRUD.NUEVO) {					
					jTextField1.setText("");
					jTextField2.setText("");
					jTextField3.setText("");
					jTextField1.setEditable(true);
					lblRecordNumber.setText("Nuevo");
				}
			}
		}	
	}
	
	
	
	@Override
	protected void dibujarEtiquetasAndCampos() {
		setFrameIcon(new javax.swing.ImageIcon(getClass()
				.getResource("resources/icons/clientes16.png"))); 
		{
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1, "Center");
			jLabel1.setLayout(null);
			jLabel1.setName("jLabel1");
			jLabel1.setBounds(52, 18, 25, 14);
		}
		
		{
			jTextField1 = new JTextField();
			getContentPane().add(jTextField1);
			jTextField1.setName("jTextField1");
			jTextField1.setBounds(95, 15, 91, 21);
			jTextField1.setEditable(false);
		}
		
		{
			jLabel2 = new JLabel();
			getContentPane().add(jLabel2);
			jLabel2.setLayout(null);
			jLabel2.setBounds(27, 51, 47, 14);
			jLabel2.setName("jLabel2");
		}
		
		{
			jTextField2 = new JTextField();
			getContentPane().add(jTextField2);
			jTextField2.setBounds(95, 48, 173, 21);
		}
		
		{
			jLabel3 = new JLabel();
			getContentPane().add(jLabel3);
			jLabel3.setLayout(null);
			jLabel3.setBounds(25, 84, 52, 14);
			jLabel3.setName("jLabel3");
		}
		
		{
			jTextField3 = new JTextField();
			getContentPane().add(jTextField3);
			jTextField3.setBounds(95, 81, 365, 21);
		}
		
		{
			btnBuscar=new JToggleButton();
			btnBuscar.setName("btnBuscar");
			btnBuscar.setBounds(270, 46, 25, 25);			
			btnBuscar.setToolTipText("Buscar registro");
					
			ItemListener itemListener = new ItemListener() {
			      public void itemStateChanged(ItemEvent itemEvent) {
			        int state = itemEvent.getStateChange();
			        if (state == ItemEvent.SELECTED) {
			        	jTextField2.setEditable(true);
						lblRecordNumber.setText("Busqueda");
			        } else {
			        	String nombre=jTextField2.getText();
			        	
			        	if (!nombre.equals("")) {
			        	
				        	String SQL="SELECT * FROM cli WHERE nombre LIKE '%"+nombre+"%'";
							try {
								
								Statement stmt=Conexion.getConexion()
									.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
													ResultSet.CONCUR_UPDATABLE);						
								rs=stmt.executeQuery(SQL);
								rs.next();
								estadoCRUD=EstadoCRUD.LECTURA;
								setEstadoBotones(estadoCRUD);
								logicaPresentacion();
								rs=stmt.executeQuery("SELECT * FROM cli ORDER BY nif_cli");
								while (rs.next()) {
									if (rs.getString("nif_cli").equals(jTextField1.getText()))
										break;
								}
								lblRecordNumber.setText(rs.getRow() + " de "
										+ regUltimo);
							} catch (SQLException e) {								
								gestionarError(e);
							}
			        	}
			        }
			      }
			    };
			btnBuscar.addItemListener(itemListener);
			
			add(btnBuscar);
		}
						
	}
	
	@Override
	protected void salir() {
		super.salir();
		FormPrincipal.setFrmClientesActivo(false);
	}
	
}
