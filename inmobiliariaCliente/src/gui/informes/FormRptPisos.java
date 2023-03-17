package gui.informes;

import gui.FormPrincipal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ws.Piso;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class FormRptPisos extends AbstractFormRpt {

	private static final long serialVersionUID = 1L;
			
	public static FormRptPisos createForm() {			
		return new FormRptPisos();
	}
	
	private FormRptPisos() {
		super("Informe de pisos", 600, 200,"rptPisos");				
	}
	
	
	/*
	 * Cargar el combo con las localidades
	 */
	@Override
	protected String[] cargarCombo() {
		
		final String[] EMPTY_LOCALIDAD = new String[0];
		
		Collection<Piso> pisos=serviciosInmobiliaria.getPisos();
		
		if (!pisos.isEmpty()) {
			List<String> localidades=new ArrayList<String>();						
			for (Piso piso:pisos) {
				String loc=piso.getLoc();
				Collections.sort(localidades);
				int nuevaLocalidad=Collections.binarySearch(localidades, loc);
				if (nuevaLocalidad<0) // Si no esta ya en el combo, lo anadimos
					localidades.add(loc);	
			}
			localidades.add(0, "Seleccionar");
			return localidades.toArray(EMPTY_LOCALIDAD);
		}
			
		return EMPTY_LOCALIDAD;				
	}

	protected void salir() {
		super.salir();
		FormPrincipal.setFrmInformePisosActivo(false);
	}
}
