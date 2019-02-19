package serverapp;

import serverapp.gui.MainFrame;
import serverapp.server.Server;

import javax.swing.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Main {

    public static void main(String[] args) {
	// write your code here

        //Server server = new Server(9543,2);


        //getNetworkInterfaces();

        SwingUtilities.invokeLater(() ->{
            MainFrame mainFrame = new MainFrame("Server");
            //mainFrame.setVisible(false);
        });

        //server.startServer();
    }

}
