package serverapp.server;

import serverapp.gui.MainFrame;

public class StartServerThread implements Runnable {

    Server server;
    MainFrame frame;


    public StartServerThread(MainFrame frame, Server server) {

        this.server = server;
        this.frame = frame;
    }

    @Override
    public void run() {

        server.startServer(frame);

        try{
            while(true){

                //Close the thread and shutdown server
                if(!frame.runServer){
                    server.shutdownServer();
                    break;
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //System.out.println("StartServerThread has been shutdown!");

    }
}
