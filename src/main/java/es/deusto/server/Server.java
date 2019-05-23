package es.deusto.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Transaction;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import es.deusto.server.jdo.Occupancy;
import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.Reservation;
import es.deusto.server.jdo.User;
import es.deusto.server.jdo.User.UserKind;

public class Server extends UnicastRemoteObject implements IServer {

	private static final long serialVersionUID = 1L;
	private PersistenceManager pm=null;
	private static Logger log;

	// Copied from: http://emailregex.com/
	private  String email_regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
	// Copied from: https://stackoverflow.com/a/18626090
	private String telephone_regex = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";
	// Copied from: https://stackoverflow.com/questions/11757013/regular-expressions-for-city-name
	private String city_regex = "^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$";

	/**
	 * Creates a new server instance.
	 * If there is no account admin/admin in the DB it creates one
	 * @throws RemoteException
	 */
	protected Server() throws RemoteException {
		super();
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		this.pm = pmf.getPersistenceManager();


		// @Todo: Remove this account creation for the final version
		// Check that there is the admin/admin user. If not create it.
		{
			Transaction tx = null;
			try {
				tx = pm.currentTransaction();

				tx.begin();
				User user = pm.getObjectById(User.class, "admin");

				tx.commit();
			}
			catch(JDOObjectNotFoundException e) {
				// @Temp: Add the missing account
				tx = pm.currentTransaction();
				User user = new User("admin", "admin", User.UserKind.ADMINISTRATOR, null, null, null, false);
				pm.makePersistent(user);
				tx.commit();
			}
		}


		// @Todo: Delete this code. This is a super @HACK so that tests can run!
		if (log == null) {
			log = Logger.getLogger(Server.class);
			PropertyConfigurator.configure("src/main/resources/log4j.properties");
		}
	}


