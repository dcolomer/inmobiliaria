package gui.utiles;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import org.jdesktop.application.Application;

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
public class AcercaDe extends JDialog {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel1, jLabel2;
	private JButton btnSalir;	
	private JSeparator jSeparator1;
	
	public AcercaDe(JFrame propietario) {
		super(propietario, "Acerca de...", ModalityType.APPLICATION_MODAL);
		initGUI();		
	}
	
	private void initGUI() {
		
		this.setPreferredSize(new java.awt.Dimension(334, 160));
		this.setBounds(0, 0, 334, 160);
		this.setLocationRelativeTo(null);
		
		{
			jLabel2 = new JLabel();			
			jLabel2.setBounds(0, 0, 83, 79);
			jLabel2.setName("jLabel2");
			jLabel2.setBackground(Color.white);
			jLabel2.setIcon(UtilesGUI.crearImageIcon(this.getClass(),"resources/icons/inmob.png" ));			
			jLabel2.setOpaque(true);
			add(jLabel2);
		}
		{
			jLabel1 = new JLabel();			
			jLabel1.setName("jLabel1");
			jLabel1.setBackground(Color.white);
			jLabel1.setBounds(83, 0, 243, 79);
			jLabel1.setOpaque(true);
			jLabel1.setText("<html><center><font size='3'><b>INMOB 10</b></font><br/>Version: 1.0, aplicacion cliente en Swing<br/>(c) Copyright DCT 2010</center></html>");
			add(jLabel1, "Center");
		}		
		{
			jSeparator1 = new JSeparator();			
			jSeparator1.setBounds(0, 81, 332, 10);
			add(jSeparator1);
		}
		{
			btnSalir = new JButton();								
			btnSalir.setLayout(null);
			btnSalir.setBounds(126, 89, 85, 30);
			btnSalir.setName("btnSalir");
			btnSalir.setToolTipText("Cerrar la pantalla de creditos");
			btnSalir.setIcon(UtilesGUI.crearImageIcon(this.getClass(),"resources/icons/salir16.png" ));
			btnSalir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {						
					dispose();
				}
			});
			add(btnSalir);
		}

		setLayout(null);
		setVisible(true);
		setResizable(false);		
		
		Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);				
	}
	
}
