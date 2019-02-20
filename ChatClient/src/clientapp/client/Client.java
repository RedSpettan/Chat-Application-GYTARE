package clientapp.client;

import clientapp.gui.MainFrame;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {

    public int remotePort;
    public String serverHost;
    public String username;

    //Stores message which will be sent to the server
    public ConcurrentLinkedQueue<String> messageToBeSentQueue = new ConcurrentLinkedQueue<>();

    //Store message which will be displayed by the GUI
    public ConcurrentLinkedQueue<String> messageToBeDisplayedList = new ConcurrentLinkedQueue<>();

    private Timer sendMessagesTimer = new Timer();

    private Timer updateClientTimer = new Timer();

    //Time in millis of how long the socket should wait for response from the server
    public int socketTimeoutTime = 7000;

    //Socket connected to the server
    public Socket socket;

    private MainFrame frame;



    //Constructor for Client, checks that the port is non-negative and prints the computers IP address
    public Client(String host, int port, String username){

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

    //Attempting and configures a connection
    void startClient(MainFrame frame){



        this.frame = frame;

        //Check if the server responds
        if(!requestConnection()){
            frame.runClient = false;
        }


        if(frame.runClient){

            Socket socket = new Socket();

            try{

                System.out.println("Tries TCP");

                //Tries to connect to the server
                socket = new Socket(serverHost, remotePort);
                this.socket = socket;

                System.out.println("\n--TCP connection has been established--");

                //Initialize a new clientapp.client.SendMessageThread and start a new thread using it
                SendMessageThread sendMessageThreadClass = new SendMessageThread(this);

                sendMessagesTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        sendMessageThreadClass.SendMessages();
                        //System.out.println("messages!");

                    }
                }, 1000, 100);


                //Start a new thread using an locally initialized RecieveMessageThread
                Thread receiveMessageThread = new Thread(new ReceiveMessagesThread(this));
                receiveMessageThread.start();



                frame.drawChatGUI();


            }catch(UnknownHostException e){
                frame.displayServerRespondError();
            }
            catch (IOException e) {
                e.printStackTrace();
                try{
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }finally {
                frame.setConnectPressed(false);
            }
        }

        System.out.println("startClient method has been terminated!");
    }


    //Send a connection request to the server
    private boolean requestConnection(){


        try(DatagramSocket socket = new DatagramSocket(0)) {

            //Wait a specified amount of time for response
            socket.setSoTimeout(socketTimeoutTime);

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
                frame.displayUsernameTakenError();
                return false;
            }else if(responseString.equalsIgnoreCase("f")){
                System.err.println("The server is full");
                frame.displayServerFullError();

            }

            return false;

        }catch(SocketTimeoutException e){
            frame.displayServerRespondError();
            return false;
        }
        catch(UnknownHostException e){
            frame.displayUnknownHostError();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();

            frame.runClient = false;
            return false;
        }finally {
            frame.setConnectPressed(false);
        }

    }

    //Close the connected socket
    public void ShutDownClient(){

        updateClientTimer.cancel();
        sendMessagesTimer.cancel();

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Something went wrong, shutting down....");
            System.exit(0);
        }

        System.out.println("Client has now been shut down!");

    }





}
