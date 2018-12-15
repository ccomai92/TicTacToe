package Helper;

import gameroom.GameRoom;
import gameroom.GameUser;

import java.util.HashMap;
import java.util.List;

public class RoomManager {
	// <Roomname, GameRoom>
	private static HashMap<String, GameRoom> roomList = new HashMap<>();
	public static HashMap<String, GameRoom> getRoomList() {
		return roomList;
	}

	public static void setRoomList(HashMap<String, GameRoom> roomLst) {
		roomList = roomLst;
	}

//	public static GameRoom createRoom(String roomName) {
//		GameRoom room = new GameRoom(roomName);
//		room.enterUser(owner);
//		room.setOwner(owner);
//
//		roomList.put(roomName, room);
//		return room;
//	}

	public static GameRoom createRoom(GameUser user, String roomName) {

		GameRoom room = new GameRoom(roomName);
		room.enterUser(user);

		roomList.put(roomName, room);
		return room;
	}

	public static boolean hasRoom(String roomName) {
		return roomList.containsKey(roomName);
	}
	
	public static GameRoom createRoom(List<GameUser> users, String roomName) {
		GameRoom room = new GameRoom(roomName);
		room.enterUser(users);
		roomList.put(roomName, room);
		return room;
	}

	public static GameRoom getRoom(String roomName) {
		return roomList.get(roomName);
	}
	
	public static void removeRoom(GameRoom room) {
		room.close();
		roomList.remove(room);
	}

	public static void removeRoom(String roomName) {
		roomList.get(roomName).close();
		roomList.remove(roomName);
	}
	
	public static int roomCount() {
		return roomList.size();
	}
}
