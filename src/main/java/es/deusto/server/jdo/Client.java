package es.deusto.server.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Client {
	@PrimaryKey
	String id = null;
	String telephone = null;
	String email = null;
	String name = null;
	String password = null;
	
	public Client(String id, String telephone, String email, String name, String password) {
		super();
		this.id = id;
		this.telephone = telephone;
		this.email = email;
		this.name = name;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Client [id=" + id + ", telephone=" + telephone + ", email=" + email + ", name=" + name + ", password="
				+ password + "]";
	}
	
}
