package clientapp.client;

import clientapp.gui.MainFrame;

import javax.swing.*;

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

        try{
            while(true){



                if(!frame.runClient || client.socket.isClosed()/*!client.clientIsRunning*/){

                    boolean showError = client.socket.isClosed();

                    client.ShutDownClient();
                    frame.DrawSetupGUI(showError);

                    break;
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
