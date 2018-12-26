package serverapp.server;

import java.net.InetAddress;
import java.net.Socket;

public class User {


    public String username;
    public Thread serverThread;
    public Socket socket;
    public InetAddress inetAddress;

    public User(String username, Thread thread, Socket socket) {


        this.username = username;
        this.serverThread = thread;
        this.socket = socket;

        inetAddress = socket.getInetAddress();




    }
}
