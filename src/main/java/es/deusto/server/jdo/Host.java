package es.deusto.server.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Host extends User {
	boolean verified = false;
	String telephone = null;
	String email = null;
	String name = null;
	
	public Host(String id, boolean verified, String telephone, String email, String name, String password) {
		super(id, name, password);
		this.verified = verified;
		this.telephone = telephone;
		this.email = email;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
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
		return "Host [id=" + username + ", verified=" + verified + ", telephone=" + telephone + ", email=" + email + ", name="
				+ name + ", password=" + password + "]";
	}
		
}
