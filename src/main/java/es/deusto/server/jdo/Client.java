package es.deusto.server.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Client extends User {
	@PrimaryKey
	String telephone = null;
	String email = null;
	String name = null;
	
	public Client(String id, String telephone, String email, String name, String password) {
		super(id, name, password);
		this.telephone = telephone;
		this.email = email;
		this.name = name;
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


	@Override
	public String toString() {
		return "Client [id=" + username + ", telephone=" + telephone + ", email=" + email + ", name=" + name + ", password="
				+ password + "]";
	}
	
}
