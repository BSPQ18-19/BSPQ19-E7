package es.deusto.client;


import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import es.deusto.server.IServer;

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
	
	private JFrame window;
	
	// private Controller controller;
	
	public Client(String[] args) {
		
		
		window = new JFrame("Window name");

		Controller controller = new Controller(window, args);
		
		window.setSize(600, 400);
		
		JPanel login_panel = createLogin(controller);
				/*new JPanel();
		login_panel.setLayout(new GridLayout(0, 2));
		
		JTextField username_field = new JTextField();
		JPasswordField password_field = new JPasswordField();
		
		login_panel.add(new JLabel("Username:"));
		login_panel.add(username_field);
		login_panel.add(new JLabel("Password:"));
		login_panel.add(password_field);
		
		JButton enter_button = new JButton("Enter");
		enter_button.addActionListener((e) -> {controller.login(username_field.getText(), new String(password_field.getPassword()));});
		
		JButton close_button = new JButton("Exit");
		close_button.addActionListener((e) -> {controller.exit();});
		
		
		login_panel.add(enter_button);
		login_panel.add(close_button);
		
		*/
		
		window.add(login_panel);
		
		// window.pack();
		
		window.setVisible(true);
	}
	
	public static JPanel createLogin(Controller controller) {
		JPanel login_panel = new JPanel();
		login_panel.setLayout(new GridLayout(0, 2));
		
		JTextField username_field = new JTextField();
		JPasswordField password_field = new JPasswordField();
		
		login_panel.add(new JLabel("Username:"));
		login_panel.add(username_field);
		login_panel.add(new JLabel("Password:"));
		login_panel.add(password_field);
		
		JButton enter_button = new JButton("Enter");
		enter_button.addActionListener((e) -> {controller.login(username_field.getText(), new String(password_field.getPassword()));});
		
		JButton close_button = new JButton("Exit");
		close_button.addActionListener((e) -> {controller.exit();});
		
		
		login_panel.add(enter_button);
		login_panel.add(close_button);
		return login_panel;
	}
	
	public static JPanel createMainWindowAdmin(Controller controller, String id) {
		// TODO
		return new JPanel();
	}
	
	public JPanel createMainWindowHost(Controller controller, String name /*, Other data for*/) {
		// TODO
		return new JPanel();
	}
	
	public JPanel createMainWindowGuest(Controller controller, String name /*, Other data for*/) {
		// TODO
		return new JPanel();
	}
	
	
}