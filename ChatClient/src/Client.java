import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class Client {

    private int remotePort;
    private String serverHost;

    Socket clientSocket;

    private Thread sendMessageThread;
    private Thread receiveMessageThread;

    private SendMessageThread sendMessageThreadClass;

    private int amountOfRetries;


    //Constructor for Client, checks that the port is non-negative and prints the computers IP address
    Client(String host, int port){

        //Check if the port is valid
        if(port < 0){
            remotePort = -1;
            System.out.println("Enter a port above 0");
        }else{
            remotePort = port;
        }

        //Check if the host method field is not empty
        if(host.isEmpty()){
            //If the host method field is in fact empty, it will use the clients current IP address instead
            try {
                String localAddress = InetAddress.getLocalHost().toString();
                serverHost = localAddress.substring(localAddress.indexOf("/") + 1);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }else{
            serverHost = host;
        }
    }

    //Method used to update the client
    private void UpdateClient(){



        while(true){

            //Check if the socket connected to the socket is closed. If the socket is in fact closed the program will try to reconnect to the server
            if(this.clientSocket.isClosed()){

                //If the client has attempted over 5 unsuccessful reconnects the client will shut down
                if(amountOfRetries >= 5 ){
                    System.err.println("The amount of reconnects has exceeded its limit");
                    System.exit(0);
                }



                //Attempt to reconnect to the server, if successful, will call upon restartClient
                if(!reconnectClient()){
                    amountOfRetries++;

                }else{

                    restartClient();

                    //break;
                }

            }
        }

        /*restartClient();
        System.out.println("Updated method has been closed!");*/
    }

    //Establishes a new connection and restarts closed threads
    private void restartClient(){


        Socket socket = new Socket();

        try {
            //Tries to bind the socket to the server
            socket = new Socket(serverHost, remotePort);

            this.clientSocket = socket;

            amountOfRetries = 0;

            System.out.println("\n--RECONNECTION SUCCESSFUL--\n");

            //Will initialize a SendMessageThread and start a new thread, if a send message thread was never assigned or shut down
            if(sendMessageThread == null || !sendMessageThread.isAlive()){
                sendMessageThreadClass = new SendMessageThread(clientSocket, this);
                sendMessageThread = new Thread(sendMessageThreadClass);
                sendMessageThread.start();
            }

            //Update the current clientSocket
            sendMessageThreadClass.setClientSocket(this.clientSocket);

            //Start a new thread for a ReceiveMessagesThread
            new Thread(new ReceiveMessagesThread(this.clientSocket, this)).start();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    void startClient(){

        Socket socket = new Socket();

        try{
            //Tries to connect to the server
            socket = new Socket(serverHost, remotePort);
            this.clientSocket = socket;

            System.out.println("\n--Connection has been established--");

            //Initialize a new SendMessageThread and start a new thread using it
            sendMessageThreadClass = new SendMessageThread(this.clientSocket, this);
            sendMessageThread = new Thread(sendMessageThreadClass);
            sendMessageThread.start();

            //Start a new thread using an locally initialized RecieveMessageThread
            receiveMessageThread = new Thread(new ReceiveMessagesThread(clientSocket, this));
            receiveMessageThread.start();

            UpdateClient();

        } catch (IOException e) {
            e.printStackTrace();
            try{
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }



    }

    //Check if the client can reconnect to a server
    public boolean reconnectClient(){

        //System.out.println(System.currentTimeMillis());

        //Store the current time
        long currentTime = System.currentTimeMillis();

        while(true){

            //Check if the 5 seconds has gone by since the method was called
            if(System.currentTimeMillis() - currentTime > 5000){
                System.out.println("\n--RECONNECTING--\n");

                //Check if socket can be bound to the server, an exception thrown indicates the server is not open
                try(Socket socket = new Socket(serverHost, remotePort)) {

                    socket.close();

                    return true;
                } catch (IOException e) {
                    System.err.println("\n--RECONNECTION FAILED--\n");
                    return false;

                }

            }
        }
    }
}
