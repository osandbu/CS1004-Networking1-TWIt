package twit.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import twit.io.ByteReader;

public class MessageSender {
	public static final String DEFAULT_HOSTNAME = "host-os75.cs.st-andrews.ac.uk";
	public static final int DEFAULT_PORT = 60514;
	public static final ServerProfile DEFAULT_SERVER = new ServerProfile(
			"os75", DEFAULT_HOSTNAME, DEFAULT_PORT);
	// timeout after 15 seconds.
	public static final int SO_TIMEOUT = 4000;

	/**
	 * Sends a String message to a server, with a given hostname and port.
	 * 
	 * @param server
	 *            The server to which the message is to be sent.
	 * @param message
	 *            A String message.
	 * @return A string sent back by the server after the message is sent.
	 * @throws SocketTimeoutException
	 *             If the connection with the client times out.
	 * @throws UnknownHostException
	 *             If the hostname cannot be located.
	 * @throws IOException
	 *             If there is a problem with the connection with the client.
	 */
	public static String send(ServerProfile server, String message)
			throws SocketTimeoutException, UnknownHostException, IOException {
		return send(server.getHostname(), server.getPort(), message);
	}

	/**
	 * Sends a String message to a server, with a given hostname and port.
	 * 
	 * @param hostname
	 *            The hostname of the server.
	 * @param port
	 *            The port to connect to on the server.
	 * @param message
	 *            A String message.
	 * @return A string sent back by the server after the message is sent.
	 * @throws SocketTimeoutException
	 *             If the connection with the client times out.
	 * @throws UnknownHostException
	 *             If the hostname cannot be located.
	 * @throws IOException
	 *             If there is a problem with the connection with the client.
	 */
	public static String send(String hostname, int port, String message)
			throws SocketTimeoutException, UnknownHostException, IOException {
		return send(hostname, port, message.getBytes());
	}

	/**
	 * Sends a message to a server, with a given hostname and port. The message
	 * is contained in an array of bytes.
	 * 
	 * @param hostname
	 *            The hostname of the server.
	 * @param port
	 *            The port to connect to on the server.
	 * @param messageBytes
	 *            The bytes of a message.
	 * @return A string sent back by the server after the message is sent.
	 * @throws SocketTimeoutException
	 *             If the connection with the client times out.
	 * @throws UnknownHostException
	 *             If the hostname cannot be located.
	 * @throws IOException
	 *             If there is a problem with the connection with the client.
	 */
	public static String send(String hostname, int port, byte[] messageBytes)
			throws SocketTimeoutException, UnknownHostException, IOException {
		return send(hostname, port, messageBytes, messageBytes.length);
	}

	/**
	 * Sends a message to a server, with a given hostname and port. The message
	 * is contained in an array of bytes, and length of the message in the array
	 * is given.
	 * 
	 * @param hostname
	 *            The hostname of the server.
	 * @param port
	 *            The port to connect to on the server.
	 * @param messageBytes
	 *            The bytes of a message.
	 * @param length
	 *            The number of bytes of the message.
	 * @return A string sent back by the server after the message is sent.
	 * @throws SocketTimeoutException
	 *             If the connection with the client times out.
	 * @throws UnknownHostException
	 *             If the hostname cannot be located.
	 * @throws IOException
	 *             If there is a problem with the connection with the client.
	 */
	public static String send(String hostname, int port, byte[] messageBytes,
			int length) throws SocketTimeoutException, UnknownHostException,
			IOException {
		return send(hostname, port, messageBytes, 0, length);
	}

	/**
	 * Sends a message to a server, with a given hostname and port, and attempts
	 * to receive a confirmation that the message was received. The message is
	 * contained in an array of bytes, and the offset and length of the message
	 * in the array is given.
	 * 
	 * @param hostname
	 *            The hostname of the server.
	 * @param port
	 *            The port to connect to on the server.
	 * @param messageBytes
	 *            The bytes of a message.
	 * @param offset
	 *            The index of the beginning of the message.
	 * @param length
	 *            The number of bytes of the message.
	 * @return A string sent back by the server after the message is sent.
	 * @throws SocketTimeoutException
	 *             If the connection with the client times out.
	 * @throws UnknownHostException
	 *             If the hostname cannot be located.
	 * @throws IOException
	 *             If there is a problem with the connection with the client.
	 */
	public static String send(String hostname, int port, byte[] messageBytes,
			int offset, int length) throws SocketTimeoutException,
			UnknownHostException, IOException {
		if(length - offset <= 0) { 
			return "Cannot send empty string.";
		}
		Socket clientSocket = new Socket(hostname, port);
		clientSocket.setSoTimeout(SO_TIMEOUT);
		OutputStream out = clientSocket.getOutputStream();
		out.write(messageBytes, offset, length);
		InputStream in = clientSocket.getInputStream();
		String messageReceived = ByteReader.read(in, length);
		out.close();
		in.close();
		clientSocket.close();
		return messageReceived;
	}
}
