package de.luh.vss.chat.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import de.luh.vss.chat.common.Message.ChatMessage;


/*
 * Klasse zum Überprüfen, ob ein User oder eine Gruppe die selben Interessen teilt
 * wenn dem so ist, wird automatisch eine Anfrage an jeweiligen User/Gruppe gesendet
 */
public class PeerDiscoveryService {
    // Prüft, ob User gesuchte Interesse teilt
    public User peer(String search, User user){
        for(String interest: user.getInterests()){
            if(interest.equals(search)){
                sendUserRequest(user);
                return user;
            }
        }
        System.out.println("User has not the same interests as you");
        return null;
    }

    // Prüft, ob Gruppe gesuchte Interesse teilt
    public Group peerGoup(String search, Group group){
        for(String interest: group.getInterests()){
            if(interest.equals(search)){
                sendGroupRequest(group.getAdmin());
                return group;
            }
        }
        System.out.println("Group has not the same interests as you");
        return null;
    }


    // sendet Freundschaftsanfrage an Nutzer
    public void sendUserRequest(User recipient){
        try (DatagramSocket socket = new DatagramSocket()) {
            InetSocketAddress recipientAddress = (InetSocketAddress) recipient.getEndpoint();
        
            // Erstellen einer ChatMessage, die Freundschaftsanfrage beinhaltet
            ChatMessage requestMessage = new ChatMessage(recipient.getUserId(), "Hey, I would like to be your friend:)");
        
            // Serialisieren der Nachricht
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            requestMessage.toStream(dos);
            byte[] msgBytes = baos.toByteArray();

            // DatagramPacket erstellen
            DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, recipientAddress);

            // Nachricht senden
            socket.send(packet);
            System.out.println("ChatMessage sent to " + recipient.getUserId() + ": " + requestMessage.getMessage());

        } catch (Exception e) {
            System.err.println("Error sending ChatMessage to " + recipient.getUserId() + ": " + e.getMessage());
        }
    }


    // sendet Beitrittsanfrage an Administrator der Gruppe
    public void sendGroupRequest(User recipient){
        try (DatagramSocket socket = new DatagramSocket()) {
            InetSocketAddress recipientAddress = (InetSocketAddress) recipient.getEndpoint();
        
            // Erstellen einer ChatMessage, die Gruppenbeitrittsanfrage beinhaltet
            ChatMessage requestMessage = new ChatMessage(recipient.getUserId(), "Hey, I would like to join your Group:)");
        
            // Serialisieren der Nachricht
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            requestMessage.toStream(dos);
            byte[] msgBytes = baos.toByteArray();

            // DatagramPacket erstellen
            DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, recipientAddress);

            // Nachricht senden
            socket.send(packet);
            System.out.println("ChatMessage sent to " + recipient.getUserId() + ": " + requestMessage.getMessage());

        } catch (Exception e) {
            System.err.println("Error sending ChatMessage to " + recipient.getUserId() + ": " + e.getMessage());
        }
    }
}
