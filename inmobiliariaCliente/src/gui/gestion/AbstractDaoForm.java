package gui.gestion;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;
import org.jdesktop.application.Application;

import servutiles.ServiciosInmobiliariaFactory;
import utiles.Misc;
import ws.ServiciosInmobiliaria;


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


enum EstadoCRUD {
	LECTURA, NUEVO, MODIFICAR, SOLO_NUEVO // el estado SOLO_NUEVO es para cuando la tabla esta vacia 
}

enum DesplazamientoRegistros {
	FIRST, PREVIOUS, NEXT, LAST
}

/**
 * Clase que contiene aquello que es comun para 
 * cualquier mantenimiento CRUD.
 */
public abstract class AbstractDaoForm extends JInternalFrame {
	
	private static final long serialVersionUID = 1L;
	private static final String HOST_NAME_FILE = "host.properties";	
	private static final String URL=Misc.getBaseDatosURL(HOST_NAME_FILE);
	private static final String msg="Error en operacion de acceso a datos.";		
	protected static EstadoCRUD estadoCRUD;		
	protected final Logger LOG = Logger.getLogger(getClass().getName());
	
	protected int regUltimo;
	protected ResultSet rs;
	
	private JButton btnFirst, btnLast, btnNext, btnPrevious;
	private JButton btnSalir, btnNuevo, btnEliminar, btnModificar, btnCancelar, btnGrabar;
	private JPanel jPanel1;	
	protected JLabel lblRecordNumber;
	private JSeparator jSeparator1;	
	private String titulo;
	private int ancho, alto;
	protected static ServiciosInmobiliaria serviciosInmobiliaria;
	
	static {
		serviciosInmobiliaria=
			ServiciosInmobiliariaFactory.getServicios();
		
		Conexion.setURL(URL);
	}
	
	/*
	 * Constructor
	 */

	public AbstractDaoForm(String SQL, String titulo, int ancho, int alto) {
		this.titulo=titulo;
		this.ancho=ancho;
		this.alto=alto;
		
		inicializar(SQL);
				
		Application.getInstance().getContext().getResourceMap(getClass())
				.injectComponents(this);
		
		// Si en la tabla hay registros entonces nos posicionamos en el primero
		if (regUltimo>0)
			desplazamiento(DesplazamientoRegistros.FIRST);		
	}

	/*
	 * Iniciar el proceso de grabacion de un nuevo registro
	 */
	protected void nuevo() {
		estadoCRUD=EstadoCRUD.NUEVO;
		setEstadoBotones(estadoCRUD);										
		try {
			rs.moveToInsertRow();
		} catch (SQLException e) {																	
			gestionarError(e);
		}
	}
	
	/*
	 * Eliminar un registro
	 */
	protected void eliminar() {
		
		// Preguntar al usuario si esta seguro
		int respuesta = JOptionPane.showConfirmDialog(null,
				"Esta accion eliminara el registro de la base de datos, ¿desea continuar?",
				"Atencion",
				JOptionPane.YES_NO_OPTION);
						
		if (respuesta == JOptionPane.YES_OPTION) {
			int regActual=0;		
			try {
				rs.deleteRow();
				rs.refreshRow();
				regActual=rs.getRow();
				regUltimo--;
				if (rs.getRow()>0)
					desplazamiento(DesplazamientoRegistros.FIRST);
				
			} catch (SQLException e) {																		
				gestionarError(e);
			} finally {
				lblRecordNumber.setText(regActual + " de "
						+ regUltimo);	
			}
		}	
	}
	
	/*
	 * La modificacion es parecida a la insercion pero no
	 * se permite modificar la clave primaria
	 */
	protected void modificar() {
		estadoCRUD=EstadoCRUD.MODIFICAR;
		setEstadoBotones(estadoCRUD);
	}
	
	/*
	 * Grabar. De los detalles se deben 
	 * encargar las clases hijas
	 */
	protected abstract void grabar();
	
	/*
	 * Cancelar la operacion actual de inserci�n o modificaci�n
	 * De los detalles se deben encargar las clases hijas
	 */
	protected abstract void cancelar();
	
	
	/*
	 * Sobre como presentar el ResultSet en los campos se deben 
	 * encargar las clases hijas
	 */
	protected abstract void logicaPresentacion() throws SQLException;
	
	/*
	 * Movimiento por los registros 
	 */
	protected void desplazamiento(DesplazamientoRegistros desplazamiento) {
		try {
			switch (desplazamiento) {
				case FIRST:
				{
					rs.first();
					logicaPresentacion();
					break;
				}
				case PREVIOUS:
				{
					
					if (!rs.isFirst()) {
						rs.previous();
						logicaPresentacion();
					}
					break;
				}
				case NEXT:
				{
					if (!rs.isLast()) {
						rs.next();
						logicaPresentacion();
					}
					break;
				}
				case LAST:
				{
					rs.last();
					logicaPresentacion();
				}
			}
		} catch (SQLException e) {							
			gestionarError(e);
		}
		
	}
	
	

