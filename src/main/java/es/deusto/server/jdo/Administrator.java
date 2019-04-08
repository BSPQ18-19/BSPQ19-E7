package es.deusto.server.jdo;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class Administrator extends User {
	
	public Administrator(String name, String password) {
		super(name, password);
	}


	@Override
	public String toString() {
		return "Administrator [id=" + username + ", password=" + password + "]";
	}
	
}
