import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {

    //Store the Servers listening port
    private int remotePort;

    //List used to store all connected client sockets
    List<Socket> socketList = new ArrayList<>();

    //Hash map used to associate a client socket to a specific Thread
    Map<Socket, Thread> socketThreadMap = new HashMap<>();

    ConcurrentLinkedQueue<String> messageList = new ConcurrentLinkedQueue<>();

    private Thread sendMessageThread;

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

            sendMessageThread = new Thread(new SendMessagesThread(this, messageList));
            sendMessageThread.start();

            //new Thread(new RemoveSocketThread(this)).start();

            while(true){

                if(!sendMessageThread.isAlive()){

                    System.err.print("*** Send Messages is not alive, attempting restart...*** ");


                    sendMessageThread = new Thread(new SendMessagesThread(this, messageList));
                    sendMessageThread.start();
                }

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

        System.out.println("***" + socket.getPort() + ": " + message);

        messageList.add("***" + socket.getPort() + ": " + message);

    }
    void ReceiveMessages(String message, String sender){
        String completeMessage = sender +": " + message;
        System.out.println(completeMessage);
        messageList.add(completeMessage);
    }

    void CheckSockets(){

        if (socketList.size() > 0) {
            //System.out.println("Hello!");
            for (int x = 0; x < socketList.size(); x++) {
                System.out.println("Socket " + x + " is closed: " + socketList.get(x).isClosed());
                if (socketList.get(x).isClosed()) {

                    System.out.println(socketList.get(x) + " Socket is no longer available!");

                    //System.out.println(socketThreadMap.get(activeServer.socketList.get(x)).isAlive());

                    socketThreadMap.remove(socketList.get(x));

                    //System.out.println("Is Alive?: " + socketThreadMap.get(activeServer.socketList.get(x)).isAlive());

                    socketList.remove(x);

                    System.out.println("Socket has been removed!");

                    //System.exit(0);

                    CheckSockets();

                    break;
                }
            }
        } else {
            //System.out.println("SocketList is empty!");
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
