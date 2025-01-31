package de.luh.vss.chat.common;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class User {

	private final UserId userId;
	private final SocketAddress endpoint;
	private final HashSet<String> interests = new HashSet<>(); // Interessen des Users in einer Liste
	private final HashSet<User> friends = new HashSet<>(); // Liste an "Freunden" des Users

	public User(UserId userId, SocketAddress endpoint) {
		this.userId = userId;
		this.endpoint = endpoint;
	}

	public UserId getUserId() {
		return userId;
	}

	public SocketAddress getEndpoint() {
		return endpoint;
	}

	// Gibt Interessen des Users in Liste wieder
	public Set<String> getInterests() {
		return interests;
	}

	// Fügt Interesse in Liste, sofern noch nicht enthalten 
	public void addInterest(String interest) {
        if (!interests.contains(interest)) {
            interests.add(interest);
        } else {
            System.out.println("Interest already in list");
        }
    }

	// Entfernt Intersse aus Gruppe, sofern enthalten 
    public void removeInterest(String interest) {
        if (interests.remove(interest)) {
            System.out.println("Interest removed");
        } else {
            System.out.println("Interest not in list");
        }
    }
	
	// Gibt Freundesliste zurück
	public Set<User> getFriends (){
		return friends;
	}

	// Fügt user in Freundesliste, sofern noch nicht enthalten 
    public void addFriend(User friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
        } else {
            System.out.println("Friend already in list");
        }
    }

	// Entfernt user aus Freundesliste, sofern enthalten 
    public void removeFriend(User friend) {
        if (friends.remove(friend)) {
            System.out.println("Friend removed");
        } else {
            System.out.println("Friend not in list");
        }
    }


	public record UserId(int id) {
		public static UserId BROADCAST = new UserId(0);
		
		public UserId(int id) {
			if (id < 0 || id > 9999)
				throw new IllegalArgumentException("wrong user ID");
			this.id = id;
		}

	}

}
