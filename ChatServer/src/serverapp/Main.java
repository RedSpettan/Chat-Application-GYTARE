package serverapp;

import serverapp.gui.MainFrame;
import serverapp.server.Server;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        Server server = new Server(9543,2);


        SwingUtilities.invokeLater(() ->{
            MainFrame mainFrame = new MainFrame("Test");
            //mainFrame.setVisible(false);
        });

        server.StartServer();





    }
}
