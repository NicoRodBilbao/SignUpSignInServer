/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signupsigninserver;

import controller.Controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;

/**
 * Detects user input from the console and
 * activates a terminate flag
 * @author Markel & Joana
 */
public class TextInterface extends Thread {
    private BufferedReader in;
    private String line = "";
    public void run(){

        in = new BufferedReader(new InputStreamReader(System.in));
        
        while(Controller.isRunning){
            try {
                line = in.readLine();
                if(!line.equals("seguir")){
                    Controller.isRunning = false;
                }
            } catch (IOException ex) {
                Logger.getLogger(TextInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
