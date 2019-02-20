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

        SwingUtilities.invokeLater(() ->{
            MainFrame mainFrame = new MainFrame("Server");
        });

    }

}
