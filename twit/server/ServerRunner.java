package twit.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

public class ServerRunner {
	public static void main(String[] args) {
		System.out.println("Starting server... Enter QUIT to quit.");
		try {
			new Server();
		} catch (SocketException e) {
			System.out
					.println("Socket exception: Port already taken by another service.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e);
		}
		Scanner consoleIn = new Scanner(System.in);
		String input;
		do {
			System.out.print(">> ");
			input = consoleIn.nextLine();
		} while (!input.equalsIgnoreCase("quit"));
		System.out.println("Shutting down server...");
		// Shut down server. Killing any active ClientHandler threads.
		System.exit(0);
	}
}
