package twit.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import twit.io.ByteWriter;

public class ClientHandler implements Runnable {

	private static final String TWIT_DIRECTORY = "/cs/home/os75/public_html/twit/";
	// wait for incoming message for 15 seconds before terminating connection
	private static final int TIME_OUT = 15000;

	private final static SimpleDateFormat DATE_FORMAT;
	private final static SimpleDateFormat TIME_FORMATTER;
	static {
		String dateFormat = new String("yyyy-MM-dd");
		DATE_FORMAT = new SimpleDateFormat(dateFormat);
		// 2009-03-02_07_32_27.0209
		String TIME_FORMAT = new String("yyyy-MM-DD_HH_mm_ss.SSSS");
		TIME_FORMATTER = new SimpleDateFormat(TIME_FORMAT);
	}

	private Socket client;

	/**
	 * Create a new ClientHandler which deals with a given client socket.
	 * 
	 * @param client
	 *            A socket connection with a client.
	 */
	public ClientHandler(Socket client) {
		this.client = client;
	}

	/**
	 * Deal with client: Attempt to receive message. If successful, send
	 * confirmation. Send error message back if the message is too long (more
	 * than 140 characters) or too short (0 characters). Otherwise, if the
	 * client times out, print error message to console.
	 */
	public void run() {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			client.setSoTimeout(TIME_OUT);
			inputStream = client.getInputStream();
			outputStream = client.getOutputStream();
			byte[] bytes = new byte[141];
			// Read up to 141 bytes from inputStream into the array "bytes".
			int length = inputStream.read(bytes);
			// length now stores the number of bytes read into the array.
			if (length > 140) {
				messageToLong(outputStream);
			} else {
				messageRightSize(outputStream, bytes, length);
			}
			// connection being closed in finally
			System.out.println("Connection closed.");
		} catch (SocketTimeoutException e) {
			// if socket timeout, terminate connection
			System.out
					.println("Client connection timed out. Terminating connection.");
		} catch (IOException e) {
			/*
			 * All other IOExceptions, including SocketException should be dealt
			 * with in the same way.
			 */
			System.out.println(e);
		} finally {
			try {
				inputStream.close();
				outputStream.close();
				client.close();
			} catch (IOException e) {
				// If connection has already closed, do nothing.
			}
		}
	}

	/**
	 * Writes the message to a file, and sends the message back to the client.
	 * Called if the received message is of the right size.
	 * 
	 * @param outputStream
	 *            An outputStream with a client.
	 * @param bytes
	 *            The message, as an array of bytes.
	 * @param length
	 *            The length of the message in the array.
	 * @throws IOException
	 *             If there is an error writing to the client's OutputStream.
	 */
	private void messageRightSize(OutputStream outputStream, byte[] bytes,
			int length) throws IOException {
		try {
			writeMessageToFile(bytes, length);
		} catch (IOException e) {
			System.out.println("Problem writing message to file:\n"
					+ e.getMessage());
		}
		// send message back to client
		ByteWriter.write(outputStream, bytes, length);
	}

	/**
	 * Called if the received message is too long. Prints an error message to
	 * the console and writes an error message back to the client.
	 * 
	 * @param outputStream
	 *            An outputStream with a client.
	 * @param bytes
	 *            The message, as an array of bytes.
	 * @param length
	 *            The length of the message in the array.
	 * @throws IOException
	 *             If there is an error writing to the client's OutputStream.
	 */
	private void messageToLong(OutputStream outputStream) throws IOException {
		System.out.println("Message recevied is too long.");
		// send error message to client
		ByteWriter.write(outputStream,
				"Message recevied was too long. Please try again.");
	}

	/**
	 * Find the appropriate directory and filename and write the message to a
	 * file.
	 * 
	 * @param bytes
	 *            An array of bytes, containing the message.
	 * @param length
	 *            The number of bytes of the message.
	 * @throws IOException
	 *             If it is not possible to write to the file.
	 */
	private void writeMessageToFile(byte[] bytes, int length)
			throws IOException {
		String dirName = TWIT_DIRECTORY + getDateStamp();
		File dir = new File(dirName);
		if (!dir.exists()) {
			createDir(dir);
		}
		String fileName = dirName + File.separator + getTimeStamp();
		File file = new File(fileName);
		if (file.exists()) {
			System.out.println("File already exists: " + fileName);
			System.out.println("Aborting.");
			return;
		}
		writeFile(file, bytes, length);
		String message = new String(bytes, 0, length);
		System.out.println("Message written to file:\n" + message);
	}

	/**
	 * Write a number of bytes to a file.
	 * 
	 * @param file
	 *            The file to be written to.
	 * @param bytes
	 *            An array of bytes.
	 * @param length
	 *            The number of bytes from the array to be written to the file.
	 * @throws IOException
	 *             If it is not possible to write to the file.
	 */
	public static void writeFile(File file, byte[] bytes, int length)
			throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(bytes, 0, length);
		out.flush();
		out.close();
	}

	/**
	 * Create a directory from a given File object.
	 * 
	 * @param dir
	 *            The directory File object to be created.
	 */
	private void createDir(File dir) {
		if (!dir.mkdirs()) {
			System.out.println("Could not create directory:\n\t"
					+ dir.getAbsolutePath());
			System.exit(0);
		}
	}

	/**
	 * Get the date stamp for today in the format: yyyy-MM-dd
	 * 
	 * @return A string date stamp.
	 */
	private String getDateStamp() {
		Date d = new Date();
		return DATE_FORMAT.format(d);
	}

	/**
	 * Get the time stamp for right now in the format: yyyy-MM-DD_HH_mm_ss.SSSS
	 * 
	 * @return A string time stamp.
	 */
	private String getTimeStamp() {
		Date d = new Date();
		return TIME_FORMATTER.format(d);

	}
}
