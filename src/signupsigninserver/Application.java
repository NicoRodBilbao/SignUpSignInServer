package signupsigninserver;

import controller.Controller;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Main class for our Server Application
 * contains the main method
 * @author Nicolas Rodriguez
 */
public class Application {

	public static boolean isRunning = true;
	
	private static Controller controller;
	private static TextInterface tui;

    protected final static Logger LOGGER = Logger.getLogger(Controller.class.getName());

	/**
	 * Runs the controller
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		controller = new Controller();
		controller.start();
		// The TextInterface is used to shutdown the server
		//when it's running from the command line
		tui = new TextInterface();
		tui.start();
	}

	public static void shutdown() {
		try {
			controller.serverSocket.close();
		} catch (IOException e) {
		}
		LOGGER.info("Server shutdown...");
		controller.interrupt();
		tui.interrupt();
	}

}
