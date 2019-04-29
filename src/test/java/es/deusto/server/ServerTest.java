package es.deusto.server;

import static org.junit.Assert.*;
import java.rmi.RemoteException;
import org.junit.Test;

public class ServerTest {

	@Test
	public void testAdd() throws RemoteException {
		Server server = new Server();
		int sum = server.add(2, 3);
		assertEquals(5, sum);
		assertNotEquals(2, sum);
		//assertEquals(2, sum);		failure

	}
}
