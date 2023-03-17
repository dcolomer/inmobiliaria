package utiles;

import java.io.File;
import java.net.URL;

public class WorkingDirectory {

	private static File WORKING_DIRECTORY;

	public static File get() {

		String Recurso = WorkingDirectory.class.getSimpleName() + ".class";
		if (WORKING_DIRECTORY == null) {
			try {
				URL url = WorkingDirectory.class.getResource(Recurso);
				System.out.println(url);
				if (url.getProtocol().equals("file")) {
					File f = new File(url.toURI());

					do {

						f = f.getParentFile();
					} while (!f.isDirectory());

					WORKING_DIRECTORY = f;
				} else if (url.getProtocol().equals("jar")) {
					String expected = "!/" + Recurso;
					String s = url.toString();
					s = s.substring(4);
					s = s.substring(0, s.length() - expected.length());
					File f = new File(new URL(s).toURI());

					do {

						f = f.getParentFile();
					} while (!f.isDirectory());
					WORKING_DIRECTORY = f;
				}
			} catch (Exception e) {
				WORKING_DIRECTORY = new File(".");
			}
		}
		return WORKING_DIRECTORY;
	}

	public static void main(String[] args) {
		System.out.println(WorkingDirectory.get());
		System.out.println(WorkingDirectory.get().isDirectory());
		System.out.println(System.getProperty("user.dir"));
	}
}
