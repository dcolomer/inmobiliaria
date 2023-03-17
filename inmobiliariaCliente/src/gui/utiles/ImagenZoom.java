package gui.utiles;

import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Abre un JDialog con la imagen del piso a tamaño real
 */
public class ImagenZoom extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	
	public ImagenZoom(Image imagen) {
		
		setSize(470, 550);
		setLayout(new BorderLayout());
		JPanel panel=new JPanel();
		
		JLabel imgZoom=new JLabel(new ImageIcon(imagen));
		
		imgZoom.setBounds(0, 0, panel.getWidth(), panel.getHeight());
		panel.add(imgZoom);
		
		add(panel);
	}
	
	
}
