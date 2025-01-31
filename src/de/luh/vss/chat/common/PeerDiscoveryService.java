package de.luh.vss.chat.common;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.*;

public class PeerDiscoveryService {
    private static final int CACHE_SIZE = 10; // Maximale Cache-Größe

    // LRU-Cache für User- und Gruppen-Discovery
    private final Map<String, List<User>> userCache = new LinkedHashMap<>(CACHE_SIZE, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, List<User>> eldest) {
            return size() > CACHE_SIZE;
        }
    };

    private final Map<String, List<Group>> groupCache = new LinkedHashMap<>(CACHE_SIZE, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, List<Group>> eldest) {
            return size() > CACHE_SIZE;
        }
    };

    // Peer Discovery für Nutzer mit Cache
    public List<User> peer(String search, List<User> users) {
        if (userCache.containsKey(search)) {
            System.out.println("Cache hit for User search: " + search);
            return userCache.get(search);
        }

        System.out.println("Cache miss - performing search for Users: " + search);
        List<User> foundUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getInterests().contains(search)) {
                sendRequest(user, false);
                foundUsers.add(user);
            }
        }

        // Speichern des Ergebnisses im Cache
        userCache.put(search, foundUsers);
        return foundUsers;
    }

    // Peer Discovery für Gruppen mit Cache
    public List<Group> peerGroup(String search, List<Group> groups) {
        if (groupCache.containsKey(search)) {
            System.out.println("Cache hit for Group search: " + search);
            return groupCache.get(search);
        }

        System.out.println("Cache miss - performing search for Groups: " + search);
        List<Group> foundGroups = new ArrayList<>();
        for (Group group : groups) {
            if (group.getInterests().contains(search)) {
                sendRequest(group.getAdmin(), true);
                foundGroups.add(group);
            }
        }

        // Speichern des Ergebnisses im Cache
        groupCache.put(search, foundGroups);
        return foundGroups;
    }

    // Freundschafts- oder Gruppenbeitrittsanfrage senden
    private void sendRequest(User recipient, boolean isGroup) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetSocketAddress recipientAddress = (InetSocketAddress) recipient.getEndpoint();
            String message = isGroup ? "Hey, I would like to join your Group:)" : "Hey, I would like to be your friend:)";

            // Nachricht serialisieren
            byte[] msgBytes = message.getBytes();
            DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, recipientAddress);

            socket.send(packet);
            System.out.println("Request sent to " + recipient.getUserId() + ": " + message);

        } catch (Exception e) {
            System.err.println("Error sending request: " + e.getMessage());
        }
    }
}
