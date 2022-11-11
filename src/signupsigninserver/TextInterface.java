/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signupsigninserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Detects user input from the console and
 * activates a terminate flag
 * @author Markel / Joana
 */
public class TextInterface extends Thread {
	private BufferedReader in;
	protected static final Logger LOGGER = Logger.getLogger(TextInterface.class.getName());

	@Override
	public void run(){

		in = new BufferedReader(new InputStreamReader(System.in));

		try {
			in.readLine();
			Application.shutdown();
		} catch (IOException ex) {
			LOGGER.severe("Exception in the TUI: " + ex.getMessage());
		}

	}
}
