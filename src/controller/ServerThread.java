package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dataAccess.DAOServer;

import model.Action;
import model.Message;
import exceptions.EmailAlreadyExistsException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserDoesNotExistException;

/**
 * This class manages a request by a client
 * It allows us to manager multiple clients, each
 * will be served by their own thread
 * 
 * @author Nicolás Rodriguez
 */
public class ServerThread extends Thread {

    private static Socket skClient;
    protected static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private model.Package pack;
    private OutputStream output;
    private ObjectOutputStream auxOut;
    private InputStream input;
    private ObjectInputStream auxIn;

    /**
     * Creates a thread and its fluxes using the given socket
     */
    public ServerThread(Socket socket) {
        try {
            // Creating the vatiables necessary for the connection with the server
            skClient = socket;
            output = skClient.getOutputStream();
            auxOut = new ObjectOutputStream(output);
            input = skClient.getInputStream();
            auxIn = new ObjectInputStream(input);
            pack = (model.Package) auxIn.readObject();
            LOGGER.info("No exceptions.");
        } catch (SocketException se) {
            Logger.getLogger(Controller.class.getName()).log(Level.INFO, "Client disconnected");
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Starts the thread
     * Perfoms different actions depending on the content
     * of the received Package
     */
    @Override
    public void run() {
        LOGGER.info("Starting ServerThread.");
        try {
            // Login case
            if (pack.getAction().equals(Action.LOGIN)) {
                LOGGER.info("Login user " + pack.getUser());
                pack.setUser(new DAOServer().login(pack.getUser().getLogin()));
                pack.setMessage(Message.OK);
            }
            // Signup case
            if (pack.getAction().equals(Action.REGISTER)) {
                LOGGER.info("Register user " + pack.getUser());
                new DAOServer().signUp(pack.getUser());
                pack.setMessage(Message.OK);
            }
        } catch (UserDoesNotExistException e) { // The user couldn't be found on the database
            LOGGER.severe(e.getMessage());
            pack.setMessage(Message.USERDOESNOTEXIST);
        } catch (EmailAlreadyExistsException e) { // The email already exists on the database
            LOGGER.severe(e.getMessage());
            pack.setMessage(Message.EMAILALREADYEXISTS);
        } catch (UserAlreadyExistsException e) { // The user already exists on the database
            LOGGER.severe(e.getMessage());
            pack.setMessage(Message.USERALREADYEXISTS);
        } finally {
            try {
                // Send the Package back to the client
                auxOut.writeObject(pack);
                // Decrease the ServerThread count in the Controller
                Controller.threadCount--;
                // Stop the Thread
                this.interrupt();
            } catch (IOException e) { // IOException
                LOGGER.severe("IOExcetion regarding the socket." + e.getMessage());
            }
        }
    }
}
