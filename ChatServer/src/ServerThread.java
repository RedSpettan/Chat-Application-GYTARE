import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread implements Runnable{

    private Server activeServer;

    private Socket clientSocket;

    public ServerThread(Socket clientSocket, Server server){

        this.activeServer = server;
        this.clientSocket = clientSocket;


    }




    @Override
    public void run() {

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String line;
            while((line = reader.readLine()) !=null){

                activeServer.ReceiveMessages(line, clientSocket);
                //activeServer.messageList.add(line);
               /* System.out.println("Size of list: " + activeServer.messageList.size());
                System.out.println("Is the socket closed?: " + clientSocket.isClosed());*/


            }



        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Thread stopped!");


        }
    }
}
