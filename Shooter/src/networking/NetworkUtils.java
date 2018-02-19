package networking;

/**
 * A collection of network utilities used by both the client and the server.
 * @author Connor Stewart
 */
public class NetworkUtils {
	
	/**
	 * Parses a command from a message.
	 * @param message the message
	 * @return the command
	 */
	public static String parseCommand(String message) {
		String command = null;
		
		if (message != null) {
			if (message.contains("/")) {
				//get everything after the first occurrence of "/" and split it into string using the delimiter "/"
				//the + 1 is to avoid an empty string by having the first substring character being a "/"
				command = message.substring(0, message.indexOf("/"));
			} else {
				//if the message has no arguments the message is the command
				command = message;
			}
			
		}
		
		return command;
	}
	
	/**
	 * Parses arguments from a message
	 * @param message the message
	 * @return an array of arguments
	 */
	public static String[] parseArguements(String message) {
		String[] arguments = new String[0];
		
		if (message.contains("/")) {
			//the command is the first string before a "/"
			arguments = message.substring(message.indexOf("/") + 1, message.length()).split("/");
		}
		
		return arguments;
	}

}
