import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

  	 public static void main(String args[]) {
		int port = Integer.parseInt(args[0]);
		Server server = new Server( port );
		server.startServer();
  	 }

  	 ServerSocket echoServer = null;
  	 Socket clientSocket = null;
  	 int numConnections = 0;
  	 int port;
  	 List <User> users = new ArrayList<User>();

  	 public Server( int port ) {
		this.port = port;
   	 }

   	 public void stopServer() {
		System.out.println( "Server shutting down." );
		System.exit(0);
    	}
	

   	public void startServer() {
     	   try {
		    echoServer = new ServerSocket(port);
    	    }
    	    catch (IOException e) {
		    System.out.println(e);
   	     }   	
		System.out.println( "Server has been started on port. " + port );	
		while ( true ) {
		    try {
			clientSocket = echoServer.accept();
			numConnections ++;
			ServerConnection oneconnection = new ServerConnection(clientSocket, numConnections, this);
			new Thread(oneconnection).start();
		    }   
		    catch (IOException e) {
			System.out.println(e);
		    }
		}
 	   }
	synchronized public String addUser(String str){
		
		for (Iterator<User> iter = users.iterator(); iter.hasNext(); ) {
    			User element = iter.next();
			StringTokenizer tok = new StringTokenizer(str);
			if(element.getUser().equals(tok.nextToken())){
				
				return "Failed to Register, that name already exists.";
				}
			}
		StringTokenizer tok = new StringTokenizer(str);
		String username = tok.nextToken();
		User newUser = new User(username, tok.nextToken(), false);
		users.add(newUser);
		return "Created user " + username + ".";
    	}

	synchronized public String requestLogin(String str){
		for (Iterator<User> iter = users.iterator(); iter.hasNext(); ) {
    			User element = iter.next();
			StringTokenizer tok = new StringTokenizer(str);
			String username = tok.nextToken();
			if(element.getUser().equals(username)){
				int logStatus = element.loginAttempt(username, tok.nextToken());
				if (logStatus == 1) {
					return "User logged in.";
				}else { return "Wrong password.";
				}
			}
			}
		StringTokenizer tok = new StringTokenizer(str);
		String username = tok.nextToken();
		return "Username " + username + " does not exist.";
    	}
	
	synchronized public String requestInterest(String user, String follower){
		for (Iterator<User> iter = users.iterator(); iter.hasNext(); ) {
    			User element = iter.next();
			StringTokenizer tok = new StringTokenizer(follower);
			String newFollower = tok.nextToken();
			if(element.getUser().equals(newFollower)){
					//System.out.println("A user " + newFollower +" exists.");
				for (Iterator<User> iter2 = users.iterator(); iter2.hasNext(); ) {
					//System.out.println("Iterating users.");
    					User element2 = iter2.next();
					StringTokenizer tok2 = new StringTokenizer(user);
					String userToFollow = tok2.nextToken();
					if(element2.getUser().equals(userToFollow)){
						element2.addFollower(newFollower);
						return "User " + element2.getUser() + " is now following " + newFollower + ".";
					}
				}
				
			}
		}
		StringTokenizer tok = new StringTokenizer(follower);
		String username = tok.nextToken();
		return "Username " + username + " does not exist.";
	}

	synchronized public String requestLogStatus(String user){
		for (Iterator<User> iter = users.iterator(); iter.hasNext(); ) {
    			User element = iter.next();
			if(element.getUser().equals(user)){
				return "User " + user + " log status: " + element.getLogStatus ();
				}
		}
		StringTokenizer tok = new StringTokenizer(user);
		String username = tok.nextToken();
		return "Username " + username + " does not exist.";
	}

	synchronized public String postMessage (String user, String message){
		String returnMessage = "";
		String usersOnline = "";
		String usersOffline = "";
		for (Iterator<User> iter = users.iterator(); iter.hasNext(); ) {
    			User element = iter.next();
			if(element.getUser().equals(user)){
				element.addMessage(message);
				// iterating on all the users, searching for those interested, and adding to their inbox //
				for (Iterator<User> iter2 = users.iterator(); iter2.hasNext();) {
    					User element2 = iter2.next();
					// iterating on the list of followers, looking for matches //
					for (Iterator<String> iter3 = element2.followers.iterator(); iter3.hasNext(); ) {
    						String element3 = iter3.next();
						if(element3.equals(user)) {
							System.out.println("Added message to inbox of " + element2.getUser() + " .");
							element2.addToInbox(message);
							if(element2.loggedInStatus == true){ usersOnline = usersOnline + " " + element2.getUser();}
							if(element2.loggedInStatus == false){ usersOffline = usersOffline + " " + element2.getUser();}
							returnMessage = "A sua mensagem " + message + " foi entregue ao(s) utilizador(es)" + usersOnline + " e nao foi entregue ao(s) utilizador(es)" + usersOffline + "!";
							}
					}
					
				}
					
			}
		}
		StringTokenizer tok = new StringTokenizer(user);
		String username = tok.nextToken();
		return returnMessage;
	}
	
	synchronized public String flushInbox(String user){
		String returnMessage = "";
		for (Iterator<User> iter = users.iterator(); iter.hasNext(); ) {
    			User element = iter.next();
			if(element.getUser().equals(user)){
				returnMessage = element.dumpInbox();
				}
			}
		return returnMessage;
	}

	synchronized public void logoutUser(String user){
		for (Iterator<User> iter = users.iterator(); iter.hasNext(); ) {
			System.out.println( "estou a fazer logout de ." + user);
    			User element = iter.next();
			if(element.getUser().equals(user)){
				element.loggedInStatus = false;
				}
			}
		return;
	}

}

