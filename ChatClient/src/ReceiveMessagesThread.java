import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceiveMessagesThread implements Runnable{


    private Socket clientSocket;

    public ReceiveMessagesThread(Socket socket){

        this.clientSocket = socket;
    }

    @Override
    public void run() {

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {


            String line;

            while((line = reader.readLine()) != null){

                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
