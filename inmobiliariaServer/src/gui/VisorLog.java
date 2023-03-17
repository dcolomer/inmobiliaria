package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class VisorLog extends Frame implements ActionListener {

	private static final long serialVersionUID = 1L;
	String directory; // The default directory to display in the FileDialog
	TextArea textarea; // The area to display the file contents into

	/** Convenience constructor: file viewer starts out blank */
	public VisorLog() {
		this(null, null);
	}

	/** Convenience constructor: display file from current directory */
	public VisorLog(String filename) {
		this(null, filename);
	}

	/**
	 * The real constructor. Create a FileViewer object to display the specified
	 * file from the specified directory
	 **/
	public VisorLog(String directory, String filename) {
		super(); // Create the frame

		// Destroy the window when the user requests it
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		// Create a TextArea to display the contents of the file in
		textarea = new TextArea("", 24, 80);
		textarea.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
		textarea.setEditable(false);
		this.add("Center", textarea);

		// Create a bottom panel to hold a couple of buttons in
		Panel p = new Panel();
		p.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		this.add(p, "South");

		// Create the buttons and arrange to handle button clicks
		Font font = new Font("SansSerif", Font.PLAIN, 14);
		Button openfile = new Button("Abrir fichero");
		Button close = new Button("Salir");
		openfile.addActionListener(this);
		openfile.setActionCommand("abrir");
		openfile.setFont(font);
		close.addActionListener(this);
		close.setActionCommand("salir");
		close.setFont(font);
		p.add(openfile);
		p.add(close);

		this.pack();

		// Figure out the directory, from filename or current dir, if necessary
		if (directory == null) {
			File f;
			if ((filename != null) && (f = new File(filename)).isAbsolute()) {
				directory = f.getParent();
				filename = f.getName();
			} else
				directory = System.getProperty("user.dir");
		}

		this.directory = directory; // Remember the directory, for FileDialog
		setFile(directory, filename); // Now load and display the file
	}

	/**
	 * Load and display the specified file from the specified directory
	 **/
	public void setFile(String directory, String filename) {
		if ((filename == null) || (filename.length() == 0))
			return;
		File f;
		FileReader in = null;
		// Read and display the file contents. Since we're reading text, we
		// use a FileReader instead of a FileInputStream.
		try {
			f = new File(directory, filename); // Create a file object
			in = new FileReader(f); // And a char stream to read it
			char[] buffer = new char[4096]; // Read 4K characters at a time
			int len; // How many chars read each time
			textarea.setText(""); // Clear the text area
			while ((len = in.read(buffer)) != -1) { // Read a batch of chars
				String s = new String(buffer, 0, len); // Convert to a string
				textarea.append(s); // And display them
			}
			this.setTitle(filename); // Set the window title
			textarea.setCaretPosition(0); // Go to start of file
		}
		// Display messages if something goes wrong
		catch (IOException e) {
			textarea.setText(e.getClass().getName() + ": " + e.getMessage());
			this.setTitle(filename + ": I/O Exception");
		}
		// Always be sure to close the input stream!
		finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Handle button clicks
	 **/
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("abrir")) { // If user clicked "Open" button
			// Create a file dialog box to prompt for a new file to display
			FileDialog f = new FileDialog(this, "Abrir fichero", FileDialog.LOAD);
			f.setDirectory(directory); // Set the default directory

			// Display the dialog and wait for the user's response
			f.setVisible(true);

			directory = f.getDirectory(); // Remember new default directory
			setFile(directory, f.getFile()); // Load and display selection
			f.dispose(); // Get rid of the dialog box
		} else if (cmd.equals("salir")) // If user clicked "Close" button
			this.dispose(); // then close the window
	}

	/**
	 * The FileViewer can be used by other classes, or it can be used standalone
	 * with this main() method.
	 **/
	static public void main(String[] args) throws IOException {
		// Create a FileViewer object
		Frame f = new VisorLog((args.length == 1) ? args[0] : null);
		// Arrange to exit when the FileViewer window closes
		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		// And pop the window up
		f.setVisible(true);
	}
}
