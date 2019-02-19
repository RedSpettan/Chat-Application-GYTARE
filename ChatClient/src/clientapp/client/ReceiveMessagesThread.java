package clientapp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ReceiveMessagesThread implements Runnable{


    private Client activeClient;

    ReceiveMessagesThread(Client client){

        this.activeClient = client;
    }

    //This run method will check for any incoming messages from the associated connected socket's input stream
    @Override
    public void run() {


        try(BufferedReader reader = new BufferedReader(new InputStreamReader(activeClient.socket.getInputStream(), StandardCharsets.ISO_8859_1));
            PrintWriter out = new PrintWriter(activeClient.socket.getOutputStream(), true, StandardCharsets.ISO_8859_1)) {

            checkForUsernameRequest(reader, out);

            String line;

            //Wait and read incoming lines on the socket's input stream
            while((line = reader.readLine()) != null){

                activeClient.messageToBeDisplayedList.add(line);

                System.out.println(line);
            }

            //Catch the error which is caused by the server shutting down
        } catch(SocketException e){

            System.err.println("\n--CONNECTION HAS BEEN DISRUPTED--\n");
            System.out.println("Receive Message Thread has been shut down");
            //System.out.println("\n***AUTOMATIC RECONNECTION WILL COMMENCE***\n");
        }
        catch (IOException e) {
            e.printStackTrace();

        }
        System.out.println("Receive Message Thread has been shut down");
    }

    //Send username on server request
    private void checkForUsernameRequest(BufferedReader reader, PrintWriter out) {

        String serverMessage;

        //Check server message "Username", send the username to the server
        try {
            if((serverMessage = reader.readLine()) != null){
                if(serverMessage.equalsIgnoreCase("Username")){

                    out.println(activeClient.username);
                    out.flush();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
