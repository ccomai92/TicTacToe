package gameroom;

import Helper.RoomManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameRoom implements Serializable {
	private String roomName; // room roomName.
	private List<GameUser> userList;
	

	public GameRoom() {
		this.roomName = null;
//		this.roomOwner = null;
		userList = new ArrayList<>();
	}

	public GameRoom(String roomName) {
		this.roomName = roomName;
//		this.roomOwner = null;
		userList = new ArrayList<>();
	}


	/**
	 *
	 * @param roomId
	 * @param owner
	 */
	public GameRoom(int roomId, GameUser owner) {
		// make owner to enter the room.
		owner.enterRoom(this);
//		this.roomOwner = owner;
		userList = new ArrayList<>();
		this.userList.add(owner);
	}

	/**
	 *
	 * @param owner
	 */
//	public void setOwner(GameUser owner) {
//		this.roomOwner = owner;
//	}

	/**
	 * 
	 * @param username
	 * @return
	 */
	public GameUser getUserByUsername(String username) {
		for(GameUser user : userList) {
			if(user.getUserName().equals(username))
				return user;
		}
		return null;
	}
	
//	public GameUser getUser(GameUser gameuser) {
//		int idx = userList.indexOf(gameuser);
//
//		if(idx > 0) {
//			return userList.get(idx);
//		}
//
//		return null;
//	}
	
	public int getUserSize( ) {
		return userList.size();
	}
	
//	public GameUser getOwner() {
//		return roomOwner;
//	}
	
	public String getRoomName() {
		return roomName;
	}
	
	public List<GameUser> getUserList() {
		return userList;
	}
	
	public void setUserList(List<GameUser> userList) {
		this.userList = userList;
	}
	
//	public GameUser getRoomOwner() {
//		return roomOwner;
//	}
	
//	public void setRoomOwner(GameUser roomOwner) {
//		this.roomOwner = roomOwner;
//	}
	
	
	public void enterUser(GameUser user) {
		userList.add(user);
	}
	
	public void enterUser(List<GameUser> users) {
		for(GameUser gameUser : users) {
			gameUser.enterRoom(this);
		}
	}
	
	/**
	 * 
	 * @param user
	 */
	public void exitUser(GameUser user) {
		user.exitRoom(this);
		userList.remove(user);
		
		if(userList.size() < 1) {
			RoomManager.removeRoom(this);
		} else if (userList.size() < 2) {
//			this.roomOwner = userList.get(0);
			return ;
		}
	}
	
	public void close() {
		for(GameUser user : userList) {
			user.exitRoom(this);
		}
		this.userList.clear();
		this.userList = null;
	}
	
	public void broadcast(byte[] data) {
		for(GameUser user : userList) {
			try {
				user.getSock().getOutputStream().write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return this.roomName;
	}
	
	
}
