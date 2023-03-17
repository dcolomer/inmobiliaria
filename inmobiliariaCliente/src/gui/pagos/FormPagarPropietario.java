package gui.pagos;

import gui.FormPrincipal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.jdesktop.application.Application;

import ws.CajaYaCerradaException_Exception;
import ws.NoPedidosPendientesPagoPropitarioException_Exception;
import ws.PedidoPiso;
import ws.Propietario;



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
public class FormPagarPropietario extends AbstractFormBasePagos {

	private static final long serialVersionUID = 1L;
	
	private JButton btnSalir, btnPagar;
	
	private JSeparator jSeparator1;
	private JLabel lblPedidos, lblPropietario;
	private JComboBox cmbNIFPropietario;
	private JList lstPedidos; // Representacion grafica de los pedidos para el usuario		

	private JScrollPane jScrollPane1;
	

	public static FormPagarPropietario createForm() {			
		return new FormPagarPropietario();
	}
	
	private FormPagarPropietario() {
		super();			
		initGUI();
	}
	
	private void initGUI() {
		
		setFrameIcon(new javax.swing.ImageIcon(getClass()
				.getResource("resources/icons/dinero16.png")));
		

		this.setPreferredSize(new java.awt.Dimension(557, 240));
		this.setBounds(0, 0, 557, 240);
		setVisible(true);
		getContentPane().setLayout(null);
		
		{
			lblPropietario = new JLabel();
			getContentPane().add(lblPropietario, "Center");
			lblPropietario.setLayout(null);
			lblPropietario.setName("lblPropietario");
			lblPropietario.setBounds(17, 19, 69, 19);
		}			
		{
			lblPedidos = new JLabel();
			getContentPane().add(lblPedidos);
			lblPedidos.setLayout(null);
			lblPedidos.setBounds(314, 19, 166, 19);
			lblPedidos.setName("lblPedidos");
		}
		
		
		{
			btnPagar = new JButton();
			getContentPane().add(btnPagar);
			btnPagar.setLayout(null);
			btnPagar.setBounds(17, 155, 85, 42);
			btnPagar.setName("btnPagar");
			btnPagar.setToolTipText("Pagar los pedidos");
			btnPagar.setHorizontalTextPosition(SwingConstants.CENTER);
			btnPagar.setVerticalTextPosition(SwingConstants.BOTTOM);				
			btnPagar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					btnPagarPedidosActionPerformed(evt);
				}					
			});
		}
		{
			btnSalir = new JButton();								
			btnSalir.setLayout(null);
			btnSalir.setBounds(448, 155, 85, 42);
			btnSalir.setName("btnSalir");
			btnSalir.setToolTipText("Cerrar la gestion de pagos a propietarios");
			btnSalir.setHorizontalTextPosition(SwingConstants.CENTER);
			btnSalir.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnSalir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						FormPrincipal.setFrmPagarPropietarioActivo(false);
						setClosed(true);
					} catch (PropertyVetoException e) {						
						gestionarError("Error en el cierre de formulario", e);
					}
				}
			});
			getContentPane().add(btnSalir);
		}
		{
			ComboBoxModel cmbNIFPropietarioModel = 
			new DefaultComboBoxModel(getNIFPropietarios());
			cmbNIFPropietario = new JComboBox();
			cmbNIFPropietario.setModel(cmbNIFPropietarioModel);
			cmbNIFPropietario.setBounds(17, 44, 188, 21);
			getContentPane().add(cmbNIFPropietario);
			
			cmbNIFPropietario.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {						
					String init=cmbNIFPropietario.getItemAt(0).toString();
					if (init.equals("Seleccionar"))
						cmbNIFPropietario.removeItem("Seleccionar");
					
					cmbNIFPropietarioItemStateChanged(evt);
				}
			});
		}
		{
			jSeparator1 = new JSeparator();
			jSeparator1.setBounds(17, 140, 515, 10);
			getContentPane().add(jSeparator1);				
		}
		
		{				
			lstPedidos = new JList();
			jScrollPane1 = new JScrollPane(lstPedidos);
			getContentPane().add(jScrollPane1);
			jScrollPane1.setBounds(300, 44, 220, 80);
		}
	
		setTitle("Pago a propietarios");
					
		Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
	}
	
	
	/*
	 * Cargar el combo con los NIF's de los propietarios
	 */
	private String[] getNIFPropietarios() {
		
		final String[] EMPTY_NIF = new String[0];
		
		Collection<Propietario> propietarios=serviciosInmobiliaria.getPropietariosCacheados();
		
		if (!propietarios.isEmpty()) {
			Collection<String> nifs=new ArrayList<String>(propietarios.size()+1);
			nifs.add("Seleccionar");
			for (Propietario propietario:propietarios) {
				nifs.add(propietario.getNifProp());	
			}
			return nifs.toArray(EMPTY_NIF);
		}
			
		return EMPTY_NIF;				
	}
	
	/**
	 * Cuando el usuario selecciona un NIF de propietario
	 * se obtiene los pedidos pendientes de este
	 */
	private void cmbNIFPropietarioItemStateChanged(ItemEvent evt) {
				
		// Limpiar la lista de pedidos, ya que vamos a buscar
		// los pedidos pendientes de pago del propietario seleccionado
		// y no queremos que aparezcan los del propietario anterior
		lstPedidos.setListData(new Object[]{});
		
		// Obtener el indice del combo de NIFs
		// Si vale -1 significa que no se ha seleccionado nada
		int index=cmbNIFPropietario.getSelectedIndex();
		
		// Obtenemos el elemento del combo
		String nif=cmbNIFPropietario.getItemAt(index).toString();
		
		// Si efectivamente se ha seleccionado algun piso...
		if (index>-1 && !nif.equals("Seleccionar"))	{			
			obtenerPedidosPendientes(nif);						
		}							
	}

	/**
	 * Rellenar la lista con los pedidos pendientes
	 * @param nif del propietario
	 */
	private void obtenerPedidosPendientes(String nif) {
				
		Collection<PedidoPiso> pedidos=serviciosInmobiliaria.getPedidosNoPagadosPropietario(nif);
		
		if (!pedidos.isEmpty()) {
			
			// Para establecer el modelo de datos al JList nos va bien un Vector
			Vector<String> listaPedidos=new Vector<String>(pedidos.size());
			
			for (PedidoPiso pedido:pedidos) {										
				listaPedidos.add(pedido.getNPedido() + " - " + pedido.getDir());
			}
							
			// Establecemos el modelo de datos para el JList
			lstPedidos.setListData(listaPedidos);
			
			// Seleccionar toda la lista de pedidos
			lstPedidos.setSelectionInterval(0, listaPedidos.size()-1);
		} else {
			lstPedidos.setListData(new Object[]{});
		}
		
	}

	/*
	 * Manejador para el boton 'Pagar'
	 */
	private void btnPagarPedidosActionPerformed(ActionEvent evt) {
		
		// Obtenemos el nif del propietario que debe cobrarlo
		
		// Obtener el indice del combo de NIF's de propietarios.
		// Si vale -1 significa que no se ha seleccionado nada
		int index=cmbNIFPropietario.getSelectedIndex();
		
		// Obtenemos el elemento del combo
		String nif_prop=cmbNIFPropietario.getSelectedItem().toString();
		
		// Si efectivamente se ha seleccionado algun nif...
		if (index>-1 && !nif_prop.equals("Seleccionar")) {
			if (LOG.isDebugEnabled())
				LOG.debug("NIF del propietario: "+nif_prop);
		
			// ****		
						
			Object[] elementosSeleccionados=lstPedidos.getSelectedValues();
			
			int totalElementos=elementosSeleccionados.length;			
					
			if (totalElementos==0) {							
				String msg="No hay pedidos pendientes de pago al propietario especificado";
				JOptionPane.showMessageDialog(this.getParent(), msg, 
						"Resultado del pago", JOptionPane.INFORMATION_MESSAGE,null);
			} else {
								
				List<Long> pedidos=new ArrayList<Long>(totalElementos);
				
				for (Object obj: elementosSeleccionados) {
					// Obtenemos el elemento actual de la lista			
					String pedido_sel=(String) obj;
					
					// Nos quedamos solo con el numero de pedido
					pedido_sel=pedido_sel.split("-")[0].trim();
					
					if (LOG.isDebugEnabled())
						LOG.debug("Pedido numero: "+pedido_sel);
					
					// Lo pasamos a numero
					pedidos.add(Long.valueOf(pedido_sel));
				}
												
				// Procedemos al pago de todos los pedidos
				try {						
					float importeTotal=serviciosInmobiliaria
						.pagarPedidosPropietario(nif_prop, pedidos);
					
					String msg="Pago realizado correctamente. Importe: "+
						NumberFormat.getNumberInstance().format(importeTotal)+ "€";		
						
					JOptionPane.showMessageDialog(this.getParent(), msg, 
							"Resultado del pago", JOptionPane.INFORMATION_MESSAGE, null);
					
					// limpiar la lista de pedidos pendientes										
					obtenerPedidosPendientes(nif_prop);
											
				} catch (NoPedidosPendientesPagoPropitarioException_Exception e) {
					String msg="Pago NO realizado. No existen pedidos pendientes de pagar al propietario especificado.";																			
					gestionarError(msg,e);		
				} catch (CajaYaCerradaException_Exception e) {
					String msg="Pago NO realizado. La caja ya se encuentra cerrada.";																			
					gestionarError(msg,e);				
				} catch (Exception e) {					
					String msg="Pago NO realizado.";
					gestionarError(msg,e);
				}
			}
		} else {
			if (LOG.isDebugEnabled())
				LOG.debug("Falta especificar el NIF del propietario.");
		}
	}		
}
