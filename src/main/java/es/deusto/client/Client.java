package es.deusto.client;


import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
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
	
	private static JFrame window;
	
	// private Controller controller;
	
	public Client(String[] args) {
		
		
		window = new JFrame("[RoomRental] Login");

		Controller controller = new Controller(window, args);
		
		window.setSize(450, 248);
		window.setResizable(false);
		
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
		JPanel pLogin = new JPanel();
		pLogin.setBounds(0, 0, 434, 209);
		pLogin.setLayout(null);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(20, 127, 62, 15);
		pLogin.add(lblUsername);
		lblUsername.setFont(new Font("Arial", Font.PLAIN, 12));
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(223, 127, 59, 15);
		pLogin.add(lblPassword);
		lblPassword.setFont(new Font("Arial", Font.PLAIN, 12));
		
		JPasswordField tfPass = new JPasswordField();
		tfPass.setBounds(292, 125, 117, 20);
		pLogin.add(tfPass);
		tfPass.setColumns(10);
		
		JTextField tfUser = new JTextField();
		tfUser.setBounds(92, 125, 121, 20);
		pLogin.add(tfUser);
		tfUser.setColumns(10);
		
		JLabel lblImg = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		lblImg.setBounds(10, 11, 414, 93);
		pLogin.add(lblImg);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener((e) -> {controller.login(tfUser.getText(), new String(tfPass.getPassword()));});
		btnLogin.setBounds(193, 166, 89, 23);
		pLogin.add(btnLogin);
		
		JButton btnReg = new JButton("Register");
		btnReg.addActionListener( (e) -> {switchReg(controller,pLogin);} );
		btnReg.setBounds(320, 166, 89, 23);
		pLogin.add(btnReg);
		return pLogin;
	}
	
	public static JPanel createRegisterWindow(Controller controller) {
		JPanel pReg = new JPanel();
		pReg.setLayout(null);
		pReg.setBounds(0, 0, 434, 286);
		pReg.setVisible(true);
		
		JLabel lblName1 = new JLabel("Name:");
		lblName1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblName1.setBounds(20, 127, 62, 15);
		pReg.add(lblName1);
		
		JLabel lblUserName1 = new JLabel("Username:");
		lblUserName1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblUserName1.setBounds(20, 153, 72, 15);
		pReg.add(lblUserName1);
		
		JTextField tfUserName1 = new JTextField();
		tfUserName1.setColumns(10);
		tfUserName1.setBounds(89, 153, 118, 20);
		pReg.add(tfUserName1);
		
		JTextField tfName1 = new JTextField();
		tfName1.setColumns(10);
		tfName1.setBounds(89, 125, 118, 20);
		pReg.add(tfName1);
		
		JLabel imgBanner1 = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		imgBanner1.setBounds(10, 11, 414, 93);
		pReg.add(imgBanner1);
		
		JButton btnBack1 = new JButton("Back");
		btnBack1.addActionListener( (e) -> {switchLog(controller,pReg);} );
		btnBack1.setBounds(212, 210, 89, 23);
		pReg.add(btnBack1);
		
		JButton btnReg1 = new JButton("Register");
		btnReg1.setBounds(311, 210, 89, 23);
		pReg.add(btnReg1);
		
		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setFont(new Font("Arial", Font.PLAIN, 12));
		lblEmail.setBounds(20, 181, 82, 15);
		pReg.add(lblEmail);
		
		JTextField tfEmail = new JTextField();
		tfEmail.setColumns(10);
		tfEmail.setBounds(89, 179, 118, 20);
		pReg.add(tfEmail);
		
		JLabel lblPhone = new JLabel("Phone:");
		lblPhone.setFont(new Font("Arial", Font.PLAIN, 12));
		lblPhone.setBounds(212, 127, 62, 15);
		pReg.add(lblPhone);
		
		JTextField tfPhone = new JTextField();
		tfPhone.setColumns(10);
		tfPhone.setBounds(292, 125, 118, 20);
		pReg.add(tfPhone);
		
		JPasswordField tfPassword1 = new JPasswordField();
		tfPassword1.setColumns(10);
		tfPassword1.setBounds(292, 151, 118, 20);
		pReg.add(tfPassword1);
		
		JLabel lblPassword1 = new JLabel("Password:");
		lblPassword1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblPassword1.setBounds(212, 153, 59, 15);
		pReg.add(lblPassword1);
		
		JLabel lblRepeat = new JLabel("Repeat Pass:");
		lblRepeat.setFont(new Font("Arial", Font.PLAIN, 12));
		lblRepeat.setBounds(212, 181, 82, 15);
		pReg.add(lblRepeat);
		
		JPasswordField tfRepeat = new JPasswordField();
		tfRepeat.setColumns(10);
		tfRepeat.setBounds(292, 179, 118, 20);
		pReg.add(tfRepeat);
		return pReg;
	}
	
	public static JPanel createMainWindowAdmin(Controller controller, String id) {
		JPanel main_panel = new JPanel();
		
		JLabel label = new JLabel("You are an administrator");
		main_panel.add(label);
		
		return main_panel;
	}
	
	public static JPanel createMainWindowHost(Controller controller, String name /*, Other data for*/) {
		// TODO
		return new JPanel();
	}
	
	public static JPanel createMainWindowGuest(Controller controller, String name /*, Other data for*/) {
		// TODO
		return new JPanel();
	}
	
	public static void switchReg(Controller controller, JPanel pLogin) {
		window.getContentPane().remove(pLogin);
		window.add(createRegisterWindow(controller));
		window.setTitle("[RoomRental] Register");
		window.setSize(450, 286);
		
	}
	public static void switchLog(Controller controller, JPanel pReg) {
		window.getContentPane().remove(pReg);
		window.add(createLogin(controller));
		window.setTitle("[RoomRental] Login");
		window.setSize(450, 248);
	}
	
	
}