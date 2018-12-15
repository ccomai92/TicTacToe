package gameroom;

import java.io.Serializable;
import java.net.Socket;

public class GameUser implements Comparable<GameUser>, Serializable {
	private String username;
	private String password;
	private int wins;
	private GameRoom room;
	private Socket sock;
	
	public GameUser(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUserName() {
		return username;
	}

	public int getWins() {
		return this.wins;
	}

	public void setWins(int score) {
		this.wins = score;
	}
	
	public GameRoom getRoom() {
		return room;
	}
	
	public void setRoom(GameRoom room) {
		this.room = room;
	}
	
	public Socket getSock() {
        return sock;
    }

    public void setSock(Socket sock) {
        this.sock = sock;
    }
    
    public String getPassword() {
    	return this.password;
    }
    
    public void enterRoom(GameRoom room) {
    	this.room = room;
    }
    
    public void exitRoom(GameRoom room) {
    	this.room=null;
    }

    @Override
    public int compareTo(GameUser o) {
		return ( this.username.compareTo(o.getUserName()) );
	}

    @Override
    public boolean equals(Object o) {
    	GameUser gameUser = (GameUser) o;

    	return ( (username.equals(gameUser.getUserName())) && password.equals(gameUser.getPassword()) );
    }

//    @Override
//    public int hashCode() {
//    	return id;
//    }
//	
//	public void enterRoom(GameRoom room) {
//		room.enterUser(this);
//		this.room = room;
//	}
}

