package controller;

import exceptions.ServerException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Logger;

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

    private void createThread(int n) {

        for (int i = 0; i < n; i++) {
            try {
                ServerThread newThread = new ServerThread();
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
    public void run(){
        this.getThread();
    }
    
    
}
