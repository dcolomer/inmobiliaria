package servicios;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class PisoOcupadoException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(PisoOcupadoException.class);
	
	public PisoOcupadoException(Date entrada, Date salida, long n_piso) {
		super();
		DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		String fIni=df.format(entrada);
		String fFin=df.format(salida);
		
		LOG.warn("El piso numero "+n_piso+" no se encuentra disponible "
				+ "para el intervalo ["+fIni+"-"+fFin+"]",this);
	}

}
