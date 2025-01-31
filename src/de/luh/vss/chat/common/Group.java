package de.luh.vss.chat.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Group {
    private String name;
    private User admin;
    private TreeSet<String> groupInterests = new TreeSet<>();
    private Set<User> groupMembers = new HashSet<>();
    private static final String MULTICAST_ADDRESS = "224.0.0.1";
    private static final int PORT = 5000;
    private InetAddress multicastGroup;
    private MulticastSocket multicastSocket;

    // Konstruktor
    public Group(String name, User admin) throws IOException {
        this.name = name;
        this.admin = admin;
        groupMembers.add(admin);
        multicastGroup = InetAddress.getByName(MULTICAST_ADDRESS);
        multicastSocket = new MulticastSocket();
    }

    // Getter und Setter
    public String getName() { 
        return name; 
    }
    public void setName(String newName) { 
        this.name = newName; 
    }
    public User getAdmin() { 
        return admin; 
    }
    public Set<User> getMembers() { 
        return groupMembers; 
    }
    public TreeSet<String> getInterests() { 
        return groupInterests; 
    }

    // Mitgliederverwaltung
    public void addMember(User member) {
        if (!groupMembers.contains(member)) {
            groupMembers.add(member);
        }
    }

    public void removeMember(User member) {
        if (member != admin) {
            groupMembers.remove(member);
        }
    }

    // Interessenverwaltung
    public void addGroupInterest(String interest) {
        if(!groupInterests.contains(interest)){
            groupInterests.add(interest);
        }
    }

    public void removeGroupInterest(String interest) {
        groupInterests.remove(interest);
    }

    // Multicast-Nachricht senden
    public void sendGroupMessage(User sender, String message) {
        try {
            byte[] msgBytes = (sender.getUserId().id() + ": " + message).getBytes();
            DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, multicastGroup, PORT);
            multicastSocket.send(packet);
            System.out.println("Multicast message sent: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}