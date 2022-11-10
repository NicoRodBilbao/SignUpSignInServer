package signupsigninserver;

import controller.Controller;

/**
 * Main class for our Server Application
 * contains the main method
 * @author Nicolás Rodríguez
 */
public class Application {

    /**
     * Runs the controller
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.run();
    }

}
