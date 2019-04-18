package es.deusto.client;


import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import es.deusto.server.IServer;
import es.deusto.server.IServer.RegistrationError;
import es.deusto.server.jdo.Administrator;
import es.deusto.server.jdo.Guest;
import es.deusto.server.jdo.Host;
import es.deusto.server.jdo.User;


public class Client {

	public static void main(String[] args) {
		
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
     	}
		catch(Exception e) {
			System.out.println("Could not set the system look and feel");
		}
		
		new Client(args);
	}	

	
	private IServer server;
	private JFrame window;

	private static Logger log;
	
	public Client(String[] args) {
		this.window = createInitWindow(this);
		
		BasicConfigurator.configure();
		log = Logger.getLogger(Controller.class);
		
		if (args.length != 3) {
			System.out.println("Use: java [policy] [codebase] Client.Client [host] [port] [server]");
			log.error("Wrong number of cmd line arguments: " + args);
			System.exit(0);
		}

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
			log.info("Updated security manager");
		}

		try {
			String name = "//" + args[0] + ":" + args[1] + "/" + args[2];
			server = (IServer) java.rmi.Naming.lookup(name);
			log.info("Server connection ready");
			// Register to be allowed to send messages
			//objHello.registerUser("dipina", "dipina");
			//System.out.println("* Message coming from the server: '" + objHello.sayMessage("dipina", "dipina", "This is a test!") + "'");
			
		} catch (Exception e) {
			System.err.println("RMI Example exception: " + e.getMessage());
			e.printStackTrace();
			log.error("RMI Example exception: " + e.getMessage());
		}
		
		window.setVisible(true);
	}
	
	private JFrame createInitWindow(Client client) {
		
		JFrame result = new JFrame("[RoomRental] Login");

		result.setSize(450, 248);
		result.setResizable(false);
		
		JPanel login_panel = PanelBuilder.createLogin(client);

		
		result.add(login_panel);
		
		
		
		return result;
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
			log.info("Trying to log user: " + username);
			user = server.login(username, password);
			
			log.debug("Login result: " + user);
			
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
			log.error("There was an error when login the user " + username);
		}
		
	}
	
	public void register(String name, String username, String email, String telephone, String password) {
		
		try {
			System.out.println("Registering user: " + username);
			log.info("Registering user: " + username);
			RegistrationError error = server.registerUser(name, username, email, telephone, password);
			
			log.debug("Registration result " + error);
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
			log.error("Error registering user");
			e.printStackTrace();
		}
		
	}
	
	public void exit() {
		// TODO: We may want to do other things in the future. Close connections, release resources, ...
		System.exit(0);
	}
	
	
	
}