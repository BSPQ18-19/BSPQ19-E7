package es.deusto.client;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.Reservation;
import es.deusto.server.jdo.User;

public class PanelBuilder {

	public static JPanel createLogin(Client client) {
		
		// @Todo @Performance Avoid creating the font each time
		
		
		JPanel pLogin = new JPanel();
		pLogin.setBounds(0, 0, 434, 209);
		pLogin.setLayout(null);
		
		JLabel lblUsername = new JLabel(client.text.getString("Username")+ ":");
		lblUsername.setBounds(20, 127, 62, 15);
		pLogin.add(lblUsername);
		lblUsername.setFont(new Font("Arial", Font.PLAIN, 12));
		
		JLabel lblPassword = new JLabel(client.text.getString("Password")+":");
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
		
		JButton btnLogin = new JButton(client.text.getString("Login"));
		btnLogin.addActionListener((e) -> {client.login(tfUser.getText(), new String(tfPass.getPassword()));});
		btnLogin.setBounds(193, 166, 89, 23);
		pLogin.add(btnLogin);
		
		JButton btnReg = new JButton(client.text.getString("Register"));
		btnReg.addActionListener( (e) -> {client.switchRegister();} );
		btnReg.setBounds(320, 166, 89, 23);
		pLogin.add(btnReg);
		return pLogin;
	}
	
	public static JPanel createRegisterWindow(Client client) {
		
		// @Todo @Performance Avoid creating the font each time
		
		
		JPanel pReg = new JPanel();
		pReg.setLayout(null);
		pReg.setBounds(0, 0, 434, 286);
		pReg.setVisible(true);
		
		JLabel lblName1 = new JLabel(client.text.getString("Name")+":");
		lblName1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblName1.setBounds(20, 127, 62, 15);
		pReg.add(lblName1);
		
		JLabel lblUserName1 = new JLabel(client.text.getString("Username")+":");
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
		
		JButton btnBack1 = new JButton(client.text.getString("Back"));
		btnBack1.addActionListener( (e) -> {client.switchLogin();} );
		btnBack1.setBounds(212, 210, 89, 23);
		pReg.add(btnBack1);
		
		JLabel lblEmail = new JLabel(client.text.getString("Email")+":");
		lblEmail.setFont(new Font("Arial", Font.PLAIN, 12));
		lblEmail.setBounds(20, 181, 82, 15);
		pReg.add(lblEmail);
		
		JTextField tfEmail = new JTextField();
		tfEmail.setColumns(10);
		tfEmail.setBounds(89, 179, 118, 20);
		pReg.add(tfEmail);
		
		JLabel lblPhone = new JLabel(client.text.getString("Phone")+":");
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
		
		JLabel lblPassword1 = new JLabel(client.text.getString("Password")+":");
		lblPassword1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblPassword1.setBounds(212, 153, 59, 15);
		pReg.add(lblPassword1);
		
		JLabel lblRepeat = new JLabel(client.text.getString("Repeat_Password")+":");
		lblRepeat.setFont(new Font("Arial", Font.PLAIN, 12));
		lblRepeat.setBounds(212, 181, 82, 15);
		pReg.add(lblRepeat);
		
		JCheckBox hostCheckBox = new JCheckBox(client.text.getString("Host"));
		hostCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
		hostCheckBox.setBounds(112, 210, 82, 15);
		pReg.add(hostCheckBox);
		
		JPasswordField tfRepeat = new JPasswordField();
		tfRepeat.setColumns(10);
		tfRepeat.setBounds(292, 179, 118, 20);
		pReg.add(tfRepeat);
		
		JButton btnReg1 = new JButton(client.text.getString("Register"));
		btnReg1.addActionListener((e) -> {client.register(tfName1.getText(), tfUserName1.getText(), tfEmail.getText(), tfPhone.getText(), new String(tfPassword1.getPassword()), hostCheckBox.isSelected());});
		btnReg1.setBounds(311, 210, 89, 23);
		pReg.add(btnReg1);
		
		return pReg;
	}
	
