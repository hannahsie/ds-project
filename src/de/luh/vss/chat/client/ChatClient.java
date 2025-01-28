package de.luh.vss.chat.client;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import de.luh.vss.chat.common.User;
import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.RegisterRequest;
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
		/*try(Socket client = new Socket( "130.75.202.197", 4444);
		DataInputStream in = new DataInputStream(client.getInputStream());
		DataOutputStream out = new DataOutputStream(client.getOutputStream());){
			UserId id = new UserId(7262);
        	User user = new User(id, client.getLocalSocketAddress());
			final InetAddress inetAddress = InetAddress.getByName("localhost");
			RegisterRequest request = new RegisterRequest(id, inetAddress, 4444);
            request.toStream(out);
			// Test 1:
			ChatMessage triggerMessage = new ChatMessage(id, "TEST 1 MISSING MESSAGE RECONCILIATION");
			DatagramSocket socket = new DatagramSocket();
           	InetAddress userAddress = InetAddress.getByName("130.75.202.197");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			triggerMessage.toStream(dos);

			byte[] sendData = baos.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, userAddress, 5005);
            socket.send(sendPacket);

			byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			Map<Integer, DatagramPacket> unacknowledgedMessages = new LinkedHashMap<>();
			int count = 0;

        while (true) {

            socket.receive(receivePacket);
            String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);

            System.out.println("Empfangen: " + receivedMessage);

            if (receivedMessage.contains("TEST 1 MISSING MESSAGE RECONCILIATION SUCCESSFULLY PASSED")) {
                System.out.println("Test erfolgreich abgeschlossen!");
                break;
            }

            socket.send(receivePacket);

            if (receivedMessage.substring(10).startsWith("Test 1")) {
				count++;
                int messageId = count;
				byte[] data = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
				DatagramPacket clonePacket = new DatagramPacket(data, data.length, receivePacket.getAddress(), receivePacket.getPort());
                unacknowledgedMessages.put(messageId, clonePacket);
            }

            if (receivedMessage.substring(10).startsWith("Acknowledged message: ")) {
                int acknowledgedId = Integer.parseInt((String) receivedMessage.subSequence(53, 54));
				System.out.println(acknowledgedId);
                unacknowledgedMessages.remove(acknowledgedId);
                System.out.println("Bestätigung erhalten für Nachricht: " + acknowledgedId);
            }

            if (!unacknowledgedMessages.isEmpty()) {
                for (DatagramPacket packet : unacknowledgedMessages.values()) {
                    socket.send(packet);
                }
            }
        }
		
			//Test2:
			triggerMessage = new ChatMessage(id, "TEST 2 OFFLINE BUFFERING OF MESSAGES");
			DatagramSocket socket2 = new DatagramSocket();
           	InetAddress userAddress2 = InetAddress.getByName("130.75.202.197");
			ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
			DataOutputStream dos2 = new DataOutputStream(baos2);
			triggerMessage.toStream(dos2);

			byte[] sendData2 = baos2.toByteArray();
            DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, userAddress2, 7007);
            socket2.send(sendPacket2);

			byte[] receiveData2 = new byte[1024];
            DatagramPacket receivePacket2 = new DatagramPacket(receiveData2, receiveData2.length);
			Map<Integer, DatagramPacket> unacknowledgedMessages2 = new LinkedHashMap<>();
			count = 0;
			while(true){
				socket2.receive(receivePacket2);
				String receivedMessage = new String(receivePacket2.getData(), 0, receivePacket2.getLength(), StandardCharsets.UTF_8);
				System.out.println(receivedMessage);
				if(receivedMessage.contains("User is now Offline and cannot receive messages.")){
					break;
				}
				socket2.send(receivePacket2);
				count++;
                int messageId = count;
				byte[] data = Arrays.copyOf(receivePacket2.getData(), receivePacket2.getLength());
				DatagramPacket clonePacket = new DatagramPacket(data, data.length, receivePacket2.getAddress(), receivePacket2.getPort());
                unacknowledgedMessages2.put(messageId, clonePacket);
			}
			socket2.receive(receivePacket2);
			String receivedMessage = new String(receivePacket2.getData(), 0, receivePacket2.getLength(), StandardCharsets.UTF_8);
			if(receivedMessage.contains("User is now Online and can receive messages")){
				while(true){
					socket2.receive(receivePacket2);
					receivedMessage = new String(receivePacket2.getData(), 0, receivePacket2.getLength(), StandardCharsets.UTF_8);
					if(receivedMessage.contains("TEST 2 OFFLINE BUFFERING OF MESSAGES")){
						System.out.println(receivedMessage);
						break;
					}
					if (receivedMessage.substring(10).startsWith("Acknowledged message: ")) {
						int acknowledgedId = Integer.parseInt((String) receivedMessage.subSequence(53, 54));
						System.out.println(acknowledgedId);
						unacknowledgedMessages2.remove(acknowledgedId);
						System.out.println("Bestätigung erhalten für Nachricht: " + acknowledgedId);
					}
		
					if (!unacknowledgedMessages2.isEmpty()) {
						for (DatagramPacket packet : unacknowledgedMessages2.values()) {
							socket2.send(packet);
						}
					}
				}
			}
			
			//Test3:
			triggerMessage = new ChatMessage(id, "TEST 3 TIMESTAMP REORDERING");
			DatagramSocket socket3 = new DatagramSocket();
           	InetAddress userAddress3 = InetAddress.getByName("130.75.202.197");
			ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
			DataOutputStream dos3 = new DataOutputStream(baos3);
			triggerMessage.toStream(dos3);

			byte[] sendData3 = baos3.toByteArray();
            DatagramPacket sendPacket3 = new DatagramPacket(sendData3, sendData3.length, userAddress3, 8008);
            socket3.send(sendPacket3);

			byte[] receiveData3 = new byte[1024];
            DatagramPacket receivePacket3 = new DatagramPacket(receiveData3, receiveData3.length);

			List<String> messageQueue = new ArrayList<>();
			List<String> finalmes = new ArrayList<>();
			socket3.setSoTimeout(2000);
			boolean running = true;
            while (running) {
				try{
					socket3.receive(receivePacket3);
					receivedMessage = new String(receivePacket3.getData(), 0, receivePacket3.getLength(), StandardCharsets.UTF_8).substring(10);
					System.out.println("new Message: " + receivedMessage);

					String[] split = receivedMessage.split("\\|",3);
					if (split.length == 3) {
                    	String timestamp = split[2];
						messageQueue.add(timestamp);
                	}
				} catch (SocketTimeoutException e) {
                    // Timeout occurred
                    System.out.println("Timeout reached! Sending sorted messages...");
                    running = false; // Stop receiving messages
                }
			}
			messageQueue.sort(null);
			int counter = 1;
			for(String times : messageQueue){
				finalmes.add(counter + "|Message " + counter + "|" + times);
				counter++;
			}
            for (String sortedMessage : finalmes) {
				ChatMessage send = new ChatMessage(id, sortedMessage);
				ByteArrayOutputStream byteStreamOut = new ByteArrayOutputStream();
				DataOutputStream dataStream = new DataOutputStream(byteStreamOut);
				send.toStream(dataStream);
                byte[] responseData = byteStreamOut.toByteArray();
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, receivePacket3.getAddress(), receivePacket3.getPort());
                socket3.send(responsePacket);
                System.out.println("Gesendet: " + sortedMessage);
            }
			socket3.receive(receivePacket3);
			receivedMessage = new String(receivePacket3.getData(), 0, receivePacket3.getLength(), StandardCharsets.UTF_8).substring(10);
			System.out.println(receivedMessage);
			socket.close();
			socket2.close();
            socket3.close();

    	} catch (Exception e) {
        	System.err.println("Connection error: " + e.getMessage());
        	e.printStackTrace();
		}
		*/
	}
		


}
