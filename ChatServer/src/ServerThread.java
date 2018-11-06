import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


//This thread is used to receive messages from the client which has been associated with this thread

public class ServerThread implements Runnable{


    private Server activeServer;
    private Socket clientSocket;

    public ServerThread(Socket clientSocket, Server server){

        this.activeServer = server;
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {

        //Initialize a new BufferedReader which will read any incoming data on the socket's input stream
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String line;

            //Will become true when the "readLine" method gives back a string which is not null
            while((line = reader.readLine()) !=null){

                //Send the message to the server
                activeServer.ReceiveMessages(line, clientSocket);
            }


        //Will catch if the client has been shut down
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("***Thread associated with socket '" + clientSocket.getPort() + "' has been stopped!***");
        }
    }
}
