package entidades;

import java.io.Serializable;
import java.util.Date;

public class Factura implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long n_factura;
	private String operacion;
	private long n_pedido;
	private float importe;
	private boolean pagado;
	private Date dia;
	
	public Factura() {
		
	}

	public Factura(String operacion, long n_pedido, float importe,
			boolean pagado, Date dia) {
		
		this.operacion = operacion;
		this.n_pedido = n_pedido;
		this.importe = importe;
		this.pagado = pagado;
		this.dia = dia;
	}

	public long getN_factura() { return n_factura; }
	public String getOperacion() { return operacion; }
	public long getN_pedido() {	return n_pedido; }
	public float getImporte() { return importe;	}
	public boolean isPagado() {	return pagado;	}
	public Date getDia() { return dia;	}

	public void setN_factura(long n_factura) {
		this.n_factura = n_factura;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public void setN_pedido(long n_pedido) {
		this.n_pedido = n_pedido;
	}

	public void setImporte(float importe) {
		this.importe = importe;
	}

	public void setPagado(boolean pagado) {
		this.pagado = pagado;
	}

	public void setDia(Date dia) {
		this.dia = dia;
	}

	@Override
	public String toString() {
		return "Factura [n_factura=" + n_factura + ", operacion=" + operacion
				+ ", n_pedido=" + n_pedido + ", importe=" + importe
				+ ", pagado=" + pagado + ", dia=" + dia + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (n_factura ^ (n_factura >>> 32));
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
		Factura other = (Factura) obj;
		if (n_factura != other.n_factura)
			return false;
		return true;
	}
	
}
