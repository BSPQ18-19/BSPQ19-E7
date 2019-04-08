package es.deusto.client;

import java.rmi.RemoteException;

import javax.swing.JFrame;

import es.deusto.server.IServer;
import es.deusto.server.jdo.Administrator;
import es.deusto.server.jdo.Guest;
import es.deusto.server.jdo.Host;
import es.deusto.server.jdo.User;

public class Controller {

	private IServer server;
	private JFrame window;
	
	public Controller(JFrame window, String[] args) {
		this.window = window;
		if (args.length != 3) {
			System.out.println("Use: java [policy] [codebase] Client.Client [host] [port] [server]");
			System.exit(0);
		}

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			String name = "//" + args[0] + ":" + args[1] + "/" + args[2];
			server = (IServer) java.rmi.Naming.lookup(name);
			// Register to be allowed to send messages
			//objHello.registerUser("dipina", "dipina");
			//System.out.println("* Message coming from the server: '" + objHello.sayMessage("dipina", "dipina", "This is a test!") + "'");
			
		} catch (Exception e) {
			System.err.println("RMI Example exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void login(String username, String password) {
		User user = null;
		try {
			System.out.println("Login...");
			user = server.login(username, password);
		} catch (RemoteException e) {
			System.out.println("There was an error when login the user " + username);
			e.printStackTrace();
		}
		System.out.println(user);
		if (user != null) {
			if (user instanceof Administrator) {
				window.getContentPane().removeAll();
				window.getContentPane().add(Client.createMainWindowAdmin(this, "Default name"));
			} 
			else if (user instanceof Host) {
				window.getContentPane().removeAll();
				window.getContentPane().add(Client.createMainWindowHost(this, "Default name"));
			}
			else if (user instanceof Guest) {
				window.getContentPane().removeAll();
				window.getContentPane().add(Client.createMainWindowGuest(this, "Default name"));
			}
			window.paintComponents(window.getGraphics());
		}
	}
	
	public void register(String username, String password) {
		
		try {
			System.out.println("Registering user: " + username);
			server.registerUser(username, password);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO: Show some kind of error
	}
	
	public void exit() {
		// TODO: We may want to do other things in the future
		System.exit(0);
	}
	
}
