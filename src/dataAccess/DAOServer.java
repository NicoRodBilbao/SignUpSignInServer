package dataAccess;

import exceptions.EmailAlreadyExistsException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserDoesNotExistException;
import interfaces.Userable;
import model.User;

/**
 *
 * @author Nicolas Rodriguez
 */
public class DAOServer implements Userable{

    @Override
    public User login(String username) throws UserDoesNotExistException {
        User user = new User();
        user.setLogin("a");
        user.setPassword("a");
        return user;
        // TODO
    }

    @Override
    public void signUp(User user) throws EmailAlreadyExistsException, UserAlreadyExistsException {
        // TODO
    }
    
}
