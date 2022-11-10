package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import signupsigninserver.TextInterface;

/**
 * This class will create and manage threads in order to access the databse from
 * multiple clients
 *
 * @author Markel & Joana
 */
public class Controller extends Thread {

    protected final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    public static boolean isRunning = true;
    private ServerSocket serverSocket;
    private Socket socket;

    // Access the config file
    protected ResourceBundle configFile = ResourceBundle.getBundle("dataAccess.config");

    // The port the server will be broadcasting in
    protected Integer PORT = Integer.parseInt(configFile.getString("PORT"));
    // The thread limit (concurrent user limit)
    private final Integer THREAD_LIMIT = Integer.parseInt(configFile.getString("CLIENT_LIMIT"));

    // This counter is accesible from anywhere in the program
    // we use it to check how many threads are currently running
    public static volatile int threadCount = 0;
    
    /**
    * Creates a thread only if the thread limit
    * hasn't been reached
    */
    private void createThread(Socket skClient) {
        if (threadCount < THREAD_LIMIT) {
            // Create the new thread with the socket recieved from
            // the accepted connection
            ServerThread thr = new ServerThread(skClient);
            // Increment thread count and start the thread
            threadCount++;
            thr.start();
        }
    }

    /**
    * Starts the controller thread
    */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);

            while (isRunning) {
                // accepts a client connection
                socket = serverSocket.accept();
                // creates a thread with the accepted connection
                this.createThread(socket);
            }
	    LOGGER.info("Shutting down the server...");
            // close the sockets when since the program is being shut down
            socket.close();
            serverSocket.close();

        } catch (IOException ex) {
            Logger.getLogger(Controller.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}
