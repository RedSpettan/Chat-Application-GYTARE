package clientapp;

import clientapp.gui.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame("Client");
        });
    }

}
