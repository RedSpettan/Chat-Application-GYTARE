package serverapp.server;

import serverapp.Main;
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

        server.StartServer(frame);

        System.out.println("Server has been shutdown!");

    }
}
