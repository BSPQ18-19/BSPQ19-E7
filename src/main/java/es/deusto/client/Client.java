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
import javax.swing.UIManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import es.deusto.server.IServer;
import es.deusto.server.IServer.RegistrationError;
import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.Reservation;
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
		Locale locale = Locale.getDefault();
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

		result.setSize(450, 265);
		result.setResizable(false);
		
		JPanel login_panel = PanelBuilder.createLogin(client);

		
		result.add(login_panel);
		
		
		
		return result;
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
	
	public void changePassword(String username, String password) {
		try {
			server.changeUserPassword(username, password);
		} catch (RemoteException e) {
			log.error("Error updating password: " + password);
			e.printStackTrace();
		}
	}
	
	public void changeTelephone(String username, String telephone) {
		try {
			server.changeUserTelephone(username, telephone);
		} catch (RemoteException e) {
			log.error("Error updating telephone: " + telephone);
			e.printStackTrace();
		}
	}
	
	public void deleteAccount(User user) {
		try {
			server.deleteUser(user.getUsername());
		} catch (RemoteException e) {
			log.error("Error deleting user: " + user);
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
	
	public void searchReservationsByCity(String cityname, JList<Reservation> resultList) {
		// @Copied and adapted from searchPropertiesByCity
		List<Reservation> reservations = null;
		try {
			reservations = server.getReservationsByCity(cityname);
		} catch (RemoteException e) {
			log.error("Error retrieving reservations by city");
			e.printStackTrace();
		}
		
		// @Todo: What does the server return when it does not find any?
		if (reservations == null || reservations.isEmpty()) {
			// @Temp: In the future we will want it to show some kind of message to the user
			return;
		}
		
		DefaultListModel<Reservation> model = new DefaultListModel<Reservation>();
		
		for (Reservation r : reservations) {
			model.addElement(r);
		}
		
		resultList.setModel(model);
		
	}
	
	public void searchReservationsByGuest(String name, JList<Reservation> resultList) {
		// @Copied and adapted from searchPropertiesByCity
		List<Reservation> reservations = null;
		try {
			reservations = server.getReservationsByGuest(name);
		} catch (RemoteException e) {
			log.error("Error retrieving reservations by user");
			e.printStackTrace();
		}
		
		// @Todo: What does the server return when it does not find any?
		if (reservations == null || reservations.isEmpty()) {
			// @Temp: In the future we will want it to show some kind of message to the user
			return;
		}
		
		DefaultListModel<Reservation> model = new DefaultListModel<Reservation>();
		
		for (Reservation r : reservations) {
			model.addElement(r);
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
	
	public void updateProperty(String address, String city, int capacity, String ocupancy, double cost) {
		try {
			server.updateProperty(address, city, capacity, ocupancy, cost);
		} catch (RemoteException e) {
			log.error("Error updating property: " + address);
			e.printStackTrace();
		}
	}
	
	public void deleteProperty(Property property) {
		try {
			server.deleteProperty(property.getAddress());
		} catch (RemoteException e) {
			log.error("Error deleting property: " + property);
			e.printStackTrace();
		}
	}
	
	public void updateReservation(Property property, User guest, String date, int duration) {
		try {
			server.updateReservation(property, guest, date, duration);
		} catch (RemoteException e) {
			log.error("Error updating Reservation");
			e.printStackTrace();
		}
	}
	
	public void deleteReservation(Reservation reservation) {
		try {
			server.deleteReservation(reservation.getDate(), reservation.getClient().getUsername(), reservation.getProperty().getAddress());
		} catch (RemoteException e) {
			log.error("Error deleting reservation ");
			e.printStackTrace();
		}
	}
	
	public void bookProperty(String name, Property property, String date, String duration) {
		try {
			server.bookProperty(name, property, date, duration);
		} catch (RemoteException e) {
			log.error("Error booking property ");
			e.printStackTrace();
		}
	}
	
	public void searchPropertiesHost(JList<Property> list, String hostname) {
		List<Property> properties = null;
		try {
			properties = server.getPropertiesByHost(hostname);
		} catch (Exception e) {
			log.error("Error retrieving properties" );
			e.printStackTrace();
		}
		
		DefaultListModel<Property> model = new DefaultListModel<Property>();
		for (Property property : properties) {
			model.addElement(property);
		}
		list.setModel(model);
	}
	
	public void publishProperty(String address, String city, int capacity, double cost, String name) {
		try {
			log.info("Registering property: " + address);
			RegistrationError error = server.registerProperty(address, city, capacity, cost, name);

			log.debug("Registration result " + error);
			switch (error) {
			case INVALID_COST: {
				JOptionPane.showMessageDialog(window, "Invalid cost. The value must be positive.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case INVALID_CAPACITY: {
				JOptionPane.showMessageDialog(window, "Invalid capacity. The value must be positive.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case INVALID_CITY: {
				JOptionPane.showMessageDialog(window, "Invalid city. It is incorrectly typed.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			default: {
				// Do nothing.
			}
			}
		} catch (RemoteException e) {
			log.error("Error registering property");
			e.printStackTrace();
		}
	}
	
	
	public void switchRegister() {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createRegisterWindow(this));
		window.setTitle("[RoomRental] Register");
		window.setSize(450, 286);
		
	}
	
	public void switchLogin() {
		window.getContentPane().removeAll();
		window.add(PanelBuilder.createLogin(this));
		window.setTitle("[RoomRental] Login");
		window.setSize(450, 265);
		window.revalidate();
	}
	
	public void switchHostPropertyNew(String name) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createHostPropertyNew(this, name));
		window.setTitle("[RoomRental] Create Property");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchPropertyEdit(Property selectedProp) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createPropertyEdit(this, selectedProp, false));
		window.setTitle("[RoomRental] Edit Property");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchHostPropertiesManagement(String name) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createHostPropertiesManagement(this, name));
		window.setTitle("[RoomRental] Properties Management");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchGuestPropertiesManagement(String name) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createGuestPropertiesManagement(this, name));
		window.setTitle("[RoomRental] Properties Management");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchGuestBookProperty(String name, Property property) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createGuestBookProperty(this, name, property));
		window.setTitle("[RoomRental] Book Property");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchGuestReservationsList(String name) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createGuestReservationList(this, name));
		window.setTitle("[RoomRental] Guest Main Window");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchHostAccountManagement() {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createHostAccountManagement(this));
		window.setTitle("[RoomRental] Account Management");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchPropertiesSearch(String id) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createPropertySearch(this,id));
		window.setTitle("[RoomRental] Search Properties");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchAdminReservationsSearch(String id) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminReservationsSearch(this,id));
		window.setTitle("[RoomRental] Search Reservations");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchAdminAccountManagment(String id) {
		// Only Admins should be able to call this
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminAccountManagement(this, id));
		window.setTitle("[RoomRental] Account Management");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchAdminAccountEdit(User selectedUser) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminAccountEdit(this, selectedUser, false));
		window.setTitle("[RoomRental] Account Edit");
		window.paintComponents(window.getGraphics());
	}
	
	public void switchAdminAccountNew() {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminAccountEdit(this, null, true));
		window.setTitle("[RoomRental] New Account");
		window.paintComponents(window.getGraphics());
	}	

	public void switchReservationEdit(Reservation selectedReserv) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createReservationEdit(this, selectedReserv, false));
		window.setTitle("[RoomRental] Edit Reservation");
		window.paintComponents(window.getGraphics());
	}
	
	public void createMainWindowHost(String name) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createMainWindowHost(this, name));
		window.setTitle("[RoomRental] Host Main Window");
		window.paintComponents(window.getGraphics());
	}
	
	public void createMainWindowGuest(String name) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createMainWindowGuest(this, name));
		window.setTitle("[RoomRental] Guest Main Window");
		window.paintComponents(window.getGraphics());
	}
	public void createMainWindowAdmin(String name) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createMainWindowAdmin(this, name));
		window.setTitle("[RoomRental] Guest Main Window");
		window.paintComponents(window.getGraphics());
	}
	
	public void exit() {
		// TODO: We may want to do other things in the future. Close connections, release resources, ...
		System.exit(0);
	}
	
}