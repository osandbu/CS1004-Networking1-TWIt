package twit.client;

import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import twit.awt.HorizontalPanel;
import twit.awt.Popup;
import twit.awt.VerticalPanel;

@SuppressWarnings("serial")
public class GUIClient extends JFrame implements ActionListener {
	private static final String SERVER_PROFILE_FILENAME = "servers.txt";
	private static final String TITLE = "TWIt Client";
	private JComboBox serverComboBox;
	private JTextArea textArea;
	private JButton sendButton;

	// Vector of ServerProfiles.
	private Vector<ServerProfile> servers;
	private MenuItem quitMenuItem;
	private MenuItem editServerMenuItem;
	private MenuItem addServerMenuItem;
	private MenuItem deleteServerMenuItem;

	/**
	 * Create a new GUIClient window and make it visible.
	 */
	public GUIClient() {
		super(TITLE);
		loadServerProfiles();

		makeGUI();

		setVisible(true);
		textArea.requestFocus();
	}

	/**
	 * Initialise all GUI components of the main GUIClient window.
	 */
	private void makeGUI() {
		setWindowListener();
		this.setSize(300, 275);
		// put frame to the center of the screen
		setLocationRelativeTo(null);
		initMenus();
		final JPanel mainPanel = new VerticalPanel();
		JPanel serverPanel = initSelectServerPanel();
		mainPanel.add(serverPanel);
		JPanel textFieldPanel = initTextAreaPanel();
		mainPanel.add(textFieldPanel);
		initTextArea();
		mainPanel.add(textArea);
		JPanel buttonPanel = initButtonPanel();
		mainPanel.add(buttonPanel);
		setContentPane(mainPanel);
	}

