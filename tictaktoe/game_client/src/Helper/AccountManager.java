package Helper;

import gameroom.GameUser;

/**
 * This is helper function for client side.
 */
public class AccountManager {
    public static GameUser gameUser;

    public AccountManager(GameUser gameUser) {
        this.gameUser = gameUser;
    }
    public AccountManager(String userName, String password) {
        this.gameUser = new GameUser(userName, password);
    }

    public static String getUsername() {
        return gameUser.getUserName();
    }
}
