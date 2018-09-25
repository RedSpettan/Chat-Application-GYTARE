import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public int remotePort;

    public Server(int port){

        if(port < 0){
            remotePort = -1;
        }else{
            remotePort = port;
        }


    }


    public void StartServer(){


        System.out.println("Remote port: " + remotePort);

        try(ServerSocket serverSocket = new ServerSocket(remotePort)){

            System.out.println("Socket has been opened, awaiting connections...");

            Socket clientSocket = serverSocket.accept();

            System.out.println("Connection has been established!");

            System.out.println(clientSocket.getInetAddress());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
