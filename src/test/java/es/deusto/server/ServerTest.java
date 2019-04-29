package es.deusto.server;

import static org.junit.Assert.*;
import java.rmi.RemoteException;
import java.util.List;

import org.junit.Test;

import es.deusto.server.IServer.RegistrationError;
import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.User;

public class ServerTest {

	
	
	@Test
	public void createUser() throws RemoteException {
		Server server = new Server();
		
		{
			RegistrationError error = server.registerUser("Test", "Test", "test@gmail.com", "901234567", "test", false);
			assertTrue(error.toString(), error == RegistrationError.NONE);
		}
		
		{
			RegistrationError error = server.registerUser("Test", "Test", "test2@gmail.com", "901234568", "test", false);
			assertTrue(error.toString(), error == RegistrationError.INVALID_NAME);
		}
		
		{
			RegistrationError error = server.registerUser("Test2", "Test2", "test2@gmail.com", "90123av4", "test", false);
			assertTrue(error.toString(), error == RegistrationError.INVALID_TELEPHONE);
		}
		
		{
			RegistrationError error = server.registerUser("Test3", "Test3", "test3gmail.com", "901234567", "test", false);
			assertTrue(error.toString(), error == RegistrationError.INVALID_EMAIL);
		}

		server.deleteUser("Test");
		
	}
	
	@Test
	public void deleteUser() throws RemoteException {
		Server server = new Server();
		
		// Admin user is created by default, let's delete it
		
		server.deleteUser("admin");
		
		List<User> results = server.getUser("admin");
		assertNotNull(results);
		assertTrue(results.size() == 0);
		
	}
	
	@Test
	public void updateUser() throws RemoteException {
		Server server = new Server();
		// Admin user is created by default, let's modify it
		
		server.updateUser("admin", "newPassword", User.UserKind.GUEST, "this method does not care about correct data!", "this is not checked!", "This is my new name", true);
		
		List<User> results = server.getUser("admin");
		
		assertTrue(results.size() == 1);
		
		User user = results.get(0);
		assertTrue(user.getUsername().equals("admin"));
		assertTrue(user.getKind() == User.UserKind.GUEST);
		assertTrue(user.getName().equals("This is my new name"));
		assertTrue(user.getPassword().equals("newPassword"));
		assertTrue(user.getTelephone().equals("this method does not care about correct data!"));
		assertTrue(user.getEmail().equals("this is not checked!"));
		assertTrue(user.isVerified());
		
	}
	
	@Test
	public void testProperties() throws RemoteException {
		Server server = new Server();
		
		// Create properties	
		RegistrationError error = server.registerProperty("Sesame street", "Barcelona", 5, 200, "admin");
		assertTrue(error.toString(), error == RegistrationError.NONE);
		
		// @Todo: Check incorrect arguments
		
		
		// Get properties
		List<Property> results = server.getPropertiesByCity("Barcelona");
		assertNotNull(results);
		assertTrue(results.size() >= 1);
		
		// Delete the created properties
		server.deleteProperty("Sesame street");
		
		
		
	}
}
