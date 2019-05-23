package es.deusto.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.Reservation;
import es.deusto.server.jdo.User;
import es.deusto.server.jdo.User.UserKind;

public class PanelBuilder {
	
	static Font genericFont =  new Font("Arial", Font.PLAIN, 12);

	/**
	 * Creates the login panel
	 * 
	 * @param client Object representing the state of the client application
	 * @return The login JPanel
	 */
	public static JPanel createLogin(Client client) {

		JPanel pLogin = new JPanel();
		pLogin.setBounds(0, 0, 434, 209);
		pLogin.setLayout(null);

		JLabel lblUsername = new JLabel(client.text.getString("Username")+ ":");
		lblUsername.setBounds(20, 127, 62, 15);
		pLogin.add(lblUsername);
		lblUsername.setFont(genericFont);

		JLabel lblPassword = new JLabel(client.text.getString("Password")+":");
		lblPassword.setBounds(223, 127, 59, 15);
		pLogin.add(lblPassword);
		lblPassword.setFont(genericFont);

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

		JButton btnLogin = new JButton(client.text.getString("Login"));
		btnLogin.addActionListener((e) -> {client.login(tfUser.getText(), new String(tfPass.getPassword()));});
		btnLogin.setBounds(193, 178, 89, 23);
		pLogin.add(btnLogin);

		JButton btnReg = new JButton(client.text.getString("Register"));
		btnReg.addActionListener( (e) -> {client.switchRegister();} );
		btnReg.setBounds(320, 178, 89, 23);
		pLogin.add(btnReg);
		
		//JLabel labelBL = new JLabel(new ImageIcon(Client.class.getResource("/imgs/p5.png")));
		//labelBL.setBounds(0, 115, 434, 93);
		//pLogin.add(labelBL);
		
		return pLogin;
	}

	/**
	 * Creates the registering panel.
	 * 
	 * @param client Client object of the client application session
	 * @return JPanel of the registration
	 */
	public static JPanel createRegisterWindow(Client client) {

		// @Todo @Performance Avoid creating the font each time


		JPanel pReg = new JPanel();
		pReg.setLayout(null);
		pReg.setBounds(48, 245, 352, 20);
		pReg.setVisible(true);

		JLabel lblName1 = new JLabel(client.text.getString("Name")+":");
		lblName1.setFont(genericFont);
		lblName1.setBounds(160, 115, 72, 15);
		pReg.add(lblName1);

		JLabel lblUserName1 = new JLabel(client.text.getString("Username")+":");
		lblUserName1.setFont(genericFont);
		lblUserName1.setBounds(48, 115, 62, 15);
		pReg.add(lblUserName1);

		JTextField tfUserName1 = new JTextField();
		tfUserName1.setColumns(10);
		tfUserName1.setBounds(48, 141, 95, 20);
		pReg.add(tfUserName1);

		JTextField tfName1 = new JTextField();
		tfName1.setColumns(10);
		tfName1.setBounds(160, 141, 95, 20);
		pReg.add(tfName1);

		JLabel imgBanner1 = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		imgBanner1.setBounds(10, 11, 414, 93);
		pReg.add(imgBanner1);

		JButton btnBack1 = new JButton(client.text.getString("Back"));
		btnBack1.addActionListener( (e) -> {client.switchLogin();} );
		btnBack1.setBounds(213, 341, 89, 23);
		pReg.add(btnBack1);

		JLabel lblEmail = new JLabel(client.text.getString("Email")+":");
		lblEmail.setFont(genericFont);
		lblEmail.setBounds(48, 172, 62, 15);
		pReg.add(lblEmail);

		JTextField tfEmail = new JTextField();
		tfEmail.setColumns(10);
		tfEmail.setBounds(48, 192, 352, 20);
		pReg.add(tfEmail);

		JLabel lblPhone = new JLabel(client.text.getString("Phone")+":");
		lblPhone.setFont(genericFont);
		lblPhone.setBounds(276, 115, 62, 15);
		pReg.add(lblPhone);

		JTextField tfPhone = new JTextField();
		tfPhone.setColumns(10);
		tfPhone.setBounds(276, 141, 95, 20);
		pReg.add(tfPhone);

		JPasswordField tfPassword1 = new JPasswordField();
		tfPassword1.setColumns(10);
		tfPassword1.setBounds(48, 245, 352, 20);
		pReg.add(tfPassword1);

		JLabel lblPassword1 = new JLabel(client.text.getString("Password")+":");
		lblPassword1.setFont(genericFont);
		lblPassword1.setBounds(48, 223, 62, 15);
		pReg.add(lblPassword1);

		JLabel lblRepeat = new JLabel(client.text.getString("Repeat_Password")+":");
		lblRepeat.setFont(genericFont);
		lblRepeat.setBounds(48, 276, 82, 15);
		pReg.add(lblRepeat);
		
		JPasswordField tfRepeat = new JPasswordField();
		tfRepeat.setColumns(10);
		tfRepeat.setBounds(48, 302, 352, 20);
		pReg.add(tfRepeat);

		JCheckBox hostCheckBox = new JCheckBox(client.text.getString("Host"));
		hostCheckBox.setFont(genericFont);
		hostCheckBox.setBounds(124, 349, 82, 15);
		pReg.add(hostCheckBox);


		JButton btnReg1 = new JButton(client.text.getString("Register"));
		btnReg1.addActionListener((e) -> {
			String password = new String(tfPassword1.getPassword());
			String password2 = new String(tfRepeat.getPassword());
			if (password.equals(password2)) {
				client.register(tfName1.getText(), tfUserName1.getText(), tfEmail.getText(), tfPhone.getText(), new String(tfPassword1.getPassword()), hostCheckBox.isSelected());
				client.switchLogin();
			} else {
				JOptionPane.showMessageDialog(client.getWindow(), "Passwords do not match", "Alert", JOptionPane.WARNING_MESSAGE);
			}
		});
		btnReg1.setBounds(311, 341, 89, 23);
		pReg.add(btnReg1);

		//JLabel imglabel = new JLabel(new ImageIcon(Client.class.getResource("/imgs/p3.png")));
		//imglabel.setBounds(0, 115, 434, 132);
		//pReg.add(imglabel);
		
		return pReg;
	}

