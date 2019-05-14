package es.deusto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import es.deusto.server.jdo.Occupancy;
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
		// @Temporary: This should be handled by the client application not the server?
		PASSWORD_MISMATCH,

		// These only apply to properties
		INVALID_COST,
		INVALID_CAPACITY,
		INVALID_CITY,
	}
	RegistrationError registerUser(String name, String username, String email, String telephone, String password, boolean isHost) throws RemoteException;
	
	List<User> getUser(String username) throws RemoteException;
	void updateUser(String username, String password, UserKind kind, String telephone, String email, String name, boolean verified) throws RemoteException;
	void deleteUser(String username) throws RemoteException;
	void deleteProperty(String address) throws RemoteException;
	void deleteReservation(String propertyAddress, String guestUsername, String startDate, String endDate) throws RemoteException;
	Boolean checkOccupancy(Property property, String startDate, String endDate) throws RemoteException;
	List<Occupancy> getOccupancyByProperty(Property property) throws RemoteException;

	void bookProperty(String name, Property property, String startDate, String endDate) throws RemoteException;
	void updateProperty (String address, String city, int capacity, double cost) throws RemoteException;
	void updateReservation(Property property, User guest, String oldStartDate, String startDate, String endDate) throws RemoteException;
	
	// @Todo: Separate this error codes from the user registration error codes (Make another enum)
	RegistrationError registerProperty(String address, String city, int capacity, double cost, String hostname) throws RemoteException;
	
	List<Property> getPropertiesByCity(String city) throws RemoteException;
	List<Property> getPropertiesByHost(String hostname) throws RemoteException;
	List<Reservation> getReservationsByCity(String city) throws RemoteException;
	List<Reservation> getReservationsByGuest(String name) throws RemoteException;
	
	Boolean changeUserPassword(String username, String password) throws RemoteException;
	Boolean changeUserTelephone(String username, String telephone) throws RemoteException;	
}
