package es.deusto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {
	/**
	 *  This is the API we can use from the client side
	 */
	
	String sayMessage(String login, String password, String message) throws RemoteException;
	void registerUser(String login, String password) throws RemoteException;

	/**
	 * 
	 * @author ImanolR
	 *
	 */
	enum UserKind {
		ADMIN,
		HOST,
		GUEST
	}
	
	/**
	 * This method check whether the user is in the DB and TEMPORARILY 	returns the user type of the account.
	 * TODO: Return the user object or at least some information of the user.
	 * @param username
	 * @param password
	 * @return
	 * @throws RemoteException
	 */
	UserKind login(String username, String password) throws RemoteException;
	
}
