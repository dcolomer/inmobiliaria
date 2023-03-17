package gui.utiles;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import utiles.Misc;

/**
 * Clase miscelanea que contiene metodo estaticos
 * relativos a operaciones graficas
 */
public class UtilesGUI {

	private static final Logger LOG = Logger.getLogger(UtilesGUI.class);

	/**
	 * Devolver el contenido del fichero (la foto) en un array de bytes
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Obtener el tamany del fichero
		long length = file.length();

		// No podemos crear un array usando un tipo long.
		// Es necesario que sea un tipo int.
		// Antes de convertirlo a int, comprobamos
		// que el fichero no es mayor que Integer.MAX_VALUE
		if (length > Integer.MAX_VALUE) {
			String msg = "Fichero demasiado grande!";
			LOG.error(msg);
			Misc.mostrarError(new IOException(), msg, false);
		}

		// Creamos el byte array que almacenara
		// temporalmente los datos leidos
		byte[] bytes = new byte[(int) length];

		// Leemos
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Comprobacion de que todos los bytes se han leido
		if (offset < bytes.length) {
			String msg = "No se ha podido leer completamente el fichero "
					+ file.getName();
			LOG.error(msg);
			Misc.mostrarError(new IOException(), msg, false);
		}

		// Cerrar el input stream y devolver los bytes
		is.close();
		return bytes;
	}

	/**
	 * Crea una version reducida de una imagen
	 */
	public static ImageIcon createThumbnail(ImageIcon icon) {
		int maxDim = 240;
		try {
			Image inImage = icon.getImage();

			double scale = (double) maxDim / (double) inImage.getHeight(null);
			if (inImage.getWidth(null) > inImage.getHeight(null)) {
				scale = (double) maxDim / (double) inImage.getWidth(null);
			}

			int scaledW = (int) (scale * inImage.getWidth(null));
			int scaledH = (int) (scale * inImage.getHeight(null));

			BufferedImage outImage = new BufferedImage(scaledW, scaledH,
					BufferedImage.TYPE_INT_RGB);

			AffineTransform tx = new AffineTransform();

			if (scale < 1.0d) {
				tx.scale(scale, scale);
			}

			Graphics2D g2d = outImage.createGraphics();
			g2d.drawImage(inImage, tx, null);
			g2d.dispose();

			return new ImageIcon(outImage);
		} catch (Exception e) {
			LOG.error(e);
			Misc.mostrarError(e, "error al crear el Thumbnail", false);
		}
		return null;
	}

	/**
	 * @param clase: this.getClass()
	 * @param path: El paquete relativo a la clase
	 * 
	 * Retorna un objeto ImageIcon a partir de una ruta, 
	 * o null si el path no es correcto.
	 */
	public static ImageIcon crearImageIcon(Class<?> clase, String path) {
		java.net.URL imgURL = clase.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			String msg="No puedo encontrar el fichero de imagen: "+ path;
			LOG.error(msg);
			Misc.mostrarError(new IOException(), "error al crear el Thumbnail", false);
		}
		return null;
	}

}
