package clientapp.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SendMessageThread {

    private Client activeClient;


    SendMessageThread(Client client){

        this.activeClient = client;

    }


    public void SendMessages(){

        PrintWriter out;

        if(!activeClient.messageToBeSentQueue.isEmpty()){

            String messageToBeSent = activeClient.messageToBeSentQueue.poll();

            try{
                out = new PrintWriter(activeClient.socket.getOutputStream(), true, StandardCharsets.ISO_8859_1);

                out.println(messageToBeSent);
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();

                System.out.println("Output stream error in Send message thread");
            }


        }
    }
}
