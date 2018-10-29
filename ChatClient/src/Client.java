import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {

    public int remotePort;
    public String serverHost;

    Socket clientSocket;

    Thread sendMessageThread;
    Thread receiveMessageThread;

    SendMessageThread sendMessageThreadClass;

    int amountOfRetries;

    ConcurrentLinkedQueue<String> unsentMessageList = new ConcurrentLinkedQueue<>();


    public Client(String host, int port){

        //Check if the port is valid
        if(port < 0){
            remotePort = -1;
            System.out.println("Enter a port above 0");
        }else{
            remotePort = port;
        }

        //Check if the host method field is not empty
        if(host.isEmpty()){
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

    private void UpdateClient(){





        while(true){

            if(this.clientSocket.isClosed()){

                System.out.println("The clientSocket is closed: " + this.clientSocket.isClosed());

                if(!reconnectClient()){

                    amountOfRetries++;
                    System.out.println("Is send message thread alive? " + sendMessageThread.isAlive());
                    System.out.println("Is receive message thread alive? " + receiveMessageThread.isAlive());

                }else{

                    restartClient();

                    //break;
                }

            }
        }

        /*restartClient();
        System.out.println("Updated method has been closed!");*/
    }

    void restartClient(){


        Socket socket = new Socket();
        try {

            socket = new Socket(serverHost, remotePort);

            System.out.println("--Connection has been established--");

            this.clientSocket = socket;

            if(sendMessageThread == null || !sendMessageThread.isAlive()){
                sendMessageThreadClass = new SendMessageThread(clientSocket, this);
                sendMessageThread = new Thread(sendMessageThreadClass);
                sendMessageThread.start();
            }

            sendMessageThreadClass.setClientSocket(this.clientSocket);


            System.out.println("Is receiveMessageThread alive?: " + receiveMessageThread.isAlive());

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


    public void startClient(){

        Socket socket = new Socket();

        try{
            socket = new Socket(serverHost, remotePort);

            System.out.println("--Connection has been established--");

            this.clientSocket = socket;

            sendMessageThreadClass = new SendMessageThread(this.clientSocket, this);
            sendMessageThread = new Thread(sendMessageThreadClass);
            sendMessageThread.start();


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


       /* try(Socket clientSocket = new Socket(serverHost, remotePort)){

            this.clientSocket = clientSocket;
            System.out.println("--Connection has been established--");

            sendMessageThreadClass = new SendMessageThread(clientSocket, this);

            sendMessageThread = new Thread(sendMessageThreadClass);
            sendMessageThread.start();

            receiveMessageThread = new Thread(new ReceiveMessagesThread(clientSocket, this));
            receiveMessageThread.start();

            UpdateClient();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    public boolean reconnectClient(){

        System.out.println(System.currentTimeMillis());

        long currentTime = System.currentTimeMillis();

        while(true){

            if(System.currentTimeMillis() - currentTime > 5000){
                System.out.println("Five Seconds!");
                try(Socket socket = new Socket(serverHost, remotePort)) {

                    socket.close();

                    return true;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;

                }

            }
            else{
                //System.out.println("counting...");

                //System.out.println(System.currentTimeMillis() - currentTime );
            }

        }





        /*try(Socket clientSocket = new Socket(serverHost, remotePort)){




        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }



}
