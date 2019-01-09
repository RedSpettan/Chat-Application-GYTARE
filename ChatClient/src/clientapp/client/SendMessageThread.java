package clientapp.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SendMessageThread implements Runnable{

    private Client activeClient;

    Scanner scanner;

    SendMessageThread(Client client){

        this.activeClient = client;

    }

    @Override
    public void run() {

        PrintWriter out = null;

        while(activeClient.clientIsRunning){

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

        System.out.println("Send Message Thread has now been shut down!");


        /*PrintWriter output = null;

        try{

            //"Scan" console input
            scanner = new Scanner(System.in);
            String line;

            //Read new lines from the console
            while((line = scanner.nextLine()) != null){

                //Print the message to the socket connected with the client
                output = new PrintWriter(activeClient.socket.getOutputStream(), true, StandardCharsets.ISO_8859_1);
                output.println(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
            if(output != null){
                output.close();
            }

        }*/

    }
}
