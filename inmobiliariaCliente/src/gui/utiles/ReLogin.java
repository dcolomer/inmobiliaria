package gui.utiles;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ReLogin extends Login {

	private static final long serialVersionUID = 1L;
		
	public ReLogin(JFrame propietario, String tit, ModalityType modalidad) {
		super(propietario, tit, modalidad);		
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
				String tit=propietario.getTitle().split("-")[0].toString().trim();
				tit+=" - usuario: "+usuario.getLogin();
				propietario.setTitle(tit);
				dispose();
				
			// Si las credenciales son incorrectas
			} else {
				String msg="Las credenciales son incorrectas.";				
				jButton1.setEnabled(false);
				contador_errores++;
				if (contador_errores==3) {
					String msg2=msg+"<br/>Ha superado el numero de intentos maximo. <br/>Se debe cerrar este formulario.";
					JLabel errorFields = new JLabel(
					"<HTML><FONT COLOR = Red>"+msg2+"</FONT></HTML>");
					JOptionPane.showMessageDialog(null, errorFields);
					if (LOG.isInfoEnabled())
						LOG.info(msg2+": "+username+"/"+password);
					dispose();
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

	@Override 
	protected void salir() {
		dispose();		
	}
}
