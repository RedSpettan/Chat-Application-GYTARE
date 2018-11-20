import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SendMessageThread implements Runnable{

    private Socket clientSocket;
    private Client activeClient;

    Scanner scanner;

    SendMessageThread(Socket socket, Client client){


        this.clientSocket = socket;
        this.activeClient = client;

    }

    void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        PrintWriter output = null;
        try{

            //Start a new scanner which reads incoming lines from the console
            scanner = new Scanner(System.in);
            String line;

            //Wait for a new line to be read by the scanner
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
