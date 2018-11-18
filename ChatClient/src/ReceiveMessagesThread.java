import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ReceiveMessagesThread implements Runnable{


    private Socket clientSocket;
    private Client activeClient;

    ReceiveMessagesThread(Socket socket, Client client){

        this.clientSocket = socket;
        this.activeClient = client;
    }

    @Override
    public void run() {

        //This run method will check for any incoming messages from the associated connected socket's input stream
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(activeClient.clientSocket.getInputStream(), StandardCharsets.ISO_8859_1));
            PrintWriter out = new PrintWriter(activeClient.clientSocket.getOutputStream(), true, StandardCharsets.ISO_8859_1)) {

            //checkForUsernameRequest();

            String serverMessage;


            if((serverMessage = reader.readLine()) != null){
                if(serverMessage.equalsIgnoreCase("Username")){

                    out.println(activeClient.username);
                    out.flush();


                }

            }

            String line;




            //Wait and read incoming lines on the socket's input stream
            while((line = reader.readLine()) != null){

                System.out.println(line);
            }

            System.out.println(line);
            System.out.println("Is the socket closed?: " + activeClient.clientSocket.isClosed());
            System.out.println("Receive message thread has been terminated!");

            //Catch the error which is caused by the server shutting down
        } catch(SocketException e){


            System.err.println("\n--CONNECTION HAS BEEN DISRUPTED--\n");
            System.out.println("\n***AUTOMATIC RECONNECTION WILL COMMENCE***\n");
            //e.printStackTrace();
            /*System.out.println("Socket ERROR!");

            System.out.println("Is the socket closed: " + clientSocket.isClosed());*/
        }
        catch (IOException e) {
            e.printStackTrace();

        }

    }

    /*private void checkForUsernameRequest() throws IOException {

        String serverMessage;



        try(BufferedReader reader = new BufferedReader(new InputStreamReader(activeClient.clientSocket.getInputStream()));
            {


        }
    }*/
}
