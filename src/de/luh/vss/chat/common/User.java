package de.luh.vss.chat.common;

import java.net.SocketAddress;
import java.util.List;

public class User {

	private final UserId userId;
	private final SocketAddress endpoint;
	private final List<String> interests; // Interessen des Users in einer Liste
	private final List<User> friends; // Liste an "Freunden" des Users

	public User(UserId userId, SocketAddress endpoint, List<String> interests, List<User> friends) {
		this.userId = userId;
		this.endpoint = endpoint;
		this.interests = interests;
		this.friends = friends; 
	}

	public UserId getUserId() {
		return userId;
	}

	public SocketAddress getEndpoint() {
		return endpoint;
	}

	// Gibt Interessen des Users in Liste wieder
	public List<String> getInterests() {
		return interests;
	}

	// Fügt eine Interesse zur Liste hinzu
	public void addtInterest(String interest){
		if(interests == null){
			interests.add(interest);
		}
		for(String check : interests){
            if(check == interest){
                System.out.println("Interest already in list");
                return;
            }
        }
		interests.add(interest);
	}

	// Entfernt Interesse aus Liste, sofern vorhanden
	public void removeInterest(String interest){
		if(interests == null){
			System.out.println("no interest in list");
			return;
		}
		for(String check : interests){
            if(check == interest){
                interests.remove(interest);
                return;
            }
        }
        System.out.println("interest not in list");
	}

	// Fügt einen User zur Freundesliste
	public void addfriend(User friend){
		if(friends == null){
			friends.add(friend);
		}
		for(User check : friends){
            if(check == friend){
                System.out.println("friend already in list");
                return;
            }
        }
		friends.add(friend);
	}

	// Entfernt User aus Freundesliste, sofern vorhanden
	public void removefriend(User friend){
		if(friends == null){
			System.out.println("no friend in list");
			return;
		}
		for(User check : friends){
            if(check == friend){
                friends.remove(friend);
                return;
            }
        }
        System.out.println("friend not in list");
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
