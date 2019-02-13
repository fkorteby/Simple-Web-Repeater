/*
 * 
 */
package com.repeater.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.repeater.server.HTTPClient;
import com.repeater.server.SimpleHttpServer;
import com.repeater.server.SimpleHttpsServer;
import com.repeater.server.SimpleServer;

// TODO: Auto-generated Javadoc
/**
 * The Class WebRepeater.
 */
public class WebRepeater {
	
	/** The server. */
	private static SimpleServer server;
	
	/** The client. */
	private static HTTPClient client;
	
	/** The out stream. */
	private static PrintStream outStream = null;
	
	/** The err stream. */
	private static PrintStream errStream = null;
	
	/** The file stream. */
	private static PrintStream fileStream = null;
	
	/** The Constant GLOBAL_CONSOLE_FILE. */
	public final static String GLOBAL_CONSOLE_FILE = "console.log";
	
	/** The dest port. */
	public static String destPort;
	
	/** The dest domain. */
	public static String destDomain;
	
	/** The dest protocol. */
	public static String destProtocol;
	
	/** The src protocol. */
	public static String srcProtocol = "http";
	
	/** The src port. */
	public static String srcPort = "8000";
	
	/** The src domain. */
	public static String srcDomain = "localhost";

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws FileNotFoundException the file not found exception
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		// Read the parameters from arguments
		/*
		if (args.length > 1) {
			
			System.setProperty("jsse.enableSNIExtension", "false");
			
			destPort = args[1];
			destDomain = args[0];
			destProtocol = args[2];

			server = new SimpleHttpServer();
			server.Start(Integer.parseInt(srcPort));
		}*/
		
		try {
			
			// Redirect the stream to the file
			outStream = System.out;
			errStream = System.err;
			fileStream = new PrintStream(new FileOutputStream(GLOBAL_CONSOLE_FILE, true));
			System.setErr(fileStream);
			System.setOut(fileStream);
			
			RepeaterMainApp window = new RepeaterMainApp();
			window.open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	
	/**
	 * Start.
	 *
	 * @param srcPort the src port
	 * @param srcProtocol the src protocol
	 * @param destDomain the dest domain
	 * @param destPort the dest port
	 * @param destProtocol the dest protocol
	 * @return true, if successful
	 */
	public static boolean start(int srcPort, String srcProtocol, String destDomain, int destPort, String destProtocol){
		
		// Create the server according the protocol
		if((srcProtocol.equals("http") && server == null) || (srcProtocol.equals("http") && (server instanceof SimpleHttpsServer))){
			server = new SimpleHttpServer();
		} else if((srcProtocol.equals("https") && server == null) || (srcProtocol.equals("https") && (server instanceof SimpleHttpServer))){
			server = new SimpleHttpsServer();
		}			
		
		// Start the server
		if(server.getStatus()){
			System.out.println("The server is already started");
		} else {
			WebRepeater.destDomain = destDomain;
			WebRepeater.destPort = String.valueOf(destPort);
			WebRepeater.destProtocol = destProtocol;
			server.Start(srcPort);
		}
		
		return true;		
	}
	
	/**
	 * Stop.
	 *
	 * @return true, if successful
	 */
	public static boolean stop(){
		
		// Create the server
		if(server == null)
			return false;
		
		// Start the server
		if(!server.getStatus()){
			System.out.println("The server is already stoped");
		} else {
			server.Stop();
		}
		
		return true;		
	}
	
	/**
	 * Gets the src port.
	 *
	 * @return the src port
	 */
	public static String getSrcPort(){
		return srcPort;
	}
	
	/**
	 * Gets the dest port.
	 *
	 * @return the dest port
	 */
	public static String getDestPort() {
		return destPort;
	}

	/**
	 * Gets the src domain.
	 *
	 * @return the src domain
	 */
	public static String getSrcDomain() {
		return srcDomain;
	}
	
	/**
	 * Gets the dest domain.
	 *
	 * @return the dest domain
	 */
	public static String getDestDomain() {
		return destDomain;
	}
	
	/**
	 * Gets the src protocol.
	 *
	 * @return the src protocol
	 */
	public static String getSrcProtocol() {
		return srcProtocol;
	}
	
	/**
	 * Gets the dest protocol.
	 *
	 * @return the dest protocol
	 */
	public static String getDestProtocol() {
		return destProtocol;
	}

}
