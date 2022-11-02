package dataAccess;

import exceptions.*;
import model.*;
import interfaces.Userable;
import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 *
 * @author Emil Nuñez
 */
public class DAOServer extends MasterConnection implements Userable {

    final String createUser = "INSERT INTO usertolog (id, login, email, fullname, status, privilege, lastPasswordChange) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    final String searchUser = "SELECT * FROM usertolog WHERE id = ?";
    final String searchEmail = "SELECT * FROM usertolog WHERE email = ?";
    final String searchUserFromUsername = "SELECT * FROM usertolog WHERE login = ?";

    /**
     * Get a username from userable login and return the user from the database
     * 
     * @param username
     * @return user
     */
    @Override
    public User login(String username) {
        User user = null;
        try {
            openConnection();
            stmt = con.prepareStatement(searchUser);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
               user = new User(rs.getInt(1), 
                       rs.getString(2), 
                       rs.getString(3), 
                       rs.getString(4), 
                       UserStatus.valueOf(rs.getString(5)), 
                       UserPrivilege.valueOf(rs.getString(6)),
                       rs.getString(7));
                
            } else {
                throw new UserAlreadyExistsException();
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            closeConnection();
        }
       return user;

    }

    /**
     * Get a user from userable signUp and creates it in the database
     * 
     * @param user 
     */
    @Override
    public void signUp(User user) {
        try {
            openConnection();
            //we search if there is an user with the same id
            stmt = con.prepareStatement(searchUser);
            stmt.setInt(1, user.getId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                throw new UserAlreadyExistsException();
            } else {
                //we search if there is an user with the same email
                stmt = con.prepareStatement(searchEmail);
                stmt.setString(1, user.getEmail());
                rs = stmt.executeQuery();
                if (rs.next()) {
                    throw new EmailAlreadyExistsException();
                } else {
                    //Don't exist any user with that id or email, we can create a new one.
                    stmt = con.prepareStatement(createUser);
                    stmt.setInt(1, user.getId());
                    stmt.setString(2, user.getLogin());
                    stmt.setString(3, user.getEmail());
                    stmt.setString(4, user.getFullName());
                    stmt.setString(5, user.getStatus().toString());  //probar a ver si funciona, no tengo ni p idea
                    stmt.setString(5, user.getPrivilege().toString()); //igual que arriba            
                    stmt.setString(7, user.getPassword());
                    stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

                    stmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            closeConnection();
        }
    }

}
