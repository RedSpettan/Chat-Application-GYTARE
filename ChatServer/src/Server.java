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

    private static Logger requestLogger;

    private static final String loggingFolderPath = "\\Logs";

    private Thread shutdownHookThread;


    private String getCurrentDate(String pattern){
        //Get the current Date
        Date calendar = Calendar.getInstance().getTime();

        //Initilize a new SimpleDateFormat using the pattern given in the method parameter
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        //Format the Date using the SimpleDateFormat
        String formattedDate = format.format(calendar);

        return formattedDate;
    }


    private void setUpLogger(){


        //Initilize the request logger and create a new FileHandler which will be used by the logger
        requestLogger = Logger.getLogger("requests");
        FileHandler requestFileHandler;

        try{
            //Get the current date by a specific pattern
            String currentDate = getCurrentDate("EEE yyyy'-'MM'-'dd 'at' HH-mm-ss z");

            //Get the project path
            String projectPath = new File(".").getCanonicalPath();


            //Checks if the log files have a folder inside the project. If it doesn't exist it will create one
            try{
                if(!Files.exists(Paths.get(projectPath + loggingFolderPath))){
                    Files.createDirectory(Paths.get(projectPath + loggingFolderPath));
                }
            }catch(IOException e){
                e.printStackTrace();
            }

            //Decide the save location for the save file, which is the project path + the folder where the logs are saved + the current date and time
            String logFilePath = projectPath + loggingFolderPath + "\\Requests " + currentDate +".log";


            //Check if the file already exist, if it doesn't it will create one
            try{
                if(!Files.exists(Paths.get(logFilePath))){
                    Files.createFile(Paths.get(logFilePath));
                }

            }catch(FileAlreadyExistsException e){
                e.printStackTrace();
            }

            //Initialize the FileHandler and add it to the request logger
            requestFileHandler = new FileHandler(logFilePath);
            requestLogger.addHandler(requestFileHandler);

            //Create a new formatter and apply it to the FileHandler
            SimpleFormatter formatter = new SimpleFormatter();
            requestFileHandler.setFormatter(formatter);


            requestLogger.info("Logger initialized ");

            //Initialize and add a new shutdown hook thread to the Runtime, used for when the program is shutdown.
            shutdownHookThread = new Thread(new ShutdownHook(requestFileHandler));
            Runtime.getRuntime().addShutdownHook(shutdownHookThread);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Server(int port){

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


        setUpLogger();

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

                    requestLogger.info("A new client has connected with port number: " + clientSocket.getPort() +
                            ".\n Host Address: " + clientSocket.getInetAddress().getHostAddress() +".\n Host name:" + clientSocket.getInetAddress().getHostName());




                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
