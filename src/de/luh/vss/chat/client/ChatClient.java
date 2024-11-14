package de.luh.vss.chat.client;

import java.io.IOException;

import de.luh.vss.chat.common.Message;
import de.luh.vss.chat.common.User;
import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.User.UserId;

public class ChatClient {
	public static void main(String... args) {
		try {
			new ChatClient().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() throws IOException {
		System.out.println("Congratulation for successfully setting up your environment for Assignment 1!");
		
		// implement your chat client logic here
		UserId id = new UserId(7262);
		
		ChatMessage mess = new ChatMessage(id, "TEST 1 USER ID CORRECTNESS");
		System.out.println(mess.toString());
	}

}
