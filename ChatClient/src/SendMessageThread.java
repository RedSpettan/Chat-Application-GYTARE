import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SendMessageThread implements Runnable{

    private Socket clientSocket;

    public SendMessageThread(Socket socket){


        this.clientSocket = socket;

    }

    @Override
    public void run() {

        try(PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {

            Scanner scanner = new Scanner(System.in);

            String line;

            while((line = scanner.nextLine()) != null){
                output.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
