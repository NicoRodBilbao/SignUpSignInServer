package factoryTest;

import dataAccess.DAOServer;
import factories.UserFactory;
import interfaces.Userable;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This test class tests that the factory creates an Interface that's an intstance
 * of a data access object.
 *
 * @author Nicolás Rodríguez
 */
public class UserFactoryTest {

    /**
     * Test of getAccessUser method, of class UserFactory, tests that it is DAOServer.
     */
    @Test
    public void testGetAccessUser() {
        assertTrue(UserFactory.getAccessUser() instanceof DAOServer);
    }
    
    /**
     * Test of getAccessUser method, of class UserFactory, tests that it is Userable.
     */
    @Test
    public void testGetAccessUserInterface() {
        assertTrue(UserFactory.getAccessUser() instanceof Userable);
    }
}
