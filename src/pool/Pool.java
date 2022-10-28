package pool;

import exceptions.ServerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nicolas Rodriguez
 */
public class Pool {
    
    private final int CONNECTION_LIMIT = 10;

    private Stack<Connection> freeConnections;
    
    private Stack<Connection> usedConnections;

    protected ResourceBundle configFile = 
        ResourceBundle.getBundle("dataAcess.config");;
    protected String url = configFile.getString("URL"),
        user = configFile.getString("USER"),
        pass = configFile.getString("PASSWORD");

    /**
     * Create the Pool object
     *
     * @param n number of freeConnections
     */
    public Pool(int n) throws ServerException {
        this.createConnections(n);
    }
    
    /**
     * Creates connections, always respecting the connection limit
     * @param n
     * @throws ServerException 
     */
    private void createConnections(int n) throws ServerException {
        for (int i = 0; i < n; i++) {
            try {
                if(freeConnections.size() >= CONNECTION_LIMIT) throw new ServerException();
                freeConnections.add(DriverManager.getConnection(url, user, pass));
            } catch (SQLException e) {
                //TODO Exception parametrization
                throw new ServerException();
            } catch (ServerException e) {
                while(freeConnections.size() > CONNECTION_LIMIT)
                    freeConnections.pop();
            }
        }
    }
    
    /**
     * Returns a connection to the database,
     * if there aren't any free connections it creates one,
     * the maximum amount of connections there can be is defined
     * by the CONNECTION_LIMIT constant
     * @return Connection a connection to be used
     * @throws ServerException 
     */
    public Connection getConnection() throws ServerException {
        try {
            //Check if there are any free connections
            // create one if the stack is empty
            if(freeConnections.empty()) this.createConnections(1);
            //Move connection from free to used and return it
            Connection con = freeConnections.pop();
            if(con == null) throw new ServerException();
            usedConnections.add(con);
            return con;
        } catch (ServerException e) {
            //TODO Exception parametrization
            throw new ServerException();
        }
    }
    
    /**
     * Returns a list of all the open connections
     * @return 
     */
    private List<Connection> getAllConnections() {
        List<Connection> allCons = new ArrayList<Connection>();
        freeConnections.stream()
                .forEach(con -> allCons.add(con));
        usedConnections.stream()
                .forEach(con -> allCons.add(con));
        return allCons;
    }
    
    /**
     * Kill all connections
     * @throws ServerException 
     */
    public void killConnections() throws ServerException {
        //Get all open connections
        List<Connection> allCons = this.getAllConnections();
        //Iterate over all connections and close them
        allCons.stream()
                .forEach(con -> {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        cleanClosedConnections();
    }
    
    private void cleanClosedConnections() {
        //Remove closed free connections
        freeConnections.forEach((con) -> {
            try {
                if(con.isClosed()) freeConnections.remove(con);
            } catch (SQLException ex) {
                Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //Remove closed used connections
        usedConnections.forEach((con) -> {
            try {
                if(con.isClosed()) usedConnections.remove(con);
            } catch (SQLException ex) {
                Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