	/*
	 * De los detalles se deben encargar las clases hijas
	 */
	protected abstract void setEstadoControles(EstadoCRUD estado);
	
	/*
	 * 
	 */
	protected void setEstadoBotones(EstadoCRUD estado) {
		switch (estado) {
			case LECTURA:
			{
				setEstadoControles(estado);
				
				btnGrabar.setEnabled(false);
				btnCancelar.setEnabled(false);
				btnNuevo.setEnabled(true);
				btnModificar.setEnabled(true);
				btnEliminar.setEnabled(true);
				btnSalir.setEnabled(true);
				
				btnFirst.setEnabled(true);
				btnPrevious.setEnabled(true);
				btnNext.setEnabled(true);
				btnLast.setEnabled(true);	
				break;
			}
			case NUEVO: case MODIFICAR:
			{
				setEstadoControles(estado);
				
				btnGrabar.setEnabled(true);
				btnCancelar.setEnabled(true);
				btnNuevo.setEnabled(false);
				btnModificar.setEnabled(false);
				btnEliminar.setEnabled(false);
				btnSalir.setEnabled(false);
				
				btnFirst.setEnabled(false);
				btnPrevious.setEnabled(false);
				btnNext.setEnabled(false);
				btnLast.setEnabled(false);
				break;
			}
			case SOLO_NUEVO:
			{
				// Podemos establecer el estado a NUEVO
				// para que los controles del formulario
				// permitan la insercion del nuevo registro 
				estadoCRUD=EstadoCRUD.NUEVO;				
				setEstadoControles(estadoCRUD);
				
				// Solo deben estar activos los botones
				// de grabar y salir
				btnGrabar.setEnabled(true);
				btnCancelar.setEnabled(false);
				btnNuevo.setEnabled(false);
				btnModificar.setEnabled(false);
				btnEliminar.setEnabled(false);
				btnSalir.setEnabled(true);
				
				btnFirst.setEnabled(false);
				btnPrevious.setEnabled(false);
				btnNext.setEnabled(false);
				btnLast.setEnabled(false);
			}
				
		}
	}
	
	/*
	 * 
	 */
	private void inicializar(String SQL) {
		int tipoDesplaz = ResultSet.TYPE_SCROLL_INSENSITIVE;
		int tipoActualiz = ResultSet.CONCUR_UPDATABLE;

		try {			
			boolean esPosible = Conexion.getConexion().getMetaData()
					.supportsResultSetConcurrency(tipoDesplaz, tipoActualiz);

			if (esPosible) {
				Statement stmt = Conexion.getConexion().createStatement(
						tipoDesplaz, tipoActualiz);
				
				rs = stmt.executeQuery(SQL);
				
				dibujarGUI();
								
				// Obtener el total de registros en el ResultSet
				if (rs != null) {  
					try {
						rs.beforeFirst();
						rs.last();  
						regUltimo = rs.getRow();  
					} catch (SQLException e) {				
						gestionarError(e);
					}  			
				}		
				
				// Si regUltimo==0 significa que la tabla est� vac�a, por tanto 
				// sugerimos al usuario que introduzca un nuevo registro
				if (regUltimo==0)
					estadoCRUD=EstadoCRUD.SOLO_NUEVO;
				else
					estadoCRUD=EstadoCRUD.LECTURA;
				setEstadoBotones(estadoCRUD);				
			} else {
				Exception e=new Exception("El ResultSet no puede ser actualizable.");
				gestionarError(e);				
			}
		} catch (SQLException e) {
			gestionarError(e);
		}				
	}

