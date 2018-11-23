import java.io.*;
import java.net.Socket;
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
        PrintWriter output = null;

        try{

            //"Scan" console input
            scanner = new Scanner(System.in);
            String line;

            //Read new lines from the console
            while((line = scanner.nextLine()) != null){

                //Print the message to the socket connected with the client
                output = new PrintWriter(activeClient.clientSocket.getOutputStream(), true, StandardCharsets.ISO_8859_1);
                output.println(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
            if(output != null){
                output.close();
            }

        }

    }
}
