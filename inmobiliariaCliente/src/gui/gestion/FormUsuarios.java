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


public class FormUsuarios extends AbstractDaoForm {

	private static final long serialVersionUID = 1L;
	
	// Util para recuperar el usuario actual cuando el usuario hace
	// Nuevo o Modificar y despues se cancela, ya que como borramos 
	// el contenido del formulario no podriamos restaurarlo 
	private static String[] BUFFER_fields=new String[2];
	
	private JLabel jLabel1, jLabel2;	
	private JTextField jTextField1, jTextField2;
	private JToggleButton btnBuscar;
	
	/*
	 * 
	 */
	public static FormUsuarios createForm() {
		String SQL="SELECT login, pwd FROM usuarios ORDER BY login";		
		return new FormUsuarios(SQL);
	}
	
	/*
	 * Constructor
	 */

	private FormUsuarios(String SQL) {
		super(SQL, "Gestion de usuarios", 515, 235);	
	}

	/*
	 * Grabar
	 */
	@Override protected void grabar() {
		int regActual=0;
		String login = jTextField1.getText();
		String pwd = jTextField2.getText();
				
		if (!login.equals("") && !pwd.equals("")) {
			try {
				regActual=rs.getRow();
				
				// Si en la tabla no habia registros entonces se trata 
				// de la grabacion del primer registro
				if (regActual==0) 
					rs.moveToInsertRow();
				
				rs.updateString(2, pwd);
								
				if (estadoCRUD==EstadoCRUD.NUEVO) {				
					rs.updateString(1, login);					

					rs.insertRow();
					rs.last();
					// Refrescamos el ResultSet para que se cargue
					// de nuevo con este registro añadido
					if (regActual>0) 
						rs.refreshRow();
															
					regActual=++regUltimo;
										
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
			JOptionPane.showMessageDialog(null,"Valores incompletos o nulos para poder grabar al usuario.");
		}

	}

	/*
	 * Cancelar la operacion actual de insercion o modificacion
	 */
	@Override protected void cancelar() {
		int regActual=0;
		try {
			rs.moveToCurrentRow();
			rs.cancelRowUpdates();
			
			jTextField1.setText(BUFFER_fields[0]);
			jTextField2.setText(BUFFER_fields[1]);
						
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
				break;
			}
			case NUEVO: case MODIFICAR:
			{
				BUFFER_fields[0]=jTextField1.getText();
				BUFFER_fields[1]=jTextField2.getText();
								
				jTextField1.setEditable(false);
				jTextField2.setEditable(true);
								
				if (estado==EstadoCRUD.NUEVO) {					
					jTextField1.setText("");
					jTextField2.setText("");
					jTextField1.setEditable(true);
					lblRecordNumber.setText("Nuevo");
				}
			}
		}	
	}
	
	
	
	@Override
	protected void dibujarEtiquetasAndCampos() {
		setFrameIcon(new javax.swing.ImageIcon(getClass()
				.getResource("resources/icons/users16.png"))); 
		{
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1, "Center");
			jLabel1.setLayout(null);
			jLabel1.setName("jLabel1");
			jLabel1.setBounds(42, 18, 35, 14);
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
			jLabel2.setBounds(20, 51, 64, 14);
			jLabel2.setName("jLabel2");
		}
		
		{
			jTextField2 = new JTextField();
			getContentPane().add(jTextField2);
			jTextField2.setBounds(95, 48, 173, 21);
		}
		
		{
			btnBuscar=new JToggleButton();
			btnBuscar.setName("btnBuscar");
			btnBuscar.setBounds(191, 13, 25, 25);			
			btnBuscar.setToolTipText("Buscar registro");
					
			ItemListener itemListener = new ItemListener() {
			      public void itemStateChanged(ItemEvent itemEvent) {
			        int state = itemEvent.getStateChange();
			        if (state == ItemEvent.SELECTED) {
			        	jTextField1.setEditable(true);
			        	jTextField1.setSelectionStart(0);
						jTextField1.setSelectionEnd(jTextField1.getText().length());
						jTextField1.requestFocusInWindow();
						lblRecordNumber.setText("Busqueda");
			        } else {
			        	String login=jTextField1.getText();
			        	
			        	if (!login.equals("")) {
			        	
				        	String SQL="SELECT login,pwd FROM usuarios WHERE login LIKE '%"+login+"%'";
							try {
								
								Statement stmt=Conexion.getConexion()
									.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
													ResultSet.CONCUR_UPDATABLE);						
								rs=stmt.executeQuery(SQL);
								rs.next();
								estadoCRUD=EstadoCRUD.LECTURA;
								setEstadoBotones(estadoCRUD);
								logicaPresentacion();
								rs=stmt.executeQuery("SELECT login,pwd FROM usuarios ORDER BY login");
								while (rs.next()) {
									if (rs.getString("login").equals(jTextField1.getText()))
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
		FormPrincipal.setFrmUsuariosActivo(false);
	}
	
}
