package test;

import java.util.Collection;

import entidades.Propietario;
import servicios.ServiciosException;
import servicios.ServiciosInmobiliaria;
import servicios.ServiciosInmobiliariaFactory;

public class TestPropietario {

	public static void main(String[] args) {

		ServiciosInmobiliaria servicios;
		servicios=ServiciosInmobiliariaFactory.getServiciosInmobiliaria();
		
		try {
			// ***Probado: eliminar propietario
			//servicios.eliminarPropietario("32323232-H");
			
			// ***Probado: modificar/buscar propietario
			/*Propietario prop=servicios.getPropietario("88888888-K");
			System.out.println("Propietario: "+prop);
			prop.setNombre("TERESA");
			servicios.grabarPropietario(prop);*/
			
			// ***Probado: nuevo propietario
			//Propietario prop=new Propietario("32323232-H","KIM","GARCIA TORRES","C/TIJUANA","Malgrat de Mar");
			//servicios.grabarPropietario(prop);
			
			// ***Probado: lista de propietarios
			Collection<Propietario> propietarios=servicios.getPropietarios();
			
			for (Propietario propietario:propietarios)
				System.out.println(propietario);
			
		} catch (ServiciosException e) {			
			System.out.println(e);
		}

	}

}
