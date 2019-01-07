package clientapp.client;

import clientapp.gui.MainFrame;

public class StartClientThread implements Runnable{


    MainFrame frame;
    Client client;


    public StartClientThread(MainFrame frame, Client client) {


        this.frame = frame;
        this.client = client;

    }

    @Override
    public void run() {


        client.startClient(frame);

    }
}
