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
		if (args.length != 3) {
			System.out.println("Use: java [policy] [codebase] Client.Client [host] [port] [server]");
			System.exit(0);
		}

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			String name = "//" + args[0] + ":" + args[1] + "/" + args[2];
			IServer objHello = (IServer) java.rmi.Naming.lookup(name);
			// Register to be allowed to send messages
			objHello.registerUser("dipina", "dipina");
			System.out.println("* Message coming from the server: '" + objHello.sayMessage("dipina", "dipina", "This is a test!") + "'");
			
		} catch (Exception e) {
			System.err.println("RMI Example exception: " + e.getMessage());
			e.printStackTrace();
		}
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
     	}
		catch(Exception e) {
			System.out.println("Could not set the system look and feel");
		}
		
		new Client();
	}
	
	private JFrame window;
	
	private Controller controller;
	
	public Client() {
		
		controller = new Controller();
		
		window = new JFrame("Window name");
		
		window.setSize(600, 400);
		
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
		close_button.addActionListener((e) -> {System.exit(0);});
		
		
		login_panel.add(enter_button);
		login_panel.add(close_button);
		
		
		
		window.add(login_panel);
		
		// window.pack();
		
		window.setVisible(true);
	}
}