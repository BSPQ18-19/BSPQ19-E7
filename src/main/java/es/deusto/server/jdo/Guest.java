package es.deusto.server.jdo;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;


@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class Guest extends User {
	String telephone = null;
	String email = null;
	String name = null;
	
	public Guest(String telephone, String email, String name, String password) {
		super(name, password);
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
