package de.luh.vss.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import de.luh.vss.chat.common.MessageType;
import de.luh.vss.chat.common.User;
import de.luh.vss.chat.common.Message;
import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.ErrorResponse;
import de.luh.vss.chat.common.Message.RegisterRequest;
import de.luh.vss.chat.common.Message.RegisterResponse;
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
		try(Socket client = new Socket( "130.75.202.197", 4444);
		DataInputStream in = new DataInputStream(client.getInputStream());
		DataOutputStream out = new DataOutputStream(client.getOutputStream());){
			UserId id = new UserId(7262);
        	User user = new User(id, client.getLocalSocketAddress());
			final InetAddress inetAddress = InetAddress.getByName("localhost");
        	RegisterRequest request = new RegisterRequest(id, inetAddress, 73874);
        	request.toStream(out);
        	ChatMessage cm = new ChatMessage(user.getUserId(), "TEST 1 USER ID CORRECTNESS");
			cm.toStream(out);
			//Test 1 bis hier
			ChatMessage triggerMessage = new ChatMessage(user.getUserId(), "TEST 2 OUT OF BAND PROTOCOL MESSAGE");
        	triggerMessage.toStream(out);
			if(Message.parse(in).getMessageType() == MessageType.REGISTER_RESPONSE){
				String error = in.readUTF();
				Message.parse(in).toStream(out);
			}
			//Test 2 bis hier
			triggerMessage = new ChatMessage(user.getUserId(), "TEST 3 EXCEEDING MAX MESSAGE LENGTH");
        	triggerMessage.toStream(out);
			String err1 = in.readUTF();
			String err2 = in.readUTF();
			String z = in.readUTF();
			byte[] bytes = z.getBytes(StandardCharsets.UTF_8);
			int sum = bytes.length * 2; 
			int res = sum - 4000;
			cm = new ChatMessage(id, String.valueOf(res));
			cm.toStream(out);
			//Test 3 bis hier
			triggerMessage = new ChatMessage(user.getUserId(), "TEST 4 HANDLING ERROR MESSAGE");
        	triggerMessage.toStream(out);
			if(Message.parse(in).getMessageType() == MessageType.ERROR_RESPONSE){
				Message.parse(in).toStream(out);
			}
			//Test 4 bis hier

    	} catch (Exception e) {
        	System.err.println("Connection error: " + e.getMessage());
        	e.printStackTrace();
		}
	}

}
