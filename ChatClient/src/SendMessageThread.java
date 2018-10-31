import java.io.*;
import java.net.Socket;
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

            scanner = new Scanner(System.in);
            String line;

            while((line = scanner.nextLine()) != null){
                output = new PrintWriter(activeClient.clientSocket.getOutputStream(), true);
                output.println(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
            if(output != null){
                output.close();
            }

        }




        /*try(PrintWriter output = new PrintWriter(activeClient.clientSocket.getOutputStream(), true)
        ) {

            System.out.println("The thread has restarted!");

            *//*while(activeClient.unsentMessageList.isEmpty()){
                System.out.println("NEED TO SEND MESSAGES");
            }*//*

            *//*for(String message : activeClient.unsentMessageList){
                System.out.println(message);
            }




            if(!activeClient.unsentMessageList.isEmpty()){

                System.out.println("HERE I AM!");

            }
*//*
            scanner = new Scanner(System.in);


            String line;

            while(((line = scanner.nextLine()) != null)){

                *//*System.out.println("Is the socket really closed?! " + clientSocket.isClosed() );
                //System.out.println("Is the socket really bound?! " + clientSocket.isBound());
                if(clientSocket.isClosed()){
                    activeClient.unsentMessageList.add(line);
                    System.out.println("IT IS CLOSED!");
                    break;
                }*//*


                System.out.println(line);

                System.out.println(clientSocket.getOutputStream());

                output.println(line);
            }


            scanner.close();
            output.close();

            System.out.println("Send message thread has been terminated!");

        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }
}
