package es.deusto.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.GuardedObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Transaction;

import org.datanucleus.util.RegularExpressionConverter;

import es.deusto.server.jdo.Administrator;
import es.deusto.server.jdo.Guest;
import es.deusto.server.jdo.Host;
import es.deusto.server.jdo.User;

public class Server extends UnicastRemoteObject implements IServer {

	private static final long serialVersionUID = 1L;
	//private int cont = 0;
	private PersistenceManager pm=null;
	//private Transaction tx=null;

	// Copied from: http://emailregex.com/
	private  String email_regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
	
	protected Server() throws RemoteException {
		super();
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		this.pm = pmf.getPersistenceManager();
//		this.tx = pm.currentTransaction();
		
//		registerUser("admin", "admin");
		
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
		
		try
        {	
            tx.begin();
            System.out.println("Checking whether the user already exits or not: '" + username+"'");
			User user = null;
			try {
				user = pm.getObjectById(User.class, username);
			} catch (javax.jdo.JDOObjectNotFoundException jonfe) {
				System.out.println("Exception launched: " + jonfe.getMessage());
			}
			System.out.println("User: " + user);
			if (user != null) {
				return RegistrationError.INVALID_EMAIL;
				
				// @Todo: Is this the supposed behavior?
				/*
				System.out.println("Setting password user: " + user);
				user.setPassword(password);
				System.out.println("Password set user: " + user);*/
			} else {
				System.out.println("Creating user: " + username);
				user = new Administrator(username, password);
				pm.makePersistent(user);					 
				System.out.println("User created: " + user);
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
	public User login(String username, String password) {
		
		System.out.println("Login " + username);
		User user = null;
		Transaction tx = null;
		try {
			tx = pm.currentTransaction();
			tx.begin();
			
			user = pm.getObjectById(User.class, username);
			
			tx.commit();
			
		} catch (JDOObjectNotFoundException e) {
			System.out.println("User not found: " + username);
			
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			
			
		}
		return user;
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("How to invoke: java [policy] [codebase] Server.Server [host] [port] [server]");
			System.exit(0);
		}

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		String name = "//" + args[0] + ":" + args[1] + "/" + args[2];

		try {
			IServer objServer = new Server();
			Naming.rebind(name, objServer);
			System.out.println("Server '" + name + "' active and waiting...");			
			System.in.read();
		} catch (Exception e) {
			System.err.println("Hello exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
