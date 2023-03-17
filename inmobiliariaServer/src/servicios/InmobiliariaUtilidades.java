package servicios;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class InmobiliariaUtilidades {
	
	/*
     * Método publico de factoria estático para obtener
     * un objeto ServiciosInmobiliaria
     */    
    public static ServiciosInmobiliaria getServicios() {    	
    	return ServiciosInmobiliariaFactory.getServiciosInmobiliaria();    	    	    	    	
    }
    
    /*
     * Retornar el numero de dias existentes entre dos fechas
     */
    public static int restarFechas(Date fechaIni, Date fechaFin) {
    	
    	GregorianCalendar gcFechaIni = new GregorianCalendar();
    	gcFechaIni.setTime(fechaIni);
    	
    	GregorianCalendar gcFechaFin = new GregorianCalendar();
    	gcFechaFin.setTime(fechaFin);
    	
        if (gcFechaIni.get(Calendar.YEAR) == gcFechaFin.get(Calendar.YEAR)) {
            return gcFechaFin.get(Calendar.DAY_OF_YEAR) - gcFechaIni.get(Calendar.DAY_OF_YEAR);
        } else {
            /* SI ESTAMOS EN DISTINTO ANYO COMPROBAMOS QUE EL ANYO DEL DATEINI NO SEA BISIESTO
             * SI ES BISIESTO SON 366 DIAS EL ANYO
             * SINO SON 365
             */
            int diasAnyo = gcFechaIni.isLeapYear(gcFechaIni.get(Calendar.YEAR)) ? 366 : 365;

            /* CALCULAMOS EL RANGO DE ANYOS */
            int rangoAnyos = gcFechaFin.get(Calendar.YEAR) - gcFechaIni.get(Calendar.YEAR);

            /* CALCULAMOS EL RANGO DE DIAS QUE HAY */
            return (rangoAnyos * diasAnyo) + 
            	(gcFechaFin.get(Calendar.DAY_OF_YEAR) - 
            			gcFechaIni.get(Calendar.DAY_OF_YEAR));

            
        }
    }
}
