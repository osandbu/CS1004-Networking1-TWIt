package twit.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public final class Server implements Runnable {

	private static final int PORT_NUMBER = 60514;
	private ServerSocket serverSocket;

	/**
	 * Set up server at PORT_NUMBER.
	 * 
	 * @throws IOException
	 *             if there is a problem establishing a server socket at the
	 *             specified port number.
	 */
	public Server() throws SocketException, IOException {
		serverSocket = new ServerSocket(PORT_NUMBER);
		String address = InetAddress.getLocalHost().getHostName();
		System.out.print("Server socket established at ");
		System.out.println(address + ":" + PORT_NUMBER + ".");
		new Thread(this).start();
	}

	/**
	 * Accept connections from serverSocket.
	 */
	@Override
	public void run() {
		try {
			while (true) {
				Socket client = serverSocket.accept();
				String address = client.getInetAddress().getHostName();
				int port = client.getPort();
				System.out.print("Connection established with ");
				System.out.println(address + ":" + port + ".");
				Runnable clientHandler = new ClientHandler(client);
				new Thread(clientHandler).start();
			}
		} catch (IOException e) {
			System.out.println("E: " + e);
		}
	}
}
