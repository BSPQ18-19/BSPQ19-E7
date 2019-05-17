package es.deusto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import es.deusto.server.jdo.Occupancy;
import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.Reservation;
import es.deusto.server.jdo.User;
import es.deusto.server.jdo.User.UserKind;

/**
 * Server-side API interface
 * 
 * @author Imanol Ramajo
 *
 */
public interface IServer extends Remote {
	/**
	 *  This is the API we can use from the client side
	 */

	/**
	 * Checks whether a user is in the DB. If so, returns the User object related to that account.
	 * 
	 * @param username Username of the account to log into
	 * @param password Password associated to the account
	 * @return User Object representing the account
	 * @throws RemoteException
	 */
	User login(String username, String password) throws RemoteException;

	/**
	 * Error codes of @see registerUser
	 * 
	 * @author Imanol Ramajo
	 *
	 */
	enum RegistrationError {
		NONE,
		INVALID_NAME,
		INVALID_EMAIL,
		INVALID_TELEPHONE,
		INVALID_EMPTY_FIELD,
		// @Temporary: This should be handled by the client application not the server?
		PASSWORD_MISMATCH,
	}
	/**
	 * Registers a new user into the DB
	 * 
	 * @param name Name to show in the account
	 * @param username Username of the account. Must be unique.
	 * @param email email address associated to the account
	 * @param telephone Telephone number of the user
	 * @param password Password for the account
	 * @param isHost Whether the account to create is a host or a guest account
	 * @return Error code if any
	 * @throws RemoteException
	 */
	RegistrationError registerUser(String name, String username, String email, String telephone, String password, boolean isHost) throws RemoteException;
	
	/**
	 * Error codes of @see registerPorperty
	 * 
	 * @author Imanol Ramajo
	 *
	 */
	enum PropertyRegistrationError {
		NONE,
		INVALID_COST,
		INVALID_CAPACITY,
		INVALID_CITY,
	}
	/**
	 * Registers a new property into the DB
	 * 
	 * @param address Address of the property
	 * @param city City in which the property is
	 * @param capacity Number of maximum people that can pass the night in the property
	 * @param cost Price of the property per night
	 * @param hostname username of the host user account manager/owner of the property
	 * @return Error code if any
	 * @throws RemoteException
	 */
	PropertyRegistrationError registerProperty(String address, String city, int capacity, double cost, String hostname) throws RemoteException;
	/**
	 * Updates a already existing property with new data
	 * 
	 * @param address Address identifying the property
	 * @param city This probably should be removed from the API. The city can't change, right?
	 * @param capacity New capacity of the property
	 * @param cost new price of the property per night
	 * @return Error code if any
	 * @throws RemoteException
	 */
	PropertyRegistrationError updateProperty (String address, String city, int capacity, double cost) throws RemoteException;

	/**
	 * Error codes of @see checkOccupancy
	 * @author Imanol Ramajo
	 *
	 */
	enum OccupancyError {
		NONE,
		INVALID_DATE,
		INVALID_OVERLAP,
	}
	
	/**
	 * Checks whether the property is available for a given date.
	 * 
	 * @param property property identifier to check
 	 * @param startDate starting date of the requested reservation
	 * @param endDate ending data of the requested reservation
	 * @return Error code if any
	 * @throws RemoteException
	 */
	OccupancyError checkOccupancy(Property property, String startDate, String endDate) throws RemoteException;