class User {
  	public  List <String> followers = new ArrayList<String>();
	private  List <String> inbox = new ArrayList<String>();
	public  List <String> messagesPosted = new ArrayList<String>();
  	public  boolean loggedInStatus;
   	private  String username;
	private  String password;
	
	// Constructor
	public User(String user, String pass, boolean loggedIn) {
   	 	username = user;
   		password = pass;
    		loggedInStatus = loggedIn;
		List <String> followers = new ArrayList<String>();
		}

	// Getters	
	public String getUser() {
		return username;
	}
	
	public String getPass() {
		return password;
	}
	
	public List<String> getFollowers () {
		return followers;
	}
	
	public boolean getLogStatus () {
		return loggedInStatus;
	}
	
	// Setters
	public void setPass (String newpass){
		password = newpass;
	}

	public void setLogin (boolean bool){
		loggedInStatus = bool;
	}
	
	// Methods
	public int loginAttempt(String user, String pass) {
		int logResponse = 0;
		if(username.equals(user) && pass.equals(password)){
			//System.out.println( "passou");
			logResponse = 1;
			this.setLogin(true);}
		return logResponse;
		}
	public void addFollower(String follower) {
		followers.add(follower);
		//System.out.println( "Following " + follower + ".");
	}
	
	public void addMessage(String message) {
		messagesPosted.add(message);
		System.out.println( "Stored message: " + message + ".");
	}

	public void addToInbox(String message) {
		inbox.add(message);
		//System.out.println( "Olá sou o : " + username + ".");
	}
	
	public String dumpInbox(){
		String returnMessage = "";
		for (Iterator<String> iter = this.inbox.iterator(); iter.hasNext();) {
  			String element = iter.next();
			returnMessage = returnMessage + element + "\n";
			iter.remove();
			}
		return returnMessage;
	}
	
}


class ServerConnection implements Runnable {
  	  BufferedReader is;
  	  PrintStream os;
  	  Socket clientSocket;
  	  int id;
  	  Server server;
	  String loggedUser = null;

