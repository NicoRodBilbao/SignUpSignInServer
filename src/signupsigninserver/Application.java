package signupsigninserver;

import controller.Controller;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for our Server Application contains the main method
 *
 * @author Nicolas Rodriguez
 */
public class Application {

	public static boolean isRunning = true;

	/**
	 * Runs the controller
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws InterruptedException {
		Controller controller = new Controller();
		controller.run();
            // The TextInterface is used to shutdown the server
            //when it's running from the command line
            TextInterface tui = new TextInterface();
            tui.start();
		while (isRunning) {
			TimeUnit.SECONDS.sleep(1);
			System.out.println("holi29");
		}
		controller.interrupt();
	}

}
