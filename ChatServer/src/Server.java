import java.io.IOException;
import java.net.*;
import java.util.*;

public class Server {

    public int remotePort;

    private List<Socket> socketList = new ArrayList<>();

    Map<Socket, Thread> socketThreadMap = new HashMap<>();

    boolean updateServer = true;

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

            new Thread(new DebugServerThread(this)).start();

            //UpdateServer();

            while(true){

                System.out.println("SocketList: " + socketList.size() + "     HashMap: " + socketThreadMap.size());

                for(int x = 0; x < socketList.size(); x++){
                    //System.out.println("Socket " + x + " is closed: " + socketList.get(x).isClosed());
                    if(socketList.get(x).isClosed()){

                        socketThreadMap.get(socketList.get(x)).interrupt();

                        System.out.println("Is Alive?: " + socketThreadMap.get(socketList.get(x)).isAlive());


                        socketList.remove(x);

                        System.exit(0);

                        break;
                    }
                }

                /*if(socketList.size() == 0){
                    System.out.println(socketList.size());

                }else{

                }*/


                //CheckSocketConnection();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void UpdateServer(){

        while(true){


        }

    }

    private void CheckSocketStatus(){

        /*if(socketList.size() > 0){
            System.out.println("Wut!");
        }*/



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

                    Thread localThread = new Thread(new ServerThread(clientSocket, server, server.socketList));

                    server.socketThreadMap.put(clientSocket, localThread);

                    localThread.start();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private class DebugServerThread implements Runnable{


        private Server activeServer;

        public DebugServerThread(Server server){
            this.activeServer = server;
        }

        @Override
        public void run() {

            String line;

            Scanner inputScanner = new Scanner(System.in);


            while((line = inputScanner.nextLine()) != null){
                if(line.equals("sockets")){
                    for(Socket socket : activeServer.socketList){
                        System.out.println(socket.getPort());
                    }
                }
            }
        }
    }




}
