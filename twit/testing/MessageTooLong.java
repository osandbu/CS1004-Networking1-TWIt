package twit.testing;

import java.io.IOException;

import twit.client.MessageSender;

public class MessageTooLong {
	public static void main(String[] args) throws IOException {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 141; i++) {
			sb.append('0');
		}
		String message = sb.toString();
		
		String received = MessageSender.send(MessageSender.DEFAULT_SERVER, message);
		System.out.println(received);
	}
}
