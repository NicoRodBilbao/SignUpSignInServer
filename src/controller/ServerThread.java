package controller;

import dataAccess.DAOServer;
import exceptions.EmailAlreadyExistsException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserDoesNotExistException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;
import model.Action;
import model.Message;

/**
 *
 * @author Nicolás Rodriguez
 */
public class ServerThread extends Thread {
    static final String HOST = "localhost"; // TODO read from properties file
    static final int PORT = 9000; // TODO read from properties file
    private static Socket skClient;
    private static ServerSocket skServer;
    protected static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private model.Package pack;
    private OutputStream output;
    private ObjectOutputStream auxOut;
    private InputStream input;
    private ObjectInputStream auxIn;

    public ServerThread() {
        LOGGER.info("Starting controller.");
        try {
            // Creating the vatiables necessary for the connection with the server
            skServer = new ServerSocket(PORT);
            // Waits for a client to connect 
            LOGGER.info("Waiting for a client to respond...");
            skClient = skServer.accept();
            LOGGER.info("Connected to a client.");
            output = skClient.getOutputStream();
            auxOut = new ObjectOutputStream(output);
            input = skClient.getInputStream();
            auxIn = new ObjectInputStream(input);
            LOGGER.info("No exceptions.");
            // Receives a Package 
            pack = (model.Package) auxIn.readObject();
            // Login case
            if (pack.getAction().equals(Action.LOGIN)){
                pack.setUser(new DAOServer().login(pack.getUser().getLogin()));
                pack.setMessage(Message.OK);
            }
            // Signin case
            if (pack.getAction().equals(Action.REGISTER)) {
                new DAOServer().signUp(pack.getUser());
                pack.setMessage(Message.OK);
            }
            auxOut.writeObject(pack);
        } catch (IOException e) { // IOException
            LOGGER.severe("IOExcetion regarding the socket." + e.getMessage());
        } catch (ClassNotFoundException e) { // ClassNotFoundException
            LOGGER.severe("The class was not found." + e.getMessage());
        } catch (UserDoesNotExistException e) { // The user couldn't be found on the database
            LOGGER.severe(e.getMessage());
            pack.setMessage(Message.USERDOESNOTEXIST);
        } catch(EmailAlreadyExistsException e) { // The email already exists on the database
            LOGGER.severe(e.getMessage());
            pack.setMessage(Message.EMAILALREADYEXISTS);
        } catch (UserAlreadyExistsException e) { // The user already exists on the database
            LOGGER.severe(e.getMessage());
            pack.setMessage(Message.USERALREADYEXISTS);
        } finally {
            try {
                auxOut.writeObject(pack);
                skClient.close();
            } catch (IOException e) { // IOException
                LOGGER.severe("IOExcetion regarding the socket." + e.getMessage());
            }
        }
}
}
