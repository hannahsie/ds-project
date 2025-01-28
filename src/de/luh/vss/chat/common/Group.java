package de.luh.vss.chat.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import de.luh.vss.chat.common.Message.GroupMessage;

/*
 * Klasse zum generieren einer Gruppe, sowie verschiedene Methoden zum einfügen/entfernen von Mitgliedern, 
 * hinzugfügen von Interessen, senden von Nachrichten an die gesamte gruppe etc. 
 */

public class Group {

    private String name;
    private User admin;
    private List<String> groupInterests;
    private Set<User> groupMembers = new HashSet<>();  // Sammlung aller Gruppenmitglieder

    // Generiert Gruppe mit Gruppennamen, Themen bzw. Interessen der Gruppe sowie einem Administrator,
    // welcher das erste Mitglied der Gruppe darstellt
    public Group(String name, User admin, List<String> groupInterests) {
        this.name = name;
        this.admin = admin;
        this.groupInterests = groupInterests;
        groupMembers.add(admin);
    }

    // Gibt Namen der Gruppe wieder
    public String getName(){
        return name;
    }

    // Ändert den Namen der Gruppe
    public void setName(String new_name){
        this.name = new_name;
    }

    // Gibt den Administrator wieder
    public User getAdmin(){
        return admin;
    }

    // Gibt Interessen der Gruppe in Liste wieder
	public List<String> getInterests() {
		return groupInterests;
	}

	// Fügt eine Interesse zur Liste hinzu
	public void addInterest(String interest){
		groupInterests.add(interest);
	}


    // Entfernt Interesse aus Liste
    public void removeInterest(String interest){
        groupInterests.remove(interest);
    }

    // Gibt alle Member der gruppe zurück
    public Set<User> getMembers(){
        return groupMembers;
    }

    // Fügt einen Benutzer zur Gruppe hinzu
    public void addMember(User user) {
        groupMembers.add(user);
    }

    // Entfernt einen Benutzer aus der Gruppe
    public void removeMember(User user) {
        groupMembers.remove(user);
    }

    // Prüft, ob Nachricht und Sender valide sind und ob Liste an Member nicht leer ist und initiiert senden an Gruppe
    public void handleGroupMessage(Message message, User sender) {
        // Validierung der Nachricht und des Absenders
        if (message == null || sender == null) {
            return;
        }

        // Prüft, ob Member der Gruppe
        if (!groupMembers.contains(sender)) {
            System.out.println("User " + sender.getUserId() + " is not a member of the group.");
            return;
        }
        
        // Verarbeitung der Nachricht
        if (message instanceof GroupMessage) {    
            // Weiterleitung der Nachricht an die Gruppenmitglieder
            forwardMessageToGroupMembers(message.toString(), sender);
        }
    }    

    // Geht Liste der Mitglieder durch und initiiert sanden der Nachricht an einzelne Mitglieder
    private void forwardMessageToGroupMembers(String message, User sender) {
        for (User recipient : groupMembers) {
            if (!recipient.equals(sender)) {
                sendMessageToUser(sender, recipient, message);
            }
        }
    }

    // Sendet Nachricht an Mitglied der gruppe
    private void sendMessageToUser(User sender, User recipient, String message) {
       try (DatagramSocket socket = new DatagramSocket()) {
            InetSocketAddress recipientAddress = (InetSocketAddress) recipient.getEndpoint();
        
            // Erstellen der GroupMessage
            GroupMessage gm = new GroupMessage(sender.getUserId(), recipient.getUserId(), message);

            // Serialisieren der Nachricht in ein Byte-Array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            gm.toStream(dos);  // Schreibt die Nachricht in den Stream
            byte[] msgBytes = baos.toByteArray();

            // DatagramPacket für den Versand
            DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, recipientAddress);

            // Nachricht senden
            socket.send(packet);
            System.out.println("GroupMessage sent from " + gm.getSender() + " to " + gm.getRecipient() + ": " + gm.getMessage());

        } catch (Exception e) {
            System.err.println("Error sending GroupMessage to " + recipient.getUserId() + ": " + e.getMessage());
        }
    }
}
