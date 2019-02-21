package serverapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


//This thread is used to receive messages from the client which has been associated with this thread

public class ClientThread implements Runnable{


    private Server activeServer;
    private Socket clientSocket;

    public ClientThread(Socket clientSocket, Server server){

        this.activeServer = server;
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {

        //Initialize a new BufferedReader which will read any incoming data on the socket's input stream
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.ISO_8859_1))) {

            SendUsernameMessage(reader);

            String line;

            //Will become true when the "readLine" method gives back a string which is not null
            while((line = reader.readLine()) != null){

                //Send the message to the server
                activeServer.receiveMessages(line, clientSocket);
            }


        //Will catch if the client has been shut down
        } catch (IOException e) {
            //e.printStackTrace();
            //System.out.println("***Thread associated with socket '" + clientSocket.getPort() + "' has been stopped!***");
        }
    }

    //Send username request to the client
    private void SendUsernameMessage(BufferedReader reader){


        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.ISO_8859_1);

            out.println("Username");

            String username = null;

            if((username = reader.readLine()) != null){
                System.out.println(username);
                associateUsername(username);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //Associate a username to the user's socket
    private void associateUsername(String username){

        for(User user : activeServer.userList){
            if(user.socket.equals(clientSocket)){
                user.username = username;
                activeServer.receiveMessages("--- " + user.username+ " has connected to the server ---");
                activeServer.LogUserConnection(user);
                break;
            }
        }


    }
}
