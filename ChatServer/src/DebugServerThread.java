import java.net.Socket;
import java.util.Scanner;

class DebugServerThread implements Runnable{


    private Server activeServer;

    public DebugServerThread(Server server){
        this.activeServer = server;
    }

    @Override
    public void run() {

        String line;

        Scanner inputScanner = new Scanner(System.in);


        while((line = inputScanner.nextLine()) != null){
            if(line.equals("sockets")){
                for(Socket socket : activeServer.socketList){
                    System.out.println(socket.getPort());
                }
            }else{
                activeServer.ReceiveMessages(line, "SERVER");
            }
        }
    }
}
