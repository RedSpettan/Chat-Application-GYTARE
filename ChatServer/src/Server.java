import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

class Server {

    //Store the Servers listening port
    private int remotePort;

    //List used to store all connected client sockets
    List<Socket> socketList = new ArrayList<>();

    //Hash map used to associate a client socket to a specific Thread
    Map<Socket, Thread> socketThreadMap = new HashMap<>();

    private ConcurrentLinkedQueue<String> messageList = new ConcurrentLinkedQueue<>();

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
    Server(int port){

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
            String currentDate = getCurrentDate("EEE yyyy'-'MM'-'dd 'at' HH-mm-ss z");

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
            shutdownHookThread = new Thread(new ShutdownHook(requestFileHandler));
            Runtime.getRuntime().addShutdownHook(shutdownHookThread);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Check if a socket is currently not used
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

    //Initialises the server and it's associated threads
    void StartServer(){


        setUpLogger();

        System.out.println("Remote port: " + remotePort);

        //Starts a new Server Socket
        try(ServerSocket serverSocket = new ServerSocket(remotePort)){

            System.out.println("Socket has been opened, awaiting connections...");
            System.out.println("IP address: " + Inet4Address.getLocalHost());


            //Start threads
            new Thread(new ServerConnection(serverSocket, this)).start();
            new Thread(new DebugServerThread(this)).start();

            sendMessageThread = new Thread(new SendMessagesThread(this, messageList));
            sendMessageThread.start();


            //Get the current system time
            long systemTime = System.currentTimeMillis();


            while(true){

                //System.out.println(systemTime % 5000);

                //If 5 seconds have surpassed, check if sockets are still connected
                if(System.currentTimeMillis() - systemTime >= 5000){
                    CheckSockets();
                    systemTime = System.currentTimeMillis();
                }

                //Check if the send message is still alive. If not, the thread will attempt a restart
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

    //Methods used by threads to transfer incoming messages
    void ReceiveMessages(String message, Socket socket){

        System.out.println("***" + socket.getPort() + ": " + message);

        messageList.add("***" + socket.getPort() + ": " + message);

    }
    void ReceiveMessages(String message, String sender){
        //This method is primarily used by the server to send message to clients

        String completeMessage = sender +": " + message;
        System.out.println(completeMessage);
        messageList.add(completeMessage);
    }

    //Check if connected sockets are still active
    void CheckSockets(){

        if (socketList.size() > 0) {
            //System.out.println("Hello!");
            for (int x = 0; x < socketList.size(); x++) {
                //System.out.println("Socket " + x + " is closed: " + socketList.get(x).isClosed());
                if (socketList.get(x).isClosed()) {

                    System.out.println(socketList.get(x) + " Socket is no longer available!");

                    //System.out.println(socketThreadMap.get(activeServer.socketList.get(x)).isAlive());

                    socketThreadMap.remove(socketList.get(x));

                    //System.out.println("Is Alive?: " + socketThreadMap.get(activeServer.socketList.get(x)).isAlive());

                    requestLogger.info("User has disconnected. \r\n Port Number: " +
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
                while(true){

                    //Accept any incoming connection
                    Socket clientSocket = serverSocket.accept();

                    System.out.println("Connection has been established!");

                    System.out.println(clientSocket.getInetAddress());

                    //Add the socket to the socket list and start a new thread
                    //The socket will be used to identify a thread, thus added in a hash map
                    server.socketList.add(clientSocket);
                    Thread localThread = new Thread(new ServerThread(clientSocket, server));
                    server.socketThreadMap.put(clientSocket, localThread);

                    localThread.start();

                    //Log the incoming client connection
                    requestLogger.info("A new client has connected. \r\n Port number: " + clientSocket.getPort() +
                            ".\r\n Host Address: " + clientSocket.getInetAddress().getHostAddress() +".\r\n Host name:" + clientSocket.getInetAddress().getHostName() +"\r\n");




                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
