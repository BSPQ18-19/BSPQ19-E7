package es.deusto.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;

import es.deusto.server.jdo.Administrator;
import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.User;

public class Server extends UnicastRemoteObject implements IServer {

	private static final long serialVersionUID = 1L;
	//private int cont = 0;
	private PersistenceManager pm=null;
	//private Transaction tx=null;
	
	private static Logger log;

	// Copied from: http://emailregex.com/
	private  String email_regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
	// Copied from: https://stackoverflow.com/a/18626090
	private String telephone_regex = "\\(?\\+[0-9]{1,3}\\)? ?-?[0-9]{1,3} ?-?[0-9]{3,5} ?-?[0-9]{4}( ?-?[0-9]{3})? ?(\\w{1,10}\\s?\\d{1,6})?";
	
	protected Server() throws RemoteException {
		super();
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		this.pm = pmf.getPersistenceManager();

	}
	
	protected void finalize () throws Throwable {
//		if (tx.isActive()) {
//            tx.rollback();
//        }
        pm.close();
	}
	
	@Override
	public RegistrationError registerUser(String name, String username, String email, String telephone, String password) {
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
	            	user = new Administrator(username, password);
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
//		
	}
	
	@Override
	public List<Property> getPropertiesByCity(String city) {
		List<Property> result = null;

		// @Robustness: I don't know if pm.currentTransaction can fail
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			
			
			Query<Property> query = pm.newQuery(Property.class);
			query.setFilter("city = " + city);
			
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
	
	@Override
	public User login(String username, String password) {
		
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
		return user;
	}

	public static void main(String[] args) {
		//BasicConfigurator.configure();
		log = Logger.getLogger(Server.class);

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
			System.out.println("Server '" + name + "' active and waiting...");			
			log.info("Server ready");
			System.in.read();
		} catch (Exception e) {
			System.err.println("Hello exception: " + e.getMessage());
			log.error("RMI error: could not bind the server to the registry");
			log.error(e.getStackTrace());
			e.printStackTrace();
		}
	}
}
