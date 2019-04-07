package es.deusto.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.GuardedObject;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Transaction;

import es.deusto.server.jdo.Administrator;
import es.deusto.server.jdo.Guest;
import es.deusto.server.jdo.Host;
import es.deusto.server.jdo.User;

public class Server extends UnicastRemoteObject implements IServer {

	private static final long serialVersionUID = 1L;
	//private int cont = 0;
	private PersistenceManager pm=null;
	//private Transaction tx=null;

	protected Server() throws RemoteException {
		super();
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		this.pm = pmf.getPersistenceManager();
//		this.tx = pm.currentTransaction();
	}
	
	protected void finalize () throws Throwable {
//		if (tx.isActive()) {
//            tx.rollback();
//        }
        pm.close();
	}
	
	@Override
	public void registerUser(String login, String password) {
//		try
//        {	
//            tx.begin();
//            System.out.println("Checking whether the user already exits or not: '" + login +"'");
//			User user = null;
//			try {
//				user = pm.getObjectById(User.class, login);
//			} catch (javax.jdo.JDOObjectNotFoundException jonfe) {
//				System.out.println("Exception launched: " + jonfe.getMessage());
//			}
//			System.out.println("User: " + user);
//			if (user != null) {
//				System.out.println("Setting password user: " + user);
//				user.setPassword(password);
//				System.out.println("Password set user: " + user);
//			} else {
//				System.out.println("Creating user: " + user);
//				user = new User(login, password);
//				pm.makePersistent(user);					 
//				System.out.println("User created: " + user);
//			}
//			tx.commit();
//        }
//        finally
//        {
//            if (tx.isActive())
//            {
//                tx.rollback();
//            }
//      
//        }
//		
//		
	}
	
	@Override
	public User login(String username, String password) {
		
		System.out.println("Login " + username);
		// TODO: Check in the database if this user exists
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