	public static JPanel createPropertySearch(Client client) {
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
		});
		
		JButton editButton = new JButton(client.text.getString("Edit"));
		editButton.addActionListener((e) -> {
			client.switchPropertyEdit(searchResults.getSelectedValue());
		});
		
		bottom.add(deleteButton);
		bottom.add(editButton);
		
		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);
		
		return result;
	}
	
	public static JPanel createAdminAccountManagement(Client client) {
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
		});
		
		JButton editButton = new JButton(client.text.getString("Edit"));
		editButton.addActionListener((e) -> {
			client.switchAdminAccountEdit(searchResults.getSelectedValue());
		});
		
		
		JButton newButton = new JButton(client.text.getString("New"));
		newButton.addActionListener((e) -> {
			client.switchAdminAccountNew();
		});
		
		bottom.add(deleteButton);
		bottom.add(editButton);
		bottom.add(newButton);
		
		
		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);
		
		
		return result;
	}
	
	public static JPanel createAdminAccountEdit(Client client, User selectedUser, boolean isNewUser) {
		
		// @Todo: Instead of having 'isNewUser' could we just check if 'selectedUser' is not null?
		
		// @Todo @Performance Avoid creating the font each time
		
		// @Todo: Enable/disable fields based on the selected UserKind
		
		JPanel result = new JPanel();
		//result.setBounds(0, 0, 434, 209);
		result.setLayout(null);
		
		// @Copied and adapted from createRegisterWindow
		// We could @Refactor the things both methods have in common
		
		JLabel lblName1 = new JLabel(client.text.getString("Name")+":");
		lblName1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblName1.setBounds(20, 127, 62, 15);
		result.add(lblName1);
		
		JLabel lblUserName1 = new JLabel(client.text.getString("Username")+":");
		lblUserName1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblUserName1.setBounds(20, 153, 72, 15);
		result.add(lblUserName1);
		
		JTextField tfUserName1 = new JTextField(selectedUser != null ? selectedUser.getUsername() : "");
		tfUserName1.setColumns(10);
		tfUserName1.setBounds(89, 153, 118, 20);
		tfUserName1.setEnabled(isNewUser);
		result.add(tfUserName1);
		
		JTextField tfName1 = new JTextField();
		tfName1.setColumns(10);
		tfName1.setBounds(89, 125, 118, 20);
		tfName1.setText(selectedUser != null ? selectedUser.getName() : "");
		result.add(tfName1);
		
		JLabel lblEmail = new JLabel(client.text.getString("Email")+":");
		lblEmail.setFont(new Font("Arial", Font.PLAIN, 12));
		lblEmail.setBounds(20, 181, 82, 15);
		result.add(lblEmail);
		
		JTextField tfEmail = new JTextField();
		tfEmail.setColumns(10);
		tfEmail.setBounds(89, 179, 118, 20);
		tfEmail.setText(selectedUser != null ? selectedUser.getEmail() : "");
		result.add(tfEmail);
		
		JLabel lblPhone = new JLabel(client.text.getString("Phone")+":");
		lblPhone.setFont(new Font("Arial", Font.PLAIN, 12));
		lblPhone.setBounds(212, 127, 62, 15);
		result.add(lblPhone);
		
		JTextField tfPhone = new JTextField();
		tfPhone.setColumns(10);
		tfPhone.setBounds(292, 125, 118, 20);
		tfPhone.setText(selectedUser != null ? selectedUser.getTelephone() : "");
		result.add(tfPhone);
		
		
		JLabel lblUserkind = new JLabel(client.text.getString("Kind"));
		lblUserkind.setFont(new Font("Arial", Font.PLAIN, 12));
		lblUserkind.setBounds(20, 25, 118, 20);
		result.add(lblUserkind);

		JComboBox<User.UserKind> userkindCombo = new JComboBox<User.UserKind>(User.UserKind.values());
		userkindCombo.setSelectedItem(selectedUser != null ? selectedUser.getKind() : User.UserKind.GUEST);
		userkindCombo.setBounds(89, 25, 118, 20);
		result.add(userkindCombo);
		
		JLabel lblVerified = new JLabel(client.text.getString("Verified"));
		lblVerified.setFont(new Font("Arial", Font.PLAIN, 12));
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
		lblPassword1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblPassword1.setBounds(212, 153, 59, 15);
		result.add(lblPassword1);
		
		JLabel lblRepeat = new JLabel(client.text.getString("Repeat_Password")+":");
		lblRepeat.setFont(new Font("Arial", Font.PLAIN, 12));
		lblRepeat.setBounds(212, 181, 82, 15);
		result.add(lblRepeat);
		
		JPasswordField tfRepeat = new JPasswordField();
		tfRepeat.setColumns(10);
		tfRepeat.setBounds(292, 179, 118, 20);
		tfRepeat.setText(selectedUser != null ? selectedUser.getPassword() : "");
		result.add(tfRepeat);
		
		
		JButton btnBack1 = new JButton(client.text.getString("Back"));
		// @Todo
		//btnBack1.addActionListener( (e) -> {client.switchLog(pReg);} );
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
				}
				else {
					client.adminUpdateAccount(tfUserName1.getText(), password,
							                  (User.UserKind) userkindCombo.getSelectedItem(),
							                  tfPhone.getText(), tfEmail.getText(), tfUserName1.getText(),
							                  verifiedCheckBox.isSelected()); // @Todo: Read from the check box
				}
			}
			else {
				// @Todo: Show some kind of error
			}
		});
		btnUpdate.setBounds(311, 210, 89, 23);
		result.add(btnUpdate);
		
		
		return result;
	}
	
	public static JPanel createHostPropertiesManagement(Client client, String name) {
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
			client.switchPropertyEdit(searchResults.getSelectedValue());
		});

		JButton newButton = new JButton(client.text.getString("New"));
		newButton.addActionListener((e) -> {
			client.switchHostPropertyNew(name);
		});
		
		JButton backButton = new JButton(client.text.getString("Back"));
		backButton.addActionListener((e) -> {
			client.createMainWindowHost(name);
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
	
	public static JPanel createGuestReservationList(Client client, String name) {
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
			client.switchReservationEdit(searchResults.getSelectedValue());
		});
	
		JButton backButton = new JButton(client.text.getString("Back"));
		backButton.addActionListener((e) -> {
			client.createMainWindowGuest(name);
		});

		bottom.add(deleteButton);
		bottom.add(editButton);

		bottom.add(backButton);

		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);

		return result;
	}
	
	public static JPanel createGuestPropertiesManagement(Client client, String name) {
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
			client.switchGuestBookProperty(name, searchResults.getSelectedValue());
		});
		
		JButton backButton = new JButton(client.text.getString("Back"));
		backButton.addActionListener((e) -> {
			client.createMainWindowGuest(name);
		});
		
		bottom.add(bookButton);
		bottom.add(backButton);
		
		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);
		
		return result;	
	}
	
	public static JPanel createGuestBookProperty(Client client, String name, Property property) {
		// @Todo: Put this pretty
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
		
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		
		JLabel lblDate = new JLabel(client.text.getString("Start_date")+":");
		lblDate.setFont(new Font("Arial", Font.PLAIN, 12));

		JTextField tfDate = new JTextField();
		tfDate.setColumns(10);
		
		JLabel lblDuration = new JLabel(client.text.getString("Duration_days")+":");
		lblDuration.setFont(new Font("Arial", Font.PLAIN, 12));

		JTextField tfDuration = new JTextField();
		tfDuration.setColumns(10);
		
		center.add(lblDate);
		center.add(tfDate);
		center.add(lblDuration);
		center.add(tfDuration);
		
		JPanel south = new JPanel();
		
		JButton btnBack = new JButton(client.text.getString("Back"));
		// TODO
		//btnBack.addActionListener( (e) -> {client.switchLog(pReg);} );
		south.add(btnBack);

		JButton btnConfirm = new JButton(client.text.getString("Confirm"));
		btnConfirm.addActionListener((e) -> {
			client.bookProperty(name, property, tfDate.getText(), tfDuration.getText());
		});
		south.add(btnConfirm);
		
		result.add(center, BorderLayout.CENTER);
		result.add(south, BorderLayout.SOUTH);
		
		return result;
	}

	public static JPanel createPropertyEdit(Client client, Property selectedProp, boolean isNewProperty) {
		JPanel result = new JPanel();
		result.setBounds(0, 0, 434, 209);
		result.setLayout(null);

		JLabel lblAddress = new JLabel(client.text.getString("Address")+":");
		lblAddress.setFont(new Font("Arial", Font.PLAIN, 12));
		lblAddress.setBounds(20, 127, 62, 15);
		result.add(lblAddress);

		JTextField tfAddress = new JTextField(selectedProp != null ? selectedProp.getAddress() : "");
		tfAddress.setColumns(10);
		tfAddress.setBounds(89, 125, 118, 20);
		tfAddress.setEnabled(false);
		result.add(tfAddress);

		JLabel lblCity = new JLabel(client.text.getString("City")+":");
		lblCity.setFont(new Font("Arial", Font.PLAIN, 12));
		lblCity.setBounds(20, 153, 72, 15);
		result.add(lblCity);

		JTextField tfCity = new JTextField();
		tfCity.setColumns(10);
		tfCity.setBounds(89, 153, 118, 20);
		tfCity.setText(selectedProp != null ? selectedProp.getCity() : "");
		result.add(tfCity);

		JLabel lblCapacity = new JLabel(client.text.getString("Capacity")+":");
		lblCapacity.setFont(new Font("Arial", Font.PLAIN, 12));
		lblCapacity.setBounds(20, 181, 82, 15);
		result.add(lblCapacity);

		JTextField tfCapacity = new JTextField();
		tfCapacity.setColumns(10);
		tfCapacity.setBounds(89, 179, 118, 20);
		tfCapacity.setText(selectedProp != null ? Integer.toString(selectedProp.getCapacity()) : "");
		result.add(tfCapacity);

		JLabel lblCost = new JLabel(client.text.getString("Cost")+":");
		lblCost.setFont(new Font("Arial", Font.PLAIN, 12));
		lblCost.setBounds(212, 127, 62, 15);
		result.add(lblCost);

		JTextField tfCost = new JTextField();
		tfCost.setColumns(10);
		tfCost.setBounds(292, 125, 118, 20);
		tfCost.setText(selectedProp != null ? Double.toString(selectedProp.getCost()) : "");
		result.add(tfCost);

		JButton btnBack = new JButton(client.text.getString("Back"));
		// TODO
		//btnBack.addActionListener( (e) -> {client.switchLog(pReg);} );
		btnBack.setBounds(212, 210, 89, 23);
		result.add(btnBack);

		JButton btnUpdate = new JButton(client.text.getString("Update"));
		btnUpdate.addActionListener((e) -> {
			client.updateProperty(tfAddress.getText(), tfCity.getText(), Integer.parseInt(tfCapacity.getText()), "", Double.parseDouble(tfCost.getText()));
		});
		btnUpdate.setBounds(311, 210, 89, 23);
		result.add(btnUpdate);	

		return result;
	}
	
	public static JPanel createReservationEdit(Client client, Reservation selectedReserv, boolean isReserv) {
		JPanel result = new JPanel();
		result.setBounds(0, 0, 434, 209);
		result.setLayout(null);

		JLabel lblProperty = new JLabel(client.text.getString("Property")+":");
		lblProperty.setFont(new Font("Arial", Font.PLAIN, 12));
		lblProperty.setBounds(20, 127, 62, 15);
		result.add(lblProperty);

		JTextField tfProperty = new JTextField(selectedReserv != null ? selectedReserv.getProperty().getAddress() : "");
		tfProperty.setColumns(10);
		tfProperty.setBounds(89, 125, 118, 20);
		tfProperty.setEnabled(false);
		result.add(tfProperty);

		JLabel lblGuest = new JLabel(client.text.getString("Name")+":");
		lblGuest.setFont(new Font("Arial", Font.PLAIN, 12));
		lblGuest.setBounds(20, 153, 72, 15);
		result.add(lblGuest);

		JTextField tfGuest = new JTextField();
		tfGuest.setColumns(10);
		tfGuest.setBounds(89, 153, 118, 20);
		tfGuest.setText(selectedReserv != null ? selectedReserv.getClient().getName() : "");
		tfGuest.setEnabled(false);
		result.add(tfGuest);

		JLabel lblDate = new JLabel(client.text.getString("Start_date")+":");
		lblDate.setFont(new Font("Arial", Font.PLAIN, 12));
		lblDate.setBounds(20, 181, 82, 15);
		result.add(lblDate);

		JTextField tfDate = new JTextField();
		tfDate.setColumns(10);
		tfDate.setBounds(89, 179, 118, 20);
		tfDate.setText(selectedReserv != null ? selectedReserv.getDate() : "");
		result.add(tfDate);

		JLabel lblDuration = new JLabel(client.text.getString("Duration_days")+":");
		lblDuration.setFont(new Font("Arial", Font.PLAIN, 12));
		lblDuration.setBounds(212, 127, 62, 15);
		result.add(lblDuration);

		JTextField tfDuration = new JTextField();
		tfDuration.setColumns(10);
		tfDuration.setBounds(292, 125, 118, 20);
		tfDuration.setText(selectedReserv != null ? Integer.toString(selectedReserv.getDuration()) : "");
		result.add(tfDuration);

		JButton btnBack = new JButton(client.text.getString("Back"));
		btnBack.addActionListener( (e) -> {client.createMainWindowGuest(selectedReserv.getClient().getUsername());;} );
		btnBack.setBounds(212, 210, 89, 23);
		result.add(btnBack);

		JButton btnUpdate = new JButton(client.text.getString("Update"));
		btnUpdate.addActionListener((e) -> {
			client.updateReservation(selectedReserv.getProperty(), selectedReserv.getClient(), tfDate.getText(), Integer.parseInt(tfDuration.getText()));
		});
		btnUpdate.setBounds(311, 210, 89, 23);
		result.add(btnUpdate);	

		return result;
	}
	
	public static JPanel createHostPropertyNew(Client client, String name) {
		JPanel result = new JPanel();
		result.setBounds(0, 0, 434, 209);
		result.setLayout(null);

		JLabel lblAddress = new JLabel(client.text.getString("Address")+":");
		lblAddress.setFont(new Font("Arial", Font.PLAIN, 12));
		lblAddress.setBounds(20, 127, 62, 15);
		result.add(lblAddress);

		JTextField tfAddress = new JTextField();
		tfAddress.setColumns(10);
		tfAddress.setBounds(89, 125, 118, 20);
		result.add(tfAddress);

		JLabel lblCity = new JLabel(client.text.getString("City")+":");
		lblCity.setFont(new Font("Arial", Font.PLAIN, 12));
		lblCity.setBounds(20, 153, 72, 15);
		result.add(lblCity);

		JTextField tfCity = new JTextField();
		tfCity.setColumns(10);
		tfCity.setBounds(89, 153, 118, 20);
		result.add(tfCity);

		JLabel lblCapacity = new JLabel(client.text.getString("Capacity")+":");
		lblCapacity.setFont(new Font("Arial", Font.PLAIN, 12));
		lblCapacity.setBounds(20, 181, 82, 15);
		result.add(lblCapacity);

		JTextField tfCapacity = new JTextField();
		tfCapacity.setColumns(10);
		tfCapacity.setBounds(89, 179, 118, 20);
		result.add(tfCapacity);

		JLabel lblCost = new JLabel(client.text.getString("Cost")+":");
		lblCost.setFont(new Font("Arial", Font.PLAIN, 12));
		lblCost.setBounds(212, 127, 62, 15);
		result.add(lblCost);

		JTextField tfCost = new JTextField();
		tfCost.setColumns(10);
		tfCost.setBounds(292, 125, 118, 20);
		result.add(tfCost);

		JButton btnBack = new JButton(client.text.getString("Back"));
		// TODO
		//btnBack.addActionListener( (e) -> {client.switchLog(pReg);} );
		btnBack.setBounds(212, 210, 89, 23);
		result.add(btnBack);

		JButton btnPublish = new JButton(client.text.getString("Publish"));
		btnPublish.addActionListener((e) -> {
			client.publishProperty(tfAddress.getText(), tfCity.getText(), Integer.parseInt(tfCapacity.getText()), Double.parseDouble(tfCost.getText()), name);
		});
		btnPublish.setBounds(311, 210, 89, 23);
		result.add(btnPublish);	

		return result;
	}
	
	public static JPanel createHostAccountManagement(Client client) {
		/*TODO
		 * change password
		 * change telephone number
		 */

		JPanel result = new JPanel();
		return result;
	}
	
	public static JPanel createAdminReservationsSearch(Client client) {
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
			//TODO
			//client.switchPropertyEdit(searchResults.getSelectedValue());
		});
		
		bottom.add(deleteButton);
		bottom.add(editButton);
		
		result.add(top, BorderLayout.NORTH);
		result.add(searchResults, BorderLayout.CENTER);
		result.add(bottom, BorderLayout.SOUTH);
		
		return result;

		
	}
	
	public static JPanel createMainWindowAdmin(Client client, String id) {
		JPanel main_panel = new JPanel();
		
		// @Todo: Put this pretty
		
		JLabel label = new JLabel(client.text.getString("Welcome_administrator")+" " + id);
		
		JButton properties = new JButton(client.text.getString("Properties"));
		properties.addActionListener((e) -> {client.switchPropertiesSearch();});
		
		JButton accounts = new JButton(client.text.getString("Accounts"));
		accounts.addActionListener((e) -> {client.switchAdminAccountManagment();});
		
		JButton reservation = new JButton(client.text.getString("Reservations"));
		reservation.addActionListener((e) -> {client.switchAdminReservationsSearch();});
		
		main_panel.add(label);
		main_panel.add(properties);
		main_panel.add(accounts);
		main_panel.add(reservation);
		
		return main_panel;
	}
	
	public static JPanel createMainWindowHost(Client client, String name /*, Other data for*/) {
		// @Copied and adapted from createMainWindowAdmin
		JPanel main_panel = new JPanel();
		
		JLabel label = new JLabel(client.text.getString("Welcome_host") + " " + name);

		JButton properties = new JButton(client.text.getString("Properties"));
		properties.addActionListener((e) -> {client.switchHostPropertiesManagement(name);});
		
		JButton account_data = new JButton(client.text.getString("Account_data"));
		account_data.addActionListener((e) -> {client.switchHostAccountManagement();});
		
		JButton logOut = new JButton(client.text.getString("Log_Out"));
		logOut.addActionListener((e) -> {client.switchLogin();});

		main_panel.add(label);
		main_panel.add(properties);
		main_panel.add(account_data);
		main_panel.add(logOut);

		return main_panel;
	}
	
	public static JPanel createMainWindowGuest(Client client, String name /*, Other data for*/) {
		// @Copied and adapted from createMainWindowAdmin
		JPanel main_panel = new JPanel();
		
		JLabel label = new JLabel(client.text.getString("Welcome_guest") + " " + name);

		JButton book = new JButton(client.text.getString("Book"));
		book.addActionListener((e) -> {client.switchGuestPropertiesManagement(name);});
		
		JButton account_data = new JButton(client.text.getString("Account_data"));
		account_data.addActionListener((e) -> {client.switchHostAccountManagement();});
		
		JButton reservations = new JButton(client.text.getString("Reservations"));
		reservations.addActionListener((e) -> {client.switchGuestReservationsList(name);});
		
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

