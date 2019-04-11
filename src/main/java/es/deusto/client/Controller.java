package es.deusto.client;

import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import es.deusto.server.IServer;
import es.deusto.server.IServer.RegistrationError;
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
	
	
	// @Todo: I think there is no reason to pass the old JPanel to this methods.
	// We can just make "getContentPane().removeAll()".
	// Although we could use it if we were to keep the previous panel for a back button
	public void switchReg(JPanel pLogin) {
		window.getContentPane().remove(pLogin);
		window.add(PanelBuilder.createRegisterWindow(this));
		window.setTitle("[RoomRental] Register");
		window.setSize(450, 286);
		
	}
	public void switchLog(JPanel pReg) {
		window.getContentPane().remove(pReg);
		window.add(PanelBuilder.createLogin(this));
		window.setTitle("[RoomRental] Login");
		window.setSize(450, 248);
	}
	
	public void login(String username, String password) {
		User user = null;
		try {
			System.out.println("Login...");
			user = server.login(username, password);
			
			System.out.println(user);
			
			if (user != null) {
				if (user instanceof Administrator) {
					window.getContentPane().removeAll();
					window.getContentPane().add(PanelBuilder.createMainWindowAdmin(this, user.getUsername()));
				} 
				else if (user instanceof Host) {
					window.getContentPane().removeAll();
					window.getContentPane().add(PanelBuilder.createMainWindowHost(this, user.getUsername()));
				}
				else if (user instanceof Guest) {
					window.getContentPane().removeAll();
					window.getContentPane().add(PanelBuilder.createMainWindowGuest(this, user.getUsername()));
				}
				window.paintComponents(window.getGraphics());
			}
		} catch (RemoteException e) {
			System.out.println("There was an error when login the user " + username);
			e.printStackTrace();
		}
		
	}
	
	public void register(String name, String username, String email, String telephone, String password) {
		
		try {
			System.out.println("Registering user: " + username);
			RegistrationError error = server.registerUser(name, username, email, telephone, password);
			
			switch (error) {
			case INVALID_EMAIL: {
				JOptionPane.showMessageDialog(window, "Invalid e-mail. E-mail address is already in use or is incorrectly typed.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;
			
			case INVALID_NAME: {
				JOptionPane.showMessageDialog(window, "Invalid username. Someone already picked that one.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;
			
			default: {
					// Do nothing.
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void exit() {
		// TODO: We may want to do other things in the future
		System.exit(0);
	}
	
}
