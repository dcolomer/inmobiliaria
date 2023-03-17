package gui.gestion;

import gui.FormPrincipal;

import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

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


public class FormPropietarios extends AbstractDaoForm {

	private static final long serialVersionUID = 1L;
	
	private TablaPisosPropietario tablaPisos;
		
	// Util para recuperar el cliente actual cuando el usuario hace
	// Nuevo o Modificar y despues se cancela, ya que como borramos 
	// el contenido del formulario no podriamos restaurarlo 
	private static String[] BUFFER_fields=new String[5];
	
	private JLabel jLabel1, jLabel2, jLabel4, jLabel5; //, jLabel3;	
	private JTextField jTextField1, jTextField2, jTextField3, jTextField4, jTextField5;
	
	/*
	 * 
	 */
	public static FormPropietarios createForm() {
		String SQL="SELECT * FROM prop ORDER BY nif_prop";		
		return new FormPropietarios(SQL);
	}
	
	/*
	 * Constructor
	 */	
	private FormPropietarios(String SQL) {
		super(SQL, "Gestion de Propietarios", 900, 235);	
	}
	
	
	/*
	 * Eliminar propietario
	 */
	@Override protected void eliminar() {
		super.eliminar();		
		
		// Hay que actualizar la lista cacheada de propietarios
		// en la capa de negocio
		try {
			serviciosInmobiliaria.refreshPropietariosCacheados();
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
		String dir = jTextField4.getText();
		String loc = jTextField5.getText();
		
		if (!nif.equals("") && !nombre.equals("") && !apel.equals("")
				&& !dir.equals("") && !loc.equals("")) {
			try {
				regActual=rs.getRow();
				
				// Si en la tabla no hab�a registros entonces se trata 
				// de la grabacion del primer registro
				if (regActual==0) 
					rs.moveToInsertRow();
				
				rs.updateString(2, nombre);
				rs.updateString(3, apel);
				rs.updateString(4, dir);
				rs.updateString(5, loc);
				
				if (estadoCRUD==EstadoCRUD.NUEVO) {				
					rs.updateString(1, nif);					

					rs.insertRow();
					rs.last();
					// Refrescamos el ResultSet para que se cargue
					// de nuevo con este registro a�adido
					if (regActual>0) 
						rs.refreshRow();
					
					regActual=++regUltimo;
										
					// Hay que actualizar la lista cacheada de propietarios
					// en la capa de negocio
					try {
						serviciosInmobiliaria.refreshPropietariosCacheados();
					} catch (ServiciosException_Exception e) {
						gestionarError(e);
					}
					
					cargarTablaPisos();					
					
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
			JOptionPane.showMessageDialog(null,"Valores incompletos o nulos para poder grabar al propietario.");
		}

	}
	
	protected void nuevo() {
		super.nuevo();
		tablaPisos.activarControles();		
	}

	protected void modificar() {
		super.modificar();
		tablaPisos.activarControles();		
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
			jTextField3.setText(BUFFER_fields[2]);
			jTextField4.setText(BUFFER_fields[3]);
			jTextField5.setText(BUFFER_fields[4]);
			
			regActual=rs.getRow();
		} catch (SQLException e) {			
			gestionarError(e);
		} finally {
			estadoCRUD=EstadoCRUD.LECTURA;
			setEstadoBotones(estadoCRUD);
			
			lblRecordNumber.setText(regActual + " de "
					+ regUltimo);		
			
			tablaPisos.desactivarControles();	
		}
	}
	
	@Override
	protected void logicaPresentacion() throws SQLException {
		jTextField1.setText(rs.getString(1));
		jTextField2.setText(rs.getString(2));
		jTextField3.setText(rs.getString(3));
		jTextField4.setText(rs.getString(4));
		jTextField5.setText(rs.getString(5));
		lblRecordNumber.setText(rs.getRow() + " de "
				+ regUltimo);
		
		cargarTablaPisos();		
	}
	
	@Override
	protected void setEstadoControles(EstadoCRUD estado) {
		switch (estado) {
			case LECTURA:
			{
				jTextField1.setEditable(false);
				jTextField2.setEditable(false);
				jTextField3.setEditable(false);
				jTextField4.setEditable(false);
				jTextField5.setEditable(false);
				break;
			}
			case NUEVO: case MODIFICAR:
			{
				BUFFER_fields[0]=jTextField1.getText();
				BUFFER_fields[1]=jTextField2.getText();
				BUFFER_fields[2]=jTextField3.getText();
				BUFFER_fields[3]=jTextField4.getText();
				BUFFER_fields[4]=jTextField5.getText();
				
				jTextField1.setEditable(false);
				jTextField2.setEditable(true);
				jTextField3.setEditable(true);
				jTextField4.setEditable(true);
				jTextField5.setEditable(true);
				
				if (estado==EstadoCRUD.NUEVO) {					
					jTextField1.setText("");
					jTextField2.setText("");
					jTextField3.setText("");
					jTextField4.setText("");
					jTextField5.setText("");
					jTextField1.setEditable(true);
					lblRecordNumber.setText("Nuevo");
				}
			}
		}	
	}
	
	
	
	@Override
	protected void dibujarEtiquetasAndCampos() {
		setFrameIcon(new javax.swing.ImageIcon(getClass()
				.getResource("resources/icons/propietarios16.png")));
		int y=15;
		{ // nif
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1, "Center");
			jLabel1.setLayout(null);
			jLabel1.setName("jLabel1");
			jLabel1.setBounds(32, y, 25, 14);
		}
		{
			jTextField1 = new JTextField();
			getContentPane().add(jTextField1);
			jTextField1.setName("jTextField1");
			jTextField1.setBounds(75, y, 91, 21);
			jTextField1.setEditable(false);
		}
		
		// ************************************************
		{ // nombre
			jLabel2 = new JLabel();
			getContentPane().add(jLabel2);
			jLabel2.setLayout(null);
			jLabel2.setBounds(10, y+25, 45, 14);
			jLabel2.setName("jLabel2");
		}
		{
			jTextField2 = new JTextField();
			getContentPane().add(jTextField2);
			jTextField2.setBounds(75, y+25, 130, 21);
		}
		
		// ************************************************
		/*{ // apellidos
			jLabel3 = new JLabel();
			getContentPane().add(jLabel3);
			jLabel3.setLayout(null);
			jLabel3.setBounds(200, y+25, 42, 14);
			jLabel3.setName("jLabel3");
		}*/
		{
			jTextField3 = new JTextField();
			getContentPane().add(jTextField3);
			jTextField3.setBounds(210, y+25, 230, 21);
		}
		
		// ************************************************
		{ // direccion
			jLabel4 = new JLabel();
			getContentPane().add(jLabel4);
			jLabel4.setLayout(null);
			jLabel4.setBounds(10, y+50, 60, 14);
			jLabel4.setName("jLabel4");
		}
		{
			jTextField4 = new JTextField();
			getContentPane().add(jTextField4);
			jTextField4.setBounds(75, y+50, 365, 21);
		}
		
		// ************************************************
		{ // localidad
			jLabel5 = new JLabel();
			getContentPane().add(jLabel5);
			jLabel5.setLayout(null);
			jLabel5.setBounds(10, y+75, 60, 14);
			jLabel5.setName("jLabel5");
		}
		{
			jTextField5 = new JTextField();
			getContentPane().add(jTextField5);
			jTextField5.setBounds(75, y+75, 365, 21);
		}
		
		
	}
	
	/**
	 * Muestra el JPanel que contiene el JTable con los pisos 
	 * del propietario. Solo lo crea la primera vez, las 
	 * siguientes veces lo reconfigura mediante el metodo resetTabla,
	 * pasandole el nif del propietario
	 */
	private void cargarTablaPisos() {
		try {						
			if (tablaPisos==null) {
				tablaPisos=new TablaPisosPropietario(rs.getString(1));
				tablaPisos.setBounds(505, 5, 380, 190);
				add(tablaPisos);
			} else {	
				tablaPisos.resetTabla(rs.getString(1));
			}
		} catch (SQLException e) {		
			gestionarError(e);
		}
	}
	
	@Override
	protected void salir() {
		super.salir();		
		FormPrincipal.setFrmPropietariosActivo(false);
	}
	
	
}
