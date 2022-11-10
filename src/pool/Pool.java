package pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Logger;

import exceptions.ServerException;

/**
 * Manage connections to the database.
 *
 * @author joana
 */
public class Pool {
	
	protected static final Logger LOGGER = Logger.getLogger(Pool.class.getName());

	private static Stack<Connection> freeConnections = new Stack<Connection>();

	private static Stack<Connection> usedConnections = new Stack<Connection>();

	protected static ResourceBundle configFile = ResourceBundle.getBundle("dataAccess.config");
	protected static String url = configFile.getString("URL"),
			user = configFile.getString("USER"),
			pass = configFile.getString("PASSWORD");

	protected static int connectionLimit = Integer.parseInt(configFile.getString("CONNECTION_LIMIT"));
	
	private static Pool pool = null;

	/**
	 * Create the Pool object
	 * specifying the initial number of connections
	 * @param n number of freeConnections
	 * @throws exceptions.ServerException
	 */
	private Pool(int n) throws ServerException {
		createConnections(n);
	}

	/**
	 * Create the Pool object
	 */
	private Pool() {
	}
	
	/*
	* Returns the singleton class
	* if it doesn't exist it's created
	* @return the pool object
     */
    public static Pool getPool() {
        if (pool == null) {
            pool = new Pool();
        }
        return pool;
    }

    /*
	* Returns the singleton class.
	* If it doesn't exist it's created.
	* @return the pool object
	* @param n number of initial connections

	*/
	public static Pool getPool(int n) throws ServerException {
		if(pool == null)
			pool = new Pool(n);
		else
			createConnections(n);	
		return pool;
	}

	/**
	* Returns the amount of free connections
	* @return number of free connections
	*/
	public synchronized int getFreeConnectionCount() {
		return freeConnections.size();
	}

	/**
	* Returns the amount of used connections
	* @return number of used connections
	*/
	public synchronized int getUsedConnectionCount() {
		return usedConnections.size();
	}

	/**
	 * Creates connections, always respecting the connection limit
	 * @param n
	 * @throws ServerException
	 */
	private static void createConnections(int n) throws ServerException {
		// Create the requested new connections
		for (int i = 0; i < n; i++) {
			try {
				Connection newCon = DriverManager.getConnection(url, user, pass);
				freeConnections.push(newCon);
				if (freeConnections.size() + usedConnections.size() >= connectionLimit)
					throw new ServerException();
			} catch (SQLException e) {
				// TODO Exception parametrization
				// error creating connection
				throw new ServerException();
			} catch (ServerException e) {
				// TODO Exception parametrization
				// connection limit surpassed
				while (freeConnections.size() > connectionLimit)
					freeConnections.pop();
			}
		}
	}

	/**
	 * Returns a connection to the database,
	 * if there aren't any free connections it creates one,
	 * the maximum amount of connections there can be is defined
	 * by the CONNECTION_LIMIT property
	 * 
	 * @return Connection a connection to be used
	 * @throws ServerException
	 */
	public synchronized Connection getConnection() throws ServerException {
		try {
			// Check if there are any free connections
			// create one if the stack is empty
			if (freeConnections.empty())
				createConnections(1);
			// Move connection from free to used and return it
			Connection con = freeConnections.pop();
			if (con == null)
				throw new ServerException();
			usedConnections.add(con);
			return con;
		} catch (ServerException e) {
			// TODO Exception parametrization
			// invalid connection
			throw new ServerException();
		}
	}

	/**
	 * Receives a connection that's not being used anymore
	 * and stores it to be used later
	 * 
	 * @param Connection
	 */
	public synchronized void returnConnection(Connection con) throws ServerException {
		try {
			if(con.isClosed())
				// TODO Exception parametrization
				// invalid connection
				throw new ServerException();
			usedConnections.remove(con);
			freeConnections.push(con);
		} catch (SQLException e) {
			// TODO Exception parametrization
			// invalid connection
			throw new ServerException();
		}
	}

	/**
	 * Close all the current connections and 
	 * clear the freeConnections and usedConnections Stacks
	 * @throws ServerException
	 */
	public synchronized void killAllConnections() throws ServerException {
		// Close the free connections
		freeConnections.forEach((con) -> {
			try {
				con.close();
			} catch (SQLException ex) {
				LOGGER.severe(ex.getMessage());
			}
		});
		// Close the used connections
		usedConnections.forEach((con) -> {
			try {
				con.close();
			} catch (SQLException ex) {
				LOGGER.severe(ex.getMessage());
			}
		});

		// Feed the previously existing connections to
		//the Garbage Collector and create new empty stacks
		freeConnections = new Stack<Connection>();
		usedConnections = new Stack<Connection>();
	}
}

