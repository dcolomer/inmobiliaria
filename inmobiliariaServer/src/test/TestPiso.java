package test;

import java.util.Collection;

import entidades.Piso;
import servicios.ServiciosException;
import servicios.ServiciosInmobiliaria;
import servicios.ServiciosInmobiliariaFactory;

public class TestPiso {

	public static void main(String[] args) {

		ServiciosInmobiliaria servicios;
		servicios=ServiciosInmobiliariaFactory.getServiciosInmobiliaria();
		
		try {
			// ***Probado: eliminar piso
			//servicios.eliminarPiso(100);
			
			// ***Probado: modificar/buscar piso
			/*Piso piso=servicios.getPiso(67);
			System.out.println("Piso: "+piso);
			piso.setLoc("LLORET DE MAR");
			servicios.grabarPiso(piso);*/
			
			// ***Probado: nuevo piso
			//Piso piso=new Piso("C/Leon","Girona",false,"99999999-P",77.0f,9.5f);
			//servicios.grabarPiso(piso);
			
			// ***Probado: lista de pisos
			Collection<Piso> pisos=servicios.getPisos();
			
			for (Piso piso:pisos)
				System.out.println(piso);
			
		} catch (ServiciosException e) {			
			System.out.println(e);
		}

	}

}
