package gui.utiles;


import java.awt.Cursor;
import java.awt.Dialog.ModalityType;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Imagen extends JLabel {

	private static final long serialVersionUID = 1L;

	private String file;
	private Image imagen;
	private String nombre;
	
	public Imagen() {		
		setHorizontalAlignment(CENTER);
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				Cursor dedoCursor = new Cursor(Cursor.HAND_CURSOR);
				setCursor(dedoCursor);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				ImagenZoom imgZoom=null;
				try {	
					imgZoom = new ImagenZoom(Imagen.this.getImage());					
					imgZoom.setTitle(Imagen.this.getNombre());
					imgZoom.setModalityType(ModalityType.APPLICATION_MODAL);
					imgZoom.setLocationRelativeTo(null);
					imgZoom.setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(normalCursor);
			}
			
		});
	}
	
	/*
	 * Asignar una imagen a la etiqueta a partir
	 * de un nombre de fichero.
	 * Esto es necesario cuando se carga la imagen
	 * desde un fichero del disco del usuario
	 */
	public void setFile(String file) {
		this.file = file;
		if (file!=null){
			Image imagenCreada = Toolkit.getDefaultToolkit().createImage(file);			
			this.imagen=imagenCreada;
			this.setIcon(UtilesGUI.createThumbnail(new ImageIcon(imagenCreada)));
		}
	}
	
	/*
	 * Retornar la ruta del fichero de imagen
	 */
	public String getFile() {
		return this.file;
	}
	
	
	
	/*
	 * Asignar una imagen a la etiqueta a partir
	 * de un byte[].
	 * Esto es necesario cuando se carga la imagen
	 * desde la base de datos.
	 */
	public void setImage(byte[] arrayBytes) {
		if (arrayBytes!=null) {
			Image imagenCreada = Toolkit.getDefaultToolkit().createImage(arrayBytes);
			this.imagen=imagenCreada;			
			this.setIcon(UtilesGUI.createThumbnail(new ImageIcon(imagenCreada)));
		} else {
			this.setIcon(null);
		}
	}
	
	public Image getImage() throws IOException {		
		return this.imagen;
	}
	
	
	/*
	 * El nombre de la imagen queda asociado
	 * al numero+dir+loc del piso
	 */
	
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}	
}
