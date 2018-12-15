package Client;

import Helper.AccountManager;
import Helper.SocketManger;
import Server.MessageControl;
import gameroom.GameRoom;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

/**
 *
 */
public class GameView extends Application implements MessageControl {
    private SocketManger socket;
    private Socket sck;
    BorderPane pane;
    ListView<GameRoom> gameRoomListView;

    @Override
    public void start(Stage primarStage) {
        pane = new BorderPane();
        gameRoomListView = new ListView<>();
        gameRoomListView.setPrefSize(400, 400);
        gameRoomListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        VBox buttonList = new VBox();
        buttonList.setSpacing(20);
        buttonList.setAlignment(Pos.CENTER);
        Button createButton = new Button("Create Room");
        Button joinButton = new Button("Join Room");
        Button refreshButton = new Button("Refresh");
        buttonList.getChildren().addAll(createButton, joinButton, refreshButton);

        createButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("Good bye network");
            dialog.setTitle("Create room");
            dialog.setHeaderText("Room");
            dialog.setContentText("Please enter room name: ");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                result.get();
                try {
                    socket.toServerData.writeInt(MessageControl.createRoomReq);
                    socket.toServerData.writeUTF(result.get());
                    socket.toServerData.writeUTF(AccountManager.getUsername());
                    int ack = socket.fromServerData.readInt();

                    if(ack == MessageControl.createRoomSuccess) {
                        System.out.println("room sucess");
                        Socket sck = new Socket("localhost", 8889);

                        new GamePlay(sck).start(new Stage());

                    } else if(ack == MessageControl.createRoomFail) {
                        System.out.println("room failed");
                    } else {
                        System.out.println("Room create serious error");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            // Refreshing the list.
            refresh();
        });

        joinButton.setOnAction(event -> {
            try {
                System.out.println(gameRoomListView.getSelectionModel().getSelectedIndex());
                GameRoom roomSelected = gameRoomListView.getSelectionModel().getSelectedItem();
                if(null!=roomSelected) {
                    System.out.println(roomSelected);
                    socket.toServerData.writeInt(joinRoomReq);
                    socket.toServerObject.writeObject(roomSelected);
                    System.out.println("getting the ack");
                    int ack = socket.fromServerData.readInt();
                    System.out.println("got ack");
                    if(ack == joinSuccess) {
                        sck = new Socket("localhost", 8889);
                        System.out.println(sck);

                        new GamePlay(sck).start(primarStage);

                        // Refresh the list
                        refresh();
                    } else {
                        // todo: join room fail message!
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        refreshButton.setOnAction(event -> refresh());

        refresh();
        pane.setCenter(new ScrollPane(gameRoomListView));
        pane.setRight(buttonList);
        Scene scene =new Scene(pane);
        primarStage.setScene(scene);
        primarStage.show();
        primarStage.setOnCloseRequest(event -> {
            try {
                socket.toServerData.writeInt(MessageControl.close);
                socket.fromServerData.readInt(); // Syncronize the moment
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            socket.close();

            System.exit(0);
        });

    }

    public GameView(SocketManger socket) {
        this.socket = socket;
    }

    /**
     * Refreshing the game room list.
     */
    public void refresh() {
        try {
            // Requesting refresh and get the room list.
            socket.toServerData.writeInt(refreshReq);
            ArrayList<GameRoom> list = (ArrayList<GameRoom>) socket.fromServerObject.readObject();

            // Clear the list
            gameRoomListView.getItems().remove(0, gameRoomListView.getItems().size());
            gameRoomListView.getItems().addAll(list);
            gameRoomListView.refresh();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
