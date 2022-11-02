package signupsigninserver;

import controller.Controller;
import controller.ServerThread;

/**
 *
 * @author Nicolas Rodriguez
 */
public class Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       ServerThread thread  = new ServerThread();
       thread.start();
    }
    
}
