package dataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import pool.Pool;

import model.*;
import exceptions.*;
import interfaces.Userable;

/**
 * This class has the usability of logging up and signing in to a server.
 *
 * @author Emil Nuñez / Nicolás Rodríguez
 */
public class DAOServer implements Userable {

    final protected Logger LOGGER = Logger.getLogger(DAOServer.class.getName());
    final String createUser = "INSERT INTO usertolog (login, email, fullname, status, privilege, password, lastPasswordChange) VALUES (?, ?, ?, ?, ?, ?, ?)";
    final String searchUser = "SELECT * FROM usertolog WHERE login = ?";
    final String searchEmail = "SELECT * FROM usertolog WHERE email = ?";
    final String searchUserFromUsername = "SELECT * FROM usertolog WHERE login = ?";
    final String createLogIn = "INSERT INTO signin (lastSignIn, userId) VALUES (?, ?)";
    final String deleteLogIn = "DELETE from signin where userId = ? ORDER BY lastSignIn ASC LIMIT 1;";
    final String getLogInNumber = "SELECT COUNT(*) FROM signin WHERE userId = ?;";

    private static Pool pool = Pool.getPool();
    private Connection con;
    private PreparedStatement stmt;
    private ResultSet rs;

    /**
     * This method gets a username from userable logIn and return a user with
     * the same login.
     *
     * @param username the username of the user to be logged in
     * @return user the user with the login introduced
     * @throws exceptions.UserDoesNotExistException
     */
    public User login(String username) throws UserDoesNotExistException {
        User user = null;
        try {
            System.out.println("hola0");
            con = pool.getConnection();
            LOGGER.info("Server Login open connection");
            //search if user already exist 
            stmt = con.prepareStatement(searchUser);
            stmt.setString(1, username);
            System.out.println("hola1");
            rs = stmt.executeQuery();
            System.out.println("hola");
            if (rs.next()) {
                user = new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        UserStatus.valueOf(rs.getString(5).toUpperCase()),
                        UserPrivilege.valueOf(rs.getString(6).toUpperCase()),
                        rs.getString(7));
                System.out.println(user.getFullName());
                //search if user has more than 10 connection in signin table
                stmt = con.prepareStatement(getLogInNumber);
                stmt.setInt(1, user.getId());
                rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 9) {
                    //delete the last connection of the user from signin table
                    stmt = con.prepareStatement(deleteLogIn);
                    stmt.setInt(1, user.getId());
                    stmt.executeUpdate();
                }
                //insert the connection in signin table
                stmt = con.prepareStatement(createLogIn);
                stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(2, user.getId());
                stmt.executeUpdate();
            } else {
                //If user does not exist
                throw new UserDoesNotExistException();
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        } catch (ServerException ex) {
            Logger.getLogger(DAOServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            LOGGER.info("Server Login close connection");
            try {
                pool.returnConnection(con);
            } catch (ServerException ex) {
                Logger.getLogger(DAOServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return user;
    }

    /**
     * This method recieves a user and sends it to a server.
     *
     * @param user the user to be sent
     * @throws exceptions.EmailAlreadyExistsException
     * @throws exceptions.UserAlreadyExistsException
     */
    @Override
    public void signUp(User user) throws EmailAlreadyExistsException, UserAlreadyExistsException {
        try {
            con = pool.getConnection();
            LOGGER.info("Server SignUp open connection");
            //we search if there is an user with the same id
            stmt = con.prepareStatement(searchUser);
            stmt.setString(1, user.getLogin());
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
                    stmt.setString(4, user.getStatus().toString());
                    stmt.setString(5, user.getPrivilege().toString());
                    stmt.setString(6, user.getPassword());
                    stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        } catch (ServerException ex) {
            Logger.getLogger(DAOServer.class.getName()).log(Level.SEVERE, null, ex);
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
