package serverapp.server;




import serverapp.gui.MainFrame;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import serverapp.gui.*;

public class Server {


    boolean serverIsRunning = false;

    //Store the Servers listening port
    private int remotePort;
    public int maximumUsers;


    //List used to store all connected client sockets
    List<Socket> socketList = new ArrayList<>();

    //Hash map used to associate a client socket to a specific Thread
    Map<Socket, Thread> socketThreadMap = new HashMap<>();

    private ConcurrentLinkedQueue<String> messageToBeSentList = new ConcurrentLinkedQueue<>();

    public ConcurrentLinkedQueue<String> messagesToBeDisplayed = new ConcurrentLinkedQueue<>();


    public MainFrame mainframe;

    public List<User> userList = new ArrayList<>();


    private Thread sendMessageThread;

    private Logger requestLogger;
    private Logger errorLogger;

    private String projectPath;

    private final String loggingFolder = "\\Logs";
    private final String errorLoggingFolder = "\\Errors";
    private final String requestLoggingFolder = "\\Requests";

    private String loggingFolderPath;
    private String requestLoggingFolderPath;
    private String errorLoggingFolderPath;

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

            if(CheckRemotePortAvailability(port)){
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


            requestLogger.info("Logger initialized \r\n ");


            //Initialize the error FileHandler and add it to the error logger
            errorFileHandler = new FileHandler(errorLogFilePath);
            errorLogger.addHandler(errorFileHandler);

            //Apply the SimpleFormatter created earlier to the error FileHandler
            errorFileHandler.setFormatter(formatter);

            errorLogger.info("Logger initialized \r\n");

            //Initialize and add a new shutdown hook thread to the Runtime, used for when the program is shutdown.
            shutdownHookThread = new Thread(new ShutdownHook(requestFileHandler, errorFileHandler));
            Runtime.getRuntime().addShutdownHook(shutdownHookThread);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Check if a socket is currently not used
    public static boolean CheckRemotePortAvailability(int portToCheck){

        try(Socket socket = new Socket(InetAddress.getLocalHost(),portToCheck )){

            System.out.println("Remote port is NOT available");

            return false;

        } catch (UnknownHostException e) {
            System.out.println("Remote port is NOT available");

            return false;

        } catch (IOException e) {
            System.out.println("Remote port is available");

            return true;
        }catch (IllegalArgumentException e){
            System.out.println("Illegal Argument");

            return false;
        }

        /*try{
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
        }*/
    }

    //Initialises the server and it's associated threads
    public void StartServer(MainFrame frame){

        setUpLogger();

        this.mainframe = frame;


        System.out.println("Remote port: " + remotePort);

        //Starts a new serverapp.server.Server Socket
        try(ServerSocket serverSocket = new ServerSocket(remotePort)){

            System.out.println("Socket has been opened, awaiting connections...");
            System.out.println("IP address: " + Inet4Address.getLocalHost());


            serverIsRunning = frame.runServer;

            //Start threads
            new Thread(new ConnectionRequests()).start();
            new Thread(new ServerConnection(serverSocket, this)).start();
            //new Thread(new DebugServerThread(this)).start();

            sendMessageThread = new Thread(new SendMessagesThread(this, messageToBeSentList));
            sendMessageThread.start();


            //Get the current system time
            long systemTime = System.currentTimeMillis();



            while(serverIsRunning){

                //System.out.println(systemTime % 5000);

                serverIsRunning = frame.runServer;

                //If 5 seconds have surpassed, check if sockets are still connected
                if(System.currentTimeMillis() - systemTime >= 5000){
                    CheckSockets();
                    systemTime = System.currentTimeMillis();

                    for(User user: userList){
                        user.calculateTime();
                        System.out.println(user.formattedTimeConnected);
                    }
                }

                //Check if the send message is still alive. If not, the thread will attempt a restart
                if(!sendMessageThread.isAlive()){

                    System.err.print("*** Send Messages is not alive, attempting restart...*** ");

                    sendMessageThread = new Thread(new SendMessagesThread(this, messageToBeSentList));
                    sendMessageThread.start();
                }

            }

            ShutdownServer();

            //updateServer = true;

            //UpdateServer();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void ShutdownServer(){
        try {

            for(User user : userList){
                user.socket.close();
                //userList.remove(user);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server has been shutdown!");
    }

    private void UpdateServer(){

        /*while(true){

            //System.out.println("Here!");

            //System.out.println("SocketList: " + socketList.size() + "     HashMap: " + socketThreadMap.size());



        }*/




        /*if(!messageToBeSentList.isEmpty()){

               System.out.println("Size of list: " + messageToBeSentList.size());

               String message = messageToBeSentList.poll();

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

    //Methods used by threads to transfer incoming messages
    void ReceiveMessages(String message, Socket socket){

       // System.out.println("***" + socket.getPort() + ": " + message);

        User senderUser = null;

        for(User user : userList){
            if(user.socket.equals(socket)){
                senderUser = user;
            }
        }

        String completeMessage;

        if(senderUser != null){
            System.out.println(senderUser.username + ": " + message);
            completeMessage = senderUser.username + ": " + message;
            //messageToBeSentList.add("***" + senderUser.username + ": " + message);
        }else{
            completeMessage = "[USERNAME UNAVAILABLE]"+ ": " + message;
            //messageToBeSentList.add("***" +"[USERNAME UNAVAILABLE]"+ ": " + message);
        }

        messageToBeSentList.add(completeMessage);
        messagesToBeDisplayed.add(completeMessage);





    }
    public void ReceiveMessages(String message, String sender){
        //This method is primarily used by the server to send message to clients

        String completeMessage = sender +": " + message;
        System.out.println(completeMessage);
        messageToBeSentList.add(completeMessage);
        messagesToBeDisplayed.add(completeMessage);
    }

    //Check if connected sockets are still active
    void CheckSockets(){


        //Check if any user's socket are currently closed, which will result in the user be removed from the "userList"
        if(!userList.isEmpty()){

            for(User user : userList){

                if(user.socket.isClosed()){
                    System.out.println("Is the Thread Alive?: " + user.serverThread.isAlive());
                    System.out.println("serverapp.server.User: " + user.username + ". Socket " + user.socket.getPort() + " is currently not active ");

                    //Log the user disconnecting
                    requestLogger.info("serverapp.server.User has disconnected. " +
                            "\r\n Username:" + user.username +
                            "\r\n PortNumber: " + user.socket.getPort() +
                            "\r\n Host Address: " + user.inetAddress.getHostAddress() +
                            "\r\n Host name: " + user.inetAddress.getHostName());

                    userList.remove(user);
                    System.out.println("serverapp.server.User removed!");

                    break;
                }

            }

        }



        /*if (socketList.size() > 0) {
            //System.out.println("Hello!");
            for (int x = 0; x < socketList.size(); x++) {
                //System.out.println("Socket " + x + " is closed: " + socketList.get(x).isClosed());
                if (socketList.get(x).isClosed()) {

                    System.out.println(socketList.get(x) + " Socket is no longer available!");

                    //System.out.println(socketThreadMap.get(activeServer.socketList.get(x)).isAlive());

                    socketThreadMap.remove(socketList.get(x));



                    //System.out.println("Is Alive?: " + socketThreadMap.get(activeServer.socketList.get(x)).isAlive());

                    requestLogger.info("serverapp.server.User has disconnected. \r\n Port Number: " +
                            socketList.get(x).getPort() +
                            "\r\n Host Address: " + socketList.get(x).getInetAddress().getHostAddress() + "\r\n Host name: " + socketList.get(x).getInetAddress().getHostName() + "\r\n");
                    System.out.print("Socket " + socketList.get(x).getPort());

                    socketList.remove(x);

                    System.out.println(" has been removed!");

                    //System.exit(0);

                    CheckSockets();

                    break;
                }
            }
        } else {
            //System.out.println("SocketList is empty!");
        }*/



    }





    private class ConnectionRequests implements Runnable{

        @Override
        public void run() {


            System.out.println("Listening for requests");
            try(DatagramSocket socket = new DatagramSocket(remotePort)) {

                socket.setSoTimeout(1000);

                while(serverIsRunning){
                    DatagramPacket request = new DatagramPacket(new byte[50], 50);

                    boolean requestRecieved = false;
                    try{

                        //System.out.println("Looking for requests");
                        socket.receive(request);
                        requestRecieved = true;

                    }catch(SocketTimeoutException e){

                        //System.out.println("Failed to get message");

                    }


                    if(requestRecieved){
                        System.out.println("Request Received! ");

                        byte[] responsMessage;
                        String clientUsername = "[UNDECIDED]";
                        System.out.println("Amount of users: " + userList.size());
                        if(userList.size() >= maximumUsers){

                            //"f" for "The server is full"
                            responsMessage = "f".getBytes(StandardCharsets.ISO_8859_1);
                        }else{
                            clientUsername = new String(request.getData(), StandardCharsets.ISO_8859_1);

                            //Remove white characters
                            clientUsername = clientUsername.trim();

                            boolean usernameExist = false;

                            for(User user : userList){
                                if(user.username.equalsIgnoreCase(clientUsername)){
                                    usernameExist = true;
                                    break;
                                }
                            }

                            // "y" for "Yes the user can connect". "u" for "Username taken", the user cannot connect

                            if(!usernameExist){
                                responsMessage = "y".getBytes(StandardCharsets.ISO_8859_1);
                            }else{
                                responsMessage = "u".getBytes(StandardCharsets.ISO_8859_1);
                            }
                        }


                        // ------ Log Request ------

                        requestLogger.info("\r\nRequest Received!\r\n" +
                                "Host Address: " + request.getAddress().getHostAddress() +
                                "\r\nHost Name: " + request.getAddress().getHostName() +
                                "\r\nUsername submitted: " + clientUsername +
                                "\r\nResponse Message: " + new String(responsMessage, StandardCharsets.ISO_8859_1 ) +
                                "\r\n\r\n");



                        DatagramPacket response = new DatagramPacket(responsMessage, responsMessage.length, request.getAddress(), request.getPort());
                        socket.send(response);
                    }



                }

                System.out.println("Connection Request thread has been shut down!");



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //Check for new client connections
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
                while(serverIsRunning){



                    //Accept any incoming connection
                    Socket clientSocket = serverSocket.accept();

                    if(userList.size() < maximumUsers){

                        System.out.println("Connection has been established!");

                        System.out.println(clientSocket.getInetAddress());

                        //Create a new Thread to run a serverapp.server.ServerThread
                        Thread localThread = new Thread(new ServerThread(clientSocket, server));

                        //Add the user to the "userList"
                        userList.add( new User("Test", localThread, clientSocket));

                        localThread.start();



                        //Log the incoming client connection
                        requestLogger.info("A new client has connected. \r\n " +
                                "Port number: " + clientSocket.getPort() +
                                ".\r\n Host Address: " + clientSocket.getInetAddress().getHostAddress() +
                                ".\r\n Host name:" + clientSocket.getInetAddress().getHostName() +"\r\n");
                    }



                }

            } catch (IOException e) {
                e.printStackTrace();

                System.out.println("Server connection thread has been shutdown!");
            }

        }
    }


}
