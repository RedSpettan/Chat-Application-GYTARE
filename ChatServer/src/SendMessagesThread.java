import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SendMessagesThread implements Runnable{


    private ConcurrentLinkedQueue<String> messageQueue;
    private Server activeServer;

    public SendMessagesThread(Server server,  ConcurrentLinkedQueue<String> messageQueue){

        this.messageQueue = messageQueue;
        this.activeServer = server;
    }

    @Override
    public void run() {



        while(true){

            if(!messageQueue.isEmpty()){

                String messageToBeSent = messageQueue.poll();

                for(Socket s : activeServer.socketList){

                    try(Writer out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

                        out.write(messageToBeSent);

                        out.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }
        }

    }
}
