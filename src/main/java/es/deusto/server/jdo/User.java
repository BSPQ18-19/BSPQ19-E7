package es.deusto.server.jdo;

import java.io.Serializable;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable="true")
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
public class User implements Serializable {

	// Administrators only have username and password
	@PrimaryKey
	private String username;
	private String password;

	// This are common for Hosts and Guests
	private String telephone = null;
	private String email = null;
	private String name = null;
	
	// This is only for Hosts
	private boolean verified = false;
	
	public enum UserKind {
		ADMINISTRATOR,
		HOST,
		GUEST,
	}
	
	private UserKind kind;
	
	public User(String username, String password, UserKind kind, String telephone, String email, String name, boolean verified) {
		this.username = username;
		this.password = password;
		this.kind = kind;
		this.telephone = telephone;
		this.email = email;
		this.name = name;
		this.verified = verified;
	}
	
	// @Todo: Should we have different constructors for each user kind ??

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public UserKind getKind() {
		return kind;
	}

	public void setKind(UserKind kind) {
		this.kind = kind;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", telephone=" + telephone + ", email=" + email
				+ ", name=" + name + ", verified=" + verified + ", kind=" + kind + "]";
	}

	
	
}
