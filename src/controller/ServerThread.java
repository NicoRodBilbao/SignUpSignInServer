package controller;

import dataAccess.DAOServer;
import exceptions.EmailAlreadyExistsException;
import exceptions.IncorrectPasswordException;
import exceptions.IncorrectUserException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserDoesNotExistException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Action;
import model.Message;

/**
 *
 * @author Nicolás Rodriguez
 */
public class ServerThread extends Thread {

    private static Socket skClient;
    private static ServerSocket skServer;
    protected static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private model.Package pack;
    private OutputStream output;
    private ObjectOutputStream auxOut;
    private InputStream input;
    private ObjectInputStream auxIn;
    public static boolean active = true;
    
    
    
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
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        LOGGER.info("Starting controller.");
        try {

            while (active) {
                // Login case
                if (pack.getAction().equals(Action.LOGIN)) {
                    try {
                        pack.setUser(new DAOServer().login(pack.getUser().getLogin()));
                    } catch (IncorrectUserException ex) {
                        Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IncorrectPasswordException ex) {
                        Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    pack.setMessage(Message.OK);
                }
                // Signup case
                if (pack.getAction().equals(Action.REGISTER)) {
                    new DAOServer().signUp(pack.getUser());
                    pack.setMessage(Message.OK);
                }
                active = false;
                auxOut.writeObject(pack);
                
            }
        } catch (IOException e) { // IOException
            LOGGER.severe("IOExcetion regarding the socket." + e.getMessage());
        }catch (UserDoesNotExistException e) { // The user couldn't be found on the database
            LOGGER.severe(e.getMessage());
            pack.setMessage(Message.USERDOESNOTEXIST);
        } catch(EmailAlreadyExistsException e) { // The email already exists on the database
            LOGGER.severe(e.getMessage());
            pack.setMessage(Message.EMAILALREADYEXISTS);
        } catch (UserAlreadyExistsException e) { // The user already exists on the database
            LOGGER.severe(e.getMessage());
            pack.setMessage(Message.USERALREADYEXISTS);
        }  finally {
            try {
                auxOut.writeObject(pack);
                this.interrupt();
            } catch (IOException e) { // IOException
                LOGGER.severe("IOExcetion regarding the socket." + e.getMessage());
            }
        }

    }
    public void stopThread(){
        this.active = false;
    }
    

}
