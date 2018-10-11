import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {

    public int remotePort;

    List<Socket> socketList = new ArrayList<>();

    Map<Socket, Thread> socketThreadMap = new HashMap<>();

    ConcurrentLinkedQueue<String> messageList = new ConcurrentLinkedQueue<>();

    private boolean updateServer = false;

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


    void StartServer(){


        System.out.println("Remote port: " + remotePort);

        try(ServerSocket serverSocket = new ServerSocket(remotePort)){

            System.out.println("Socket has been opened, awaiting connections...");
            System.out.println("IP address: " + Inet4Address.getLocalHost());



            new Thread(new ServerConnection(serverSocket, this)).start();

            new Thread(new DebugServerThread(this)).start();

            new Thread(new SendMessagesThread(this, messageList)).start();

            new Thread(new RemoveSocketThread(this)).start();

            while(true){

            }

            //updateServer = true;

            //UpdateServer();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void UpdateServer(){

        /*while(true){

            //System.out.println("Here!");

            //System.out.println("SocketList: " + socketList.size() + "     HashMap: " + socketThreadMap.size());



        }*/




        /*if(!messageList.isEmpty()){

               System.out.println("Size of list: " + messageList.size());

               String message = messageList.poll();

               for(Socket socket : socketList){
                   try {
                       PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                       out.println(message);

                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   System.out.println(socket.getPort() + " is closed?: " + socket.isClosed());
               }

               System.out.println("Polled message: " + message);
           }*/

    }

    void ReceiveMessages(String message, Socket socket){

        System.out.println(socket.getPort() + ": " + message);
        messageList.add(message);

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

                    Thread localThread = new Thread(new ServerThread(clientSocket, server));

                    server.socketThreadMap.put(clientSocket, localThread);

                    localThread.start();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
