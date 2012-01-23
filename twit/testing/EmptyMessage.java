package twit.testing;

import java.io.IOException;

import twit.client.MessageSender;

public class EmptyMessage {
	public static void main(String[] args) throws IOException {
		String message = "";
		String received = MessageSender.send(MessageSender.DEFAULT_SERVER,
				message);
		System.out.println(received);
	}
}
