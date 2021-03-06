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
import es.deusto.server.IServer.OccupancyError;
import es.deusto.server.IServer.PropertyRegistrationError;
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

	public JFrame getWindow() {
		return window;
	}

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
					window.getContentPane().add(PanelBuilder.createMainWindowAdmin(this, user.getUsername(), user.getKind()));
				} break;
				case HOST: {
					window.getContentPane().removeAll();
					window.getContentPane().add(PanelBuilder.createMainWindowHost(this, user.getUsername(), user.getKind()));
				} break;
				case GUEST: {
					window.getContentPane().removeAll();
					window.getContentPane().add(PanelBuilder.createMainWindowGuest(this, user.getUsername(), user.getKind()));
				} break;

				default: {
					// This should be unreachable
					assert false : "This code should be unreachable!";
				}				
				}
				window.paintComponents(window.getGraphics());
			} else {
				JOptionPane.showMessageDialog(window, "Combination of username and password does not exist or is incorrect.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			log.error("There was an error when login the user " + username);
		}

	}

	public void register(String name, String username, String email, String telephone, String password, boolean isHost) {

		try {
			log.info("Registering user: " + username);
			RegistrationError error = server.registerUser(username, name, email, telephone, password, isHost);

			log.debug("Registration result " + error);
			switch (error) {

			case INVALID_EMPTY_FIELD: {
				JOptionPane.showMessageDialog(window, "You can not leave empty fields.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case INVALID_EMAIL: {
				JOptionPane.showMessageDialog(window, "Invalid e-mail. E-mail address is already in use or is incorrectly typed.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case INVALID_NAME: {
				JOptionPane.showMessageDialog(window, "Invalid username. Someone already picked that one.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case INVALID_TELEPHONE: {
				JOptionPane.showMessageDialog(window, "Invalid telephone. It is incorrectly typed.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case NONE: {
				JOptionPane.showMessageDialog(window, "Registration succesfull", "Information", JOptionPane.INFORMATION_MESSAGE, null);
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

			log.info("Updating user: " + username);
			RegistrationError error = server.updateUser(username, password, kind, telephone, email, name, verified);

			log.debug("Updating result " + error);
			switch (error) {

			case INVALID_EMPTY_FIELD: {
				JOptionPane.showMessageDialog(window, "You can not leave empty fields.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case INVALID_EMAIL: {
				JOptionPane.showMessageDialog(window, "Invalid e-mail. E-mail address is already in use or is incorrectly typed.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case INVALID_TELEPHONE: {
				JOptionPane.showMessageDialog(window, "Invalid telephone. It is incorrectly typed.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case NONE: {
				JOptionPane.showMessageDialog(window, "Update succesfull", "Information", JOptionPane.INFORMATION_MESSAGE, null);
			} break;

			default: {
				// Do nothing.
			}
			}

		} catch (RemoteException e) {
			log.error("Error updating user: " + username);
			e.printStackTrace();
		}
	}
	
	public User getUser(String username) {
		User user = null;
		try {
			user = server.getUser(username);
		} catch (RemoteException e) {
			log.error("Error gettin user: " + username);
			e.printStackTrace();
		}
		return user;
	}

	public void changeAccountData (String username, String password, String telephone) {
		try {
			boolean pass = server.changeUserPassword(username, password);
			boolean tlf = server.changeUserTelephone(username, telephone);
			if(tlf == true && pass == true) {
				JOptionPane.showMessageDialog(window, "Update succesfull", "Information", JOptionPane.INFORMATION_MESSAGE, null);
			} else if (tlf == false) {
				JOptionPane.showMessageDialog(window, "Invalid telephone. It is incorrectly typed.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			}
		} catch (RemoteException e) {
			log.error("Error updating account data: " + username);
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

		if (properties == null || properties.isEmpty()) {
			JOptionPane.showMessageDialog(window, "No results found.", "Alert", JOptionPane.WARNING_MESSAGE, null);
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

		if (reservations == null || reservations.isEmpty()) {
			JOptionPane.showMessageDialog(window, "No results found.", "Alert", JOptionPane.WARNING_MESSAGE, null);
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

		if (reservations == null || reservations.isEmpty()) {
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
			users = server.getUsers(username);
		} catch (RemoteException e) {
			log.error("Error retrieving properties by city");
			e.printStackTrace();
		}

		if (users == null || users.isEmpty()) {
			JOptionPane.showMessageDialog(window, "No results found.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			return;
		}

		DefaultListModel<User> model = new DefaultListModel<User>();

		for (User u : users) {
			model.addElement(u);
		}

		resultList.setModel(model);

	}

	public void deleteProperty(Property property) {
		try {
			log.info("Deleting property: " + property);
			server.deleteProperty(property.getAddress());
		} catch (RemoteException e) {
			log.error("Error deleting property: " + property);
			e.printStackTrace();
		}
	}

	public void updateReservation(Property property, User guest, String oldStartDate, String startDate, String endDate) {
		try {
			log.info("Checking occupancy...");
			OccupancyError error = server.checkOccupancy(property, startDate, endDate);

			log.debug("Checking result " + error);
			switch (error) {
			case INVALID_DATE: {
				JOptionPane.showMessageDialog(window, "Invalid dates. Start date must be earlier than end date.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case INVALID_OVERLAP: {
				JOptionPane.showMessageDialog(window, "Invalid dates. The property is not available on these dates.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case NONE: {
				server.updateReservation(property, guest, oldStartDate, startDate, endDate);
				JOptionPane.showMessageDialog(window, "The reservation has been successfully updated.", "Information", JOptionPane.INFORMATION_MESSAGE, null);
			} break;

			default: {
				// Do nothing.
			}
			}
		} catch (RemoteException e) {
			log.error("Error updating Reservation");
			e.printStackTrace();
		}
	}

	public void deleteReservation(Reservation reservation) {
		try {
			server.deleteReservation(reservation.getProperty().getAddress(), reservation.getGuest().getUsername(), reservation.getStartDate(), reservation.getEndDate());
		} catch (RemoteException e) {
			log.error("Error deleting reservation ");
			e.printStackTrace();
		}
	}

	public void bookProperty(String name, Property property, String startDate, String endDate) {
		try {
			log.info("Checking occupancy...");
			OccupancyError error = server.checkOccupancy(property, startDate, endDate);

			log.debug("Checking result " + error);
			switch (error) {
			case INVALID_DATE: {
				JOptionPane.showMessageDialog(window, "Invalid dates. Start date must be earlier than end date.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case INVALID_OVERLAP: {
				JOptionPane.showMessageDialog(window, "Invalid dates. The property is not available on these dates.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case NONE: {
				server.bookProperty(name, property, startDate, endDate);			
				JOptionPane.showMessageDialog(window, "The property has been successfully booked.", "Information", JOptionPane.INFORMATION_MESSAGE, null);
			} break;

			default: {
				// Do nothing.
			}
			}
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
			PropertyRegistrationError error = server.registerProperty(address, city, capacity, cost, name);

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

			case NONE: {
				JOptionPane.showMessageDialog(window, "The property has been successfully published.", "Information", JOptionPane.INFORMATION_MESSAGE, null);
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

	public void updateProperty(String address, int capacity, double cost) {
		try {
			log.info("Updating property: " + address);
			PropertyRegistrationError error = server.updateProperty(address, capacity, cost);

			log.debug("Update result " + error);
			switch (error) {
			case INVALID_COST: {
				JOptionPane.showMessageDialog(window, "Invalid cost. The value must be positive.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case INVALID_CAPACITY: {
				JOptionPane.showMessageDialog(window, "Invalid capacity. The value must be positive.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			} break;

			case NONE: {
				JOptionPane.showMessageDialog(window, "The property has been successfully updated.", "Information", JOptionPane.INFORMATION_MESSAGE, null);
			} break;

			default: {
				// Do nothing.
			}
			}

		} catch (RemoteException e) {
			log.error("Error updating property: " + address);
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

	public void switchHostPropertyNew(String name, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createHostPropertyNew(this, name, kind));
		window.setTitle("[RoomRental] Create Property");
		window.paintComponents(window.getGraphics());
	}

	public void switchPropertyEdit(Property selectedProp, UserKind kind, String id) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createPropertyEdit(this, selectedProp, false, kind, id));
		window.setTitle("[RoomRental] Edit Property");
		window.paintComponents(window.getGraphics());
	}

	public void switchHostPropertiesManagement(String name, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createHostPropertiesManagement(this, name, kind));
		window.setTitle("[RoomRental] Properties Management");
		window.paintComponents(window.getGraphics());
	}

	public void switchGuestPropertiesManagement(String name, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createGuestPropertiesManagement(this, name, kind));
		window.setTitle("[RoomRental] Properties Management");
		window.paintComponents(window.getGraphics());
	}

	public void switchGuestBookProperty(String name, Property property, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createGuestBookProperty(this, name, property, kind));
		window.setTitle("[RoomRental] Book Property");
		window.paintComponents(window.getGraphics());
	}

	public void switchGuestReservationsList(String name, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createGuestReservationList(this, name, kind));
		window.setTitle("[RoomRental] Guest Main Window");
		window.paintComponents(window.getGraphics());
	}

	public void switchAccountManagement(String name, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAccountManagement(this, name, kind));
		window.setTitle("[RoomRental] Account Management");
		window.paintComponents(window.getGraphics());
	}

	public void switchPropertiesSearch(String id, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createPropertySearch(this, id, kind));
		window.setTitle("[RoomRental] Search Properties");
		window.paintComponents(window.getGraphics());
	}

	public void switchAdminReservationsSearch(String id, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminReservationsSearch(this, id, kind));
		window.setTitle("[RoomRental] Search Reservations");
		window.paintComponents(window.getGraphics());
	}

	public void switchAdminAccountManagment(String id, UserKind kind) {
		// Only Admins should be able to call this
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminAccountManagement(this, id, kind));
		window.setTitle("[RoomRental] Account Management");
		window.paintComponents(window.getGraphics());
	}

	public void switchAdminAccountEdit(User selectedUser, String id, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminAccountEdit(this, selectedUser, false, id, kind));
		window.setTitle("[RoomRental] Account Edit");
		window.paintComponents(window.getGraphics());
	}

	public void switchAdminAccountNew(String id, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createAdminAccountEdit(this, null, true, id, kind));
		window.setTitle("[RoomRental] New Account");
		window.paintComponents(window.getGraphics());
	}	

	public void switchReservationEdit(Reservation selectedReserv, UserKind kind, String id) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createReservationEdit(this, selectedReserv, false, kind, id));
		window.setTitle("[RoomRental] Edit Reservation");
		window.paintComponents(window.getGraphics());
	}

	public void createMainWindowHost(String name, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createMainWindowHost(this, name, kind));
		window.setTitle("[RoomRental] Host Main Window");
		window.paintComponents(window.getGraphics());
	}

	public void createMainWindowGuest(String name, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createMainWindowGuest(this, name, kind));
		window.setTitle("[RoomRental] Guest Main Window");
		window.paintComponents(window.getGraphics());
	}

	public void createMainWindowAdmin(String name, UserKind kind) {
		window.getContentPane().removeAll();
		window.getContentPane().add(PanelBuilder.createMainWindowAdmin(this, name, kind));
		window.setTitle("[RoomRental] Guest Main Window");
		window.paintComponents(window.getGraphics());
	}

	public void exit() {
		// TODO: We may want to do other things in the future. Close connections, release resources, ...
		System.exit(0);
	}

}