package entidades;

import java.io.Serializable;

public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	private String login;
	private String pwd;
	
	public Usuario() {
		
	}
	
	public Usuario(String usuario, String pwd) {
		this.login=usuario;
		this.pwd=pwd;
	}
	
	@Override
	public String toString() {
		return "Usuario [usuario=" + login + ", pwd=" + pwd + "]";
	}

	public String getLogin() { return login; }
	public String getPwd() { return pwd; }
	
	public void setLogin(String usuario) {
		this.login = usuario;
	}
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pwd == null) ? 0 : pwd.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
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
		Usuario other = (Usuario) obj;
		if (pwd == null) {
			if (other.pwd != null)
				return false;
		} else if (!pwd.equals(other.pwd))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}
	
}
