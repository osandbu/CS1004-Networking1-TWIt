package twit.testing;

import java.io.IOException;

import twit.client.MessageSender;

public class ValidMessage {
	public static void main(String[] args) throws IOException {
		String message = "Here is my message to you.";

		String received = MessageSender.send(MessageSender.DEFAULT_SERVER,
				message);
		if (message.equals(received)) {
			System.out.println("Message sent successfully.");
		} else {
			System.out.println("Confirmation was not received.");
		}
	}
}