	/**
	 * Registers a new user in the DB.
	 * Creates a new user in the DB and checks if all the passed data is valid
	 * 
	 * @param name name of the account
	 * @param username Username that uniquely identifies the account
	 * @param email email associated with the user
	 * @param telephone number of the user
	 * @param password password of the account
	 * @param isHost Whether the account to create is a Host account or a guest account
	 * 
	 */
	@Override
	public synchronized RegistrationError registerUser(String username, String name, String email, String telephone, String password, boolean isHost) {
		System.out.println(telephone);
		// @Todo: In the future this method will only be used to register hosts and guests
		// @Security Administrators will only be able to be created by other administrators. And should be
		// created using another method that checks that the user requesting the information is
		// indeed an admin. We could store some secret password for each admin an never pass that information
		// to the client and check on the server-side that the secret password of the administrator matches
		// For this we will need to create a UserDTO or stub that does not contain that secret password when
		// transferring the User object to the client application.

		Transaction tx = pm.currentTransaction();

		// Check all the input are correct
		if(name.isEmpty() == true || username.isEmpty() == true || email.isEmpty() == true || telephone.isEmpty() == true || password.isEmpty() == true) {
			return RegistrationError.INVALID_EMPTY_FIELD;
		}
		if (!Pattern.matches(email_regex, email)) {
			return RegistrationError.INVALID_EMAIL;
		}
		if (!Pattern.matches(telephone_regex, telephone)) {
			return RegistrationError.INVALID_TELEPHONE;
		}

		try {	
			tx.begin();
			log.info("Checking whether the user already exits or not: '" + username+"'");
			User user = null;
			try {
				user = pm.getObjectById(User.class, username);
			} catch (javax.jdo.JDOObjectNotFoundException jonfe) {
				System.out.println("Exception launched: " + jonfe.getMessage());
			}
			log.info("User: " + user);

			if (user != null) {
				return RegistrationError.INVALID_NAME;

				// @Todo: Is this the supposed behavior? We should make a method that exclusively changes passwords
				/*
				System.out.println("Setting password user: " + user);
				user.setPassword(password);
				System.out.println("Password set user: " + user);*/
			} else {
				log.info("Creating user: " + username);

				// @Note: Hosts are not verified by default. Verification must be done by an administrator manually.
				user = new User(username, password, isHost ? User.UserKind.HOST : User.UserKind.GUEST, telephone, email, name, false);
				pm.makePersistent(user);					 

				log.info("User created: " + user);
			}
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		return RegistrationError.NONE;
	}

	/**
	 * Gets the list of properties on a city.
	 * 
	 * @param city Name of the city in which to search
	 * @return List of cities with the results of the query. If there was any error on the query the result is null. If there were no matches found the list is empty
	 */
	@Override
	public synchronized List<Property> getPropertiesByCity(String city) {
		List<Property> result = null;

		// @Robustness: I don't know if pm.currentTransaction can fail
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();

			Query<Property> query = pm.newQuery(Property.class);
			query.setFilter("city == '" + city + "'");

			result = query.executeList();

			tx.commit();
		} catch (JDOObjectNotFoundException e) {
			log.error("Property not found: " + city);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return result;
	}

	/**
	 * Gets the list of reservations of properties of a city
	 * 
	 * @param city name of the city to search
	 * @return List of reservations with the results of the query. If there was any error on the query the result is null. If there were no matches found the list is empty.
	 */
	public synchronized List<Reservation> getReservationsByCity(String city) throws RemoteException{
		// @Copied and adapted from getPropertiesByCity
		List<Reservation> result = null;

		// @Robustness: I don't know if pm.currentTransaction can fail
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query<Reservation> query = pm.newQuery(Reservation.class);
			query.setFilter("property.city == '" + city + "'");
			result = query.executeList();
			tx.commit();
		} catch (JDOObjectNotFoundException e) {
			log.error("Reservation not found: " + city);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return result;

	}

	/**
	 * Gets the list of reservations made by a guest account
	 * 
	 * @param name Username of the Guest account to search
	 * @return List of reservations with the results of the query. If there was any error on the query the result is null. If there were no matches found the list is empty.
	 */
	public synchronized List<Reservation> getReservationsByGuest(String name) throws RemoteException{
		// @Copied and adapted from getPropertiesByCity
		List<Reservation> result = null;

		// @Robustness: I don't know if pm.currentTransaction can fail
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query<Reservation> query = pm.newQuery(Reservation.class);
			query.setFilter("guest.username == '" + name + "'");
			result = query.executeList();
			tx.commit();
		} catch (JDOObjectNotFoundException e) {
			log.error("Reservation not found: " + name);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return result;

	}

	/**
	 * Gets the list of user that share the same prefix
	 * 
	 * @param username String to search
	 * @return List of users matching the query. If there was an error in the query the return is null. If there were no matches the list is empty
	 */
	public synchronized List<User> getUsers(String username) {
		// @Security: We should pass some kind of token to verify that the user requesting data from a user is an administrator

		List<User> result = null;
		Transaction tx = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();

			Query<User> query = pm.newQuery(User.class);
			query.setFilter("this.username.startsWith(\""+ username + "\")");

			result = query.executeList();

			tx.commit();

		} catch (JDOObjectNotFoundException e) {
			log.error("User not found: " + username);
		} finally {
			// @Robustness: I don't know if pm.currentTransaction can fail, but if it does
			// this code is not correct because it does not check if tx is null
			// in case currentTransaction cannot fail we should take it out of the try block
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return result;
	}
	
	/**
	 * Gets the user matching the username
	 * 
	 * @param username String to search
	 * @return User matching the query
	 */
	public synchronized User getUser (String username) throws RemoteException {
		User user = null;
		Transaction tx = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();
			user = pm.getObjectById(User.class, username);
			tx.commit();
		} catch (JDOObjectNotFoundException e) {
			log.error("User not found: " + username);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return user;
	}

	/**
	 * Checks whether the username and the password introduced match
	 * 
	 * @Todo: Describe what happens if the it fails.
	 * 
	 * @param username Username identifying the account to log into.
	 * @param password Secret password for the account
	 * 
	 * @return User object representing the account
	 */
	@Override
	public synchronized User login(String username, String password) throws RemoteException {

		// @Refactor: Can we use 'getUser()' above, inside this method?
		
		log.info("Login " + username);
		User user = null;
		Transaction tx = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();

			user = pm.getObjectById(User.class, username);

			tx.commit();

		} catch (JDOObjectNotFoundException e) {
			log.error("User not found: " + username);
		} finally {
			// @Robustness: I don't know if pm.currentTransaction can fail, but if it does
			// this code is not correct because it does not check if tx is null
			// in case currentTransaction cannot fail we should take it out of the try block
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		// Check passwords match
		if (user!=null && password.equals(user.getPassword())) {
			return user;
		} else {
			return null;
		}
	}


	/**
	 * Updates the data of an account checking if values are valid
	 * 
	 * @Todo: This should only be called by administrators
	 * 
	 * @param username Username of the account to update
	 * @param password new password of the account
	 * @param kind new type of the account @see User.UserKind
	 * @param telephone New telephone number associated to the account
	 * @param email new email for the account
	 * @param name New name of the account
	 * @param isVerified Whether the account is a verified account
	 * 
	 */
	public synchronized RegistrationError updateUser(String username, String password, UserKind kind, String telephone, String email, String name, boolean verified) throws RemoteException {
		// Check all the input are correct
//		if(name.isEmpty() == true || username.isEmpty() == true || email.isEmpty() == true || telephone.isEmpty() == true || password.isEmpty() == true) {
//			return RegistrationError.INVALID_EMPTY_FIELD;
//		}
//		if (!Pattern.matches(email_regex, email)) {
//			return RegistrationError.INVALID_EMAIL;
//		}
//		if (!Pattern.matches(telephone_regex, telephone)) {
//			return RegistrationError.INVALID_TELEPHONE;
//		}

		Transaction tx = null;
		User user = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();

			// Get the original user 
			user = pm.getObjectById(User.class, username);

			tx.commit();

		} catch (JDOObjectNotFoundException e) {
			log.info("User not found: " + username);
		} finally {
			// @Robustness: I don't know if pm.currentTransaction can fail, but if it does
			// this code is not correct because it does not check if tx is null
			// in case currentTransaction cannot fail we should take it out of the try block
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		if (user == null) {
			return RegistrationError.INVALID_NAME;
		}
		
		try {
			tx = pm.currentTransaction();
			tx.begin();

			// Update the user
			log.info("Updating existing user");
			user.setPassword(password);
			user.setKind(kind);
			user.setTelephone(telephone);
			user.setEmail(email);
			user.setName(name);
			user.setVerified(verified);

			// Store the updated user
			pm.makePersistent(user);
			log.info("User successfully saved");

			tx.commit();

		} catch (JDOException e) {
			log.error("Error updating User: " + user);
			log.error(e.getStackTrace());
		} finally {
			// @Robustness: I don't know if pm.currentTransaction can fail, but if it does
			// this code is not correct because it does not check if tx is null
			// in case currentTransaction cannot fail we should take it out of the try block
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return RegistrationError.NONE;

	}

	/**
	 * Changes the password of a user account
	 * 
	 * @Todo: We should make sure that the one requesting the change is the user itself.
	 * 
	 * @param username ID of the account to change the password
	 * @param password New password to replace.
	 * 
	 * @return Boolean whether the change of password was made successfully
	 */
	public Boolean changeUserPassword(String username, String password) throws RemoteException {
		Transaction tx = null;
		User user = null;
		Boolean chnged = false;
		try {
			tx = pm.currentTransaction();
			tx.begin();
			user = pm.getObjectById(User.class, username);
			user.setPassword(password);
			chnged = true;
			tx.commit();
		} catch (JDOObjectNotFoundException e) {
			log.error("User: " + user);
			log.error(e.getStackTrace());
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return chnged;
	}

	/**
	 * Changes the telephone of a user account
	 * 
	 * @Todo: We should make sure that the one requesting the change is the user itself.
	 * 
	 * @param username ID of the account to change the password
	 * @param telephone New password to replace.
	 * 
	 * @return Boolean whether the change of telephone was made successfully
	 */
	public Boolean changeUserTelephone(String username, String telephone) {
		Transaction tx = null;
		User user = null;
		Boolean chnged = true;
		try{
			tx = pm.currentTransaction();
			tx.begin();
			user = pm.getObjectById(User.class, username);
			// // @Temporary: telephone_regex is not working
			if (telephone.matches("^[0-9]{9}$")) {
				user.setTelephone(telephone);
			} else {
				chnged = false;
				System.out.println("error tlf regex");
			}

			tx.commit();
		} catch (JDOObjectNotFoundException e) {
			log.error("User: " + user);
			log.error(e.getStackTrace());
		} finally{
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return chnged;
	}

	/**
	 * Deletes  a user from the DB.
	 * @Todo: This should only be called by administrators
	 * 
	 * @param username Username of the user to delete
	 */
	public synchronized void deleteUser(String username) throws RemoteException {
		// @Security: How can we guarantee that this is called by a user onto its own account, or by an administrator?

		User user = pm.getObjectById(User.class, username);
		UserKind kind = user.getKind();
		
		Transaction tx = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();
			
			if(kind.equals(UserKind.HOST)) {
				Query<Property> queryP = pm.newQuery(Property.class);
				queryP.setFilter("host.username == '" + username + "'");
				List<Property> listProperties = queryP.executeList();
				if(!listProperties.isEmpty()) {
					for(Property property : listProperties) {
						Query<Reservation> queryR = pm.newQuery(Reservation.class);
						queryR.setFilter("property.address == '" + property.getAddress() + "'");
						List<Reservation> reservation = queryR.executeList();
						if(!reservation.isEmpty()) { pm.deletePersistentAll(reservation); }

						Query<Occupancy> queryO = pm.newQuery(Occupancy.class);
						queryO.setFilter("property.address == '" + property.getAddress() + "'");
						List<Occupancy> occupancy = queryO.executeList();
						if(!occupancy.isEmpty()) { pm.deletePersistentAll(occupancy); }

						Query<Property> queryP2 = pm.newQuery(Property.class);
						queryP2.setFilter("address == '" + property.getAddress() + "'");
						Property p = queryP2.executeUnique();
						pm.deletePersistent(p);
					}
				}
			} else if (kind.equals(UserKind.GUEST)) {
				Query<Reservation> queryR = pm.newQuery(Reservation.class);
				queryR.setFilter("guest.username == '" + username + "'");
				List<Reservation> listReservations = queryR.executeList();
				if(!listReservations.isEmpty()) {
					for(Reservation reserv : listReservations) {
						Query<Occupancy> queryO = pm.newQuery(Occupancy.class);
						queryO.setFilter("property.address == '" + reserv.getProperty().getAddress() + "'");
						Occupancy occupancy = queryO.executeUnique();
						pm.deletePersistent(occupancy);
						
						Query<Reservation> queryR2 = pm.newQuery(Reservation.class);
						queryR2.setFilter("guest.username == '" + username + "'");
						Reservation reservation = queryR2.executeUnique();
						pm.deletePersistent(reservation);
					}
				}
			}
			
			pm.deletePersistent(user);
			
			tx.commit();

		} catch (JDOObjectNotFoundException e) {
			log.info("User not found: " + username);
		} finally {
			// @Robustness: I don't know if pm.currentTransaction can fail, but if it does
			// this code is not correct because it does not check if tx is null
			// in case currentTransaction cannot fail we should take it out of the try block
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

	/**
	 * Deletes a property
	 * @Todo: This should only be called by those with privileges over the property (host & administrators)
	 * 
	 * @param address Address that identifies the property to delete
	 */
	public synchronized void deleteProperty(String address) throws RemoteException {
		Transaction tx = null;
		List<Reservation> reservation;
		List<Occupancy> occupancy;
		Property property;
		try {
			tx = pm.currentTransaction();
			tx.begin();

			Query<Reservation> queryR = pm.newQuery(Reservation.class);
			queryR.setFilter("property.address == '" + address + "'");
			reservation = queryR.executeList();
			if(!reservation.isEmpty()) {
				pm.deletePersistentAll(reservation);
			}

			Query<Occupancy> queryO = pm.newQuery(Occupancy.class);
			queryO.setFilter("property.address == '" + address + "'");
			occupancy = queryO.executeList();
			if(!occupancy.isEmpty()) {
				pm.deletePersistentAll(occupancy);
			}

			Query<Property> queryP = pm.newQuery(Property.class);
			queryP.setFilter("address == '" + address + "'");
			property = queryP.executeUnique();
			pm.deletePersistent(property);

			tx.commit();
		} catch (Exception e) {
			log.info("Property not found: " + address);
			e.getStackTrace();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

	/**
	 * Deletes a reservation
	 * 
	 * @param propertyAddress Address of the property to make the reservation
	 * @param guestUsername Username of the guest requesting the reservation
	 * @param startDate Starting date of the reservation
	 * @param endDate Ending date of the reservation
	 */
	public synchronized void deleteReservation(String propertyAddress, String guestUsername, String startDate, String endDate) throws RemoteException {
		Transaction tx = null;
		Reservation reservation;
		Occupancy occupancy;
		try {
			tx = pm.currentTransaction();
			tx.begin();

			Query<Reservation> queryR = pm.newQuery(Reservation.class);
			queryR.setFilter("startDate == '" + startDate + "' && guest.username == '" + guestUsername + "' && property.address == '" + propertyAddress + "'");
			reservation = queryR.executeUnique();
			pm.deletePersistent(reservation);

			Query<Occupancy> queryO = pm.newQuery(Occupancy.class);
			queryO.setFilter("startDate == '" + startDate + "' && property.address == '" + propertyAddress + "'");
			occupancy = queryO.executeUnique();
			pm.deletePersistent(occupancy);

			tx.commit();
		} catch (Exception e) {
			log.info("Reservation not found ");
			log.error(e.getStackTrace());
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}


	public synchronized List<Occupancy> getOccupancyByProperty(Property property) throws RemoteException {
		List<Occupancy> result = null;
		Transaction tx = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();

			Query<Occupancy> query = pm.newQuery(Occupancy.class);
			query.setFilter("property.address == '"+ property.getAddress() + "'");
			result = query.executeList();

			tx.commit();

		} catch (JDOObjectNotFoundException e) {
			log.error("Occupancy not found ");
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return result;
	}

	public synchronized OccupancyError checkOccupancy(Property property, String startDate, String endDate) throws RemoteException {
		Date checkStartDate = null;
		Date checkEndDate = null;
		try {
			checkStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
			checkEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
			if(checkStartDate.after(checkEndDate)) {
				return OccupancyError.INVALID_DATE;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}  

		List<Occupancy> list = getOccupancyByProperty(property);
		for(Occupancy o : list) {
			Date sDate = null;
			Date eDate = null;
			try {
				sDate = new SimpleDateFormat("dd/MM/yyyy").parse(o.getStartDate());
				eDate = new SimpleDateFormat("dd/MM/yyyy").parse(o.getEndDate());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if(checkEndDate.before(sDate) || checkStartDate.after(eDate)) {	
				//they do not overlap
			} else {
				//they overlap
				return OccupancyError.INVALID_OVERLAP;
			}
		}
		return OccupancyError.NONE;
	}

	/**
	 * Makes a reservation on a property
	 * 
	 * @param name Username of the guest making the reservation
	 * @param property Property to reserve
	 * @param startDate Starting date of the reservation
	 * @param endDate Ending date of the reservation
	 * 
	 */
	public synchronized void bookProperty(String name, Property property, String startDate, String endDate) throws RemoteException {

		Transaction tx = null;
		Reservation reservation = null;

		try {
			tx = pm.currentTransaction();
			tx.begin();
			log.info("Creating reservation... ");
			reservation = new Reservation(pm.getObjectById(Property.class, property.getAddress()), pm.getObjectById(User.class, name), startDate, endDate);
			Occupancy occupancy = new Occupancy(pm.getObjectById(Property.class, property.getAddress()), startDate, endDate);
			pm.makePersistent(reservation);
			pm.makePersistent(occupancy);
			log.info("Reservation created: " + reservation);
			tx.commit();
		} finally {
			if(tx.isActive()) {
				tx.rollback();
			}
		}

	}


	/**
	 * Updates the data of a reservation
	 * 
	 * @param property Property that is reserved
	 * @param guest User object of the guest that made the reservation
	 * @param oldStartDate Origina starting date
	 * @param startDate New starting date
	 * @param endDate New ending date
	 */
	@Override
	public synchronized void updateReservation(Property property, User guest, String oldStartDate, String startDate, String endDate) throws RemoteException {
		Transaction tx = null;
		Reservation reservation = null;
		Occupancy occupancy = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();

			Query<Reservation> queryR = pm.newQuery(Reservation.class);
			queryR.setFilter("startDate == '" + oldStartDate + "' && guest.username == '" + guest.getUsername() + "' && property.address == '" + property.getAddress() + "'");
			reservation = queryR.executeUnique();

			Query<Occupancy> queryO = pm.newQuery(Occupancy.class);
			queryO.setFilter("startDate == '" + oldStartDate + "' && property.address == '" + property.getAddress() + "'");
			occupancy = queryO.executeUnique();

			tx.commit();	
		} catch (JDOObjectNotFoundException e) {
			log.info("Reservation not found!");
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		try {
			System.out.println("reservation : " + reservation.toString());
			System.out.println("occupancy: " + occupancy.toString());
			tx = pm.currentTransaction();
			tx.begin();

			if (reservation != null) {
				log.info("Updating existing reservation");
				User objGuest = pm.getObjectById(User.class, guest.getUsername());
				Property objProp = pm.getObjectById(Property.class, property.getAddress());
				reservation.setGuest(objGuest);
				reservation.setProperty(objProp);
				reservation.setStartDate(startDate);
				reservation.setEndDate(endDate);
			} 

			pm.makePersistent(reservation);
			log.info("Reservation successfully saved");

			if (occupancy != null) {
				log.info("Updating existing occupancy for this property");
				Property objProp = pm.getObjectById(Property.class, property.getAddress());
				occupancy.setProperty(objProp);
				occupancy.setStartDate(startDate);
				occupancy.setEndDate(endDate);
			}

			pm.makePersistent(occupancy);
			log.info("Occupancy successfully saved");

			tx.commit();

		} catch (JDOException e) {
			log.error("Reservation: " + reservation);
			log.error(e.getStackTrace());
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

	/**
	 * Gets a list of properties belonging to a host
	 * 
	 * @param hostname Username of the host to search
	 * @return List of properties managed by the host. If there was an error in the query the result is null. If there are no matching properties the list is empty.
	 */
	@Override
	public synchronized List<Property> getPropertiesByHost(String hostname) throws RemoteException {
		List<Property> result = null;
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query<Property> query = pm.newQuery(Property.class);
			query.setFilter("host.username == '" + hostname + "'");
			result = query.executeList();
			tx.commit();
		} catch (JDOObjectNotFoundException e) {
			log.error("Properties not found");
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return result;
	}

	/**
	 * Creates a new property in the DB
	 * 
	 * @param address Address where the property is.
	 * @param city City in which the property is.
	 * @param capacity number of people that could sleep in the property
	 * @param cost Price of the property per night
	 * @param hostname Username of the host owner of the property
	 */
	public synchronized PropertyRegistrationError registerProperty(String address, String city, int capacity, double cost, String hostname) throws RemoteException {
		// @Robustness @Security: Do something more appropriate than passing the host/owner name as a parameter,
		// Maybe pass a User object?

		Transaction tx = pm.currentTransaction();

		if(!Pattern.matches(city_regex, city)) {
			return PropertyRegistrationError.INVALID_CITY;
		}
		if(cost <= 0) {
			return PropertyRegistrationError.INVALID_COST;
		}
		if(capacity <= 0) {
			return PropertyRegistrationError.INVALID_CAPACITY;
		}

		try {
			tx.begin();
			log.info("Checking whether the property already exits or not");
			Property property = null;
			try {
				property = pm.getObjectById(Property.class, address);
			} catch (javax.jdo.JDOObjectNotFoundException jonfe) {
				log.info("Exception launched: " + jonfe.getMessage());
			}
			log.info("Property: " + property);

			if(property == null) {
				log.info("Creating property: " + address);
				property = new Property(address, city, capacity, cost, pm.getObjectById(User.class, hostname));
				pm.makePersistent(property);
				log.info("Property created: " + property);
			}
			tx.commit();
		} finally {
			if(tx.isActive()) {
				tx.rollback();
			}
		}
		return PropertyRegistrationError.NONE;
	}

	/**
	 * Updates the information of a property
	 * 
	 * @param Address of the property to update
	 * @param capacity New capacity of the place
	 * @param cost New price of the property per night
	 */
	public synchronized PropertyRegistrationError updateProperty (String address, int capacity, double cost) throws RemoteException {

		if(cost <= 0) {
			return PropertyRegistrationError.INVALID_COST;
		}
		if(capacity <= 0) {
			return PropertyRegistrationError.INVALID_CAPACITY;
		}

		Transaction tx = null;
		Property property = null;
		User host = null;
		String city = null;

		try {
			tx = pm.currentTransaction();
			tx.begin();
			property = pm.getObjectById(Property.class, address);
			host = property.getHost();
			city = property.getCity();
			tx.commit();	
		} catch (JDOObjectNotFoundException e) {
			log.info("Property not found: " + address);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		try {
			tx = pm.currentTransaction();
			tx.begin();

			if (property != null) {
				log.info("Updating existing property");
				property.setCity(city);
				property.setCapacity(capacity);
				property.setCost(cost);
			} else {
				log.info("Creating new property");
				property = new Property(address, city, capacity, cost, host);
			}

			pm.makePersistent(property);
			log.info("Property successfully saved");

			tx.commit();

		} catch (JDOException e) {
			log.error("Property: " + property);
			log.error(e.getStackTrace());
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return PropertyRegistrationError.NONE;
	}


	public static void main(String[] args) {
		{
			// @Investigate how to correctly configure the logger
			//			try {
			//				BasicConfigurator.configure();
			log = Logger.getLogger(Server.class);
			PropertyConfigurator.configure("src/main/resources/log4j.properties");
			//				FileAppender fa;
			//				fa = new FileAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN),
			//						"logger_log.log", true);
			//				log.addAppender(fa);
			//			} catch (IOException e) {
			// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
		}

		if (args.length != 3) {
			System.out.println("How to invoke: java [policy] [codebase] Server.Server [host] [port] [server]");
			log.warn("Wrong number of arguments passed: " + args);
			System.exit(0);
		}

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
			log.trace("Security manager updated");
		}

		String name = "//" + args[0] + ":" + args[1] + "/" + args[2];
		log.info("server name: " + name);


		try {
			IServer objServer = new Server();
			Naming.rebind(name, objServer);
			log.info("Server ready");

			// We need this so that the server process does not close.
			// It seems that in recent versions of java, from Java 8 forward
			// they broke the process from remaining opened.
			System.in.read();
		} catch (Exception e) {
			log.error("RMI error: could not bind the server to the registry");
			log.error(e.getStackTrace());
			e.printStackTrace();
		}
	}


}
