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
 * Manage connections to the database
 * 
 * @author joana
 */
public class Pool {

	private Stack<Connection> freeConnections = new Stack<Connection>();

	private Stack<Connection> usedConnections = new Stack<Connection>();

	protected ResourceBundle configFile = ResourceBundle.getBundle("dataAccess.config");
	protected String url = configFile.getString("URL"),
			user = configFile.getString("USER"),
			pass = configFile.getString("PASSWORD");

	protected int connectionLimit = Integer.parseInt(configFile.getString("CONNECTION_LIMIT"));

	/**
	 * Create the Pool object
	 *
	 * @param n number of freeConnections
	 * @throws exceptions.ServerException
	 */
	public Pool(int n) throws ServerException {
		this.createConnections(n);
	}

	public Pool() {
	}

	public int getFreeConnectionCount() {
		return freeConnections.size();
	}

	public int getUsedConnectionCount() {
		return usedConnections.size();
	}

	/**
	 * Creates connections, always respecting the connection limit
	 * 
	 * @param n
	 * @throws ServerException
	 */
	private void createConnections(int n) throws ServerException {
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
	public Connection getConnection() throws ServerException {
		try {
			// Check if there are any free connections
			// create one if the stack is empty
			if (freeConnections.empty())
				this.createConnections(1);
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
	public void returnConnection(Connection con) throws ServerException {
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
	 * Returns a list of all the open connections
	 * 
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
	 * 
	 * @throws ServerException
	 */
	public void killAllConnections() throws ServerException {
		// Get all open connections
		List<Connection> allCons = this.getAllConnections();
		// Iterate over all connections and close them
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

	/**
	 * Removes all the closed connections from both
	 * free and used collections
	 */
	private void cleanClosedConnections() {
		// Remove closed free connections
		freeConnections.forEach((con) -> {
			try {
				if (con.isClosed())
					freeConnections.remove(con);
			} catch (SQLException ex) {
				Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		// Remove closed used connections
		usedConnections.forEach((con) -> {
			try {
				if (con.isClosed())
					usedConnections.remove(con);
			} catch (SQLException ex) {
				Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
	}
}
