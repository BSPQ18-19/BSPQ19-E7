package es.deusto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.Reservation;
import es.deusto.server.jdo.User;
import es.deusto.server.jdo.User.UserKind;

public interface IServer extends Remote {
	/**
	 *  This is the API we can use from the client side
	 */

	
	User login(String username, String password) throws RemoteException;
	enum RegistrationError {
		NONE,
		INVALID_NAME,
		INVALID_EMAIL,
		INVALID_TELEPHONE,
		INVALID_COST,
		INVALID_CAPACITY,
		INVALID_CITY,
		// @Temporary: This should be handled by the client application not the server?
		PASSWORD_MISMATCH,
	}
	RegistrationError registerUser(String name, String username, String email, String telephone, String password, boolean isHost) throws RemoteException;
	
	List<User> getUser(String username) throws RemoteException;
	void updateUser(String username, String password, UserKind kind, String telephone, String email, String name, boolean verified) throws RemoteException;
	void deleteUser(String username) throws RemoteException;
	void deleteProperty(String address) throws RemoteException;
	void deleteReservation(String date, String guestUsername, String propertyAddress) throws RemoteException;
	void bookProperty(String name, Property property, String date, String duration) throws RemoteException;
	void updateProperty (String address, String city, int capacity, String ocupancy, double cost) throws RemoteException;
	void updateReservation(Property property, User guest, String date, int duration) throws RemoteException;
	
	// @Todo: Separate this error codes from the user registration error codes (Make another enum)
	RegistrationError registerProperty(String address, String city, int capacity, double cost, String name) throws RemoteException;
	
	List<Property> getPropertiesByCity(String city) throws RemoteException;
	List<Property> getPropertiesByHost(String hostname) throws RemoteException;
	List<Reservation> getReservationsByCity(String city) throws RemoteException;
	
	void changeUserPassword(String username, String password) throws RemoteException;
	void changeUserTelephone(String username, String telephone) throws RemoteException;
	
	public int add(int a, int b) throws RemoteException;
}
