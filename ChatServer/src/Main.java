import GUI.InformationFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        Server server = new Server(9543,2);


        SwingUtilities.invokeLater(() ->{
            new InformationFrame("Test");
        });

        server.StartServer();





    }
}