	/*
	 * 
	 */
	private void dibujarBotones() {
		{
			btnNuevo = new JButton();			
			btnNuevo.setLayout(null);
			btnNuevo.setBounds(5, 155, 75, 42);
			btnNuevo.setName("btnNuevo");
			btnNuevo.setToolTipText("Nuevo registro");
			btnNuevo.setHorizontalTextPosition(SwingConstants.CENTER);
			btnNuevo.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnNuevo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					nuevo();															
				}
			});
			getContentPane().add(btnNuevo);
		}
		
		{
			btnModificar = new JButton();			
			btnModificar.setLayout(null);
			btnModificar.setBounds(85, 155, 75, 42);
			btnModificar.setName("btnModificar");
			btnModificar.setToolTipText("Editar el registro actual");
			btnModificar.setHorizontalTextPosition(SwingConstants.CENTER);
			btnModificar.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnModificar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					modificar();
				}
			});
			getContentPane().add(btnModificar);
		}
		
		{
			btnEliminar = new JButton();			
			btnEliminar.setLayout(null);
			btnEliminar.setBounds(165, 155, 75, 42);
			btnEliminar.setName("btnEliminar");
			btnEliminar.setToolTipText("Eliminar registro actual");
			btnEliminar.setHorizontalTextPosition(SwingConstants.CENTER);
			btnEliminar.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnEliminar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					eliminar();
				}
			});
			getContentPane().add(btnEliminar);
		}
		
		{
			btnGrabar = new JButton();			
			btnGrabar.setLayout(null);
			btnGrabar.setName("btnGrabar");
			btnGrabar.setBounds(245, 155, 75, 42);
			btnGrabar.setToolTipText("Grabar registro actual");
			btnGrabar.setHorizontalTextPosition(SwingConstants.CENTER);
			btnGrabar.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnGrabar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					grabar();
					
				}
			});
			getContentPane().add(btnGrabar);
		}
		
		{
			btnCancelar = new JButton();			
			btnCancelar.setLayout(null);
			btnCancelar.setBounds(325, 155, 85, 42);
			btnCancelar.setName("btnCancelar");
			btnCancelar.setToolTipText("Cancelar la operacion en curso");
			btnCancelar.setHorizontalTextPosition(SwingConstants.CENTER);
			btnCancelar.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					cancelar();										
				}
			});
			getContentPane().add(btnCancelar);
		}
				
		
		{
			btnSalir = new JButton();			
			btnSalir.setLayout(null);
			btnSalir.setBounds(423, 155, 75, 42);
			btnSalir.setName("btnSalir");
			btnSalir.setToolTipText("Cerrar el mantenimiento");
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
			getContentPane().add(jSeparator1);
			jSeparator1.setLayout(null);
			jSeparator1.setBounds(6, 148, 493, 9);
		}
		
		
		
		/*
		 * Barra de navegacion		
		 */
		
		{
			jPanel1 = new JPanel();
			getContentPane().add(jPanel1);
			jPanel1.setLayout(null);
			jPanel1.setBounds(151, 114, 309, 28);
			jPanel1.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			jPanel1.setName("jPanel1");
			{
				btnPrevious = new JButton();				
				btnPrevious.setLayout(null);
				btnPrevious.setName("btnPrevious");
				btnPrevious.setBounds(54, 3, 50, 21);
				btnPrevious.setToolTipText("Registro anterior");
				btnPrevious.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						desplazamiento(DesplazamientoRegistros.PREVIOUS);
					}
				});
				jPanel1.add(btnPrevious);
			}
			{
				btnNext = new JButton();				
				btnNext.setLayout(null);
				btnNext.setName("btnNext");
				btnNext.setBounds(204, 3, 50, 21);
				btnNext.setToolTipText("Registro siguiente");
				btnNext.addActionListener(new ActionListener() {					
					public void actionPerformed(ActionEvent evt) {
						desplazamiento(DesplazamientoRegistros.NEXT);						
					}
				});
				jPanel1.add(btnNext);
			}
			{
				btnLast = new JButton();				
				btnLast.setLayout(null);
				btnLast.setName("btnLast");
				btnLast.setBounds(255, 3, 52, 21);
				btnLast.setSize(50, 21);
				btnLast.setToolTipText("Ultimo registro");
				btnLast.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						desplazamiento(DesplazamientoRegistros.LAST);						
					}
				});
				jPanel1.add(btnLast);
			}
			{
				btnFirst = new JButton();				
				btnFirst.setLayout(null);
				btnFirst.setBounds(3, 3, 43, 21);
				btnFirst.setName("btnFirst");
				btnFirst.setSize(50, 21);
				btnFirst.setToolTipText("Primer registro");
				btnFirst.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						desplazamiento(DesplazamientoRegistros.FIRST);						
					}
				});
				jPanel1.add(btnFirst);
			}
			{
				lblRecordNumber = new JLabel();				
				lblRecordNumber.setLayout(null);
				lblRecordNumber.setBounds(103, 3, 101, 18);
				lblRecordNumber.setName("lblRecordNumber");
				lblRecordNumber.setBorder(BorderFactory
						.createBevelBorder(BevelBorder.LOWERED));
				lblRecordNumber.setBackground(new Color(255, 255, 255));
				lblRecordNumber.setHorizontalAlignment(SwingConstants.CENTER);
				lblRecordNumber.setSize(101, 21);
				jPanel1.add(lblRecordNumber);
			}
		}
		
		
	}
	
	/*
	 * etiquetas y campos
	 * De los detalles se deben encargar las clases hijas
	 */
	protected abstract void dibujarEtiquetasAndCampos();
	
	
	
	/*
	 * 
	 */
	protected void dibujarGUI() {
		setSize(ancho, alto);
		setTitle(titulo);
				
		getContentPane().setLayout(null);

		this.setPreferredSize(new java.awt.Dimension(ancho, alto));
		this.setBounds(0, 0, ancho, alto);
		
		dibujarEtiquetasAndCampos();
		dibujarBotones();
						
		this.pack();

		Application.getInstance().getContext().getResourceMap(getClass())
				.injectComponents(this);
	}

	/*
	 * 
	 */
	protected void salir() {		
		try {
			rs.getStatement().close();
			rs.close();			
			setClosed(true);
		} catch (Exception e) {			
			LOG.error(e);
			Misc.mostrarError(e, "Error al salir del formulario", false);
		}
		
	}
	
	protected void gestionarError(Exception e) {
		LOG.error(msg, e);			
		Misc.mostrarError(e, msg, false);
	}
}
