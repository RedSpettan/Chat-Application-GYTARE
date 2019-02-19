package clientapp;

import clientapp.client.Client;
import clientapp.gui.MainFrame;

import javax.swing.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame("Client");
        });
    }

}
