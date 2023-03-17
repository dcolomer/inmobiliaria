package entidades;

import java.io.Serializable;

public class Piso implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long n_piso;	
	private String dir;
	private String loc;
	private boolean piscina;
	private String nif_prop;
	private float precio;
	private float comision;
	private byte[] foto;
	
	public Piso() {
		
	}
	
	public Piso(String dir, String loc, boolean piscina, String nif_prop,
			float precio, float comision) {		
		this.dir = dir;
		this.loc = loc;
		this.piscina = piscina;
		this.nif_prop = nif_prop;
		this.precio = precio;
		this.comision = comision;
	}
	
	public long getN_piso() { return n_piso; }
	public String getDir() { return dir; }
	public String getLoc() { return loc; }
	public boolean isPiscina() { return piscina; }
	public String getNif_prop() { return nif_prop; }
	public float getPrecio() { return precio; }
	public float getComision() { return comision; }
	public byte[] getFoto() { return foto; }
		
	public void setN_piso(long n_piso) {
		this.n_piso = n_piso;
	}
	
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	public void setLoc(String loc) {
		this.loc = loc;
	}
	
	public void setPiscina(boolean piscina) {
		this.piscina = piscina;
	}
	
	public void setNif_prop(String nif_prop) {
		this.nif_prop = nif_prop;
	}
	
	public void setPrecio(float precio) {
		this.precio = precio;
	}
	
	public void setComision(float comision) {
		this.comision = comision;
	}
	
	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	@Override
	public String toString() {
		return "Piso [n_piso=" + n_piso + ", dir=" + dir + ", loc=" + loc
				+ ", piscina=" + piscina + ", nif_prop=" + nif_prop
				+ ", precio=" + precio + ", comision=" + comision + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (n_piso ^ (n_piso >>> 32));
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
		Piso other = (Piso) obj;
		if (n_piso != other.n_piso)
			return false;
		return true;
	}
		
}
