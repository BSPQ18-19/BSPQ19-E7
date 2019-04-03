package es.deusto.server.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Administrator {
	@PrimaryKey
	String id = null;
	String password = null;
	
	public Administrator(String id, String password) {
		super();
		this.id = id;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Administrator [id=" + id + ", password=" + password + "]";
	}
	
}
