package es.deusto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.User;
import es.deusto.server.jdo.User.UserKind;

public interface IServer extends Remote {
	/**
	 *  This is the API we can use from the client side
	 */

	
	/**
	 * This method check whether the user is in the DB and TEMPORARILY 	returns the user type of the account.
	 * TODO: Return the user object or at least some information of the user.
	 * @param username
	 * @param password
	 * @return
	 * @throws RemoteException
	 */
	User login(String username, String password) throws RemoteException;
	enum RegistrationError {
		NONE,
		INVALID_NAME,
		
		INVALID_EMAIL,
		INVALID_TELEPHONE,

		// @Temporary: This should be handled by the client application not the server?
		PASSWORD_MISMATCH,
	}
	RegistrationError registerUser(String name, String username, String email, String telephone, String password) throws RemoteException;
	
	List<User> getUser(String username) throws RemoteException;
	void updateUser(String username, String password, UserKind kind, String telephone, String email, String name, boolean verified) throws RemoteException;
			
	List<Property> getPropertiesByCity(String city) throws RemoteException;
}
