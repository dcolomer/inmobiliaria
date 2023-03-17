package gui.utiles;

import javax.swing.JTextArea;
import java.io.PrintStream;
import java.io.OutputStream;

/**
* Simple <code>PrintStream</code> coupled to a <code>JTextArea</code>
*/
public class JTextAreaPrintStream extends PrintStream {

	private static class JTextAreaOutputStream extends OutputStream {		
		private JTextArea textArea;
		
		private JTextAreaOutputStream(JTextArea textArea) {
			this.textArea = textArea;
		}
		
		public void write(int i) {
			textArea.append(new String(new char[] {(char)i}));
		}

		public void write(byte[] b) {
			textArea.append(new String(b));
		}

		public void write(byte[] b, int offset, int len) {
			textArea.append(new String(b, offset, len));
		}
	}

	/**
	* Creates a new <code>JTextAreaPrintStream</code> coupled to a given <code>JTextArea</code>.
	* @param textAreaThe <code>JTextArea</code> to couple this print stream to
	*/
	public JTextAreaPrintStream(JTextArea textArea) {
		super(new JTextAreaOutputStream(textArea));
	}
}

