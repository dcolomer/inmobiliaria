package gui.gestion;

import java.sql.*;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import utiles.Misc;

/**
 * Modelo de datos generico que proporciona a cualquier JTable los datos
 * obtenidos a partir de un ResultSet.
 * 
 * Dispone de metodos para acceder a cualquer fila del JTable, asi como para
 * insertar, actualizar, eliminar.
 */
public final class RSetTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(RSetTableModel.class);
	private static final String msg = "Error en operacion de acceso a datos.";

	private ResultSet rs = null;
	private ResultSetMetaData rsmd = null;

	/**
	 * Tamanyo de pagina. Muy util cuando queremos que la tabla limite los
	 * registros a visualizar debido a que el ResultSet contiene una gran
	 * cantidad de datos. El valor predeterminado es -1, lo cual significa que
	 * el ResultSet no utiliza paginacion
	 */
	private int pagTamany = 40;

	/**
	 * Pagina actual que se esta visualizando en la JTable (0=primera pagina).
	 * Solo aplica si se utiliza paginacion.
	 */
	private int numPagActual = 0;

	/**
	 * flag para indicar si la tabla es de solo lectura. Por defecto la tabla NO
	 * es editable.
	 */
	private boolean IS_READONLY = false;

	/**
	 * Este atributo sirve para indicar si queremos que los titulos de los
	 * campos se obtenga automaticamente a partir del ResultSetMetaData o los
	 * piensa proporcionar el usuario. Por defecto se obtienen automaticamente.
	 */
	private boolean TITULOS_AUTOMATICOS = true;

	/**
	 * En el caso de que el usuario proporcione los titulos los almacenamos en
	 * este array
	 */
	private String[] titulos = null;

	/*
	 * Constructores
	 */

	/**
	 * @param rs
	 *            : un ResultSet inicializado
	 * 
	 *            Mediante este constructor se utilizan los nombres de los
	 *            campos de la BD para los titulos de la JTable
	 */
	public RSetTableModel(ResultSet rs) throws SQLException {
		super();
		if (rs == null) {
			Exception e = new IllegalArgumentException();
			gestionarError(e);
		}
		this.rs = rs;
		this.rsmd = rs.getMetaData();
	}

	/**
	 * @param rs
	 *            : un ResultSet inicializado
	 * @param titulos
	 *            : Array con los nombres de las columnas
	 * 
	 *            Este constructor permite especificarle el titulo de cada
	 *            columna y evitar que se recuperen del ResultSetMetaData los
	 *            verdaderos nombres, los cuales pueden no ser user-friendy
	 */
	public RSetTableModel(ResultSet rs, String[] titulos) throws SQLException {
		this(rs); // llamada al constructor anterior

		if (titulos == null || titulos.length == 0) {
			String msg = "No se han proporcionado titulos para las columnas";
			Exception e = new IllegalArgumentException(msg);
			gestionarError(e);
		} else if (titulos.length != rsmd.getColumnCount()) {
			String msg = "No coincide el numero de columnas del RS con el numero de titulos proporcionado";
			Exception e = new IllegalArgumentException(msg);
			gestionarError(e);
		} else {
			this.TITULOS_AUTOMATICOS = false;
			this.titulos = titulos;
		}
	}

	/**
	 * Recupera el valor de la celda especificado por row, col
	 */
	@Override
	public Object getValueAt(int row, int col) {
		try {
			if (this.pagTamany == -1) { // aplica si no estamos usando paginacion										
				rs.absolute(row + 1);
			} else { // aplica si estamos usando paginacion
				int startRow = this.numPagActual * this.pagTamany;
				rs.absolute(startRow + row + 1);
			}
			return rs.getObject(col + 1);
		} catch (SQLException e) {
			gestionarError(e);
			return "error";
		}
	}

	/**
	 * Escribe el valor especificado en la celda indicada por row,col
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		try {
			if (this.pagTamany == -1) {
				rs.absolute(row + 1);
			} else {
				int startRow = this.numPagActual * this.pagTamany;
				rs.absolute(startRow + row + 1);
			}
			rs.updateObject(col + 1, value);
			rs.updateRow();
		} catch (SQLException e1) {
			gestionarError(e1);
			try {
				rs.cancelRowUpdates();
			} catch (SQLException e2) {
				gestionarError(e2);
			}
		}
		// Notificamos a los listener que el modelo ha cambiado
		this.fireTableDataChanged();
	}

	/**
	 * Devuelve la clase Java de la columna recibida por parametro.
	 * Todas las columnas son por defecto de tipo String, excepto
	 * la primera columna que es de tipo RowNumber
	 * 
	 * @param : int col
	 * @return : Class class
	 */
	public Class<?> getColumnClass(int col) {
		try {
			if (this.rsmd.getColumnType(col + 1) == java.sql.Types.ARRAY) {
				return java.sql.Array.class;
			}
			String colname = this.rsmd.getColumnClassName(col + 1);
			Class<?> c = ClassLoader.getSystemClassLoader().loadClass(colname);
			if (c == null)
				return String.class;
			return c;
		} catch (SQLException e) {
			gestionarError(e);
			return String.class;
		} catch (ClassNotFoundException e) {
			gestionarError(e);
			return String.class;
		}
	}

	/**
	 * Devuelve el numero de columnas de la tabla
	 * 
	 * @return : int colCount
	 */
	public int getColumnCount() {
		try {
			return this.rsmd.getColumnCount();
		} catch (SQLException e) {
			gestionarError(e);
			return 0;
		}
	}

	/****
	 * Overided API: this method return the row count of the table. Including
	 * the last new editing row. In case of the last page, a number which is
	 * small than the page size is returned, in all other valid case, the page
	 * size returned. If an exception is through, return 0;
	 * Devuelve el numero de filas de la tabla, in 
	 * @return : int rowCount
	 * ***/
	public int getRowCount() {
		try {
			if (!rs.isClosed()) {
				try {
					if (this.pagTamany == -1) { // Aplica si no usamos paginacion
						rs.last();
						return rs.getRow();
					} else { // Si usamos paginacion...
						int startRow = this.numPagActual * this.pagTamany;
						rs.last();
						int rowCount = rs.getRow();
						int lastPageSize = rowCount % this.pagTamany;
						if (startRow >= rowCount - lastPageSize) {
							return rowCount - startRow;
						}
						return this.pagTamany;
					}
				} catch (SQLException e) {
					gestionarError(e);
					return 0;
				}
			}
		} catch (SQLException e) {
			gestionarError(e);
		}
		return 0;
	}

	/**
	 * Devuelve el nombre formateado para la columna recibida por parametro
	 * @param : int col
	 * @return : String 
	 */
	public String getColumnName(int col) {
		try {

			if (TITULOS_AUTOMATICOS) {
				return "<html><body>" + "<b>" + rsmd.getColumnName(col + 1)
						+ "</b>" + "<body></html>";
			} else {
				return "<html><body>" + "<b>" + titulos[col] + "</b>"
						+ "<body></html>";
			}
		} catch (SQLException e) {
			gestionarError(e);
			return "error";
		}
	}

	/**
	 * Comprueba si la celda especificada por parametro es o no editable.
     *
	 * @param : int row, int col
	 * @return : boolean isEditable
	 */
	public boolean isCellEditable(int row, int col) {
		try {
			boolean colWritable = !this.rsmd.isReadOnly(col + 1);
			return (colWritable && !this.isReadOnly());
		} catch (SQLException e) {
			gestionarError(e);
			return false;
		}
	}

	/**
	 * Forzar que la tabla sea de solo lectura
	 * 
	 * @param flag: si la tabla es de solo lectura o no lo es
	 */
	public void setReadOnly(boolean flag) {
		this.IS_READONLY = flag;
	}

	/**
	 * Obtener si la tabla es de solo lectura
	 * 
	 * @return si la tabla es de solo lectura o no lo es
	 */
	public boolean isReadOnly() {
		return this.IS_READONLY;
	}

	
	/**
	 * Elimina un registro del ResultSet.
	 * 
	 * @param row: la fila a borrar
	 *            
	 * @return si el borrado fue bien devuelve null, en otro caso
	 * devuelve un String
	 */
	public String deleteRecord(int row) {
		if (row < 0 || row >= this.getRowCount()) {
			return "Error: la fila especificada no existe!";
		}
		try {
			if (this.pagTamany == -1) { // aplica si no usamos paginacion
				this.rs.deleteRow();
				this.fireTableDataChanged();
				return null;
			} else { // si usamos paginacion...
				int startNum = this.numPagActual * this.pagTamany;
				this.rs.absolute(startNum + row + 1);
				this.rs.deleteRow();
				this.fireTableDataChanged();
				return null;
			}			
		} catch (SQLException e) {
			gestionarError(e);			
			return e.getMessage();
		}
	}

	/**
	 * This method insert a new record into the data set. In case of an error
	 * occurs, an error message is return. In all other cases, null returned.
	 * Inserta un nuevo registro en el ResultSet.
	 * @param newRow
	 *    la nueva fila a insertar
	 * @return 
	 *    si la insercion fue bien retorna null, sino podemos retornar cualquier cosa
	 */
	public String insertRecord(Vector<?> newRow) {
		if (newRow == null) {
			return "Error: intento de insertar un fila nula!";
		}
		try {
			this.rs.moveToInsertRow();
			for (int i = 0; i < this.getColumnCount(); i++) {
				if (i < newRow.size()) {
					this.rs.updateObject(i + 1, newRow.get(i));					
				}
			}
			this.rs.insertRow();
			this.rs.moveToCurrentRow();
			return null;
		} catch (SQLException e1) {
			gestionarError(e1);
			try {
				this.rs.moveToCurrentRow();
				this.rs.cancelRowUpdates();
			} catch (SQLException e2) {
				gestionarError(e2);
			}
			return e1.getMessage();
		} finally {
			this.fireTableDataChanged();
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
