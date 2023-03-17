package servicios;

/**
 * Clase Singleton
 * Crea/devuelve un objeto ServiciosInmobiliariaImpl, el
 * cual contiene todos los métodos de negocio de la aplicacion
 *
 */
public class ServiciosInmobiliariaFactory {
	private static ServiciosInmobiliaria serviciosInmobiliaria;
	
	private ServiciosInmobiliariaFactory() {
		serviciosInmobiliaria=new ServiciosInmobiliariaImpl();
	}
	
	public static synchronized ServiciosInmobiliaria getServiciosInmobiliaria() {
		if (serviciosInmobiliaria==null)
			new ServiciosInmobiliariaFactory();
		
		return serviciosInmobiliaria;
	}
	
	/**
	 * Evitar el clonado: un Singleton no debe poder clonarse
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
}
