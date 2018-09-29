import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class ServerThread implements Runnable{

    private List<Socket> socketList;

    private Server activeServer;

    private Socket clientSocket;

    public ServerThread(Socket clientSocket, Server server, List<Socket> socketList){

        this.socketList = socketList;
        this.activeServer = server;
        this.clientSocket = clientSocket;

    }




    @Override
    public void run() {

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String line;
            while((line = reader.readLine()) !=null){

                System.out.println(line);
            }



        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Thread stopped!");
        }
    }
}
