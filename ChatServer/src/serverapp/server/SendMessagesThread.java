package serverapp.server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SendMessagesThread implements Runnable{


    private ConcurrentLinkedQueue<String> messageQueue;
    private Server activeServer;

    SendMessagesThread(Server server, ConcurrentLinkedQueue<String> messageQueue){

        this.messageQueue = messageQueue;
        this.activeServer = server;
    }


    public void CheckMessage(){
        //Check if the message queue in the server class (the class which started this thread) currently has any message.
        if(!messageQueue.isEmpty()){

            //Check if all sockets still are connected
            activeServer.CheckSockets();

            //Retrieve the first message in the queue
            String messageToBeSent = messageQueue.poll();

            //System.out.println("Amount of connected sockets: " + activeServer.socketList.size());

            //Print the retrieved message to all connected clients
            for(User user : activeServer.userList){
                if(!user.socket.isClosed()){
                    try{


                        //Print the message onto the socket user's output stream
                        PrintWriter out = new PrintWriter(user.socket.getOutputStream(), true, StandardCharsets.ISO_8859_1);

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

        //System.out.println("Check for messages!");
    }

    @Override
    public void run() {



        while(activeServer.serverIsRunning){

            //Check if the message queue in the server class (the class which started this thread) currently has any message.
            if(!messageQueue.isEmpty()){

                //Check if all sockets still are connected
                activeServer.CheckSockets();

                //Retrieve the first message in the queue
                String messageToBeSent = messageQueue.poll();

                //System.out.println("Amount of connected sockets: " + activeServer.socketList.size());

                //Print the retrieved message to all connected clients
                for(User user : activeServer.userList){
                    if(!user.socket.isClosed()){
                        try{


                            //Print the message onto the socket user's output stream
                            PrintWriter out = new PrintWriter(user.socket.getOutputStream(), true, StandardCharsets.ISO_8859_1);

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

        System.out.println("Send message thread has been terminated!");
    }
}
