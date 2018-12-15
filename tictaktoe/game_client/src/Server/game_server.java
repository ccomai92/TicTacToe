package Server;

import Client.TicTacToeConstants;
import Helper.RoomManager;
import gameroom.GameRoom;
import gameroom.GameUser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class game_server extends Application {
    Map<String, GameUser> GameUsers = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        //Reading data from disk
        readData();
        // Server log area
        TextArea serverLog = new TextArea();

        // Create a scene and place it in the stage
        Scene scene = new Scene(new ScrollPane(serverLog), 450, 200);
        primaryStage.setTitle("Game Server"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
        primaryStage.setOnCloseRequest(event -> System.exit(0));

        // socket open
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8888);
                ServerSocket sck = new ServerSocket(8889);
                serverLog.appendText(": Server start at socket 8888 \n");

                while (true) {
                    Socket player = serverSocket.accept();
                    Platform.runLater(() -> {
                        serverLog.appendText("Someone join the server \n");
                        new Thread(new HandleASession(player, sck)).start();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    class HandleASession implements Runnable, MessageControl {
        private Socket player;
        private ServerSocket sck;
        private DataInputStream fromPlayerData;
        private ObjectInputStream fromPlayerObject;
        private DataOutputStream toPlayerData;
        private ObjectOutputStream toPlayerObject;
        private String username;


        HandleASession(Socket player, ServerSocket playBoard) {
            try {
                // Initialize the socket, and run the program.
                this.player = player;
                this.sck = playBoard;
                this.fromPlayerData = new DataInputStream(this.player.getInputStream());
                this.fromPlayerObject = new ObjectInputStream(this.player.getInputStream());
                this.toPlayerData = new DataOutputStream(this.player.getOutputStream());
                this.toPlayerObject = new ObjectOutputStream(this.player.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                boolean nextScene = false;

                while (!nextScene) {
                    int action = fromPlayerData.readInt();
                    if (action == signInReq) {
                        System.out.println("Sign in Action detected");
                        GameUser clientUser = (GameUser) fromPlayerObject.readObject();
                        String username = clientUser.getUserName();
                        GameUser serverUser = GameUsers.get(username);

                        if ((serverUser != null) && (serverUser.equals(clientUser))) {
                            System.out.println("Success");
                            this.username = username;
                            toPlayerData.writeInt(signInSuccess);
                            toPlayerObject.writeObject(GameUsers.get(username));
                        } else {
                            toPlayerData.writeInt(signInFail);
                        }
                    } else if (action == signUpReq) {
                        String username = fromPlayerData.readUTF();     // Reading username
                        String userpass = fromPlayerData.readUTF();     // REading password

                        //Length check (longer than 3)
                        if (username.length() < 3) {
                            toPlayerData.writeInt(signUpFail);
                        } else if (null == GameUsers.put(username, new GameUser(username, userpass))) {
                            toPlayerData.writeInt(signUpSuccess);
                            saveData();
                        } else {
                            toPlayerData.writeInt(signUpFail);
                        }


                    } else if (action == joinRoomReq) {
                        GameRoom room = (GameRoom) fromPlayerObject.readObject();

                        System.out.println(RoomManager.hasRoom(room.getRoomName()));

                        if (RoomManager.hasRoom(room.getRoomName())) {
                            toPlayerData.writeInt(joinSuccess);

                            System.out.println(room.getRoomName());

                            // Setting the play socket
                            Socket play_sck = sck.accept();

                            // Assign the first player is player2
                            new DataOutputStream(play_sck.getOutputStream()).writeInt(TicTacToeConstants.PLAYER2);
                            new DataInputStream(play_sck.getInputStream());


                            Socket player1 = RoomManager.getRoom(room.getRoomName()).getUserList().get(0).getSock();
                            RoomManager.removeRoom(room.getRoomName());

                            System.out.println(player1 + "\n" + play_sck);

                            String player = new DataInputStream(play_sck.getInputStream()).readUTF();
                            System.out.println(player + " is ready");

                            // Create the thread and join
                            Thread t = new Thread(new HandleGame(player1, play_sck));
                            t.start();

                        } else {
                            toPlayerData.writeInt(joinFail);
                        }

                    } else if (action == createRoomReq) {

                        System.out.println("create Room request detected");

                        String roomName = fromPlayerData.readUTF();
                        String username = fromPlayerData.readUTF();
                        if (!RoomManager.hasRoom(roomName)) {

                            // Setting the play socket
                            toPlayerData.writeInt(createRoomSuccess);
                            Socket play_sck = sck.accept();


                            System.out.println(sck);

                            // Assign the first player is player1
                            new DataOutputStream(play_sck.getOutputStream()).writeInt(TicTacToeConstants.PLAYER1);
                            String player = new DataInputStream(play_sck.getInputStream()).readUTF();
                            System.out.println(player + " is ready");

                            // Making the room, when room does not exist.
                            GameUsers.get(username).setSock(play_sck);
                            RoomManager.createRoom(GameUsers.get(username), roomName);
                        } else {
                            // when room exist, do not create room.
                            toPlayerData.writeInt(createRoomFail);
                        }
                    } else if (action == refreshReq) {
                        ArrayList<GameRoom> roomList = new ArrayList<>(RoomManager.getRoomList().values());
                        System.out.println(roomList);
                        toPlayerObject.writeObject(roomList);
                    } else if (action == deregisterReq) {
                        System.out.println("Unregister requested");
                        GameUser clientUser = (GameUser) fromPlayerObject.readObject();
                        String username = clientUser.getUserName();
                        GameUser serverUser = GameUsers.get(username);

                        if ((serverUser != null) && (serverUser.equals(clientUser))) {
                            System.out.println("Success");
                            GameUsers.remove(username);
                            toPlayerData.writeInt(deregisterSuceess);
                            saveData();
                        } else {
                            toPlayerData.writeInt(deregisterFail);
                        }


                    } else if (action == close) {
                        toPlayerData.writeInt(close);
                        player.close();
                        sck.close();
                        System.out.println("Successful");
                        return;
                    } else {
                        System.out.println(action);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saving data to hardisk
     */
    void saveData() {
        try { // Create an output stream for file object.dat
            ObjectOutputStream output =
                    new ObjectOutputStream(new FileOutputStream("tictactoe.dat"));
            output.writeObject(GameUsers);
            output.close();

            System.out.println("saved");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("File not Found");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("IOException");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Loading the data from the disk
     */
    void readData() {
        try {
            ObjectInputStream input =
                    new ObjectInputStream(new FileInputStream("tictactoe.dat"));

            GameUsers = (Map<String, GameUser>) (input.readObject());
            input.close();
            System.out.println("load data from file");

        } catch (FileNotFoundException e) {
            System.out.println("File not Found");
        } catch (
                ClassNotFoundException e) {
            System.out.println("ClassNotfoundException");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }
}
