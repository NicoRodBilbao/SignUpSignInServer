package dataAccessTest;

import java.util.logging.Logger;

import org.junit.Test;
import static org.junit.Assert.*;

import dataAccess.DAOServer;

import model.User;
import model.UserPrivilege;
import model.UserStatus;
import exceptions.EmailAlreadyExistsException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserDoesNotExistException;
import org.junit.runner.RunWith;
import suite.Order;
import suite.OrderedRunner;

/**
 * This class tests the correct functioning of the DAOServer class.
 *
 * @author Emil Nuñez / Nicolás Rodríguez
 */
@RunWith(OrderedRunner.class)
public class DAOServerTest {

    protected final Logger LOGGER = Logger.getLogger(DAOServerTest.class.getName());

    public DAOServerTest() {
    }

    /**
     * Tests the signUp method from the class DAOServer.
     */
    @Order (order=1)
    @Test
    public void testSignUp() {
        LOGGER.severe("testSignUp");
        User user = new User(9,
                "Prueba9",
                "Prueba9",
                "Prueba9@gmail.com",
                UserStatus.ENABLED,
                UserPrivilege.USER,
                "Prueba9");
        DAOServer instance = new DAOServer();
        try {
            instance.signUp(user);
            assertTrue(true);
        } catch (EmailAlreadyExistsException | UserAlreadyExistsException ex) {
            assertTrue(false);
        }
    }

    /**
     * Tests that UserAlreadyExistsException is thrown.
     */
    @Order (order=2)
    @Test
    public void testSignUpUserAlreadyExistsException() {
        LOGGER.severe("testSignUp");
        User user = new User(9,
                "Prueba9",
                "Prueba9",
                "Prueba8@gmail.com",
                UserStatus.ENABLED,
                UserPrivilege.USER,
                "Prueba9");
        DAOServer instance = new DAOServer();
        try {
            instance.signUp(user);
            assertTrue(false);
        } catch (EmailAlreadyExistsException ex) {
            assertTrue(false);
        } catch (UserAlreadyExistsException ex) {
            assertTrue(true);
        }
    }

    /**
     * Tests that EmailAlreadyExistsException is thrown.
     */
    @Order (order=3)
    @Test
    public void testSignUpEmailAlreadyExistsException() {
        LOGGER.severe("testSignUp");
        User user = new User(9,
                "Prueba",
                "Prueba9",
                "Prueba9@gmail.com",
                UserStatus.ENABLED,
                UserPrivilege.USER,
                "Prueba9");
        DAOServer instance = new DAOServer();
        try {
            instance.signUp(user);
            assertTrue(false);
        } catch (EmailAlreadyExistsException ex) {
            assertTrue(true);
        } catch (UserAlreadyExistsException ex) {
            assertTrue(false);
        }
    }

    /**
     * Tests the logIn method from the class DAOServer.
     */
    @Order (order=4)
    @Test
    public void testLogin() {
        LOGGER.severe("testLogin");
        String username = "Prueba9";
        DAOServer instance = new DAOServer();
        User user = null;
        try {
            user = instance.login(username);
            assertTrue(true);
        } catch (UserDoesNotExistException ex) {
            assertTrue(false);
        }
    }

    /**
     * Tests that UserDoesNotExistException is thrown.
     */
    @Order (order=5)
    @Test
    public void testLoginUserDoesNotExistException() {
        LOGGER.severe("testLogin");
        String username = "Prueba1";
        DAOServer instance = new DAOServer();
        User user;
        try {
            user = instance.login(username);
            assertTrue(false);
        } catch (UserDoesNotExistException ex) {
            assertTrue(true);
        }
    }
}
