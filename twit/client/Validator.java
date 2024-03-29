package twit.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

	private static final int DOMAIN_NAME_MAX_LENGTH = 253;
	private static final int MIN_PORT_NUMBER = 32768;
	private static final int MAX_PORT_NUMBER = 61000;
	private static final String HOSTNAME_REGEX = "\\A[A-Za-z0-9]{1}[A-Za-z0-9-.]*[A-Za-z0-9]{1}\\z|\\A[A-Za-z0-9]{1}\\z";

	/**
	 * Tells whether or not a given integer is in the range of valid port
	 * numbers.
	 * 
	 * @param port
	 *            A port number.
	 * @return Returns true if the given integer falls in the range 32768-61000
	 *         inclusive.
	 */
	public static boolean isValidPortNumber(int port) {
		return port >= MIN_PORT_NUMBER && port <= MAX_PORT_NUMBER;
	}

	/**
	 * Returns true if the given host-name is valid, false otherwise. A valid
	 * host-name consists of up to 127 levels, each containing up to 63
	 * characters. The allowed characters are a-z, 0-9, period (.) and dash (-).
	 * However, dash and period cannot be at the beginning or end of any of the
	 * parts of the domain name. The full domain name may not exceed 253
	 * characters.
	 * 
	 * Source: http://en.wikipedia.org/wiki/Domain_name#Parts_of_a_domain_name
	 * 
	 * @param hostname
	 *            A hostname String.
	 * @return Whether or not the hostname is valid.
	 */
	public static boolean isValidHostname(String hostname) {
		if (hostname.length() == 0
				|| hostname.length() > DOMAIN_NAME_MAX_LENGTH)
			return false;
		Pattern pattern = Pattern.compile(HOSTNAME_REGEX);
		Matcher matcher = pattern.matcher(hostname);
		return matcher.matches();
	}

	/**
	 * Returns true if the given profile name is valid, false otherwise. It is
	 * in valid if the string is empty or contains a semi-colon.
	 * 
	 * @param profileName
	 *            A profile name String.
	 * @return Whether or not the profile name is valid.
	 */
	public static boolean isValidProfileName(String profileName) {
		return profileName.length() > 0 && !profileName.contains(";");
	}
}
