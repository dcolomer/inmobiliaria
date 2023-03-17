package gui.utiles;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.apache.log4j.Logger;

import servutiles.ServiciosInmobiliariaFactory;

import ws.ServiciosInmobiliaria;
import ws.Usuario;

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

/**
 * Clase que muestra una cuadro de dialogo para que el 
 * usuario introduzca sus credenciales. En caso de no
 * validarse la aplicacion no permite el acceso a la 
 * aplicacion.
 * 
 */
public class Login extends JDialog {

	private static final long serialVersionUID = 1L;
	protected final Logger LOG=Logger.getLogger(this.getClass());
	protected final static ServiciosInmobiliaria serviciosInmobiliaria;
	protected Usuario usuario;
	protected JFrame propietario;
	
	private JLabel jLabel1, jLabel2, jLabel3;
	protected JTextField jTextField1;
	protected JPasswordField jPasswordField1;
	protected JButton jButton1, jButton2;
	protected int contador_errores;
	
	static {		
		serviciosInmobiliaria=ServiciosInmobiliariaFactory.getServicios();		
	}
	
	/**
	 * Constructor.
	 * @propietario: la ventana que contiene al cuadro de dialogo
	 * @modalidad: modal/no modal (normalmente modal) 
	 */
	public Login(JFrame propietario, String tit, Dialog.ModalityType modalidad) {
		// Invocamos a super, indicando adicionalmente
		// el titulo para la vetana
		super(propietario, tit, modalidad);
		this.propietario=propietario;		
		this.setResizable(false);		
		this.setSize(337, 175);
		
		// Centramos el cuadro de dialogo justo en la
		// mitad de la pantalla
		int posx=(propietario.getWidth()/2)-(this.getWidth()/2);
		int posy=(propietario.getHeight()/2)-(this.getHeight()/2);
		this.setLocation(new Point(posx,posy));
		
		create(); // Creacion de los controles		
	}

	/**
	 * Creacion de la GUI
	 */
	private void create() {
		
		// Listener de la ventana. Si el usuario
		// la cierra finaliza la aplicacion
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				salir();
			}
		});

		// No usamos ningun layout manager
		setLayout(null);
										
		
		//
		// Etiqueta 'usuario:'
		//
		jLabel1 = new JLabel();
		jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
		jLabel1.setForeground(new Color(0, 0, 255));
		jLabel1.setText("usuario:");
		jLabel1.setBounds(110, 9, 106, 18);
		add(jLabel1);

		//
		// Etiqueta 'contraseña:'
		//
		jLabel2 = new JLabel();
		jLabel2.setHorizontalAlignment(SwingConstants.LEFT);
		jLabel2.setForeground(new Color(0, 0, 255));
		jLabel2.setText("contraseña:");
		jLabel2.setBounds(110, 54, 97, 18);
		add(jLabel2);
		
		// Icono con las llaves
		// Cargamos la imagen manualmente
		jLabel3 = new JLabel(UtilesGUI.crearImageIcon(this.getClass(), "resources/icons/llaves.png"));
		jLabel3.setBounds(6, 24, 86, 81);		
		add(jLabel3);
		
		
		//
		// Caja de texto para introducir el nombre de usuario
		//
		jTextField1 = new JTextField();
		jTextField1.setForeground(new Color(0, 0, 255));
		jTextField1.setSelectedTextColor(new Color(0, 0, 255));
		jTextField1.setToolTipText("Introducir un nombre de usuario valido");
		jTextField1.setBounds(110, 27, 183, 22);		
		add(jTextField1);
		
		//
		// Caja de texto para la contraseña.  
		//
		jPasswordField1 = new JPasswordField();
		jPasswordField1.setForeground(new Color(0, 0, 255));
		jPasswordField1
				.setToolTipText("Introducir la contraseña");
		jPasswordField1.setBounds(110, 71, 183, 22);
		jPasswordField1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}

		});
		add(jPasswordField1);
		
		//
		// Boton 'Acceder'
		//
		jButton1 = new JButton();		
		jButton1.setBounds(110, 105, 85, 27);
		jButton1.setText("Acceder");
		jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}

		});
		add(jButton1);
		
		//
		// Boton 'Salir'
		//
		jButton2 = new JButton();
		jButton2.setBounds(206, 105, 85, 27);
		jButton2.setText("Salir");
		jButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				salir();
			}
		});		
		add(jButton2);
	}


	/**
	 * Manejador para la pulsacion del boton 'Acceder'.	 
	 *
	 */
	protected void jButton1_actionPerformed(ActionEvent e) {
		
		String username = jTextField1.getText();
		String password = new String(jPasswordField1.getPassword());

		/*
		 * Se comprueba que ni el nombre de usuario ni la
		 * contraseña esten en blanco. En caso contrario se
		 * muestra una ventana informando de este hecho.
		 */
		if (username.equals("") || password.equals("")) {
			String msg="Debe proporcionar un nombre de usuario y una contraseña.";
			jButton1.setEnabled(false);
			JLabel errorFields = new JLabel(
					"<HTML><FONT COLOR = Blue>"+msg+"</FONT></HTML>");
			JOptionPane.showMessageDialog(null, errorFields);			
			if (LOG.isInfoEnabled())
				LOG.info(msg);
			jButton1.setEnabled(true);
			jTextField1.requestFocusInWindow();
			
		// Ambos campos tienen valor 	
		} else {

			usuario=serviciosInmobiliaria.comprobarCredenciales(username, password);
			// Si las credenciales son correctas
			if (usuario!=null) {
				String tit=propietario.getTitle()+" - usuario: "+usuario.getLogin();
				propietario.setTitle(tit);
				dispose();
				
			// Si las credenciales son incorrectas
			} else {
				String msg="Las credenciales son incorrectas.";				
				jButton1.setEnabled(false);
				contador_errores++;
				if (contador_errores==3) {
					String msg2=msg+"<br/>Ha superado el numero de intentos maximo. <br/>La aplicacion no puede continuar.";
					JLabel errorFields = new JLabel(
					"<HTML><FONT COLOR = Red>"+msg2+"</FONT></HTML>");
					JOptionPane.showMessageDialog(null, errorFields);
					if (LOG.isInfoEnabled())
						LOG.info(msg2+": "+username+"/"+password);
					System.exit(0);
				} else {					
					JLabel errorFields = new JLabel(
						"<HTML><FONT COLOR = Blue>"+msg+"</FONT></HTML>");					
					JOptionPane.showMessageDialog(null, errorFields);
					if (LOG.isInfoEnabled())
						LOG.info(msg+": "+username+"/"+password);
				}
				jButton1.setEnabled(true);
				jPasswordField1.setText("");
				jTextField1.setSelectionStart(0);
				jTextField1.setSelectionEnd(jTextField1.getText().length());
				jTextField1.requestFocusInWindow();
			}
		}		
	}
	
	protected void salir() {
		System.exit(0);		
	}
}