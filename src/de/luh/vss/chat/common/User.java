package de.luh.vss.chat.common;

import java.net.SocketAddress;
import java.util.List;

public class User {

	private final UserId userId;
	private final SocketAddress endpoint;
	private final List<String> interests; //Interessen des Users in einer Liste

	public User(UserId userId, SocketAddress endpoint, List<String> interests) {
		this.userId = userId;
		this.endpoint = endpoint;
		this.interests = interests;
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
	public void setInterests(String interest){
		interests.add(interest);
	}

	// Entfernt Interesse aus Liste
	public void removeInterest(String interest){
		interests.remove(interest);
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
