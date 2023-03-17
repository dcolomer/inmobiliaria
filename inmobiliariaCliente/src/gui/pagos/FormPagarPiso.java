package gui.pagos;

import gui.FormPrincipal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

import org.jdesktop.application.Application;

import ws.CajaYaCerradaException_Exception;
import ws.PedidoPiso;
import ws.PedidoYaPagadoException_Exception;
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
public class FormPagarPiso extends AbstractFormBasePagos {

	private static final long serialVersionUID = 1L;
	
	private JButton btnSalir, btnPagar;
	private JComboBox cmbPedidosPend, cmbNIFCliente;
	
	private DefaultComboBoxModel cmbPedidosModel;
	
	private JSeparator jSeparator1;
	private JLabel lblPedidos, lblCliente;

	public static FormPagarPiso createForm() {			
		return new FormPagarPiso();
	}
	
	private FormPagarPiso() {
		super();				
		initGUI();
	}
	
	private void initGUI() {
		
		setFrameIcon(new javax.swing.ImageIcon(getClass()
				.getResource("resources/icons/pagarpiso16.png")));
		
		this.setPreferredSize(new java.awt.Dimension(557, 193));
		this.setBounds(0, 0, 557, 193);
		setVisible(true);
		getContentPane().setLayout(null);
		
		{
			lblCliente = new JLabel();
			getContentPane().add(lblCliente, "Center");
			lblCliente.setLayout(null);
			lblCliente.setName("lblCliente");
			lblCliente.setBounds(17, 19, 59, 19);
		}			
		{
			lblPedidos = new JLabel();
			getContentPane().add(lblPedidos);
			lblPedidos.setLayout(null);
			lblPedidos.setBounds(314, 19, 104, 18);
			lblPedidos.setName("lblPedidos");
		}
		{
			cmbPedidosModel = 
				new DefaultComboBoxModel(
						new String[] { "?" });
			cmbPedidosPend = new JComboBox();
			cmbPedidosPend.setModel(cmbPedidosModel);
			cmbPedidosPend.setBounds(314, 44, 218, 21);
			getContentPane().add(cmbPedidosPend);
		}
		{
			btnPagar = new JButton();
			getContentPane().add(btnPagar);
			btnPagar.setLayout(null);
			btnPagar.setBounds(17, 112, 85, 42);
			btnPagar.setName("btnPagar");
			btnPagar.setToolTipText("Realizar el pago");
			btnPagar.setHorizontalTextPosition(SwingConstants.CENTER);
			btnPagar.setVerticalTextPosition(SwingConstants.BOTTOM);				
			btnPagar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					btnPagarActionPerformed(evt);
				}					
			});
		}
		{
			btnSalir = new JButton();								
			btnSalir.setLayout(null);
			btnSalir.setBounds(448, 111, 85, 42);
			btnSalir.setName("btnSalir");
			btnSalir.setToolTipText("Cerrar la gestion de pago de pisos");
			btnSalir.setHorizontalTextPosition(SwingConstants.CENTER);
			btnSalir.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnSalir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						FormPrincipal.setFrmPagarPisoActivo(false);
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
			cmbNIFCliente.setBounds(17, 44, 188, 21);
			getContentPane().add(cmbNIFCliente);
			
			cmbNIFCliente.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					String init=cmbNIFCliente.getItemAt(0).toString();
					if (init.equals("Seleccionar"))
						cmbNIFCliente.removeItem("Seleccionar");
					cmbNIFClienteItemStateChanged(evt);
				}
			});
		}
		{
			jSeparator1 = new JSeparator();
			jSeparator1.setBounds(17, 89, 515, 10);
			getContentPane().add(jSeparator1);
		}
				
		setTitle("Pago de piso");
					
		Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);		
	}
	
	
	/*
	 * Cuando el usuario selecciona un NIF de cliente
	 * se obtiene los pendidos pendientes de este
	 */
	private void cmbNIFClienteItemStateChanged(ItemEvent evt) {

		Collection<PedidoPiso> pedidos;
		
		// Obtener el indice del combo de NIFs
		// Si vale -1 significa que no se ha seleccionado nada
		int index=cmbNIFCliente.getSelectedIndex();
		
		// Obtenemos el elemento del combo
		String nif=cmbNIFCliente.getItemAt(index).toString();
		
		// Si efectivamente se ha seleccionado algun piso...
		if (index>-1 && !nif.equals("?"))	{
			
			cmbPedidosPend.removeAllItems();
			// Alternativamente podriamos utilizar el modelo del combo
			//cmbPedidosModel.removeAllElements();
			
			pedidos=serviciosInmobiliaria.getPedidosNoPagadosByNIF(nif);
			if (!pedidos.isEmpty()) {				
				for (PedidoPiso pedido:pedidos) {					
					cmbPedidosPend.addItem(pedido.getNPedido() + " - " + pedido.getDir());
					// Alternativamente podriamos utilizar el modelo del combo
					//cmbPedidosModel.addElement(pedido.getN_pedido());
				}				
			} else {
				cmbPedidosModel.addElement("?");
			}
		}	
						
	}

	
	/*
	 * Manejador para el boton 'Pagar'
	 */
	private void btnPagarActionPerformed(ActionEvent evt) {
		
		// Obtener el indice del combo de pedidos pendientes.
		// Si vale -1 significa que no se ha seleccionado nada
		int indexPP=cmbPedidosPend.getSelectedIndex();
		
		// Obtenemos el elemento del combo
		String pedido_sel=cmbPedidosPend.getSelectedItem().toString();
		
		// Si efectivamente se ha seleccionado algun pedido...
		if (indexPP>-1 && !pedido_sel.equals("?"))	{
						
			// Nos quedamos solo con el numero de pedido
			pedido_sel=pedido_sel.split("-")[0].trim();
			if (LOG.isDebugEnabled())
				LOG.debug("Pedido numero: "+pedido_sel);
			
			// Lo pasamos a numero
			Integer int_pedido_sel=Integer.valueOf(pedido_sel);
			
			// Ahora obtenemos el nif del cliente que debe pagarlo
			
			// Obtener el indice del combo de NIF's de clientes.
			// Si vale -1 significa que no se ha seleccionado nada
			int indexNF=cmbNIFCliente.getSelectedIndex();
			
			// Obtenemos el elemento del combo
			String nif_cli=cmbNIFCliente.getSelectedItem().toString();
			
			// Si efectivamente se ha seleccionado algun nif...
			if (indexNF>-1 && !nif_cli.equals("")) {
				if (LOG.isDebugEnabled())
					LOG.debug("NIF del cliente: "+nif_cli);
				
				// Procedemos al pago
				try {
					long num_factura=serviciosInmobiliaria.pagarPiso(nif_cli, int_pedido_sel);
					
					if (num_factura>0) {
						String msg="Pago realizado correctamente. Factura: "+num_factura+ "\n"
						+"Importe a cobrar: "+serviciosInmobiliaria.getFactura(num_factura).getImporte()+"€";		
						
						JOptionPane.showMessageDialog(this.getParent(), msg, 
								"Resultado del pago", JOptionPane.INFORMATION_MESSAGE, null);
						
						limpiarPantalla(indexPP);
						
					} else {
						String msg="Ha ocurrido algun problema al pagar el pedido.";						
						Exception e=new ServiciosException_Exception(msg, new ServiciosException());
						gestionarError(msg,e);
						
					}
				} catch (PedidoYaPagadoException_Exception e) {
					String msg="Pago NO realizado. Comprobar que el pedido no se encuentre ya pagado.";
					gestionarError(msg,e);
				} catch (CajaYaCerradaException_Exception e) {
					String msg="Pago NO realizado. La caja ya se encuentra cerrada.";																			
					gestionarError(msg,e);	
				} catch (Exception e) {					
					String msg="Pago NO realizado.";
					gestionarError(msg,e);
				}
			} else {
				if (LOG.isDebugEnabled())
					LOG.debug("Falta especificar el NIF del cliente.");
			}
		}	
		else {
			if (LOG.isDebugEnabled())
				LOG.debug("No hay pedidos pendientes de pago para el cliente especificado");
		}	
	}

	// Eliminar el pedido del combo - limpiar pantalla -
	private void limpiarPantalla(int indexPP) {		
		cmbPedidosPend.removeItemAt(indexPP);
		if (cmbPedidosModel.getSize()==0) // si ya no quedan pisos
			cmbPedidosPend.addItem(crearObj("?"));
		
	}
	
}