	/**
	 * Returns a list of User objects given a username.
	 * Can only be called from an administrator account.
	 * 
	 * @Todo: Make sure that only administrators can call this method
	 * 
	 * @param username Username to search for users.
	 * @return Returns a list of users
	 * @throws RemoteException
	 */
	List<User> getUser(String username) throws RemoteException;
	/**
	 * Updates a user data checking that the input data is valid.
	 * @Todo: It can only be used by administrators.
	 * 
	 * @param username Username identifying the account to update
	 * @param password new password for the account
	 * @param kind Type of account
	 * @param telephone new telephone of the user
	 * @param email new email address of related to the account
	 * @param name new name associated to the account
	 * @param verified Whether the user account is a verified account or not
	 * @throws RemoteException
	 */
	RegistrationError updateUser(String username, String password, UserKind kind, String telephone, String email, String name, boolean verified) throws RemoteException;
	/**
	 * Deletes a user account from the DB.
	 * @Todo: This can only be called by administrators
	 * 
	 * @param username Username identifying the User to delete
	 * @throws RemoteException
	 */
	void deleteUser(String username) throws RemoteException;
	/**
	 * Deletes a property from the DB
	 * 
	 * @param address Address identifying the property
	 * @throws RemoteException
	 */
	void deleteProperty(String address) throws RemoteException;
	/**
	 * Deletes a reservation
	 * @param propertyAddress Address of the property reserved
	 * @param guestUsername Username of the guest that reserved the property
	 * @param startDate Starting date of the reservation
	 * @param endDate Ending date of the reservation
	 * @throws RemoteException
	 */
	void deleteReservation(String propertyAddress, String guestUsername, String startDate, String endDate) throws RemoteException;
	/**
	 * Gets the periods in which a property is already reserved
	 * 
	 * @param property Property to search for
	 * @return List of Occupancy objects
	 * @throws RemoteException
	 */
	List<Occupancy> getOccupancyByProperty(Property property) throws RemoteException;

	/**
	 * Makes a reservation of a property for a given period of time
	 * @param name Username of the guest account asking to do the reservation
	 * @param property Property to reserve
	 * @param startDate Starting date of the reservation
	 * @param endDate Ending date of the reservation
	 * @throws RemoteException
	 */
	void bookProperty(String name, Property property, String startDate, String endDate) throws RemoteException;
	/**
	 * Updates the data of a reservation
	 * 
	 * @param property Property on which the reservation was made
	 * @param guest Guest account that made the reservation
	 * @param oldStartDate original starting date of the reservation
	 * @param startDate new starting date of the reservation
	 * @param endDate new end date of the reservation
	 * @throws RemoteException
	 */
	void updateReservation(Property property, User guest, String oldStartDate, String startDate, String endDate) throws RemoteException;
	
	/**
	 * Gets the list of properties belonging to a city
	 * 
	 * @param city Name of the city to search
	 * @return List of properties in the city
	 * @throws RemoteException
	 */
	List<Property> getPropertiesByCity(String city) throws RemoteException;
	/**
	 * Gets the list of properties associated/owned/managed by a host account
	 * @param hostname Username of the host account to search
	 * @return List of properties associated to that host
	 * @throws RemoteException
	 */
	List<Property> getPropertiesByHost(String hostname) throws RemoteException;
	/**
	 * Gets a list of reservations made in a city
	 * 
	 * @param city Name of the city to search
	 * @return List of reservations made in properties of that city
	 * @throws RemoteException
	 */
	List<Reservation> getReservationsByCity(String city) throws RemoteException;
	/**
	 * Gets a list of reservations made by a guest account
	 * @param name Username of the guest account to search
	 * @return List of reservations made by that guest user
	 * @throws RemoteException
	 */
	List<Reservation> getReservationsByGuest(String name) throws RemoteException;
	
	/**
	 * Changes the password of an account
	 * 
	 * @param username Username of the account to change password
	 * @param password password new password for that account
	 * @return Boolean whether the change of password completed successfully
	 * @throws RemoteException
	 */
	Boolean changeUserPassword(String username, String password) throws RemoteException;
	/**
	 * Changes the telephone number of an account
	 * @param username Username of the account to change the telephone
	 * @param telephone new telephone of the account
	 * @return Boolean whether the change of telephone was completed successfully
	 * @throws RemoteException
	 */
	Boolean changeUserTelephone(String username, String telephone) throws RemoteException;	
}
