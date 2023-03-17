package gui.gestion;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;
import org.apache.lucene.swing.models.TableSearcher;

import utiles.Misc;

/**
 * Clase que Delegada (Vista-Controlador) que controla el JTable
 * con los pisos pertenencientes a cada propietario
 * 
 * El modelo esta encapsulado en la clase RSetTableModel
 *
 */
public class TablaPisosPropietario extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(TablaPisosPropietario.class);
	private static final String msg = "Error en operacion de acceso a datos.";
	
	private static RSetTableModel modeloTabla;
	private static String restriccion;
	private JTable tablaResultados;
	private JButton btnAddRow, btnRmvRow, btnBuscar;
	private TableSearcher buscadorTabla;
	private JTextField txtBusqueda;
	private JPanel panelBotones;
	
	private Statement stmt;
	private ResultSet rs;

	
	/**
	 * Constructor
	 * @param restric: Condicion SQL (where) para restringir el conjunto 
	 * de resultados al propietario indicado 	
	 */
	public TablaPisosPropietario(String restric) {		
		super();
		restriccion=restric;
		inicializar(); // Creacion de JTable y TableModel
		initGUI();
		desactivarControles();
	}
	
	/**
	 * Metodo que es invocado cada vez que se requiere
	 * una nueva consulta SQL
	 */
	private void getDatos() {		
		try {			
			// Obtener los pisos del propietario indicado por 'restriccion'
			rs = stmt.executeQuery("SELECT n_piso, dir, loc, piscina, precio, comision, nif_prop "
					+"FROM piso WHERE nif_prop='"+restriccion+"'");
			
			// Creamos un nuevo Modelo con el ResultSet anterior
			modeloTabla = new RSetTableModel(rs, 
				new String[] {"Piso", "Dir.", "Loc.", "Pisci.", "Precio", "Com.", "Prop."});
			
		} catch (SQLException e) {
			gestionarError(e);
		}
				
		
	}
	
	/**
	 * Creacion de JTable y TableModel
	 * -Creamos el ResultSet: Realizar la consulta SQL (obtener 
	 * los pisos del propietario). 
	 * -Creamos un nuevo TableModel y le registramos un listener 
	 * para cambios en el modelo de datos.
	 * -Creamos un JTable basado en el TableModel anterior
	 */
	private void inicializar() {		
		try {
			
			// El ResultSet sera actualizable
			stmt = Conexion.getConexion().createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			
			getDatos();
					
			// Registramos un listener para cambios en el modelo de datos.
			// ModeloTablaListener es una clase definida en este mismo fichero
			modeloTabla.addTableModelListener(
				new TablaPisosPropietario.ModeloTablaListener());
		
			rs.next();
		
			// Crear el JTable asociando a un modelo "normal" o a uno de Lucene 
			// Si el ResultSet tiene registros activamos la funcionalidad de busqueda
			if (rs.getRow()>0) { 
				buscadorTabla=new TableSearcher(modeloTabla);
				tablaResultados = new JTable(buscadorTabla);
			} else { // No activamos la funcionalidad de buqueda
				tablaResultados = new JTable(modeloTabla);				
			}			
		
		} catch (Exception ex) {			
			gestionarError(ex);
		}
	}
	
	/**
	 * Metodo que permite obtener los pisos asociados a cada propietario.
	 * @param restric: Condicion SQL (where) para restringir el conjunto 
	 * de resultados al propietario indicado 
	 */
	public void resetTabla(String restric) {
		restriccion=restric;
		
		try {						
			getDatos();
			
			rs.next();
			
			// Si el ResultSet tiene registros activamos la funcionalidad de busqueda
			if (rs.getRow()>0) { 
				buscadorTabla=new TableSearcher(modeloTabla);
				tablaResultados.setModel(buscadorTabla);							
			} else { // No activamos la funcionalidad de buqueda
				tablaResultados.setModel(modeloTabla);
			}	
			txtBusqueda.setText("<introducir criterio>");
		} catch (SQLException e) {			
			gestionarError(e);
		}
		
	}
	
	
	/**
	 * Dibujar la tabla de pisos de los propietarios, asi
	 * como los botones para el mantenimiento CRUD de la
	 * misma
	 */
	private void initGUI() {
		
		try {
			//establecerAnchos(); // Fijamos el ancho de cada columna de la tabla
			
			/**
			 * Utilizamos un gestor de presentacion que ubique los componenes verticamente.
			 * La clase TablaPisosPropietario extiende a JPanel y contendra dos paneles
			 * verticalmente:
			 * -El primero es un JScrollPane que contendra la tabla de piso.
			 * -El segundo es un JPanel que contedra los botones para insertar y eliminar filas  
			 */
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
		    JScrollPane jsp = new JScrollPane(tablaResultados);		    
		    add(jsp);
		    
		    tablaResultados.setFillsViewportHeight(true);
		    
		    panelBotones=new JPanel();
		    panelBotones.setBackground(Color.gray);
		    panelBotones.setSize(tablaResultados.getSize().width, 29);
		    
		    add(panelBotones);
		    
		    // Este listener nos servira tanto para cuando el
		    // usuario pulse return en el campo de busqueda
		    // como cuando pulse el boton 'buscar'
		    ActionListener buscadorListener=new ActionListener() {
		    	public void actionPerformed(ActionEvent evt) {	
		    		actionPerformedExecute();	
		    	}
		    };
		    
		    txtBusqueda=new JTextField();
		    txtBusqueda.setSize(80, 25);
		    txtBusqueda.setText("<introducir criterio>");
		    txtBusqueda.addActionListener(buscadorListener);
		    txtBusqueda.requestFocus();
		    txtBusqueda.setToolTipText("Comodines: ?->1 caracter cualquiera, *->varios");
		    
		    btnBuscar=new JButton("Buscar");
		    btnBuscar.setSize(80, 25);
		    btnBuscar.addActionListener(buscadorListener);		    		    
		    
		    btnAddRow=new JButton("Insertar");
			btnAddRow.setSize(80, 25);
			btnAddRow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					FormNuevaFila fnf=new FormNuevaFila(TablaPisosPropietario.this, restriccion);
					fnf.setVisible(true);					
				}
			});
			
			btnRmvRow=new JButton("Eliminar");
			btnRmvRow.setSize(80, 25);
			btnRmvRow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					int fila=tablaResultados.getSelectedRow();
					if (fila>=0) {
						
						int opcion = JOptionPane.showConfirmDialog(null, "¿Eliminar la fila de la tabla?", 
								"Eliminar fila", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if (opcion == JOptionPane.YES_OPTION)
							getModeloTabla().deleteRecord(fila);
					} else {
						String msg="Primero debe seleccionar la fila que quiere eliminar.";
						JOptionPane.showMessageDialog(null, msg, 
								"Eliminar fila", JOptionPane.INFORMATION_MESSAGE,null);
						
					}
				}
			});
			
			panelBotones.add(txtBusqueda);
			panelBotones.add(btnBuscar);
			panelBotones.add(btnAddRow);
			panelBotones.add(btnRmvRow);
			this.setBorder(new BevelBorder(BevelBorder.LOWERED));
					    
		    setVisible(true);
			
		} catch (Exception ex) {			
			gestionarError(ex);
		}

	}
	
	/**
	 * Devolver el TableModel asociado al JTable.
	 * Este metodo es invocado al insertar y al borrar
	 */
	public RSetTableModel getModeloTabla() {
		return modeloTabla;
	}
	
	/**
	 * Clase que recibe eventos de actualizacion,
	 * insercion y eliminacion desde el TableModel.
	 * Solo se utiliza el evento UPDATE para resaltar
	 * en color azul la siguiente columna a la actualizada
	 *
	 */
	public class ModeloTablaListener implements TableModelListener {
        public void tableChanged(TableModelEvent evt) {
            if (evt.getType() == TableModelEvent.UPDATE) {
                int column = evt.getColumn();
                int row = evt.getFirstRow();                
                TablaPisosPropietario.this.tablaResultados.setColumnSelectionInterval(column + 1, column + 1);
                TablaPisosPropietario.this.tablaResultados.setRowSelectionInterval(row, row);
            } else if (evt.getType() == TableModelEvent.INSERT) {
            	//int row = evt.getFirstRow();            	            	            
            } else if (evt.getType() == TableModelEvent.DELETE) {
            	//int row = evt.getFirstRow();            	            	            	
            }            
        }
    }
		
	/**
	 * Controlar si la tabla y sus botones deben o
	 * no estar activados
	 */
	public void desactivarControles() {
		tablaResultados.setEnabled(false);
		btnAddRow.setEnabled(false);
		btnRmvRow.setEnabled(false);
	}
	
	public void activarControles() {
		tablaResultados.setEnabled(true);
		btnAddRow.setEnabled(true);
		btnRmvRow.setEnabled(true);
	}


	/**
	 * Cuando el usuario pulsa return en el campo de 
	 * busqueda del JTable o cuando pulsa el boton
	 * 'buscar' se ejecuta este metodo
	 */
	private void actionPerformedExecute() {
		String criterio=txtBusqueda.getText().trim().toLowerCase();				
		if (!criterio.startsWith("*")) {
			try {
				buscadorTabla.search(txtBusqueda.getText().trim().toLowerCase());
			} catch (Exception e) {
				String msg="El criterio de busqueda es incorrecto.";
				JOptionPane.showMessageDialog(null, msg, 
					"Atencion", JOptionPane.ERROR_MESSAGE,null);
			}	
		} else {
			String msg="El criterio de busqueda no puede comenzar por '*'";
			JOptionPane.showMessageDialog(null, msg, 
				"Atencion", JOptionPane.ERROR_MESSAGE,null);
		}
	}
	
	/**
	 * Anotar en el log la excepcion recibida por parametro y 
	 * mostrarla en un cuadro de dialogo 
	 */
	private void gestionarError(Exception e) {
		LOG.error(msg, e);
		Misc.mostrarError(e, msg, false);
	}
}
