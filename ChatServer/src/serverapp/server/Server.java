package serverapp.server;




import serverapp.gui.MainFrame;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.*;

public class Server {

    //Toggle if the server should be running or not
    private boolean serverIsRunning = false;

    //Store the Servers listening port
    public int remotePort;
    public int maximumUsers;


    private ServerSocket theServerSocket;

    public InetAddress inet4Address;

    //Queue used to store messages pending to be sent to users
    private ConcurrentLinkedQueue<String> messageToBeSentList = new ConcurrentLinkedQueue<>();

    //Queue for message which will be displayed by the GUI
    public ConcurrentLinkedQueue<String> messagesToBeDisplayed = new ConcurrentLinkedQueue<>();

    //List to store connected users
    public List<User> userList = new ArrayList<>();

    public MainFrame mainframe;


    private Timer sendMessageTimer = new Timer();
    private Timer updateServerTimer = new Timer();

    private Timer logUserTimer = new Timer();
    private ConcurrentLinkedQueue<User> usersToBeLogged = new ConcurrentLinkedQueue<>();

    private SendMessages sm;

    private Logger requestLogger;
    private Logger errorLogger;

    private String projectPath;

        //Different folder paths

    private final String loggingFolder = "\\Logs";
    private final String errorLoggingFolder = "\\Errors";
    private final String requestLoggingFolder = "\\Requests";

        //String used to store the path to a folder

    private String loggingFolderPath;
    private String requestLoggingFolderPath;
    private String errorLoggingFolderPath;

    //Shutdown hook will be used when the users closes the program
    private Thread shutdownHookThread;




    //Constructor
    public Server(int port, int maximumUsers){

        if(maximumUsers <= 0){
            System.err.println("INVALID AMOUNT OF USERS!");
            System.exit(0);
        }
        this.maximumUsers = maximumUsers;

        //Check if the port is not below 0, since that is not a valid port
        if(port < 0){
            remotePort = -1;
        }else{

            if(checkRemotePortAvailability(port)){
                remotePort = port;

            }else{
                remotePort = 0;
            }


        }


    }

    //Returns the current date and time through a given pattern
    private String getCurrentDate(String pattern){
        //Get the current Date
        Date calendar = Calendar.getInstance().getTime();

        //Initialize a new SimpleDateFormat using the pattern given in the method parameter
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        //Format the Date using the SimpleDateFormat
        String formattedDate = format.format(calendar);

        return formattedDate;
    }

