package gui.utiles;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * Cuadro de dialogo para mostrar excepciones de forma comoda.
 * Permite ocultar/visualizar la pila de errores 
 *
 */
public class ExceptionDialog extends JDialog 
	implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JLabel lblDesc, lblExcep, lblMsg;
	private JButton btnCerrar, btnDetalles;
	private JTextField txtExcep, txtMsg;
	private JScrollPane scDetalles;
	private JTextArea txtaDetalles;

	// La caja de texto multilinea asociada
	private JTextAreaPrintStream detallesPrintStream;

	/**
	 * 
	 * @param title: El mensaje resumen del error
	 * @param t: la excepcion
	 */
	public void showForThrowable(String titulo, Throwable t) {
		setTitle("Se ha producido un error");
		lblDesc.setText(titulo);
		String nombre = t.getClass().getName();
		nombre = nombre.substring(nombre.lastIndexOf(".") + 1);
		txtExcep.setText(nombre);
		txtMsg.setText(t.getMessage());
		txtaDetalles.setText(null);
		t.printStackTrace(detallesPrintStream);
		txtaDetalles.setCaretPosition(0);
		showDetails(false);
		setVisible(true);
	}
	
	/**
	 * 
	 * @param show
	 */
	public void showDetails(boolean show) {
		btnDetalles.setText(show?"Ocultar detalles":"Mostrar detalles");
		lblExcep.setVisible(show);
		txtExcep.setVisible(show);
		lblMsg.setVisible(show);
		txtMsg.setVisible(show);
		scDetalles.setVisible(show);
		pack();
		//positionOverFrame();
	}
		
	/*private void positionOverFrame() {
		Point p = getOwner().getLocation();
		Dimension d = getOwner().getSize();
		p.x += (d.width - getWidth()) >> 1;
		p.y += (d.height - getHeight()) >> 1;
		if(p.x < 0)
			p.x = 0;
		if(p.y < 0)
			p.y = 0;
		
		this.setLocation(p);
	}*/
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCerrar) {
			setVisible(false);
		} else if(e.getSource() == btnDetalles) {
			showDetails(!scDetalles.isVisible());
		}
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			setVisible(false);
		}
	}
	
	public void keyReleased(KeyEvent e)	{
	}
	
	public void keyTyped(KeyEvent e) {
	}
	
	/**
	 * Constructor
	 * @param tipoModalidad 
	 */
	public ExceptionDialog(JFrame owner, ModalityType tipoModalidad) {
		super(owner, tipoModalidad);
		setResizable(false);
		addKeyListener(this);
		Icon icon = UIManager.getIcon("ExceptionDialog.errorIcon");
		
		if (icon == null) {
			icon = UIManager.getIcon("OptionPane.errorIcon");
		}
		
		lblDesc = new JLabel(icon, SwingConstants.CENTER);
		lblDesc.setIconTextGap(10);
		btnCerrar = new JButton("Cerrar");
		btnCerrar.addActionListener(this);
		btnDetalles = new JButton();
		btnDetalles.setMnemonic('d');
		btnDetalles.addActionListener(this);
		lblExcep = new JLabel("Excepcion:", SwingConstants.LEFT);
		txtExcep = new JTextField(30);
		txtExcep.setEditable(false);
		lblExcep.setLabelFor(txtExcep);
		lblMsg = new JLabel("Mensaje:", SwingConstants.LEFT);
		txtMsg = new JTextField(30);
		txtMsg.setEditable(false);
		lblMsg.setLabelFor(txtMsg);
		txtaDetalles = new JTextArea();
		txtaDetalles.setFont(new Font("Dialog", Font.PLAIN, 9));
		txtaDetalles.setTabSize(4);
		txtaDetalles.setEditable(false);
		detallesPrintStream = new JTextAreaPrintStream(txtaDetalles);
		scDetalles = new JScrollPane(txtaDetalles);
		scDetalles.setPreferredSize(new Dimension(500, 200));
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0f;
		gbc.weighty = 0.01f;
		gbc.insets = new Insets(12, 12, 0, 12);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		getContentPane().add(lblDesc, gbc);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0f;
		gbc.insets = new Insets(11, 12, 0, 12);
		gbc.anchor = GridBagConstraints.WEST;
		getContentPane().add(lblExcep, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0f;
		gbc.insets = new Insets(11, 0, 0, 12);
		gbc.anchor = GridBagConstraints.WEST;
		getContentPane().add(txtExcep, gbc);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0f;
		gbc.insets = new Insets(11, 12, 0, 12);
		gbc.anchor = GridBagConstraints.WEST;
		getContentPane().add(lblMsg, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0f;
		gbc.insets = new Insets(11, 0, 0, 12);
		gbc.anchor = GridBagConstraints.WEST;
		getContentPane().add(txtMsg, gbc);
		gbc.weighty = 1.0f;
		gbc.insets = new Insets(11, 12, 0, 12);
		gbc.anchor = GridBagConstraints.CENTER;
		getContentPane().add(scDetalles, gbc);
		JPanel buttonPanel = new JPanel(new BorderLayout(5, 0));
		buttonPanel.add(btnCerrar, BorderLayout.WEST);
		buttonPanel.add(btnDetalles, BorderLayout.EAST);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0.0f;
		gbc.insets = new Insets(17, 12, 11, 11);
		gbc.anchor = GridBagConstraints.EAST;
		getContentPane().add(buttonPanel, gbc);
		getRootPane().setDefaultButton(btnCerrar);
	}

}
