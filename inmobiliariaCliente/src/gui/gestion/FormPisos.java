package gui.gestion;

import ws.Propietario;

import gui.FormPrincipal;
import gui.utiles.Imagen;
import gui.utiles.UtilesGUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;


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


public class FormPisos extends AbstractDaoForm {

	private static final long serialVersionUID = 1L;
		
	/**
	 * BUFFER_fields es util para recuperar el piso actual cuando el usuario hace
	 * Nuevo o Modificar y despues se cancela, ya que como borramos
	 * el contenido del formulario no podriamos restaurarlo
	 * 
	 * Posicion 0: guardamos txtN_piso
	 * Posicion 1: guardamos el indice actual de cmbNifProp
	 * Posicion 2: guardamos txtDir
	 * Posicion 3: guardamos txtLoc
	 * Posicion 4: guardamos el nombre del radiobuton seleccionado: rbtPiscinaSI/rbtPiscinaNO
	 * Posicion 5: guardamos txtPrecio
	 * Posicion 6: guardamos txtComision
	 * 
	 */
	private static String[] BUFFER_fields=new String[7];
	
	private JLabel lblN_piso, lblDir, lblLoc, lblPiscina, lblNif_prop, lblPrecio, lblComision;	
	private JTextField txtN_piso, txtDir, txtLoc, txtPrecio, txtComision;
	private JRadioButton rbtPiscinaSI, rbtPiscinaNO;
	private ButtonGroup grupoPiscina;
	private JComboBox cmbNifProp;
	private JButton btnAbrirImagen;
	
	// Cuadro de dialogo para buscar imagenes en el disco local del usuario
	private JFileChooser selectorFichero;
	
	// imagenBuffer es la que almacena la imagen antes de modificar, 
	// por si hay que restaurarla
	private Imagen imagen, imagenBuffer; 
	
	
	/*
	 * 
	 */
	public static FormPisos createForm() {
		String SQL="SELECT * FROM piso ORDER BY n_piso";		
		return new FormPisos(SQL);
	}
	
	/*
	 * Constructor
	 */

