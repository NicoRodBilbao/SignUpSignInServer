package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import signupsigninserver.TextInterface;

/**
 * This class will create and manage threads in order to access the databse from
 * multiple clients
 *
 * @author Markel & Joana
 */
public class Controller {

    private static Stack<ServerThread> freeThreads = new Stack<ServerThread>();
    private static ArrayList<ServerThread> usedThreads = new ArrayList<ServerThread>();

    protected final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    public static boolean isRunning = true;
    private ServerSocket serverSocket;
    private Socket socket;
    private InputStream input;
    private ObjectInputStream auxIn;
    private OutputStream os;
    private ObjectOutputStream oos;

    protected ResourceBundle configFile = ResourceBundle.getBundle("dataAccess.config");

    protected Integer PORT = Integer.parseInt(configFile.getString("PORT"));
    private final Integer THREADLIMIT = Integer.parseInt(configFile.getString("CLIENT_LIMIT"));

    public static volatile int threadCount = 0;
    
    private void createThread(Socket skClient) {
        if (threadCount < 10) {
            ServerThread thr = new ServerThread(skClient);
            threadCount++;
            thr.start();
        }
    }

    private void stopAllThreads() {
        freeThreads.forEach(thr -> thr.interrupt());
        usedThreads.forEach(thr -> thr.interrupt());
    }

    public void run() {

        try {
            serverSocket = new ServerSocket(PORT);
            TextInterface tui = new TextInterface();
            tui.start();

            while (isRunning) {
                socket = serverSocket.accept();
                this.createThread(socket);
            }
            socket.close();
            serverSocket.close();
            this.stopAllThreads();

        } catch (IOException ex) {
            Logger.getLogger(Controller.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
