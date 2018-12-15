package Helper;

import java.io.*;
import java.net.Socket;

public class SocketManger {
    public static Socket socket;
    public static DataInputStream fromServerData;
    public static DataOutputStream toServerData;
    public static ObjectInputStream fromServerObject;
    public static ObjectOutputStream toServerObject;

    public SocketManger() {
        try {
            socket = new Socket("localhost", 8888);
            toServerData = new DataOutputStream(socket.getOutputStream());
            fromServerData = new DataInputStream(socket.getInputStream());
            toServerObject = new ObjectOutputStream(socket.getOutputStream());
            fromServerObject = new ObjectInputStream(socket.getInputStream());
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void close() {
        try {
            socket.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
