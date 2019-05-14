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

	protected Server() throws RemoteException {
		super();
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		this.pm = pmf.getPersistenceManager();


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

	protected void finalize () throws Throwable {
		//		if (tx.isActive()) {
		//            tx.rollback();
		//        }
		pm.close();
	}

	@Override
	public synchronized RegistrationError registerUser(String name, String username, String email, String telephone, String password, boolean isHost) {

		// @Todo: In the future this method will only be used to register hosts and guests
		// @Security Administrators will only be able to be created by other administrators. And should be
		// created using another method that checks that the user requesting the information is
		// indeed an admin. We could store some secret password for each admin an never pass that information
		// to the client and check on the server-side that the secret password of the administrator matches
		// For this we will need to create a UserDTO or stub that does not contain that secret password when
		// transferring the User object to the client application.

		Transaction tx = pm.currentTransaction();

		// Check all the input are correct
		if (!Pattern.matches(email_regex, email)) {
			return RegistrationError.INVALID_EMAIL;
		}
		if (!Pattern.matches(telephone_regex, telephone)) {
			return RegistrationError.INVALID_TELEPHONE;
		}

		try
		{	
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
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}

		}

		return RegistrationError.NONE;
	}

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

	public synchronized List<User> getUser(String username) {
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
		if (password.equals(user.getPassword())) {
			return user;
		}
		else {
			return null;
		}
	}

	public synchronized void updateUser(String username, String password, UserKind kind, String telephone, String email, String name, boolean verified) throws RemoteException {
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

		try {
			tx = pm.currentTransaction();
			tx.begin();

			if (user != null) {
				// Update the user
				log.info("Updating existing user");
				user.setPassword(password);
				user.setKind(kind);
				user.setTelephone(telephone);
				user.setEmail(email);
				user.setName(name);
				user.setVerified(verified);
			}
			else {
				// Create a new user
				log.info("Creating new user");
				user = new User(username, password, kind, telephone, email, name, verified);
			}

			// Store the updated user
			pm.makePersistent(user);
			log.info("User successfully saved");

			tx.commit();

		} catch (JDOException e) {
			log.error("User: " + user);
			log.error(e.getStackTrace());
		} finally {
			// @Robustness: I don't know if pm.currentTransaction can fail, but if it does
			// this code is not correct because it does not check if tx is null
			// in case currentTransaction cannot fail we should take it out of the try block
			if (tx.isActive()) {
				tx.rollback();
			}
		}

	}

	public Boolean changeUserPassword(String username, String password) throws RemoteException {
		//TODO
		Transaction tx = null;
		User user = null;
		Boolean chnged = false;
		try{
			tx = pm.currentTransaction();
			tx.begin();
			user = pm.getObjectById(User.class, username);

			if(password.equals(user.getPassword())) {

			} else {
				user.setPassword(password);
				chnged=true;
			}
			tx.commit();
		} catch (JDOObjectNotFoundException e) {
		}finally{
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return chnged;
	}

	public Boolean changeUserTelephone(String username, String telephone) {
		//TODO
		Transaction tx = null;
		User user = null;
		Boolean chnged = false;
		try{
			tx = pm.currentTransaction();
			tx.begin();
			user = pm.getObjectById(User.class, username);
			String PhoneConst = "^[0-9]{9}$";
			if (telephone.matches(PhoneConst)) {
				user.setTelephone(telephone);
				chnged=true;
			}
			else {System.out.println("Invalid phone number.");}
			tx.commit();
		} catch (JDOObjectNotFoundException e) {
		}finally{
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		return chnged;
	}

	public synchronized void deleteUser(String username) throws RemoteException {
		// @Security: How can we guarantee that this is called by a user onto its own account,
		// or by an administrator?

		Transaction tx = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();

			User user = pm.getObjectById(User.class, username);
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

	public synchronized void deleteProperty(String address) throws RemoteException {
		Transaction tx = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();
			Property property = pm.getObjectById(Property.class, address);
			pm.deletePersistent(property);
			tx.commit();
		} catch (Exception e) {
			log.info("Property not found: " + address);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

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

	//if overlaps -> return true
	public synchronized Boolean checkOccupancy(Property property, String startDate, String endDate) throws RemoteException {
		Date checkStartDate = null;
		Date checkEndDate = null;
		try {
			checkStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
			checkEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}  

		boolean result = false;
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

			if(checkEndDate.before(sDate) || checkStartDate.after(eDate)) {	//they do not overlap
				result = false;
			} else {	//they overlap
				result = true;
			}
		}

		return result;
	}

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

	public synchronized void updateProperty (String address, String city, int capacity, double cost) throws RemoteException {
		Transaction tx = null;
		Property property = null;
		User host = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();
			property = pm.getObjectById(Property.class, address);
			host = property.getHost();
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
	}

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

	public synchronized RegistrationError registerProperty(String address, String city, int capacity, double cost, String hostname) throws RemoteException {
		// @Robustness @Security: Do something more appropriate than passing the host/owner name as a parameter,
		// Maybe pass a User object?

		Transaction tx = pm.currentTransaction();

		if(!Pattern.matches(city_regex, city)) {
			return RegistrationError.INVALID_CITY;
		}
		if(cost <= 0) {
			return RegistrationError.INVALID_COST;
		}
		if(capacity <= 0) {
			return RegistrationError.INVALID_CAPACITY;
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
		return RegistrationError.NONE;
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
