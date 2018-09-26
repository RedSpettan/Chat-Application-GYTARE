import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public int remotePort;
    public String serverHost;

    public Client(String host, int port){

        //Check if the port is valid
        if(port < 0){
            remotePort = -1;
            System.out.println("Enter a port above 0");
        }else{
            remotePort = port;
        }

        //Check if the host method field is not empty
        if(host.isEmpty()){
            try {
                String localAddress = InetAddress.getLocalHost().toString();
                serverHost = localAddress.substring(localAddress.indexOf("/") + 1);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }else{
            serverHost = host;
        }
    }

    public void StartClient(){

        try(Socket clientSocket = new Socket(serverHost, remotePort)){

            System.out.println("Got Here");

            while(clientSocket.isConnected()){
                System.out.println("Connected...");
            }

            System.out.println("Not connected");


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
