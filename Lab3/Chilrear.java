import java.io.*;
import java.net.*;

public class Chilrear extends Thread {
	
	static Socket clientSocket = null;  
	static DataOutputStream os = null;
	static BufferedReader is = null;

    public static void main(String[] args) {
	
	String hostname = args[0];
	int port = Integer.parseInt(args[1]);
///// CREATING THE SOCKET ///// 
        try {
            clientSocket = new Socket(hostname, port);
            os = new DataOutputStream(clientSocket.getOutputStream()); // Output
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Input
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + hostname);
        } catch (IOException e) {
            System.err.println("Couldn't connect to: " + hostname  + " with port: " + port);
        }
	 catch (IllegalArgumentException e) {
            System.err.println("Error: Please use a valid host and port.");
	    return;
        }
	
	if (clientSocket == null || os == null || is == null) {
	    System.err.println( "Perhaps the server is not running?" );
	    return;
	}
///// INITIALIZING CLIENT - WRITE SIDE /////	
	Chilrear cliente = new Chilrear();
	cliente.start();
	System.out.println("/--- AVAILABLE COMMANDS ARE ---/");
	System.out.println("-register [name] [password]");
	System.out.println("-login    [name] [password]");
	System.out.println("-interest [name]");
	System.out.println("-post     [message]");
	System.out.println("-exit");
	System.out.println("-refresh\n");
	System.out.print( "Enter a command: " );
	try {
	    while ( true ) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String keyboardInput = br.readLine();
		os.writeBytes( keyboardInput + "\n" );
		if (keyboardInput.equals("exit")) break;
	    }
	    os.close();
	    is.close();
	    clientSocket.close(); 
	} catch (UnknownHostException e) {
	    System.err.println("Trying to connect to unknown host: " + e);
	} catch (IOException e) {}
    }  

	public void run(){
///// INITIALIZING CLIENT - READ SIDE /////	
		try {
	  	  while ( true ) {
			String responseLine = is.readLine();
			if (responseLine.equals("exit")) break;
			System.out.print(">" + responseLine + "\n-");
		    }
		}  catch (IOException e) {
		    System.err.println("Connection with server closed.");
		}
		  catch (NullPointerException e) {
		    System.err.println("Connection with server has been lost.");
		}
	}         
}