  	  public ServerConnection(Socket clientSocket, int id, Server server) {
		this.clientSocket = clientSocket;
		this.id = id;
		this.server = server;
		System.out.println( "Connection " + id + " established");
		try {
		    is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    os = new PrintStream(clientSocket.getOutputStream());
		} catch (IOException e) {
		    System.out.println(e);
		}
 	   }
//////////////////////////// Threaded Server Connection Instance //////////////////
 	   public void run() {
 	       	String line;
		String response;
		String inbox;
		int nrTokens;
		try {
		    boolean serverStop = false;
       	     while (true) {
			response = "Invalid Command";
			if (loggedUser != null) {
				inbox = server.flushInbox(loggedUser);
				if (inbox.equals("")){;} else {
					os.println(inbox);
					}
				}
       	       		line = is.readLine();
			StringTokenizer str = new StringTokenizer(line);
			nrTokens = str.countTokens();
			System.out.println( "Received -" + line + "- from Connection " + id + "." );
			//System.out.println( "Number of tokens: " + nrTokens + "\nToken 1: -" + str.nextToken() + "-." );
			str = new StringTokenizer(line);
			if (str.nextToken().equals("register")){
				if (nrTokens != 3) {  response = "Invalid number of Arguments for command -register-.";
				} else {
				response = server.addUser(str.nextToken() + " " + str.nextToken());
				}	
			}
			str = new StringTokenizer(line);
			if (str.nextToken().equals("login")){
				if (loggedUser != null) {response = "A user is already logged in, please exit the client and open a new connection.";}
				 else {
					if (nrTokens != 3) {  response = "Invalid number of Arguments for command -login-.";
					} else {
					response = server.requestLogin(str.nextToken() + " " + str.nextToken());
					if (response.equals("User logged in.")){
					str = new StringTokenizer(line);
					str.nextToken();
					loggedUser = str.nextToken();
					System.out.println( "Logged user: " + loggedUser);
					}
					}	
				}
			}
			str = new StringTokenizer(line);
			if (str.nextToken().equals("interest")){
				if (loggedUser == null) {response = "Please log in to follow the user " + str.nextToken() + "." ;}
				 else {
					if (nrTokens != 2) {  response = "Invalid number of Arguments for command -interest-.";
					} else {
					response = server.requestInterest(loggedUser, str.nextToken());
					}	
				}	
			}
			str = new StringTokenizer(line);
			if (str.nextToken().equals("post")){
				if (loggedUser == null) {response = "Please log in to post a message." ;}
				 else {
					if (nrTokens < 2) {  response = "Invalid number of Arguments for command -post-.";
					} else {
					Calendar calendar = Calendar.getInstance();
					java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
					String [] message = line.split(" ", 2);
					String processedMessage = "[" + currentTimestamp + "] [" + loggedUser + "] [" + message[1] + "]";
					//System.out.println(processedMessage);
					response = server.postMessage(loggedUser, processedMessage);
					}	
				}	
			}
			str = new StringTokenizer(line);
			if (str.nextToken().equals("refresh")){
				if (loggedUser == null) {response = "" ;}
				 else {
					if (nrTokens > 1) {  response = "Invalid number of Arguments for command -refresh-.";
					} else { response = "" ;
					}	
				}	
			}
        	       	os.println(response); 
			if (line.equals("exit")) {
				if (loggedUser != null) {server.logoutUser(loggedUser);
				System.out.println( "passo por aqui");}
			 	break;}
		}

	   	 System.out.println( "Connection " + id + " closed." );
           	 is.close();
           	 os.close();
           	 clientSocket.close();

	   	 if ( serverStop ) server.stopServer();
		} catch (IOException e) {
		    System.out.println(e);
		    if (loggedUser != null) {server.logoutUser(loggedUser);}
		}
		  catch (NullPointerException e) {
		    System.err.println("Connection has been lost with Client " + id);
		    if (loggedUser != null) {server.logoutUser(loggedUser);}
		}
    	}
}
