package factories;

import dataAccess.DAOServer;
import interfaces.Userable;

/**
 * This class creates an interface Userable and instantiates it as DAOServer.
 *
 * @author Nicolas Rodriguez
 */
public class UserFactory {

    private static Userable user = new DAOServer();

    public static Userable getAccessUser() {
        return user;
    }
}
