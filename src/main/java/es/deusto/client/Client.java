package es.deusto.client;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;


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
		
		
		window = new JFrame("[RoomRental] Login");

		Controller controller = new Controller(window, args);
		
		window.setSize(450, 248);
		window.setResizable(false);
		
		JPanel login_panel = PanelBuilder.createLogin(controller);

		
		window.add(login_panel);
		
		
		window.setVisible(true);
	}
	

	
	
	
}