package twit.testing;

import twit.client.Validator;

public class HostnameValidationTesting {
	public static void main(String[] args) {
		String[] hostnames = { "host-os75", "host-os75.cs.st-andrews.ac.uk",
				"a", "host.com" , "-host-", "host-", "-host"};

		for (String hostname : hostnames) {
			boolean bol = Validator.isValidHostname(hostname);
			System.out.println(hostname + ": " + bol);
		}
	}
}
