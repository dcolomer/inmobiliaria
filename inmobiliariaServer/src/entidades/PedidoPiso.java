package entidades;

import java.io.Serializable;
import java.util.Date;

public class PedidoPiso implements Serializable {

	private static final long serialVersionUID = 1L;

	private long n_pedido;
	private String dir;
	private String loc;
	private Date llegada;
	private Date partida;
	
	public PedidoPiso() {
		
	}
	
	public PedidoPiso(long n_pedido, String dir, String loc, Date llegada,
			Date partida) {		
		this.n_pedido = n_pedido;
		this.dir = dir;
		this.loc = loc;
		this.llegada = llegada;
		this.partida = partida;
	}

	public long getN_pedido() { return n_pedido; }
	public String getDir() { return dir; }
	public String getLoc() { return loc; }
	public Date getLlegada() { return llegada; }
	public Date getPartida() { return partida; }

	public void setN_pedido(long n_pedido) {
		this.n_pedido = n_pedido;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public void setLlegada(Date llegada) {
		this.llegada = llegada;
	}

	public void setPartida(Date partida) {
		this.partida = partida;
	}

	@Override
	public String toString() {
		return "PedidoPiso [n_pedido=" + n_pedido + ", dir=" + dir + ", loc="
				+ loc + ", llegada=" + llegada + ", partida=" + partida + "]";
	}
		
}
