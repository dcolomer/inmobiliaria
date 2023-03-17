package gui.informes;

import gui.FormPrincipal;

import java.util.ArrayList;
import java.util.Collection;

import ws.Propietario;

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
public class FormRptPropietarios extends AbstractFormRpt {

	private static final long serialVersionUID = 1L;
			
	public static FormRptPropietarios createForm() {			
		return new FormRptPropietarios();
	}
	
	private FormRptPropietarios() {
		super("Informe de propietarios", 600, 200,"rptPropietarios");				
	}
	
	
	/*
	 * Cargar el combo con los NIF's de los propietarios
	 */
	@Override
	protected String[] cargarCombo() {
		
		final String[] EMPTY_NIF = new String[0];
		
		Collection<Propietario> propietarios=serviciosInmobiliaria.getPropietarios();
		
		if (!propietarios.isEmpty()) {
			Collection<String> nifs=new ArrayList<String>(propietarios.size()+1);
			nifs.add("Seleccionar");
			for (Propietario propietario:propietarios) {
				nifs.add(propietario.getNifProp());	
			}
			return nifs.toArray(EMPTY_NIF);
		}
			
		return EMPTY_NIF;				
	}

	protected void salir() {
		super.salir();
		FormPrincipal.setFrmInformePropietariosActivo(false);
	}
}
