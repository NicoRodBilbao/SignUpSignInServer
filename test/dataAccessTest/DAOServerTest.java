/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccessTest;

import dataAccess.DAOServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
                       Prueba9, 
                       Prueba9, 
                       Prueba9@gmail.com, 
                       UserStatus.ENABLED,
                       UserPrivilege.USER,
                       Prueba9);
        DAOServer instance = new DAOServer();
        instance.singUp(user);
        // TODO review the generated test code and remove the default call to fail.
        
    }
    
    /**
     * Test LogIn from the class DAOServer
     */
    @Test
    public void testLogin() {
        LOGGER.severe("testLogin");
        String username = "Prueba9";
        DAOServer instance = new DAOServer();
        instance.login(username);
        // TODO review the generated test code and remove the default call to fail.
    }
    
}
