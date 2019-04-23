package es.deusto.client;


import java.rmi.RemoteException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.UIManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import es.deusto.server.IServer;
import es.deusto.server.IServer.RegistrationError;
import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.User;
import es.deusto.server.jdo.User.UserKind;


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

	// i18n
	public ResourceBundle text;
	
	private static Logger log;
	
	public Client(String[] args) {
		Locale locale = Locale.ENGLISH;
		text = ResourceBundle.getBundle("app_text", locale);

		System.out.println();
		
		
		this.window = createInitWindow(this);
		
		BasicConfigurator.configure();
		log = Logger.getLogger(Client.class);
		
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
				
				switch(user.getKind()) {
				case ADMINISTRATOR: {
					window.getContentPane().removeAll();
					window.getContentPane().add(PanelBuilder.createMainWindowAdmin(this, user.getUsername()));
				} break;
				case HOST: {
					window.getContentPane().removeAll();
					window.getContentPane().add(PanelBuilder.createMainWindowHost(this, user.getUsername()));
				} break;
				case GUEST: {
					window.getContentPane().removeAll();
					window.getContentPane().add(PanelBuilder.createMainWindowGuest(this, user.getUsername()));
				} break;
				
				default: {
					// This should be unreachable
					assert false : "This code should be unreachable!";
				}				
				}
				window.paintComponents(window.getGraphics());
			}
		} catch (RemoteException e) {
			System.out.println("There was an error when login the user " + username);
			e.printStackTrace();
			log.error("There was an error when login the user " + username);
		}
		
	}
	
	public void register(String name, String username, String email, String telephone, String password, boolean isHost) {
		
		try {
			System.out.println("Registering user: " + username);
			log.info("Registering user: " + username);
			RegistrationError error = server.registerUser(name, username, email, telephone, password, isHost);
			
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
	
	public void adminUpdateAccount(String username, String password, UserKind kind, String telephone, String email, String name, boolean verified) {
		try {
			server.updateUser(username, password, kind, telephone, email, name, verified);
		} catch (RemoteException e) {
			log.error("Error updating user: " + username);
			e.printStackTrace();
		}
	}
	
	public void deleteAccount(User user) {
		try {
			server.deleteUser(user.getUsername());
		} catch (RemoteException e) {
			log.error("Error deleting user " + user);

			e.printStackTrace();
		}
	}
	
	/**This method searches for all the properties of a given city and adds them to the JList
	 * 
	 * @param cityname Name of the city in which to search for all the properties
	 * @param resultList JList in which to display the results
	 */
	public void searchPropertiesByCity(String cityname, JList<Property> resultList) {
		List<Property> properties = null;
		try {
			properties = server.getPropertiesByCity(cityname);
		} catch (RemoteException e) {
			log.error("Error retrieving properties by city");
			e.printStackTrace();
		}
		
		// @Todo: What does the server return when it does not find any?
		if (properties == null || properties.isEmpty()) {
			// @Temp: In the future we will want it to show some kind of message to the user
			return;
		}
		
		DefaultListModel<Property> model = new DefaultListModel<Property>();
		
		for (Property p : properties) {
			model.addElement(p);
		}
		
		resultList.setModel(model);
		
	}
	
	public void searchUsers(String username, JList<User> resultList) {
		List<User> users = null;
		try {
			users = server.getUser(username);
		} catch (RemoteException e) {
			log.error("Error retrieving properties by city");
			e.printStackTrace();
		}
		
		// @Todo: What does the server return when it does not find any?
		if (users == null || users.isEmpty()) {
			// @Temp: In the future we will want it to show some kind of message to the user
			return;
		}
		
		DefaultListModel<User> model = new DefaultListModel<User>();
		
		for (User u : users) {
			model.addElement(u);
		}
		
		resultList.setModel(model);
		
	}
	

	public void switchPropertiesSearch() {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createPropertySearch(this));
		window.paintComponents(window.getGraphics());
	}
	
	public void switchAdminAccountManagment() {
		// Only Admins should be able to call this
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminAccountManagement(this));
		window.paintComponents(window.getGraphics());
	}
	
	public void switchAdminAccountEdit(User selectedUser) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminAccountEdit(this, selectedUser, false));
		window.paintComponents(window.getGraphics());
	}
	
	public void switchAdminAccountNew() {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminAccountEdit(this, null, true));
		window.paintComponents(window.getGraphics());
	}
	
	public void exit() {
		// TODO: We may want to do other things in the future. Close connections, release resources, ...
		System.exit(0);
	}

	

	
	
	
}