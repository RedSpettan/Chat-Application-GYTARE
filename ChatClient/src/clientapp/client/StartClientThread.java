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

        try{
            while(true){

                if(!frame.runClient || client.socket.isClosed()/*!client.clientIsRunning*/){

                    //Show error prompt if the server closed down
                    boolean showError = client.socket.isClosed();

                    client.ShutDownClient();
                    frame.drawSetupGUI(showError);
                    break;
                }

                //Sleep for 1 second
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch(NullPointerException ignored){
        }

    }
}