	/**
	 * Setup a window listener which makes the program save the profile servers
	 * and close the problem when the window is closed.
	 */
	private void setWindowListener() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				/*
				 * Make the program save the profile servers and shut down the
				 * process when the window is closing.
				 */
				saveAndExit();
			}
		});
	}

	/**
	 * Initialise the text area; turning on word-wrap, and adding a
	 * keyboard-shortcut to send a message (Ctrl+S) to the textarea.
	 */
	private void initTextArea() {
		textArea = new JTextArea();
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				int keyCode = keyEvent.getKeyCode();
				if (keyCode == KeyEvent.VK_S && keyEvent.isControlDown()) {
					sendButton.doClick();
				}
			}
		});
		// turn on word-wrap
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
	}

	/**
	 * Initialise the menus. Including adding ActionListeners and shortcuts.
	 */
	private void initMenus() {
		Menu fileMenu = new Menu("File");
		quitMenuItem = new MenuItem("Quit");
		quitMenuItem.setShortcut(new MenuShortcut(KeyEvent.VK_Q));
		fileMenu.add(quitMenuItem);

		Menu editMenu = new Menu("Edit");
		addServerMenuItem = new MenuItem("Add new server");
		addServerMenuItem.setShortcut(new MenuShortcut(KeyEvent.VK_N));
		editMenu.add(addServerMenuItem);
		editServerMenuItem = new MenuItem("Edit selected server");
		editServerMenuItem.setShortcut(new MenuShortcut(KeyEvent.VK_E));
		editMenu.add(editServerMenuItem);
		deleteServerMenuItem = new MenuItem("Delete selected server");
		editMenu.add(deleteServerMenuItem);

		addMenuActionListeners();

		MenuBar menuBar = new MenuBar();
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		this.setMenuBar(menuBar);
	}

	/**
	 * Add ActionListener to each of the MenuItems.
	 */
	private void addMenuActionListeners() {
		MenuItem[] menuItems = { quitMenuItem, editServerMenuItem,
				addServerMenuItem, deleteServerMenuItem };

		for (MenuItem item : menuItems) {
			item.addActionListener(this);
		}
	}

	/**
	 * Initialise select server-profile panel, containing a label and a ComboBox
	 * from which server-profiles can be chosen.
	 * 
	 * @return The select server profile panel.
	 */
	private JPanel initSelectServerPanel() {
		JPanel serverPanel = new HorizontalPanel();
		JLabel serverLabel = new JLabel("Select server: ");
		serverComboBox = new JComboBox(servers);
		initButtonPanel();
		serverPanel.add(serverLabel);
		serverPanel.add(serverComboBox);
		return serverPanel;
	}

	private JPanel initTextAreaPanel() {
		JPanel textPanel = new HorizontalPanel();
		JLabel enterTextLabel = new JLabel("Enter text to TWIt:");
		textPanel.add(enterTextLabel);
		Dimension min = new Dimension(10000, 1);
		textPanel.add(new Box.Filler(min, min, min));
		return textPanel;
	}

	private JPanel initButtonPanel() {
		JPanel buttonPanel = new HorizontalPanel();
		sendButton = new JButton("Send");
		addListeners(sendButton);
		buttonPanel.add(sendButton);
		return buttonPanel;
	}

	/**
	 * Add Action- and KeyListeners to a button.
	 * 
	 * @param button
	 *            A button.
	 */
	private void addListeners(final JButton button) {
		button.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					button.doClick();
			}
		});
		button.addActionListener(this);
	}

	/**
	 * Load server profiles from a file. If the file does not exist, or is
	 * empty, add a default profile to the list of profiles.
	 */
	private void loadServerProfiles() {
		servers = new Vector<ServerProfile>();
		File serverFile = new File(SERVER_PROFILE_FILENAME);
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(serverFile));
		} catch (FileNotFoundException fnfe) {
			createProfileServerFile(serverFile);
			addDefaultServer();
			return;
		}
		try {
			while (in.ready()) {
				ServerProfile server = ServerProfile.fromLine(in.readLine());
				if (server != null)
					servers.add(server);
			}
			in.close();
		} catch (IOException ioe) {
			reportError("Error reading profile server file, loading defaults.",
					"Reading file");
		}
		if (servers.isEmpty()) {
			addDefaultServer();
		}
	}

	/**
	 * Add default server to the list of profiles.
	 */
	private void addDefaultServer() {
		servers.add(MessageSender.DEFAULT_SERVER);
	}

	private void createProfileServerFile(File serverFile) {
		try {
			serverFile.createNewFile();
		} catch (IOException e) {
			reportError(
					"The profile server file could not be found or created.\nIt may not be possible to save profile servers.",
					"Writing error");
		}
	}

	/**
	 * Save server profiles to a pre-specified file.
	 */
	private void saveServerProfiles() {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(SERVER_PROFILE_FILENAME)));
			for (ServerProfile server : servers) {
				out.println(server.toLine());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			reportError("The server profile file could not be saved.",
					"Writing error");
		}
	}

	/**
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source instanceof JButton) {
			JButton button = (JButton) source;
			if (button == sendButton) {
				sendMessage();
			}
		} else if (source instanceof MenuItem) {
			MenuItem item = (MenuItem) source;
			if (item == quitMenuItem) {
				saveAndExit();
			} else if (item == editServerMenuItem) {
				editServer();
			} else if (item == addServerMenuItem) {
				addServer();
			} else if (item == deleteServerMenuItem) {
				deleteServer();
			}
		}
	}

	/**
	 * Save server profiles and close the application.
	 */
	private void saveAndExit() {
		saveServerProfiles();
		System.exit(0);
	}

	/**
	 * Open a window in which the user can edit the server currently selected in
	 * the ComboBox.
	 */
	private void editServer() {
		int index = serverComboBox.getSelectedIndex();
		if (index == -1) {
			reportError("There is no server to edit.", "No server");
		}
		new ServerProfileDialog(this, servers, index);
		refreshServerComboBox();
		serverComboBox.setSelectedIndex(index);
	}

	/**
	 * Open a window from which the user can add a new server profile.
	 */
	private void addServer() {
		new ServerProfileDialog(this, servers);
		refreshServerComboBox();
		serverComboBox.setSelectedIndex(servers.size() - 1);
	}

	/**
	 * Refresh the profile server combo box with a newly edited/added profile.
	 */
	private void refreshServerComboBox() {
		serverComboBox.setModel(new DefaultComboBoxModel(servers));
	}

	/**
	 * Delete the currently selected server profile.
	 */
	private void deleteServer() {
		Object selected = serverComboBox.getSelectedItem();
		if (selected == null) {
			reportError("There is no server to delete.", "No server");
		}
		ServerProfile server = (ServerProfile) selected;
		String profileName = server.getProfileName();
		int option = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete the " + profileName
						+ " profile?", "Delete?", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (option == JOptionPane.YES_OPTION) {
			servers.remove(serverComboBox.getSelectedIndex());
			refreshServerComboBox();
		}
	}

	/**
	 * Send the text written in the textArea to the server specified by the
	 * combobox.
	 */
	private void sendMessage() {
		String message = textArea.getText().trim();
		if (message.length() == 0) {
			reportError("Please enter a message before trying to send.",
					"No message entered");
			return;
		} else if (message.length() > 140) {
			reportError(
					"The maximum message length is 140 characters.\nPlease shorten it.",
					"Message too long");
			return;
		} else if (message.indexOf('\n') != -1)
			// replace new line symbols with spaces.
			message = message.replaceAll("\n", " ");

		Object selected = serverComboBox.getSelectedItem();
		if (selected == null) {
			reportError(
					"There is no server to send the message to.\nPlease create a server profile and try again.",
					"No server");
		}
		ServerProfile server = (ServerProfile) selected;
		try {
			String received = MessageSender.send(server, message);
			if (received.equals(message)) {
				report("Message sent.", "Success");
				textArea.setText("");
			} else {
				reportError(received, "Unsuccessful");
			}
		} catch (SocketTimeoutException ste) {
			reportError("Connection timed out. Please try again later.",
					"Server not responding");
		} catch (UnknownHostException uhe) {
			reportError("Could not connect to server.\n"
					+ "Make sure the hostname and port is correct.",
					"Invalid server");
		} catch (IOException ioe) {
			reportError("Could not connect to server. Please try again later.",
					"Server not responding");
		}
	}

	/**
	 * Report some information to the user in a pop-up window.
	 * 
	 * @param message
	 *            The message to be displayed.
	 * @param title
	 *            The title of the window.
	 */
	public void report(String message, String title) {
		Popup.report(this, message, title);
	}

	/**
	 * Report an error to the user in a pop-up window.
	 * 
	 * @param message
	 *            The error-message to be displayed.
	 * @param title
	 *            The title of the window.
	 */
	public void reportError(String message, String title) {
		Popup.reportError(this, message, title);
	}

	/**
	 * Runs the program.
	 * 
	 * @param args
	 *            Array of strings. Unused.
	 */
	public static void main(String[] args) {
		new GUIClient();
	}
}
