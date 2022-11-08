package controller;

import exceptions.ServerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
    private Integer threadLimit = 10;
    protected final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    public static boolean isRunning = true;
    private ServerSocket serverSocket;
    private Socket socket;
    private InputStream input;
    private ObjectInputStream auxIn;
    private model.Package pack;
    
    protected ResourceBundle configFile = ResourceBundle.getBundle("dataAccess.config");

    protected Integer PORT = Integer.parseInt(configFile.getString("PORT"));
    

    private void createThread(int n) {

        for (int i = 0; i < n; i++) {
            try {
                ServerThread newThread = new ServerThread(serverSocket, socket, pack);
                freeThreads.push(newThread);
                if (freeThreads.size() + usedThreads.size() >= threadLimit) {
                    throw new ServerException();
                }
            } catch (ServerException e) {
                // TODO Exception parametrization
                // connection limit surpassed
                while (freeThreads.size() > threadLimit) {
                    freeThreads.pop().interrupt();
                }
            }
        }
    }

    private ServerThread getThread() {
            // Check if there are any free connections
            // create one if the stack is empty
            if (freeThreads.empty()) {
                this.createThread(1);
            }
            // Move connection from free to used and return it
            ServerThread thr = freeThreads.pop();
            if (thr == null) {
                LOGGER.severe("There are no free threads");
            }
            usedThreads.add(thr);
            return thr;
        
    }
    
    private void startAllThreads() {
        freeThreads.forEach(thr -> thr.start());
    }
    
    private void stopAllThreads(){
        freeThreads.forEach(thr -> thr.interrupt());
        usedThreads.forEach(thr -> thr.interrupt());
    }
    
    private void removeDeadThreads() {
        freeThreads.removeIf(thr -> !thr.isAlive());
        usedThreads.removeIf(thr -> !thr.isAlive());
    }
   
    public void run(){
        try {
            serverSocket = new ServerSocket(PORT);
            socket = serverSocket.accept();
            TextInterface tui = new TextInterface();
            tui.start();
            input = socket.getInputStream();
            auxIn = new ObjectInputStream(input);
            while(isRunning){
                
                pack = (model.Package) auxIn.readObject();
                if(pack != null){
                    this.createThread(1);
                    ServerThread thr = freeThreads.get(0);
                    thr.start();
                }else{
                    LOGGER.info("Package is null");
                }
                
                this.removeDeadThreads();
            }
            socket.close();
            serverSocket.close();
            this.stopAllThreads();
            
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
}
