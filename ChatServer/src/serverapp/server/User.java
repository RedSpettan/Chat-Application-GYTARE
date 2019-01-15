package serverapp.server;

import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class User {


    public String username;
    public Thread serverThread;
    public Socket socket;
    public InetAddress inetAddress;
    public long timeConnected;
    public long amountOfTimeConnected;
    public String formattedTimeConnected;

    public User(String username, Thread thread, Socket socket) {


        this.username = username;
        this.serverThread = thread;
        this.socket = socket;

        inetAddress = socket.getInetAddress();

        timeConnected = System.currentTimeMillis();


    }



    public void calculateTime(){

        long currentTime = System.currentTimeMillis();

        amountOfTimeConnected = currentTime - timeConnected;

        formattedTimeConnected = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(amountOfTimeConnected),
                TimeUnit.MILLISECONDS.toMinutes(amountOfTimeConnected) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(amountOfTimeConnected) % TimeUnit.MINUTES.toSeconds(1));

    }
}
