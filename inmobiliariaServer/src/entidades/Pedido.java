package entidades;

import java.io.Serializable;
import java.util.Date;

public class Pedido implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private long n_pedido;
	private String nif_cli;
	private long n_piso;
	private Date llegada;
	private Date partida;
	private boolean pagado;
	private boolean cancelado;
	
	public Pedido() {
		
	}

	public Pedido(String nif_cli, long n_piso, Date llegada,
			Date partida, boolean pagado, boolean cancelado) {
		
		this.nif_cli = nif_cli;
		this.n_piso = n_piso;
		this.llegada = llegada;
		this.partida = partida;
		this.pagado = pagado;
		this.cancelado = cancelado;
	}

	public long getN_pedido() {	return n_pedido; }
	public String getNif_cli() { return nif_cli; }
	public long getN_piso() { return n_piso; }
	public Date getLlegada() { return llegada; }
	public Date getPartida() { return partida; }
	public boolean isPagado() {	return pagado; }
	public boolean isCancelado() { return cancelado; }

	public void setN_pedido(long n_pedido) {
		this.n_pedido = n_pedido;
	}

	public void setNif_cli(String nif_cli) {
		this.nif_cli = nif_cli;
	}

	public void setN_piso(long n_piso) {
		this.n_piso = n_piso;
	}

	public void setLlegada(Date llegada) {
		this.llegada = llegada;
	}

	public void setPartida(Date partida) {
		this.partida = partida;
	}

	public void setPagado(boolean pagado) {
		this.pagado = pagado;
	}

	public void setCancelado(boolean cancelado) {
		this.cancelado = cancelado;
	}

	@Override
	public String toString() {
		return "Pedido [n_pedido=" + n_pedido + ", nif_cli=" + nif_cli
				+ ", n_piso=" + n_piso + ", llegada=" + llegada + ", partida="
				+ partida + ", pagado=" + pagado + ", cancelado=" + cancelado
				+ "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (n_pedido ^ (n_pedido >>> 32));
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
		Pedido other = (Pedido) obj;
		if (n_pedido != other.n_pedido)
			return false;
		return true;
	}
		
}