	/**
	 * Creates the property search panel.
	 * 
	 * @param client Session of the client application
	 * @param id 
	 * @param kind 
	 * @return JPanel of the property search
	 */
	public static JPanel createPropertySearch(Client client, String id, UserKind kind) {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());

		// @Todo: Put this elements pretty

		JLabel searchLabel = new JLabel(client.text.getString("City"));
		JTextField citySearch = new JTextField();
		citySearch.setColumns(20);
		JButton searchButton = new JButton(client.text.getString("Search"));
		JList<Property> searchResults = new JList<Property>();

		searchButton.addActionListener((e) -> {
			client.searchPropertiesByCity(citySearch.getText(), searchResults);			
		});

		JPanel top = new JPanel();

		top.add(searchLabel);
		top.add(citySearch);
		top.add(searchButton);

		JPanel bottom = new JPanel();

		JButton deleteButton = new JButton(client.text.getString("Delete"));
		deleteButton.addActionListener((e) -> {
			client.deleteProperty(searchResults.getSelectedValue());
			JOptionPane.showMessageDialog(client.getWindow(), "The property has been successfully deleted.", "Information", JOptionPane.INFORMATION_MESSAGE, null);
		});

		JButton editButton = new JButton(client.text.getString("Edit"));
		editButton.addActionListener((e) -> {
			client.switchPropertyEdit(searchResults.getSelectedValue(), kind, id);
		});

		JButton backButton = new JButton(client.text.getString("Back"));
		backButton.addActionListener((e) -> {
			client.createMainWindowAdmin(id, kind);
		});

