package controller;

import dataAccess.DAOServer;
import exceptions.EmailAlreadyExistsException;
import model.Package;
import exceptions.ServerException;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Action;
import model.Message;

/**
 * This class will create and manage threads in order to access the databse from
 * multiple clients
 *
 * @author Nicolas Rodriguez
 */
public class Controller {
    private static ArrayList<ServerThread> threadList = new ArrayList();
    
    /*private void createThread() {
        
    }*/
}
