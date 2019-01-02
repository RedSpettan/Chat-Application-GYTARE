import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class Client {

    private int remotePort;
    private String serverHost;
    public String username; /*"UsernameTest";*/


    Socket clientSocket;

    private Thread sendMessageThread;
    private Thread receiveMessageThread;

    private SendMessageThread sendMessageThreadClass;

    private int amountOfRetries;


    //Constructor for Client, checks that the port is non-negative and prints the computers IP address
    Client(String host, int port, String username){

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

        //Username should not be null
        if(username.isEmpty()){
            this.username = "username";
        }else{
            this.username = username;
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
                sendMessageThreadClass = new SendMessageThread(this);
                sendMessageThread = new Thread(sendMessageThreadClass);
                sendMessageThread.start();
            }

            //Update the current clientSocket
            //endMessageThreadClass.setClientSocket(this.clientSocket);

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

    private boolean requestConnection(){


        try(DatagramSocket socket = new DatagramSocket(0)) {

            //Wait a specified amount of time for response
            socket.setSoTimeout(10000);

            //Construct server message, client's username
            String message = username;
            byte[] messageInBytes= message.getBytes(StandardCharsets.ISO_8859_1);

            //DatagramPackets for outgoing message to the server and one for incoming message
            DatagramPacket request = new DatagramPacket(messageInBytes, messageInBytes.length, InetAddress.getByName(serverHost), remotePort);
            DatagramPacket response = new DatagramPacket(new byte[1], 1 );

            //Send and receive DatagramPackets
            socket.send(request);
            socket.receive(response);

            //Response converted to String
            String responseString = new String(response.getData(), StandardCharsets.ISO_8859_1);


            // "y" for "Yes the user can connect". "u" for "Username taken", the user cannot connect. "f" for "The server is full"
            if(responseString.equalsIgnoreCase("y")){
                return true;
            }else if(responseString.equalsIgnoreCase("u")){
                System.err.println("Username already taken");
                return false;
            }else if(responseString.equalsIgnoreCase("f")){
                System.err.println("The server is full");
            }

            return false;





        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No response from server");
            return false;
        }

    }

    void startClient(){

        if(!requestConnection()){
            System.exit(0);
        }

        Socket socket = new Socket();

        try{
            //Tries to connect to the server
            socket = new Socket(serverHost, remotePort);
            this.clientSocket = socket;

            System.out.println("\n--TCP connection has been established--");

            //Initialize a new SendMessageThread and start a new thread using it
            sendMessageThreadClass = new SendMessageThread(this);
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

                return requestConnection();


                /*//Check if socket can be bound to the server, an exception thrown indicates the server is not open
                try(Socket socket = new Socket(serverHost, remotePort)) {

                    socket.close();

                    return true;
                } catch (IOException e) {
                    System.err.println("\n--RECONNECTION FAILED--\n");
                    return false;

                }*/

            }
        }
    }
}
