import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SendMessagesThread implements Runnable{


    private ConcurrentLinkedQueue<String> messageQueue;
    private Server activeServer;

    SendMessagesThread(Server server, ConcurrentLinkedQueue<String> messageQueue){

        this.messageQueue = messageQueue;
        this.activeServer = server;
    }

    @Override
    public void run() {



        while(true){

            //Check if the message queue in the server class (the class which started this thread) currently has any message.
            if(!messageQueue.isEmpty()){

                //Check if all sockets still are connected
                activeServer.CheckSockets();

                //Retrieve the first message in the queue
                String messageToBeSent = messageQueue.poll();

                //System.out.println("Amount of connected sockets: " + activeServer.socketList.size());

                //Print the retrieved message to all connected clients
                for(Socket s : activeServer.socketList){

                    if(!s.isClosed()){
                        try{

                            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

                            out.println(messageToBeSent);

                            out.flush();

                        } catch (IOException e) {
                            System.out.println("Crash here!");
                            e.printStackTrace();
                        }
                    }else{
                        activeServer.CheckSockets();
                    }
                }
            }
        }
    }
}
