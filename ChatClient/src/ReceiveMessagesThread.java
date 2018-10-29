import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ReceiveMessagesThread implements Runnable{


    private Socket clientSocket;
    private Client activeClient;

    public ReceiveMessagesThread(Socket socket, Client client){

        this.clientSocket = socket;
        this.activeClient = client;
    }

    @Override
    public void run() {

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(activeClient.clientSocket.getInputStream()))) {


            String line;

            while((line = reader.readLine()) != null){

                System.out.println(line);
            }

            System.out.println("Receive message thread has been terminated!");

        } catch(SocketException e){
            e.printStackTrace();
            System.out.println("Socket ERROR!");

            System.out.println("Is the socket closed: " + clientSocket.isClosed());
        }
        catch (IOException e) {
            e.printStackTrace();

        }

    }
}
