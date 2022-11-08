/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccessTest;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import dataAccess.DAOServer;
import exceptions.EmailAlreadyExistsException;
import exceptions.IncorrectUserException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserDoesNotExistException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import model.UserPrivilege;
import model.UserStatus;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 2dam
 */
public class DAOServerTest {

    public DAOServerTest() {
    }

    /**
     * Test of login method, of class DAOServer.
     */
    /**
     * Test SignUp from the class DAOServer
     */
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
        } catch (EmailAlreadyExistsException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserAlreadyExistsException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         User prueba = null;
         
        try {
           prueba = instance.login("Prueba9");
        } catch (UserDoesNotExistException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IncorrectUserException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals("user sign up well", prueba.getLogin(), "Prueba9");
        
    }

    /**
     * Test UserAlreadyExistsException
     */
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
        } catch (EmailAlreadyExistsException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserAlreadyExistsException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("ERROR: The username already exists.", ex.getMessage());
        }
    }

    /**
     * Test EmailAlreadyExistsException
     */
    @Test
    public void testSignUpEmailAlreadyExistsException() {
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
        } catch (EmailAlreadyExistsException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("ERROR: The email already exists.)", ex.getMessage());
        } catch (UserAlreadyExistsException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test LogIn from the class DAOServer
     */
    @Test
    public void testLogin() {
        LOGGER.severe("testLogin");
        String username = "Prueba9";
        DAOServer instance = new DAOServer();
        User user = null;
        try {
            user = instance.login(username);
        } catch (UserDoesNotExistException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IncorrectUserException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
        assertEquals("user log in well", user.getLogin(), username);

    }

    /**
     * Test LogIn from the class DAOServer
     */
    @Test
    public void testLoginUserDoesNotExistException() {
        LOGGER.severe("testLogin");
        String username = "Prueba1";
        DAOServer instance = new DAOServer();
        User user;
        try {
            user = instance.login(username);
        } catch (UserDoesNotExistException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("User do not exists error throws", "ERROR: The username does not match any existent User.", ex.getMessage());
        } catch (IncorrectUserException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    /**
     * Test LogIn from the class DAOServer
     */
    @Test
    public void testLoginIncorrectUserException() {
        LOGGER.severe("testLogin");
        String username = "Prue ba9";
        DAOServer instance = new DAOServer();
        User user;
        try {
            user = instance.login(username);
        } catch (UserDoesNotExistException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IncorrectUserException ex) {
            Logger.getLogger(DAOServerTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("Incorrect user error throws", "ERROR: The username is incorrect or has an incorrect format.\n\n(Avoid using spaces)", ex.getMessage());
        } 

    }

}

