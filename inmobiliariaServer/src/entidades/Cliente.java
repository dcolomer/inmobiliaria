package entidades;

import java.io.Serializable;

public class Cliente implements Serializable {		
		
	private static final long serialVersionUID = 1L;
	private String nif_cli;
	private String nombre;
	private String apel;
		
	public Cliente() {
		
	}
		
	public Cliente(String nif_cli, String nombre, String apel) {		
		this.nif_cli = nif_cli;
		this.nombre = nombre;
		this.apel = apel;
	}
	
	public String getNif_cli() { return nif_cli; }
	public String getNombre() { return nombre; }
	public String getApel() { return apel; }
	
	public void setNif_cli(String nif_cli) {
		this.nif_cli = nif_cli;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setApel(String apel) {
		this.apel = apel;
	}
	
	@Override
	public String toString() {
		return "Cliente [nif_cli=" + nif_cli + ", nombre=" + nombre + ", apel="
				+ apel + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nif_cli == null) ? 0 : nif_cli.hashCode());
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
		Cliente other = (Cliente) obj;
		if (nif_cli == null) {
			if (other.nif_cli != null)
				return false;
		} else if (!nif_cli.equals(other.nif_cli))
			return false;
		return true;
	}
	
}
