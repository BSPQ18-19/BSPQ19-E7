package es.deusto.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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

import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.User;
import es.deusto.server.jdo.User.UserKind;

public class Server extends UnicastRemoteObject implements IServer {

	private static final long serialVersionUID = 1L;
	private PersistenceManager pm=null;
	
	private static Logger log;

	// Copied from: http://emailregex.com/
	private  String email_regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
	// Copied from: https://stackoverflow.com/a/18626090
	private String telephone_regex = "\\(?\\+[0-9]{1,3}\\)? ?-?[0-9]{1,3} ?-?[0-9]{3,5} ?-?[0-9]{4}( ?-?[0-9]{3})? ?(\\w{1,10}\\s?\\d{1,6})?";
	
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

	}
	
	protected void finalize () throws Throwable {
//		if (tx.isActive()) {
//            tx.rollback();
//        }
        pm.close();
	}
	
	@Override
	public RegistrationError registerUser(String name, String username, String email, String telephone, String password) {
		
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
//		if (!Pattern.matches(telephone_regex, telephone)) {
//			return RegistrationError.INVALID_TELEPHONE;
//		}
		
		try
        {	
            tx.begin();
            System.out.println("Checking whether the user already exits or not: '" + username+"'");
			log.info("Checking whether the user already exits or not: '" + username+"'");
            User user = null;
			try {
				user = pm.getObjectById(User.class, username);
			} catch (javax.jdo.JDOObjectNotFoundException jonfe) {
				System.out.println("Exception launched: " + jonfe.getMessage());
			}
			System.out.println("User: " + user);
			log.info("User: " + user);
			
			if (user != null) {
				return RegistrationError.INVALID_NAME;
				
				// @Todo: Is this the supposed behavior? We should make a method that exclusively changes passwords
				/*
				System.out.println("Setting password user: " + user);
				user.setPassword(password);
				System.out.println("Password set user: " + user);*/
			} else {
				System.out.println("Creating user: " + username);
				log.info("Creating user: " + username);
	            	user = new User(username, password, User.UserKind.ADMINISTRATOR, null, null, null, false);
				pm.makePersistent(user);					 
				System.out.println("User created: " + user);
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
	public List<Property> getPropertiesByCity(String city) {
		List<Property> result = null;

		// @Robustness: I don't know if pm.currentTransaction can fail
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			
			
			Query<Property> query = pm.newQuery(Property.class);
			query.setFilter("city == " + city);
			
			result = query.executeList();
			
			tx.commit();
		} catch (JDOObjectNotFoundException e) {
			System.out.println("Property not found: " + city);
			log.error("Property not found: " + city);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			
			
		}
		return result;
	}
	
	public List<User> getUser(String username) {
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
			System.out.println("User not found: " + username);
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
	public User login(String username, String password) throws RemoteException {
		
		// @Refactor: Can we use 'getUser()' above, inside this method?
		
		System.out.println("Login " + username);
		log.info("Login " + username);
		User user = null;
		Transaction tx = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();
			
			user = pm.getObjectById(User.class, username);
			
			tx.commit();
			
		} catch (JDOObjectNotFoundException e) {
			System.out.println("User not found: " + username);
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
	
	public void updateUser(String username, String password, UserKind kind, String telephone, String email, String name, boolean verified) throws RemoteException {
		Transaction tx = null;
		User user = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();
			
			// Get the original user 
			user = pm.getObjectById(User.class, username);
			
			
			tx.commit();
			
		} catch (JDOObjectNotFoundException e) {
			System.out.println("User not found: " + username);
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
				System.out.println("Updating existing user");
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
				System.out.println("Creating new user");
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
	
	public void deleteUser(String username) throws RemoteException {
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
			System.out.println("User not found: " + username);
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