    //Set up save paths and file handlers for the request logger and the error logger
    private void setUpLogger(){


        //Initialize the request logger and create a new FileHandler which will be used by the logger
        requestLogger = Logger.getLogger("requests");
        FileHandler requestFileHandler;

        errorLogger = Logger.getLogger("error");
        FileHandler errorFileHandler;

        try{
            //Get the current date by a specific pattern
            String currentDate = getCurrentDate("EEE yyyy'-'MM'-'dd 'at' HH.mm.ss z");

            //Get the project path
            String projectPath = new File(".").getCanonicalPath();

            this.projectPath = projectPath;

            this.loggingFolderPath = this.projectPath + loggingFolder;
            this.requestLoggingFolderPath = this.loggingFolderPath + requestLoggingFolder;
            this.errorLoggingFolderPath = this.loggingFolderPath + errorLoggingFolder;

            //Checks if the log files have a folder inside the project. If it doesn't exist it will create one
            try{
                if(!Files.exists(Paths.get(loggingFolderPath))){
                    Files.createDirectory(Paths.get(loggingFolderPath));
                }

                //Check if the log files folder have sub-folders corresponding to "Error" and "Requests" log files, if not create two folders for error and requests

                if(!Files.exists(Paths.get(requestLoggingFolderPath))){
                    Files.createDirectory(Paths.get(requestLoggingFolderPath));
                }
                if(!Files.exists(Paths.get(errorLoggingFolderPath))){
                    Files.createDirectory(Paths.get(errorLoggingFolderPath));
                }


            }catch(IOException e){
                e.printStackTrace();
            }

            //Decide the save location for the request and error log file, which is the request/error folder path + the current date and time
            String requestLogFilePath = requestLoggingFolderPath + "\\Requests " + currentDate +".log";
            String errorLogFilePath = errorLoggingFolderPath + "\\Errors " + currentDate + ".log";

            //These two try block will check if log files already exist at the current time and date, if it doesn't it will create one (this will almost be false, but it's just in case)

            try{
                if(!Files.exists(Paths.get(requestLogFilePath))){
                    Files.createFile(Paths.get(requestLogFilePath));
                }

            }catch(FileAlreadyExistsException e){
                e.printStackTrace();
            }
            try{
                if(!Files.exists(Paths.get(errorLogFilePath))){
                    Files.createFile(Paths.get(errorLogFilePath));
                }
            }catch(FileAlreadyExistsException e){
                e.printStackTrace();
            }

            //Initialize the request FileHandler and add it to the request logger
            requestFileHandler = new FileHandler(requestLogFilePath);
            requestLogger.addHandler(requestFileHandler);


            //Create a new formatter and apply it to the request FileHandler
            SimpleFormatter formatter = new SimpleFormatter();
            requestFileHandler.setFormatter(formatter);


            requestLogger.info("Logger initialized\r\n\r\n");


            //Initialize the error FileHandler and add it to the error logger
            errorFileHandler = new FileHandler(errorLogFilePath);
            errorLogger.addHandler(errorFileHandler);

            //Apply the SimpleFormatter created earlier to the error FileHandler
            errorFileHandler.setFormatter(formatter);

            errorLogger.info("Logger initialized\r\n\r\n");

            //Initialize and add a new shutdown hook thread to the Runtime, used for when the program is shutdown.
            shutdownHookThread = new Thread(new ShutdownHook(requestFileHandler, errorFileHandler));
            Runtime.getRuntime().addShutdownHook(shutdownHookThread);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Check if a socket is currently not used
    public static boolean checkRemotePortAvailability(int portToCheck){

        //Try to connect a new socket, if the socket manages to connect it means  that the port is not available.
        //But if the try method catches an IOException that means that the socket failed to connect to the port and
        //thus the port is available
        try(Socket socket = new Socket(InetAddress.getLocalHost(),portToCheck )){

            //System.out.println("Remote port is NOT available");

            return false;

        } catch (UnknownHostException e) {
            //System.out.println("Remote port is NOT available");

            return false;

        } catch (IOException e) {
            //System.out.println("Remote port is available");

            return true;
        }catch (IllegalArgumentException e){
            //System.out.println("Illegal Argument");

            return false;
        }

    }

    //Initialises the server and it's associated threads
    public void startServer(MainFrame frame){

        setUpLogger();

        this.mainframe = frame;


        //System.out.println("Remote port: " + remotePort);

        //Starts a new Server Socket
        try{

            theServerSocket = new ServerSocket(remotePort);

            /*System.out.println("Socket has been opened, awaiting connections...");
            System.out.println("IP address: " + Inet4Address.getLocalHost());*/

            requestLogger.info("\r\n--- Server Started ---" +
                    "\r\nPort Number: " + remotePort +
                    "\r\nMaximum Users: " + maximumUsers +
                    "\r\n\r\n");

            serverIsRunning = frame.runServer;

            //Start threads
            new Thread(new ServerRequests()).start();
            new Thread(new ServerConnection(theServerSocket, this)).start();


            sm = new SendMessages(this, messageToBeSentList);

            //Check messages to be sent every 200 milliseconds
            sendMessageTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sm.CheckMessage();
                }
            }, 1000, 200);

            //Update sockets and users every 3 seconds
            updateServerTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    checkSockets();

                    for(User user: userList){
                        user.calculateTime();
                        //System.out.println(user.formattedTimeConnected);
                    }

                }
            }, 1000, 3000);

            //Check if a user needs to be logged
            logUserTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logUserConnection();
                }
            }, 1000, 1000);



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Close all sockets connected to the server
    public void shutdownServer(){
        try {

            //Close socket
            theServerSocket.close();

            //Stop timers
            updateServerTimer.cancel();
            sendMessageTimer.cancel();
            logUserTimer.cancel();

            serverIsRunning = false;



            //Stop each user socket
            for(User user : userList){
                user.socket.close();
            }

            userList.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }

        requestLogger.info("\r\n--- Server shutdown ---\r\n\r\n");

        Handler[] rHandlers = requestLogger.getHandlers();
        Handler[] eHandlers = errorLogger.getHandlers();


        for(Handler h : rHandlers){
            h.close();
        }
        for(Handler ha : eHandlers){
            ha.close();
        }


        System.out.println("Server has been shutdown!");
    }

        //Methods used by threads to transfer incoming messages

    //Method used to receive messages from the server threads
    void receiveMessages(String message, Socket socket){


        User senderUser = null;

        //Get the username associated with a certain socket
        for(User user : userList){
            if(user.socket.equals(socket)){
                senderUser = user;
            }
        }

        String completeMessage;

        if(message == null){
            System.out.println("Message is null?");
        }

        if(senderUser != null){
            //System.out.println(senderUser.username + ": " + message);
            completeMessage = senderUser.username + ": " + message;
        }else{
            completeMessage = "[USERNAME UNAVAILABLE]"+ ": " + message;
        }

        //Add the message to two different queues
        messageToBeSentList.add(completeMessage);
        messagesToBeDisplayed.add(completeMessage);





    }

    //This method is primarily used by the server to send message to clients
    public void receiveMessages(String message, String sender){


        String completeMessage = sender +": " + message;
        //System.out.println(completeMessage);
        messageToBeSentList.add(completeMessage);
        messagesToBeDisplayed.add(completeMessage);
    }

    public void receiveMessages(String message){
        messageToBeSentList.add(message);
        messagesToBeDisplayed.add(message);
    }

    //Check if connected sockets are still active
    void checkSockets(){


        //Check if any user's socket are currently closed, which will result in the user be removed from the "userList"
        if(!userList.isEmpty()){

            for(User user : userList){

                if(user.socket.isClosed()){
                    /*System.out.println("Is the Thread Alive?: " + user.serverThread.isAlive());
                    System.out.println("serverapp.server.User: " + user.username + ". Socket " + user.socket.getPort() + " is currently not active ");*/

                    //Log the user disconnecting
                    requestLogger.info("\r\n--- User has disconnected ---" +
                            "\r\nUsername: " + user.username +
                            "\r\nPort Number: " + user.socket.getPort() +
                            "\r\nHost Address: " + user.inetAddress.getHostAddress() +
                            "\r\nHost name: " + user.inetAddress.getHostName() + "\r\n\r\n");

                    String chatMessage = "--- " + user.username+ " has left the server ---";
                    messageToBeSentList.add(chatMessage);
                    messagesToBeDisplayed.add(chatMessage);

                    userList.remove(user);
                    //System.out.println("serverapp.server.User removed!");

                    break;
                }

            }

        }
    }


    //Listen for different request send by client using UDP
    private class ServerRequests implements Runnable{

        @Override
        public void run() {


            //System.out.println("Listening for requests");
            try(DatagramSocket socket = new DatagramSocket(remotePort)) {

                //The socket will only listen for packets in one second before breaking the block
                socket.setSoTimeout(1000);

                while(serverIsRunning){

                    DatagramPacket request = new DatagramPacket(new byte[50], 50);

                    boolean requestReceived = false;

                    //Check if any packet has arrived
                    try{
                        socket.receive(request);
                        requestReceived = true;

                    }catch(SocketTimeoutException ignored){
                        Thread.sleep(500);
                    }


                    if(requestReceived){

                        //Get the message from the packet and trim it (to remove excess white spaces)
                        String requestMessage = new String(request.getData(), StandardCharsets.ISO_8859_1);
                        requestMessage = requestMessage.trim();

                        //Check if the sender wanted to receive the connected clients' usernames
                        if(requestMessage.equals("%USERS%")){

                            String responseText = "";

                            //Put all usernames in a string
                            if(!userList.isEmpty()){
                                for(User user : userList){
                                    responseText = responseText + user.username + ",";
                                }
                            }
                            byte[] responseMessage = responseText.getBytes(StandardCharsets.ISO_8859_1);

                            //System.out.println("Username request received");

                            //Send the usernames back to the sender
                            DatagramPacket response = new DatagramPacket(responseMessage, responseMessage.length, request.getAddress(), request.getPort());
                            socket.send(response);

                        }else{

                            System.out.println("Connection request received ");

                            byte[] responseMessage;
                            String clientUsername = "[UNDECIDED]";
                            //System.out.println("Amount of users: " + userList.size());

                            //Check if the maximum user size has been exceeded, thus the sender cannot connect
                            if(userList.size() >= maximumUsers){

                                //"f" for "The server is full"
                                responseMessage = "f".getBytes(StandardCharsets.ISO_8859_1);
                            }else{

                                //Get the text from the packet
                                clientUsername = new String(request.getData(), StandardCharsets.ISO_8859_1);

                                //Remove white characters
                                clientUsername = clientUsername.trim();

                                boolean usernameExist = false;

                                //Confirm the username does not already exist
                                for(User user : userList){
                                    if(user.username.equalsIgnoreCase(clientUsername)){
                                        usernameExist = true;
                                        break;
                                    }
                                }

                                if(clientUsername.equalsIgnoreCase("server")){
                                    usernameExist = true;
                                }

                                // "y" for "Yes the user can connect". "u" for "Username taken", the user cannot connect
                                if(!usernameExist){
                                    responseMessage = "y".getBytes(StandardCharsets.ISO_8859_1);
                                }else{
                                    responseMessage = "u".getBytes(StandardCharsets.ISO_8859_1);
                                }
                            }


                            // ------ Log Request ------

                            requestLogger.info("\r\n--- Request Received ---\r\n" +
                                    "Host Address: " + request.getAddress().getHostAddress() +
                                    "\r\nHost Name: " + request.getAddress().getHostName() +
                                    "\r\nUsername submitted: " + clientUsername +
                                    "\r\nResponse Message: " + new String(responseMessage, StandardCharsets.ISO_8859_1 ) +
                                    "\r\n\r\n");


                            //Send a response to the sender
                            DatagramPacket response = new DatagramPacket(responseMessage, responseMessage.length, request.getAddress(), request.getPort());
                            socket.send(response);
                        }
                    }
                }

               // System.out.println("Connection Request thread has been shut down!");



            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                errorLogger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    //Method to add users which has joined
    synchronized void logUser(User user){

        usersToBeLogged.add(user);
    }

    //Log when a client connects to the server
    private void logUserConnection(){

        User currentUser = usersToBeLogged.poll();

        if(currentUser == null){
            //System.out.println("No user!");
            return;
        }

        //Log the incoming client connection
        requestLogger.info("\r\n--- Client connected ---" +
                "\r\nUsername: " + currentUser.username +
                "\r\nPort number: " + currentUser.socket.getPort() +
                "\r\nHost Address: " + currentUser.socket.getInetAddress().getHostAddress() +
                "\r\nHost name: " + currentUser.socket.getInetAddress().getHostName() +"\r\n\r\n");


    }

    //Listen for connection requests using TCP/IP
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

                serverSocket.setSoTimeout(1000);

                while(serverIsRunning){

                    Thread.sleep(1000);


                    /*//Accept any incoming connection
                    Socket clientSocket = serverSocket.accept();*/

                    Socket clientSocket = null;
                    try{
                         clientSocket = serverSocket.accept();

                    }catch(SocketTimeoutException ignored){
                    }

                    //Confirm the amout of connected users is below or equal to the limit
                    if((userList.size() <= maximumUsers) && clientSocket != null){

                        //System.out.println("Connection has been established!");

                        //System.out.println(clientSocket.getInetAddress());

                        //Create a new Thread to run a ClientThread
                        Thread localThread = new Thread(new ClientThread(clientSocket, server));

                        //Add the user to the "userList"
                        userList.add( new User("Test", localThread, clientSocket));

                        localThread.start();

                    }



                }

            } catch (IOException e) {
                //e.printStackTrace();

                //System.out.println("Server connection thread has been shutdown!");
            } catch (InterruptedException e) {

                errorLogger.log(Level.WARNING, e.getMessage(), e);

                e.printStackTrace();
            }

        }
    }


}
