import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public int remotePort;

    private List<Socket> socketList = new ArrayList<>();


    public Server(int port){

        if(port < 0){
            remotePort = -1;
        }else{

            if(CheckRemotePortAvailability(port)){
                remotePort = port;

            }else{
                remotePort = 0;
            }


        }


    }

    private boolean CheckRemotePortAvailability(int portToCheck){

        try{
            Socket socket = new Socket(InetAddress.getLocalHost(),portToCheck );
            socket.close();

            System.out.println("Remote port is NOT available");

            return false;
        } catch (UnknownHostException e) {

            System.out.println("Remote port is NOT available");

            return false;

        } catch (IOException e) {

            System.out.println("Remote port is available");

            return true;
        }
    }


    public void StartServer(){


        System.out.println("Remote port: " + remotePort);

        try(ServerSocket serverSocket = new ServerSocket(remotePort)){

            System.out.println("Socket has been opened, awaiting connections...");
            System.out.println("IP address: " + Inet4Address.getLocalHost());

            new Thread(new ServerConnection(serverSocket, this)).start();

            while(true){

            }





        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private class ServerConnection implements Runnable{


        private ServerSocket serverSocket;
        private Server server;

        public ServerConnection(ServerSocket socket, Server activeServer){

            this.serverSocket = socket;
            this.server = activeServer;
        }

        @Override
        public void run() {

            try {
                while(true){

                    Socket clientSocket = serverSocket.accept();

                    System.out.println("Connection has been established!");

                    System.out.println(clientSocket.getInetAddress());

                    server.socketList.add(clientSocket);


                }



            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
