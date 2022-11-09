package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;
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

    public static volatile Integer threadCount = 0;
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

    private Optional<ServerThread> createThread(Socket skClient) {

        Optional<ServerThread> newThread = Optional.empty();

        if (threadCount < THREADLIMIT) {
            newThread = Optional.of(new ServerThread(skClient));
            threadCount++;
        }

        return newThread;

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
                Optional<ServerThread> thr = this.createThread(socket);
                if(thr.isPresent())
                    thr.get().start();
                else
                    Logger.getLogger(Controller.class
                        .getName()).log(Level.SEVERE, null, "Cannot accept any more clients");
                Logger.getLogger(Controller.class
                    .getName()).log(Level.INFO, null, threadCount);
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