	private FormPisos(String SQL) {
		super(SQL, "Gestion de pisos", 780, 235);		
	}

	
	/*
	 * Grabar un piso
	 */
	@Override protected void grabar() {
		int regActual=0;		
		String nif_prop= cmbNifProp.getSelectedItem().toString();
		String dir = txtDir.getText();
		String loc = txtLoc.getText();
		
		// piscina valdra false si no esta seleccionada
		boolean piscina=rbtPiscinaSI.getSelectedObjects()==null?false:true;
		
		String precio = txtPrecio.getText();
		String comision = txtComision.getText();
		
		if (!nif_prop.equals("") && !dir.equals("")
				&& !loc.equals("") && !precio.equals("")
				&& !comision.equals("")) {
			
			FileInputStream fis = null;
			BufferedInputStream bis=null;
			
			try {
				regActual=rs.getRow();
				
				// Si en la tabla no habia registros entonces se trata 
				// de la grabacion del primer registro
				if (regActual==0) 
					rs.moveToInsertRow();
				
				rs.updateString(2, dir);
				rs.updateString(3, loc);
				rs.updateBoolean(4, piscina);
				rs.updateString(5, nif_prop);
				rs.updateString(6, precio);
				rs.updateString(7, comision);
				
				try { // Comprobar si procede grabar la imagen del piso
					if (imagen.getFile()!=null) {
						byte[] bytes=UtilesGUI
							.getBytesFromFile(new File(imagen.getFile()));
				        rs.updateBytes(8, bytes);						
					}
				} catch (Exception e) {					
					gestionarError(e);				
				} 
				
				if (estadoCRUD==EstadoCRUD.NUEVO) {				
					rs.insertRow();
					rs.last();
					// Refrescamos el ResultSet para que se cargue
					// de nuevo con este registro a�adido
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
				try {
					if (fis!=null) fis.close();
					if (bis!=null) bis.close();
				} catch (IOException e) {					
					gestionarError(e);
				}
				estadoCRUD=EstadoCRUD.LECTURA;
				setEstadoBotones(estadoCRUD);
				
				lblRecordNumber.setText(regActual + " de "
						+ regUltimo);							
			}

		} else {
			JOptionPane.showMessageDialog(null,"Valores incompletos o nulos para poder grabar el piso.");
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
			
			txtN_piso.setText(BUFFER_fields[0]);
			cmbNifProp.setSelectedItem(BUFFER_fields[1]);
			txtDir.setText(BUFFER_fields[2]);
			txtLoc.setText(BUFFER_fields[3]);
			rbtPiscinaSI.setSelected(BUFFER_fields[4].equals("rbtPiscinaSI")?true:false);
			rbtPiscinaNO.setSelected(BUFFER_fields[4].equals("rbtPiscinaNO")?true:false);
			txtPrecio.setText(BUFFER_fields[5]);
			txtComision.setText(BUFFER_fields[6]);
			imagen=imagenBuffer; // imagen apunta al objeto apuntado por imagenBuffer
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
		txtN_piso.setText(rs.getString(1));
		cmbNifProp.setSelectedItem(rs.getString(5));
		txtDir.setText(rs.getString(2));
		txtLoc.setText(rs.getString(3));
		rbtPiscinaSI.setSelected(rs.getBoolean(4)==true?true:false);
		rbtPiscinaNO.setSelected(rs.getBoolean(4)==true?false:true);
		txtPrecio.setText(rs.getString(6));
		txtComision.setText(rs.getString(7));
		
		Blob b = rs.getBlob(8);

        if (b!=null) {
        	// Creamos la imagen a partir de los bytes almacenados en la base de datos
			imagen.setImage(b.getBytes(1L, (int) b.length()));
			imagen.setNombre(txtN_piso.getText()+" - "+txtDir.getText()+" - "+txtLoc.getText());
        } else {        	
        	imagen.setImage(null);
        }
		lblRecordNumber.setText(rs.getRow() + " de "
				+ regUltimo);		
	}
	
	@Override
	protected void setEstadoControles(EstadoCRUD estado) {
		switch (estado) {
			case LECTURA:
			{				
				cmbNifProp.setEditable(false);
				cmbNifProp.setEnabled(false);
				txtDir.setEditable(false);
				txtLoc.setEditable(false);
				rbtPiscinaSI.setEnabled(false);
				rbtPiscinaNO.setEnabled(false);
				txtPrecio.setEditable(false);
				txtComision.setEditable(false);
				btnAbrirImagen.setEnabled(false);
				break;
			}
			case NUEVO: case MODIFICAR:
			{
				BUFFER_fields[0]=txtN_piso.getText();
				try {
					BUFFER_fields[1]=cmbNifProp.getSelectedItem().toString();
				} catch (Exception e){} // ignorar para el caso en que no hay registros en la BD	
				BUFFER_fields[2]=txtDir.getText();
				BUFFER_fields[3]=txtLoc.getText();
				BUFFER_fields[4]=rbtPiscinaSI.getSelectedObjects()==null?"rbtPiscinaNO":"rbtPiscinaSI";
				BUFFER_fields[5]=txtPrecio.getText();
				BUFFER_fields[6]=txtComision.getText();	
				imagenBuffer=imagen; // Apuntar al objeto imagen actual (si lo hay)
				
				cmbNifProp.setEditable(true);
				cmbNifProp.setEnabled(true);
				txtDir.setEditable(true);
				txtLoc.setEditable(true);
				rbtPiscinaSI.setEnabled(true);
				rbtPiscinaNO.setEnabled(true);
				txtPrecio.setEditable(true);
				txtComision.setEditable(true);
				btnAbrirImagen.setEnabled(true);
				
				if (estado==EstadoCRUD.NUEVO) {					
					txtN_piso.setText("");
										
					try {
						cmbNifProp.setSelectedIndex(0);
					} catch (Exception e){} // ignorar para el caso en que no hay registros en la BD
					
					txtDir.setText("");
					txtLoc.setText("");
					rbtPiscinaSI.setSelected(false);
					rbtPiscinaNO.setSelected(true);
					txtPrecio.setText("");
					txtComision.setText("");
					imagen.setImage(null);
					lblRecordNumber.setText("Nuevo");
				}
			}
		}	
	}
	
	
	
	@Override
	protected void dibujarEtiquetasAndCampos() {
		setFrameIcon(new javax.swing.ImageIcon(getClass()
				.getResource("resources/icons/pisos16.png")));
		final int y=12;
		{
			lblN_piso = new JLabel();  // n_piso
			getContentPane().add(lblN_piso, "Center");
			lblN_piso.setName("lblN_piso");
			lblN_piso.setBounds(10, y, 60, 14);
		}
		
		{
			txtN_piso = new JTextField(); // n_piso
			getContentPane().add(txtN_piso);
			txtN_piso.setName("txtN_piso");
			txtN_piso.setBounds(75, y-3, 91, 21);
			txtN_piso.setEditable(false);
		}
		
		{
			lblNif_prop = new JLabel(); // nif_prop
			getContentPane().add(lblNif_prop);			
			lblNif_prop.setBounds(180, y, 85, 14);
			lblNif_prop.setName("lblNif_prop");
		}
		
		{
			ComboBoxModel cmbNIFPropModel = 
				new DefaultComboBoxModel(getNIFPropietarios());
				
			cmbNifProp = new JComboBox(); //nif_prop
			cmbNifProp.setModel(cmbNIFPropModel);
			cmbNifProp.setBounds(270, y-3, 150, 21);
			cmbNifProp.setName("cmbNifProp");
			
			getContentPane().add(cmbNifProp);
		}
		
		{
			lblDir = new JLabel(); // dir
			getContentPane().add(lblDir);
			lblDir.setBounds(10, y+35, 60, 14);
			lblDir.setName("lblDir");
		}
		
		{
			txtDir = new JTextField(); // direccion
			getContentPane().add(txtDir);
			txtDir.setName("txtN_piso");
			txtDir.setBounds(75, y+32, 215, 21);
		}
		
		{ 
			lblLoc = new JLabel(); // loc
			getContentPane().add(lblLoc);
			lblLoc.setBounds(295, y+35, 60, 14);
			lblLoc.setName("lblLoc");
		}
		
		{
			txtLoc = new JTextField(); // loc
			getContentPane().add(txtLoc);
			txtLoc.setBounds(355, y+32,145, 21);
			txtLoc.setName("txtLoc");
		}
		
		{
			lblPiscina = new JLabel(); // piscina
			getContentPane().add(lblPiscina);			
			lblPiscina.setBounds(22, y+70, 50, 14);
			lblPiscina.setName("lblPiscina");
		}
		
		
		{
			rbtPiscinaSI = new JRadioButton("SI");
			getContentPane().add(rbtPiscinaSI);			
			rbtPiscinaSI.setBounds(70, y+67, 40, 21);
			rbtPiscinaSI.setName("rbtPiscinaSI");
			
			rbtPiscinaNO = new JRadioButton("NO");
			getContentPane().add(rbtPiscinaNO);			
			rbtPiscinaNO.setBounds(120, y+67, 45, 21);
			rbtPiscinaNO.setName("rbtPiscinaNO");
			rbtPiscinaNO.setSelected(true);
						
		}
				
		{
			grupoPiscina=new ButtonGroup();
			grupoPiscina.add(rbtPiscinaSI);
			grupoPiscina.add(rbtPiscinaNO);
		}
		
		
		{
			lblPrecio = new JLabel(); // precio
			getContentPane().add(lblPrecio);			
			lblPrecio.setBounds(180, y+70, 40, 14);
			lblPrecio.setName("lblPrecio");
		}
		
		{
			txtPrecio = new JTextField(); // precio
			getContentPane().add(txtPrecio);
			txtPrecio.setBounds(220, y+67, 47, 21);
		}
		
		{
			lblComision = new JLabel(); // comision
			getContentPane().add(lblComision);			
			lblComision.setBounds(280, y+70, 85, 14);
			lblComision.setName("lblComision");
		}
		
		
		{
			txtComision = new JTextField(); // comision
			getContentPane().add(txtComision);
			txtComision.setBounds(355, y+67, 47, 21);
		}
		
		{			
			// inicializar la clase Imagen
			imagen=new Imagen();			
						
			imagen.setBounds(510, y, 240, 175);
			imagen.setOpaque(true);
			imagen.setBackground(Color.white);
			imagen.setBorder(new BevelBorder(BevelBorder.LOWERED));
			getContentPane().add(imagen);														
		}
		
		{
			// Inicializar el cuadro de dialogo
			// para buscar imagenes en disco
			selectorFichero = new JFileChooser();
		}
		
		{			
			// Boton para abrir imagen desde disco
			btnAbrirImagen=new JButton("");
			btnAbrirImagen.setName("btnAbrirImagen");
			btnAbrirImagen.setToolTipText("Cargar imagen para vincular al piso");
			getContentPane().add(btnAbrirImagen);			
			btnAbrirImagen.setBounds(10, y+100, 120, 30);
			
			/*
			 *  Asociamos un listener para el boton que abre
			 *  el cuadro de dialogo de seleccion de imagenes
			 *  Hay que destacar que la clase anonima anida a
			 *  otra clase anonima para establecer el filtro
			 *  de ficheros para archivos de imagen.
			 */
			btnAbrirImagen.addActionListener(new ActionListener() {
			
				@Override
				public void actionPerformed(ActionEvent e) {
					selectorFichero.addChoosableFileFilter(new FileFilter() {
						// Aceptar todos los directorios y las extensiones gif, jpg, tiff, png.
					    public boolean accept(File f) {
					        if (f.isDirectory()) {
					            return true;
					        }

					        String extension = ExtensionImagenes.getExtension(f);
					        if (extension != null) {
					            if (extension.equals(ExtensionImagenes.tiff) ||
					                extension.equals(ExtensionImagenes.tif) ||
					                extension.equals(ExtensionImagenes.gif) ||
					                extension.equals(ExtensionImagenes.jpeg) ||
					                extension.equals(ExtensionImagenes.jpg) ||
					                extension.equals(ExtensionImagenes.png)) {
					                    return true;
					            } else {
					                return false;
					            }
					        }

					        return false;
					    }

					    // La descripcion de este filtro
					    public String getDescription() {
					        return "Solo ficheros de imagen";
					    }
					});
				
					int ret=selectorFichero.showOpenDialog(FormPisos.this);
					if (ret == JFileChooser.APPROVE_OPTION) {						
						String imagenFile=selectorFichero.getSelectedFile().getAbsolutePath();
						// Creamos la imagen a partir del fichero en el disco del usuario
						imagen.setFile(imagenFile);
						imagen.setNombre(txtN_piso.getText()+" - "+txtDir.getText()+" - "+txtLoc.getText());
					}					
				}
			});	
		}
		
	}
	
	
	/*
	 * Cargar el combo con los NIF's de los propietarios
	 */
	private String[] getNIFPropietarios() {
		
		final String[] EMPTY_NIF = new String[0];
		
		//Collection<Propietario> propietarios=serviciosInmobiliaria.getPropietarios();
		Collection<Propietario> propietarios=serviciosInmobiliaria.getPropietariosCacheados();
		
		if (!propietarios.isEmpty()) {
			Collection<String> nifs=new ArrayList<String>(propietarios.size());
			for (Propietario propietario:propietarios) {
				nifs.add(propietario.getNifProp());	
			}
			return nifs.toArray(EMPTY_NIF);
		}
			
		return EMPTY_NIF;				
	}
	
	
	
	@Override
	protected void salir() {
		super.salir();
		FormPrincipal.setFrmPisosActivo(false);
	}
}

/*
 * Clase de apoyo para el filtro de imagenes 
 * en el cuadro de dialogo que permite seleccionar
 * imagenes del disco
 */

class ExtensionImagenes {

    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";

    /*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}