		bottom.add(deleteButton);
		bottom.add(editButton);
		bottom.add(backButton);

		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);

		return result;
	}

	/**
	 * Creates the JPanel of administrators account management
	 * 
	 * @param client Session of the client application
	 * @param id
	 * @param kind
	 * @return JPanel of the administrators account management
	 */
	public static JPanel createAdminAccountManagement(Client client, String id, UserKind kind) {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());

		// @Todo: Put this pretty

		JLabel searchLabel = new JLabel(client.text.getString("Username"));
		JTextField userSearch = new JTextField();
		userSearch.setColumns(20);
		JButton searchButton = new JButton(client.text.getString("Search"));
		JList<User> searchResults = new JList<User>();

		searchButton.addActionListener((e) -> {
			client.searchUsers(userSearch.getText(), searchResults);
		});

		JPanel top = new JPanel();
		top.add(searchLabel);
		top.add(userSearch);
		top.add(searchButton);


		JPanel bottom = new JPanel();

		JButton deleteButton = new JButton(client.text.getString("Delete"));
		deleteButton.addActionListener((e) -> {
			client.deleteAccount(searchResults.getSelectedValue());
			JOptionPane.showMessageDialog(client.getWindow(), "The account has been successfully deleted.", "Information", JOptionPane.INFORMATION_MESSAGE, null);
		});

		JButton editButton = new JButton(client.text.getString("Edit"));
		editButton.addActionListener((e) -> {
			client.switchAdminAccountEdit(searchResults.getSelectedValue(), id, kind);
		});

		JButton newButton = new JButton(client.text.getString("New"));
		newButton.addActionListener((e) -> {
			client.switchAdminAccountNew(id, kind);
		});
		JButton backButton = new JButton(client.text.getString("Back"));
		backButton.addActionListener((e) -> {
			client.createMainWindowAdmin(id, kind);
		});

		bottom.add(deleteButton);
		bottom.add(editButton);
		bottom.add(newButton);
		bottom.add(backButton);

		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);


		return result;
	}

	/**
	 * Creates the panel of account administration used by administrators.
	 * 
	 * @param client Session of the client application
	 * @param selectedUser User account to edit
	 * @param isNewUser Whether we are creating a new user or editing an existing one.
	 * @param id
	 * @param kind
	 * @return Panel with all the fields to modify/create user accounts
	 */
	public static JPanel createAdminAccountEdit(Client client, User selectedUser, boolean isNewUser, String id, UserKind kind) {

		// @Todo: Instead of having 'isNewUser' could we just check if 'selectedUser' is not null?

		// @Todo: Enable/disable fields based on the selected UserKind

		JPanel result = new JPanel();
		//result.setBounds(0, 0, 434, 209);
		result.setLayout(null);

		// @Copied and adapted from createRegisterWindow
		// We could @Refactor the things both methods have in common

		JLabel lblImg = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		lblImg.setBounds(10, 11, 414, 93);
		result.add(lblImg);

		JLabel lblName1 = new JLabel(client.text.getString("Name")+":");
		lblName1.setFont(genericFont);
		lblName1.setBounds(20, 127, 62, 15);
		result.add(lblName1);
		
		JTextField tfName1 = new JTextField();
		tfName1.setColumns(10);
		tfName1.setBounds(89, 125, 118, 20);
		tfName1.setText(selectedUser != null ? selectedUser.getName() : "");
		if(selectedUser.getKind() == UserKind.ADMINISTRATOR) {
			tfName1.setEnabled(false);
		}
		result.add(tfName1);

		JLabel lblUserName1 = new JLabel(client.text.getString("Username")+":");
		lblUserName1.setFont(genericFont);
		lblUserName1.setBounds(20, 153, 72, 15);
		result.add(lblUserName1);

		JTextField tfUserName1 = new JTextField(selectedUser != null ? selectedUser.getUsername() : "");
		tfUserName1.setColumns(10);
		tfUserName1.setBounds(89, 153, 118, 20);
		tfUserName1.setEnabled(isNewUser);
		result.add(tfUserName1);

		JLabel lblEmail = new JLabel(client.text.getString("Email")+":");
		lblEmail.setFont(genericFont);
		lblEmail.setBounds(20, 181, 82, 15);
		result.add(lblEmail);

		JTextField tfEmail = new JTextField();
		tfEmail.setColumns(10);
		tfEmail.setBounds(89, 179, 118, 20);
		tfEmail.setText(selectedUser != null ? selectedUser.getEmail() : "");
		if(selectedUser.getKind() == UserKind.ADMINISTRATOR) {
			tfEmail.setEnabled(false);
		}
		result.add(tfEmail);

		JLabel lblPhone = new JLabel(client.text.getString("Phone")+":");
		lblPhone.setFont(genericFont);
		lblPhone.setBounds(212, 127, 62, 15);
		result.add(lblPhone);

		JTextField tfPhone = new JTextField();
		tfPhone.setColumns(10);
		tfPhone.setBounds(292, 125, 118, 20);
		tfPhone.setText(selectedUser != null ? selectedUser.getTelephone() : "");
		if(selectedUser.getKind() == UserKind.ADMINISTRATOR) {
			tfPhone.setEnabled(false);
		}
		result.add(tfPhone);

		JLabel lblUserkind = new JLabel(client.text.getString("Kind"));
		lblUserkind.setFont(genericFont);
		lblUserkind.setBounds(20, 25, 118, 20);
		result.add(lblUserkind);

		JComboBox<User.UserKind> userkindCombo = new JComboBox<User.UserKind>(User.UserKind.values());
		userkindCombo.setSelectedItem(selectedUser != null ? selectedUser.getKind() : User.UserKind.GUEST);
		userkindCombo.setBounds(89, 25, 118, 20);
		result.add(userkindCombo);

		JLabel lblVerified = new JLabel(client.text.getString("Verified"));
		lblVerified.setFont(genericFont);
		lblVerified.setBounds(20, 55, 118, 20);
		result.add(lblVerified);

		JCheckBox verifiedCheckBox = new JCheckBox();
		verifiedCheckBox.setBounds(89, 55, 118, 20);
		verifiedCheckBox.setSelected(selectedUser != null ? selectedUser.isVerified() : false);
		result.add(verifiedCheckBox);

		JPasswordField tfPassword1 = new JPasswordField();
		tfPassword1.setColumns(10);
		tfPassword1.setBounds(292, 151, 118, 20);
		tfPassword1.setText(selectedUser != null ? selectedUser.getPassword() : "");
		result.add(tfPassword1);

		JLabel lblPassword1 = new JLabel(client.text.getString("Password")+":");
		lblPassword1.setFont(genericFont);
		lblPassword1.setBounds(212, 153, 59, 15);
		result.add(lblPassword1);

		JLabel lblRepeat = new JLabel(client.text.getString("Repeat_Password")+":");
		lblRepeat.setFont(genericFont);
		lblRepeat.setBounds(212, 181, 82, 15);
		result.add(lblRepeat);

		JPasswordField tfRepeat = new JPasswordField();
		tfRepeat.setColumns(10);
		tfRepeat.setBounds(292, 179, 118, 20);
		tfRepeat.setText(selectedUser != null ? selectedUser.getPassword() : "");
		result.add(tfRepeat);


		JButton btnBack1 = new JButton(client.text.getString("Back"));
		btnBack1.addActionListener( (e) -> {
			client.switchAdminAccountManagment(id, kind);
		});
		btnBack1.setBounds(212, 210, 89, 23);
		result.add(btnBack1);

		JButton btnUpdate = new JButton(client.text.getString("Update"));
		btnUpdate.addActionListener((e) -> {
			String password = new String(tfPassword1.getPassword());
			String password2 = new String(tfRepeat.getPassword());
			if (password.equals(password2)) {
				if (!isNewUser) {
					client.adminUpdateAccount(tfUserName1.getText(), password,
							(User.UserKind) userkindCombo.getSelectedItem(),
							tfPhone.getText(), tfEmail.getText(), tfUserName1.getText(),
							verifiedCheckBox.isSelected()); // @Todo: Read from the check box
				} else {
					client.adminUpdateAccount(tfUserName1.getText(), password,
							(User.UserKind) userkindCombo.getSelectedItem(),
							tfPhone.getText(), tfEmail.getText(), tfUserName1.getText(),
							verifiedCheckBox.isSelected()); // @Todo: Read from the check box
				}
			} else {
				JOptionPane.showMessageDialog(client.getWindow(), "Passwords do not match", "Alert", JOptionPane.WARNING_MESSAGE);
			}
		});
		btnUpdate.setBounds(311, 210, 89, 23);
		result.add(btnUpdate);

		//JLabel labelimg2 = new JLabel(new ImageIcon(Client.class.getResource("/imgs/p1.png")));
		//labelimg2.setBounds(0, 103, 450, 147);
		//result.add(labelimg2);
		
		return result;
	}

	/**
	 * 
	 * @param client
	 * @param name
	 * @param kind
	 * @return
	 */
	public static JPanel createHostPropertiesManagement(Client client, String name, UserKind kind) {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());

		JList<Property> searchResults = new JList<Property>();
		client.searchPropertiesHost(searchResults, name);

		JPanel top = new JPanel();
		JLabel txt = new JLabel(name + ", these are your properties");
		top.add(txt);

		JPanel bottom = new JPanel();

		JButton deleteButton = new JButton(client.text.getString("Delete"));
		deleteButton.addActionListener((e) -> {
			client.deleteProperty(searchResults.getSelectedValue());
		});

		JButton editButton = new JButton(client.text.getString("Edit"));
		editButton.addActionListener((e) -> {
			client.switchPropertyEdit(searchResults.getSelectedValue(), kind, name);
		});

		JButton newButton = new JButton(client.text.getString("New"));
		newButton.addActionListener((e) -> {
			client.switchHostPropertyNew(name, kind);
		});

		JButton backButton = new JButton(client.text.getString("Back"));
		backButton.addActionListener((e) -> {
			client.createMainWindowHost(name, kind);
		});

		bottom.add(deleteButton);
		bottom.add(editButton);
		bottom.add(newButton);
		bottom.add(backButton);

		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);

		return result;
	}

	/**
	 * 
	 * @param client
	 * @param name
	 * @param kind
	 * @return
	 */
	public static JPanel createGuestReservationList(Client client, String name, UserKind kind) {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());

		JList<Reservation> searchResults = new JList<Reservation>();
		client.searchReservationsByGuest(name, searchResults);

		JPanel top = new JPanel();
		JLabel txt = new JLabel(name + ", these are your Reservations");
		top.add(txt);

		JPanel bottom = new JPanel();

		JButton deleteButton = new JButton(client.text.getString("Delete"));
		deleteButton.addActionListener((e) -> {
			client.deleteReservation(searchResults.getSelectedValue());
		});

		JButton editButton = new JButton(client.text.getString("Edit"));
		editButton.addActionListener((e) -> {
			client.switchReservationEdit(searchResults.getSelectedValue(), kind, name);
		});

		JButton backButton = new JButton(client.text.getString("Back"));
		backButton.addActionListener((e) -> {
			client.createMainWindowGuest(name, kind);
		});

		bottom.add(deleteButton);
		bottom.add(editButton);

		bottom.add(backButton);

		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);

		return result;
	}

	/**
	 * 
	 * @param client
	 * @param name
	 * @param kind
	 * @return
	 */
	public static JPanel createGuestPropertiesManagement(Client client, String name, UserKind kind) {
		// @Copied and adapted from createPropertySearch
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());

		// @Todo: Put this elements pretty

		JLabel searchLabel = new JLabel(client.text.getString("City"));
		JTextField citySearch = new JTextField();
		citySearch.setColumns(20);
		JButton searchButton = new JButton(client.text.getString("Search"));
		JList<Property> searchResults = new JList<Property>();

		searchButton.addActionListener((e) -> {
			client.searchPropertiesByCity(citySearch.getText(), searchResults);
		});

		JPanel top = new JPanel();

		top.add(searchLabel);
		top.add(citySearch);
		top.add(searchButton);

		JPanel bottom = new JPanel();

		JButton bookButton = new JButton(client.text.getString("Book"));
		bookButton.addActionListener((e) -> {
			client.switchGuestBookProperty(name, searchResults.getSelectedValue(), kind);
		});

		JButton backButton = new JButton(client.text.getString("Back"));
		backButton.addActionListener((e) -> {
			client.createMainWindowGuest(name, kind);
		});

		bottom.add(bookButton);
		bottom.add(backButton);

		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);

		return result;	
	}

	/**
	 * 
	 * @param client
	 * @param name
	 * @param property
	 * @param kind
	 * @return
	 */
	public static JPanel createGuestBookProperty(Client client, String name, Property property, UserKind kind) {
		// @Todo: Put this pretty
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());

		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

		JLabel lblStartDate = new JLabel(client.text.getString("Start_date")+":");
		lblStartDate.setFont(genericFont);

		JDateChooser dateChooserStart = new JDateChooser();
		dateChooserStart.setDateFormatString("dd/MM/yyyy");

		JLabel lblEndDate = new JLabel(client.text.getString("End_date")+":");
		lblEndDate.setFont(genericFont);

		JDateChooser dateChooserEnd = new JDateChooser();
		dateChooserEnd.setDateFormatString("dd/MM/yyyy");

		center.add(lblStartDate);
		center.add(dateChooserStart);
		center.add(lblEndDate);
		center.add(dateChooserEnd);

		JPanel south = new JPanel();

		JButton btnBack = new JButton(client.text.getString("Back"));
		btnBack.addActionListener( (e) -> {
			client.switchGuestPropertiesManagement(name, kind);
		});
		south.add(btnBack);

		JButton btnConfirm = new JButton(client.text.getString("Confirm"));
		btnConfirm.addActionListener((e) -> {
			if(dateChooserStart.getDate() != null && dateChooserEnd.getDate() != null) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String startDate = df.format(dateChooserStart.getDate());
				String endDate = df.format(dateChooserEnd.getDate());
				client.bookProperty(name, property, startDate, endDate);
			} else {
				JOptionPane.showMessageDialog(client.getWindow(), "You can not leave empty fields.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			}
		});
		south.add(btnConfirm);

		result.add(center, BorderLayout.CENTER);
		result.add(south, BorderLayout.SOUTH);

		return result;
	}

	/**
	 * Creates a panel to edit/create a property.
	 * 
	 * @param client Session of the client application
	 * @param selectedProp Property to edit
	 * @param isNewProperty Whether to edit an existing property or create a new one
	 * @param kind
	 * @param id
	 * @return JPanel with all the elements to edit/create properties
	 */
	public static JPanel createPropertyEdit(Client client, Property selectedProp, boolean isNewProperty, UserKind kind, String id) {
		JPanel result = new JPanel();
		result.setBounds(0, 0, 436, 260);
		result.setLayout(null);

		JLabel lblAddress = new JLabel(client.text.getString("Address")+":");
		lblAddress.setFont(genericFont);
		lblAddress.setBounds(20, 115, 62, 15);
		result.add(lblAddress);

		JLabel imgBanner1 = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		imgBanner1.setBounds(10, 11, 414, 93);
		result.add(imgBanner1);

		JTextField tfAddress = new JTextField(selectedProp != null ? selectedProp.getAddress() : "");
		tfAddress.setColumns(10);
		tfAddress.setBounds(20, 141, 391, 20);
		tfAddress.setEnabled(false);
		result.add(tfAddress);

		JLabel lblCity = new JLabel(client.text.getString("City")+":");
		lblCity.setFont(genericFont);
		lblCity.setBounds(20, 172, 72, 15);
		result.add(lblCity);

		JTextField tfCity = new JTextField();
		tfCity.setColumns(10);
		tfCity.setBounds(20, 198, 118, 20);
		tfCity.setEnabled(false);
		tfCity.setText(selectedProp != null ? selectedProp.getCity() : "");
		result.add(tfCity);

		JLabel lblCapacity = new JLabel(client.text.getString("Capacity")+":");
		lblCapacity.setFont(genericFont);
		lblCapacity.setBounds(156, 172, 82, 15);
		result.add(lblCapacity);

		JTextField tfCapacity = new JTextField();
		tfCapacity.setColumns(10);
		tfCapacity.setBounds(156, 198, 118, 20);
		tfCapacity.setText(selectedProp != null ? Integer.toString(selectedProp.getCapacity()) : "");
		result.add(tfCapacity);

		JLabel lblCost = new JLabel(client.text.getString("Cost")+":");
		lblCost.setFont(genericFont);
		lblCost.setBounds(293, 172, 62, 15);
		result.add(lblCost);

		JTextField tfCost = new JTextField();
		tfCost.setColumns(10);
		tfCost.setBounds(293, 198, 118, 20);
		tfCost.setText(selectedProp != null ? Double.toString(selectedProp.getCost()) : "");
		result.add(tfCost);

		JButton btnBack = new JButton(client.text.getString("Back"));
		btnBack.addActionListener( (e) -> {
			if(kind.equals(UserKind.ADMINISTRATOR)) {
				client.switchPropertiesSearch(id, kind);
			} else if (kind.equals(UserKind.HOST)) {
				client.switchHostPropertiesManagement(selectedProp.getHost().getUsername(), kind);
			}
		});
		btnBack.setBounds(223, 229, 89, 23);
		result.add(btnBack);

		JButton btnUpdate = new JButton(client.text.getString("Update"));
		btnUpdate.addActionListener((e) -> {
			if(!tfCapacity.getText().isEmpty() && !tfCost.getText().isEmpty()) {
				client.updateProperty(tfAddress.getText(), Integer.parseInt(tfCapacity.getText()), Double.parseDouble(tfCost.getText()));
			} else {
				JOptionPane.showMessageDialog(client.getWindow(), "You can not leave empty fields.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			}
		});
		btnUpdate.setBounds(322, 229, 89, 23);
		result.add(btnUpdate);	

		//JLabel labelBe = new JLabel(new ImageIcon(Client.class.getResource("/imgs/p6.png")));
		//labelBe.setBounds(0, 104, 434, 146);
		//result.add(labelBe);
		
		return result;
	}

	/**
	 * Creates a JPanel to edit reservations.
	 * 
	 * @param client Session of the client application
	 * @param selectedReserv Reservation to edit
	 * @param isReserv
	 * @param kind
	 * @param id
	 * @return
	 */
	public static JPanel createReservationEdit(Client client, Reservation selectedReserv, boolean isReserv, UserKind kind, String id) {
		JPanel result = new JPanel();
		result.setBounds(0, 0, 434, 209);
		result.setLayout(null);

		JLabel lblProperty = new JLabel(client.text.getString("Property")+":");
		lblProperty.setFont(genericFont);
		lblProperty.setBounds(20, 127, 62, 15);
		result.add(lblProperty);

		JTextField tfProperty = new JTextField(selectedReserv != null ? selectedReserv.getProperty().getAddress() : "");
		tfProperty.setColumns(10);
		tfProperty.setBounds(89, 125, 118, 20);
		tfProperty.setEnabled(false);
		result.add(tfProperty);

		JLabel imgBanner1 = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		imgBanner1.setBounds(10, 11, 414, 93);
		result.add(imgBanner1);

		JLabel lblGuest = new JLabel(client.text.getString("Name")+":");
		lblGuest.setFont(genericFont);
		lblGuest.setBounds(20, 153, 72, 15);
		result.add(lblGuest);

		JTextField tfGuest = new JTextField();
		tfGuest.setColumns(10);
		tfGuest.setBounds(89, 153, 118, 20);
		tfGuest.setText(selectedReserv != null ? selectedReserv.getGuest().getName() : "");
		tfGuest.setEnabled(false);
		result.add(tfGuest);

		JLabel lblStartDate = new JLabel(client.text.getString("Start_date")+":");
		lblStartDate.setFont(genericFont);
		lblStartDate.setBounds(20, 181, 82, 15);
		result.add(lblStartDate);
		
		JDateChooser dateChooserStart = new JDateChooser();
		dateChooserStart.setBounds(89, 179, 118, 20);
		result.add(dateChooserStart);
		dateChooserStart.setDateFormatString("dd/MM/yyyy");

		JLabel lblEndDate = new JLabel(client.text.getString("End_date")+":");
		lblEndDate.setFont(genericFont);
		lblEndDate.setBounds(212, 127, 62, 15);
		result.add(lblEndDate);
		
		JDateChooser dateChooserEnd = new JDateChooser();
		dateChooserEnd.setBounds(292, 125, 118, 20);
		result.add(dateChooserEnd);
		dateChooserEnd.setDateFormatString("dd/MM/yyyy");

		JButton btnBack = new JButton(client.text.getString("Back"));
		btnBack.addActionListener( (e) -> {
			if(kind.equals(UserKind.ADMINISTRATOR)) {
				client.switchAdminReservationsSearch(id, kind);
			} else if (kind.equals(UserKind.GUEST)) {
				client.switchGuestReservationsList(id, kind);
			}
		});
		btnBack.setBounds(212, 210, 89, 23);
		result.add(btnBack);

		JButton btnUpdate = new JButton(client.text.getString("Update"));
		btnUpdate.addActionListener((e) -> {
			if(dateChooserStart.getDate() != null && dateChooserEnd.getDate() != null) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String startDate = df.format(dateChooserStart.getDate());
				String endDate = df.format(dateChooserEnd.getDate());
				client.updateReservation(selectedReserv.getProperty(), selectedReserv.getGuest(), selectedReserv.getStartDate(), startDate, endDate);
			} else {
				JOptionPane.showMessageDialog(client.getWindow(), "You can not leave empty fields.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			}
		});
		btnUpdate.setBounds(311, 210, 89, 23);
		result.add(btnUpdate);	
		
		//JLabel labelBRE = new JLabel(new ImageIcon(Client.class.getResource("/imgs/p7.png")));
		//labelBRE.setBounds(0, 0, 414, 93);
		//result.add(labelBRE);
		
		return result;
	}
	
	/**
	 * 
	 * @param client Session of the client application
	 * @param kind
	 * @param name
	 * @return
	 */
	public static JPanel createHostPropertyNew(Client client, String name, UserKind kind) {
		JPanel result = new JPanel();
		result.setBounds(0, 0, 434, 209);
		result.setLayout(null);
		
		JLabel imgBanner1 = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		imgBanner1.setBounds(10, 11, 414, 93);
		result.add(imgBanner1);

		JLabel lblAddress = new JLabel(client.text.getString("Address")+":");
		lblAddress.setFont(genericFont);
		lblAddress.setBounds(20, 115, 62, 15);
		result.add(lblAddress);

		JTextField tfAddress = new JTextField();
		tfAddress.setColumns(10);
		tfAddress.setBounds(20, 141, 391, 20);
		result.add(tfAddress);

		JLabel lblCity = new JLabel(client.text.getString("City")+":");
		lblCity.setFont(genericFont);
		lblCity.setBounds(20, 172, 72, 15);
		result.add(lblCity);

		JTextField tfCity = new JTextField();
		tfCity.setColumns(10);
		tfCity.setBounds(20, 198, 118, 20);
		result.add(tfCity);

		JLabel lblCapacity = new JLabel(client.text.getString("Capacity")+":");
		lblCapacity.setFont(genericFont);
		lblCapacity.setBounds(156, 172, 82, 15);
		result.add(lblCapacity);

		JTextField tfCapacity = new JTextField();
		tfCapacity.setColumns(10);
		tfCapacity.setBounds(156, 198, 118, 20);
		result.add(tfCapacity);

		JLabel lblCost = new JLabel(client.text.getString("Cost")+":");
		lblCost.setFont(genericFont);
		lblCost.setBounds(293, 172, 62, 15);
		result.add(lblCost);

		JTextField tfCost = new JTextField();
		tfCost.setColumns(10);
		tfCost.setBounds(293, 198, 118, 20);
		result.add(tfCost);

		JButton btnBack = new JButton(client.text.getString("Back"));
		btnBack.addActionListener( (e) -> {
			client.switchHostPropertiesManagement(name, kind);
		});
		btnBack.setBounds(223, 229, 89, 23);
		result.add(btnBack);

		JButton btnPublish = new JButton(client.text.getString("Publish"));
		btnPublish.addActionListener((e) -> {
			if(!tfAddress.getText().isEmpty() && !tfCity.getText().isEmpty() && !tfCapacity.getText().isEmpty() && !tfCost.getText().isEmpty()) {
				client.publishProperty(tfAddress.getText(), tfCity.getText(), Integer.parseInt(tfCapacity.getText()), Double.parseDouble(tfCost.getText()), name);
				client.switchHostPropertiesManagement(name, kind);
			} else {
				JOptionPane.showMessageDialog(client.getWindow(), "You can not leave empty fields.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			}
		});
		btnPublish.setBounds(322, 229, 89, 23);
		result.add(btnPublish);	

		//JLabel imglabel = new JLabel(new ImageIcon(Client.class.getResource("/imgs/p2.png")));
		//imglabel.setBounds(0, 0, 434, 250);
		//result.add(imglabel);
		
		return result;
	}
	
	/**
	 * 
	 * @param client Session of the client application
	 * @param kind
	 * @param name
	 * @return
	 */
	public static JPanel createAccountManagement(Client client, String name, UserKind kind) {
		User user = client.getUser(name);
		
		JPanel result = new JPanel();
		result.setBounds(0, 0, 434, 310);
		result.setLayout(null);

		JLabel imgBanner1 = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		imgBanner1.setBounds(10, 11, 414, 93);
		result.add(imgBanner1);
		
		JLabel lblUsername = new JLabel(client.text.getString("Username")+":");
		lblUsername.setFont(genericFont);
		lblUsername.setBounds(48, 115, 62, 15);
		result.add(lblUsername);

		JTextField tfUsername = new JTextField();
		tfUsername.setColumns(10);
		tfUsername.setBounds(48, 141, 95, 20);
		tfUsername.setText(user.getUsername());
		tfUsername.setEnabled(false);
		result.add(tfUsername);

		JLabel lblGuest = new JLabel(client.text.getString("Name")+":");
		lblGuest.setFont(genericFont);
		lblGuest.setBounds(160, 115, 72, 15);
		result.add(lblGuest);

		JTextField tfGuest = new JTextField();
		tfGuest.setColumns(10);
		tfGuest.setBounds(160, 141, 95, 20);
		tfGuest.setText(user.getName());
		tfGuest.setEnabled(false);
		result.add(tfGuest);

		JLabel lblPhone = new JLabel(client.text.getString("Telephone")+":");
		lblPhone.setFont(genericFont);
		lblPhone.setBounds(276, 115, 82, 15);
		result.add(lblPhone);

		JTextField tfPhone = new JTextField();
		tfPhone.setColumns(10);
		tfPhone.setBounds(276, 141, 95, 20);
		tfPhone.setText(user.getTelephone());
		result.add(tfPhone);

		JLabel lblEmail = new JLabel(client.text.getString("Email")+":");
		lblEmail.setFont(genericFont);
		lblEmail.setBounds(48, 172, 62, 15);
		result.add(lblEmail);

		JTextField tfEmail = new JTextField();
		tfEmail.setColumns(10);
		tfEmail.setBounds(48, 192, 352, 20);
		tfEmail.setText(user.getEmail());
		tfEmail.setEnabled(false);
		result.add(tfEmail);

		JLabel lblPass = new JLabel(client.text.getString("Password")+":");
		lblPass.setFont(genericFont);
		lblPass.setBounds(48, 227, 62, 15);
		result.add(lblPass);

		JPasswordField tfPass = new JPasswordField();
		tfPass.setColumns(10);
		tfPass.setBounds(48, 245, 352, 20);
		tfPass.setText(user.getPassword());
		result.add(tfPass);

		JButton btnBack = new JButton(client.text.getString("Back"));
		btnBack.addActionListener( (e) -> {
			if(kind.equals(UserKind.GUEST)) {
				client.createMainWindowGuest(name, kind);
			} else if (kind.equals(UserKind.HOST)) {
				client.createMainWindowHost(name, kind);
			}
		});
		btnBack.setBounds(213, 276, 89, 23);
		result.add(btnBack);

		JButton btnUpdate = new JButton(client.text.getString("Update"));
		btnUpdate.addActionListener((e) -> {
			if(!new String(tfPass.getPassword()).isEmpty() && !tfPhone.getText().isEmpty()) {
				client.changeAccountData(name, new String(tfPass.getPassword()), tfPhone.getText());
			} else {
				JOptionPane.showMessageDialog(client.getWindow(), "You can not leave empty fields.", "Alert", JOptionPane.WARNING_MESSAGE, null);
			}
		});
		btnUpdate.setBounds(311, 276, 89, 23);
		result.add(btnUpdate);	
		
		//JLabel labelB = new JLabel(new ImageIcon(Client.class.getResource("/imgs/p4.png")));
		//labelB.setBounds(0, 106, 434, 136);
		//result.add(labelB);
		
		return result;
	}

	/**
	 * 
	 * @param client Session of the client application
	 * @param kind
	 * @param id
	 * @return
	 */
	public static JPanel createAdminReservationsSearch(Client client, String id, UserKind kind) {
		//// @Copied and adapted createPropertySearch

		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());

		// @Todo: Put this elements pretty

		JLabel searchLabel = new JLabel(client.text.getString("City"));
		JTextField citySearch = new JTextField();
		citySearch.setColumns(20);
		JButton searchButton = new JButton(client.text.getString("Search"));
		JList<Reservation> searchResults = new JList<Reservation>();

		searchButton.addActionListener((e) -> {
			client.searchReservationsByCity(citySearch.getText(), searchResults);			
		});

		JPanel top = new JPanel();

		top.add(searchLabel);
		top.add(citySearch);
		top.add(searchButton);

		JPanel bottom = new JPanel();

		JButton deleteButton = new JButton(client.text.getString("Delete"));
		deleteButton.addActionListener((e) -> {
			client.deleteReservation(searchResults.getSelectedValue());
		});

		JButton editButton = new JButton(client.text.getString("Edit"));
		editButton.addActionListener((e) -> {
			client.switchReservationEdit(searchResults.getSelectedValue(), kind, id);
		});

		JButton backButton = new JButton(client.text.getString("Back"));
		backButton.addActionListener((e) -> {
			client.createMainWindowAdmin(id, kind);
		});

		bottom.add(deleteButton);
		bottom.add(editButton);
		bottom.add(backButton);

		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);

		return result;


	}

	/**
	 * Creates the main panel of administrators
	 * 
	 * @param client Session of the client application
	 * @param id Name of the administrator to appear in the panel 
	 * @return JPanel of the main panel of administrators
	 */
	public static JPanel createMainWindowAdmin(Client client, String id, UserKind kind) {
		JPanel main_panel = new JPanel();

		JLabel lblImg = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		main_panel.add(lblImg);

		JLabel label = new JLabel(client.text.getString("Welcome_administrator")+" " + id);

		JButton properties = new JButton(client.text.getString("Properties"));
		properties.addActionListener((e) -> {client.switchPropertiesSearch(id, kind);});

		JButton accounts = new JButton(client.text.getString("Accounts"));
		accounts.addActionListener((e) -> {client.switchAdminAccountManagment(id, kind);});

		JButton reservation = new JButton(client.text.getString("Reservations"));
		reservation.addActionListener((e) -> {client.switchAdminReservationsSearch(id, kind);});

		JButton logOut = new JButton(client.text.getString("Log_Out"));
		logOut.addActionListener((e) -> {client.switchLogin();});

		main_panel.add(label);
		main_panel.add(properties);
		main_panel.add(accounts);
		main_panel.add(reservation);
		main_panel.add(logOut);

		return main_panel;
	}

	/**
	 * Creates the main panel of hosts.
	 * 
	 * @param client Session of the client application
	 * @param name Name of the host to appear in the panel
	 * @return JPanel of the main panel for hosts
	 */
	public static JPanel createMainWindowHost(Client client, String name, UserKind kind) {
		// @Copied and adapted from createMainWindowAdmin
		JPanel main_panel = new JPanel();

		JLabel lblImg = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		main_panel.add(lblImg);

		JLabel label = new JLabel(client.text.getString("Welcome_host") + " " + name);

		JButton properties = new JButton(client.text.getString("Properties"));
		properties.addActionListener((e) -> {client.switchHostPropertiesManagement(name, kind);});

		JButton account_data = new JButton(client.text.getString("Account_data"));
		account_data.addActionListener((e) -> {client.switchAccountManagement(name, kind);});

		JButton logOut = new JButton(client.text.getString("Log_Out"));
		logOut.addActionListener((e) -> {client.switchLogin();});

		main_panel.add(label);
		main_panel.add(properties);
		main_panel.add(account_data);
		main_panel.add(logOut);

		return main_panel;
	}

	/**
	 * Creates the main window of guests
	 * 
	 * @param client Session of the client application
	 * @param name Name of the guest to appear in the panel
	 * @return JPanel of the main panel of guests
	 */
	public static JPanel createMainWindowGuest(Client client, String name, UserKind kind) {
		// @Copied and adapted from createMainWindowAdmin
		JPanel main_panel = new JPanel();

		JLabel lblImg = new JLabel(new ImageIcon(Client.class.getResource("/imgs/banner.png")));
		main_panel.add(lblImg);

		JLabel label = new JLabel(client.text.getString("Welcome_guest") + " " + name);

		JButton book = new JButton(client.text.getString("Book"));
		book.addActionListener((e) -> {client.switchGuestPropertiesManagement(name, kind);});

		JButton account_data = new JButton(client.text.getString("Account_data"));
		account_data.addActionListener((e) -> {client.switchAccountManagement(name, kind);});

		JButton reservations = new JButton(client.text.getString("Reservations"));
		reservations.addActionListener((e) -> {client.switchGuestReservationsList(name, kind);});

		JButton logOut = new JButton(client.text.getString("Log_Out"));
		logOut.addActionListener((e) -> {client.switchLogin();});

		main_panel.add(label);
		main_panel.add(book);
		main_panel.add(account_data);
		main_panel.add(reservations);
		main_panel.add(logOut);

		return main_panel;
	}

}

