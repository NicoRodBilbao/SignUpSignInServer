package factories;

import dataAccess.DAOServer;
import interfaces.Userable;

/**
 *
 * @author Nicolas Rodriguez
 */
public class UserFactory {

    private static Userable user = new DAOServer();

    public static Userable getAccessUser() {
        return user;
    }
}
