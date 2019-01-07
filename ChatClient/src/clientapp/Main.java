package clientapp;

import clientapp.client.Client;
import clientapp.gui.MainFrame;

import javax.swing.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here


        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame("Test");
        });

        //Get username from console
        String username = RetrieveUsername();

        Client mainClient = new Client("LAPTOP-9V1U1J17", 9543, username);

        mainClient.startClient();

    }

    private static String RetrieveUsername(){

        System.out.println("Enter your username");

        //Read username from console
        Scanner scanner = new Scanner(System.in);
        String line;
        String username = null;

        while(true){

            //Username cannot be 20 characters or longer
            for(;;){
                if((line = scanner.nextLine()) != null){
                    if(line.length() <= 20){
                        username = line;
                        break;
                    }
                    System.out.println("Username too long, try again!");
                }
            }

            //Remove any special characters, only latin characters, numbers and spaces are allowed
            if(!username.equals(username.replaceAll("[^a-zA-Z0-9\\s]",""))){
                System.err.println("Username uses illegal characters, try again!");
                System.out.println("Previous username: " + username + "\nNew usersame: " + username.replaceAll("[^a-zA-Z0-9\\s]",""));
            }else{
                username = username.replaceAll("[^a-zA-Z0-9\\s]","");
                break;
            }
        }

        return username;

    }
}
