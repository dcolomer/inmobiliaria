package entidades;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Propietario implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String nif_prop;
	private String nombre;
	private String apel;
	private String dir;
	private String loc;	
	private Set<Piso> pisos;

	public Propietario() {
		
	}
	
	public Propietario(String nif_prop, String nombre, String apel, String dir, String loc) {	
		this.nif_prop = nif_prop;
		this.nombre = nombre;
		this.apel = apel;
		this.dir = dir;
		this.loc = loc;
		this.pisos=new HashSet<Piso>();
	}
	
	public String getNif_prop() { return nif_prop; }
	public String getNombre() { return nombre; }
	public String getApel() { return apel; }
	public String getDir() { return dir; }
	public String getLoc() { return loc; }	
	public Set<Piso> getPisos() { return pisos;	}
	
	public void setNif_prop(String nif_prop) {
		this.nif_prop = nif_prop;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setApel(String apel) {
		this.apel = apel;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public void setPisos(Set<Piso> pisos) {
		this.pisos = pisos;
	}
	
	public void addPiso(Piso piso) {
		this.pisos.add(piso);
	}
	
	@Override
	public String toString() {
		return "Propietario [nif_prop=" + nif_prop + ", nombre=" + nombre
				+ ", apel=" + apel + ", dir=" + dir + ", loc=" + loc + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nif_prop == null) ? 0 : nif_prop.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Propietario other = (Propietario) obj;
		if (nif_prop == null) {
			if (other.nif_prop != null)
				return false;
		} else if (!nif_prop.equals(other.nif_prop))
			return false;
		return true;
	}
				
}
