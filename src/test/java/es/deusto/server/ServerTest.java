package es.deusto.server;

import static org.junit.Assert.*;
import java.rmi.RemoteException;
import java.util.List;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import es.deusto.server.IServer.PropertyRegistrationError;
import es.deusto.server.IServer.RegistrationError;
import es.deusto.server.jdo.Property;
import es.deusto.server.jdo.Reservation;
import es.deusto.server.jdo.User;
import junit.framework.JUnit4TestAdapter;

public class ServerTest {

	// @TODO: Use '@Before' and '@After to only create the server once during the test suite!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	@Rule
	public ContiPerfRule rule = new ContiPerfRule();
	public static junit.framework.Test suite() {
		 return new JUnit4TestAdapter(ServerTest.class);
	}
	
	public static Server server;
	
	@BeforeClass
	public static void initServer() throws RemoteException {
		server = new Server();
	}
	
	
	@Test
    public void createUser() throws RemoteException {
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
	public void updateUser() throws RemoteException {
		// Admin user is created by default, let's modify it
		
		
		RegistrationError reg_error = server.registerUser("Test", "test1", "test-email@gmail.com", "1234", "pass", true);
		System.out.println(reg_error);
		assertTrue(reg_error == RegistrationError.NONE);

		RegistrationError upd_error = server.updateUser("Test", "newPassword", User.UserKind.GUEST, "this method does not care about correct data!", "this is not checked!", "This is my new name", true);
		System.out.println(upd_error);
		assertTrue(upd_error == RegistrationError.NONE);
		
		List<User> results = server.getUsers("Test");
		
		assertTrue(results.size() == 1);
		
		User user = results.get(0);
		assertTrue(user.getUsername().equals("Test"));
		assertTrue(user.getKind() == User.UserKind.GUEST);
		assertTrue(user.getName().equals("This is my new name"));
		assertTrue(user.getPassword().equals("newPassword"));
		assertTrue(user.getTelephone().equals("this method does not care about correct data!"));
		assertTrue(user.getEmail().equals("this is not checked!"));
		assertTrue(user.isVerified());
		
		
		// Test getters and setters (just for code coverage)
		
		user.setUsername(user.getUsername());
		user.setPassword(user.getPassword());
		user.setKind(user.getKind());
		user.setEmail(user.getEmail());
		user.setTelephone(user.getTelephone());
		user.setName(user.getName());
		user.setVerified(user.isVerified());
		
		
		server.deleteUser("Test");
	}
	
	@Test
	public void deleteUser() throws RemoteException {
		// Admin user is created by default, let's delete it
		
		server.registerUser("Test", "test1", "test-email@gmail.com", "1234", "pass", true);
		server.deleteUser("Test");
		
		
		List<User> results = server.getUsers("Test");
		assertNotNull(results);
		assertTrue(results.size() == 0);
		
	}
	
	@Test
	public void testProperties() throws RemoteException {
		//
		// Create properties	
		//
		
		// @Todo: Create a user for this test!  :NotThisUser
		{
			PropertyRegistrationError error = server.registerProperty("Sesame street", "Barcelona", 5, 200, "admin"); // :NotThisUser
			assertTrue(error.toString(), error == PropertyRegistrationError.NONE);
		}
		{
			PropertyRegistrationError error = server.registerProperty("Sesame street", "51st area", 5, 200, "admin"); // :NotThisUser
			assertTrue(error.toString(), error == PropertyRegistrationError.INVALID_CITY);
		}
		{
			// Cost must be positive
			PropertyRegistrationError error = server.registerProperty("Sesame street", "Barcelona", 5, -200, "admin"); // :NotThisUser
			assertTrue(error.toString(), error == PropertyRegistrationError.INVALID_COST);
		}
		{
			// Capacity must be positive
			PropertyRegistrationError error = server.registerProperty("Sesame street", "Barcelona", -5, 200, "admin"); // :NotThisUser
			assertTrue(error.toString(), error == PropertyRegistrationError.INVALID_CAPACITY);
		}
		
		//
		// Get properties
		//
		{
			List<Property> results = server.getPropertiesByCity("Barcelona");
			assertNotNull(results);
			assertTrue(results.size() >= 1);
		}
		{
			List<Property> results = server.getPropertiesByHost("admin"); // :NotThisUser
			assertNotNull(results);
			assertTrue(results.size() >= 1);
		}
		
		//
		// Delete the created properties
		//
		server.deleteProperty("Sesame street");
	}
	
	@Test
	@PerfTest(threads=1, invocations=800)
	@Required(max = 3500, average = 3500, totalTime=5000)
	public void testLogin() throws RemoteException {	
		User user = server.login("admin", "admin");
		
		assertNotNull(user);
		assertTrue(user.getUsername().equals("admin"));
		assertTrue(user.getPassword().equals("admin")); // @Todo: We may want in the future to not send the password in plain text or even send it through Internet
	}
	
	@Test
	public void testReservation() throws RemoteException {
		{
			PropertyRegistrationError error = server.registerProperty("Sesame street", "Barcelona", 5, 200, "admin"); // :NotThisUser
			assertTrue(error.toString(), error == PropertyRegistrationError.NONE);
		}
		
		// @Todo: Properly make all the checks
		
		Property property = server.getPropertiesByCity("Barcelona").get(0);
		
		server.bookProperty("admin", property, "some date", "some date");
		
		List<Reservation> reservations = server.getReservationsByCity("Barcelona");
		
		assertNotNull(reservations);
		assertTrue(reservations.size() >= 1);
		
		server.deleteReservation("Sesame street", "admin", "some date", "some date");
		server.deleteProperty("Sesame street");
		
		// @Todo: Assert that the reservation is correct
		
	}
	
	@Test
	@PerfTest(threads=10, invocations=1000)
	@Required(average=5000)
	public void testGetUser() throws RemoteException {
		server.getUser("admin");
	}
	
	@AfterClass
	public static void cleanDB() throws RemoteException {
		// TODO
	}
}
