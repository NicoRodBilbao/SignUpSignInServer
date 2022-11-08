
package dataAccess;

import exceptions.*;
import model.*;
import interfaces.Userable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import pool.Pool;


/**
 *
 * @author Emil Nuñez
 */
public class DAOServer implements Userable {
    final protected  Logger LOGGER = Logger.getLogger(DAOServer.class.getName());
    final String createUser = "INSERT INTO usertolog (login, email, fullname, status, privilege, password, lastPasswordChange) VALUES (?, ?, ?, ?, ?, ?, ?)";
    final String searchUser = "SELECT * FROM usertolog WHERE login = ?";
    final String searchEmail = "SELECT * FROM usertolog WHERE email = ?";
    final String searchUserFromUsername = "SELECT * FROM usertolog WHERE login = ?";
    final String createLogIn = "INSERT INTO signin (lastSignIn, userId) VALUES (?, ?)";
    final String searchLogIn = "SELECT * FROM signin WHERE userId = ?";
    final String deleteLogIn = "DELETE from signin where userId = ? ORDER BY lastSignIn ASC LIMIT 1;";
    final String getLogInNumber = "SELECT COUNT(*) FROM signin WHERE userId = ?;"; 
    
    private static Pool pool = Pool.getPool();
    private  Connection con;
    private PreparedStatement stmt;
    private ResultSet rs;

    /**
     * Get a username from userable login and return the user from the database
     * 
     * @param username
     * @return user
     * @throws exceptions.UserDoesNotExistException
     * @throws exceptions.IncorrectUserException
     * @throws exceptions.IncorrectPasswordException
     */
    @Override
      public User login(String username) throws UserDoesNotExistException, IncorrectUserException, IncorrectPasswordException {
        User user = null;
        try {
            con = pool.getConnection();
            LOGGER.info("Server Login open connection");
            stmt = con.prepareStatement(searchUser);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
               user = new User(rs.getInt(1), 
                       rs.getString(2), 
                       rs.getString(3), 
                       rs.getString(4), 
                       UserStatus.valueOf(rs.getString(5).toUpperCase()), 
                       UserPrivilege.valueOf(rs.getString(6).toUpperCase()),
                       rs.getString(7));
                stmt = con.prepareStatement(searchLogIn);
                stmt.setString(1, user.getID());
                rs = stmt.executeQuery();
                 if (rs.next()) {
                    stmt = con.prepareStatement(getLogInNumber);
                    stmt.setString(1, user.getID());
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        stmt = con.prepareStatement(deleteLogIn);
                        stmt.setString(1, user.getID());
                        rs = stmt.executeQuery();
                    }
                 }
                stmt = con.prepareStatement(getLogInNumber);
                stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(2, user.getID());
                rs = stmt.executeQuery();
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        } finally {
            LOGGER.info("Server Login close connection");
            try {
                pool.returnConnection(con);
            } catch (ServerException ex) {
                Logger.getLogger(DAOServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       return user;
        // TODO
    }

    /**
     * Get a user from userable signUp and creates it in the database
     * 
     * @param user 
     * @throws exceptions.EmailAlreadyExistsException 
     * @throws exceptions.UserAlreadyExistsException 
     */
    @Override
    public void signUp(User user)throws EmailAlreadyExistsException, UserAlreadyExistsException {
        
        try {
            con = pool.getConnection();
            LOGGER.info("Server SignUp open connection");
            //we search if there is an user with the same id
            stmt = con.prepareStatement(searchUser);
            stmt.setInt(1, user.getId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                //if user exist, throw the UserAlreadyExistsException exception
                throw new UserAlreadyExistsException();
            } else {
                //we search if there is an user with the same email
                stmt = con.prepareStatement(searchEmail);
                stmt.setString(1, user.getEmail());
                rs = stmt.executeQuery();
                if (rs.next()) {
                    //if the email exist, throw the EmailAlreadyExistsException exception
                    throw new EmailAlreadyExistsException();
                } else {
                    //Don't exist any user with that id or email, we can create a new one.
                    stmt = con.prepareStatement(createUser);
                    stmt.setString(1, user.getLogin());
                    stmt.setString(2, user.getEmail());
                    stmt.setString(3, user.getFullName());
                    stmt.setString(4, user.getStatus().toString());  //probar a ver si funciona, no tengo ni p idea
                    stmt.setString(5, user.getPrivilege().toString()); //igual que arriba            
                    stmt.setString(6, user.getPassword());
                    stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.executeUpdate();
                }
            }
        } catch (Exception e) {
           LOGGER.severe(e.getMessage());
        } finally {
           LOGGER.info("Server SignUp close connection");
            try {
                pool.returnConnection(con);
            } catch (ServerException ex) {
                Logger.getLogger(DAOServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
