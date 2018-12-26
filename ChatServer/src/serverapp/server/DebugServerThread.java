package serverapp.server;

import java.util.Scanner;

class DebugServerThread implements Runnable{


    private Server activeServer;

    DebugServerThread(Server server){
        this.activeServer = server;
    }

    @Override
    public void run() {

        String line;


        //Initialize a scanner to check for console inputs
        Scanner inputScanner = new Scanner(System.in);

        //Wait for new line to be written in the console
        while((line = inputScanner.nextLine()) != null){
            //Check if the word corresponds with "sockets" which will print all connected socket's port number
            if(line.equals("sockets")){
                for(User user : activeServer.userList){
                    System.out.println(user.socket.getPort());
                }
            }else{
                activeServer.ReceiveMessages(line, "SERVER");
            }
        }
    }
}
