package clientapp.gui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class UserSetUpPanel extends JPanel {

    //Store client information
    String hostAddress;
    int port;
    String username;


    final Color fadeColor = new Color(180,180,180);
    Color normalColor;


    //Labels
    JLabel hostLabel;
    JLabel portLabel;
    JLabel usernameLabel;

    //Text fields
    JTextField hostTextField;
    JTextField portTextField;
    JTextField usernameTextField;

    //Button to connect
    JButton connectButton;

    GridBagConstraints constraints;

    MainFrame frame;


    public UserSetUpPanel(MainFrame currentFrame) {

        this.frame = currentFrame;
        Dimension size = getPreferredSize();

        //Set the size half of the current window
        size.width = (currentFrame.getSize().width / 2);
        size.height = (currentFrame.getSize().height / 2);

        //Apply the size
        setPreferredSize(size);
        setSize(size);

        //Make a nice border
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));


            //Create the 3 different fields

        hostLabel = new JLabel("Host Address: ");
        hostTextField = new JTextField(20);

        portLabel = new JLabel("Port Number: ");
        portTextField = new JTextField(5);

        usernameLabel = new JLabel("Username: ");
        usernameTextField = new JTextField(15);


        connectButton = new JButton("Connect");
        connectButton.addActionListener(currentFrame);

        normalColor = connectButton.getBackground();

        //Set Layout and create con
        setLayout(new GridBagLayout());

        constraints = new GridBagConstraints();


        // <--- First Column ---------------->

        // <--- First Row ----->
        constraints.gridx = 0;
        constraints.gridy = 0;

        constraints.weighty = 0.0;

        constraints.anchor = GridBagConstraints.LINE_START;

        constraints.insets =  new Insets(0,0,5,0);

        add(hostLabel, constraints);


        // <--- Second Row ----->
        constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 1;

        constraints.weighty = 0.0;

        constraints.anchor = GridBagConstraints.LINE_START;

        constraints.insets =  new Insets(0,0,20,0);

        add(portLabel, constraints);


        // <--- Third Row ----->
        constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 2;

        constraints.weighty = 0.0;

        constraints.anchor = GridBagConstraints.LINE_START;
        add(usernameLabel, constraints);


        // <--- Fourth Row ----->
        constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 3;


        constraints.gridwidth = 3;

        constraints.anchor = GridBagConstraints.SOUTH;

        constraints.insets =  new Insets(20,0,0,0);

        add(connectButton, constraints);


        // <--- Second Column ---------------->

        // <--- First Row ----->
        constraints = new GridBagConstraints();

        constraints.gridx = 1;
        constraints.gridy = 0;

        constraints.weighty = 0.0;

        constraints.anchor = GridBagConstraints.LINE_START;

        //constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.insets =  new Insets(0,0,5,0);



        add(hostTextField, constraints);



        // <--- Second Row ----->
        constraints = new GridBagConstraints();

        constraints.gridx = 1;
        constraints.gridy = 1;

        constraints.weighty = 0.0;

        constraints.anchor = GridBagConstraints.LINE_START;

        //constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.insets =  new Insets(0,0,20,0);

        add(portTextField, constraints);


        // <--- Third Row ----->

        constraints = new GridBagConstraints();


        constraints.gridx = 1;
        constraints.gridy = 2;

        constraints.weighty = 0.0;

        constraints.anchor = GridBagConstraints.LINE_START;

        //constraints.fill = GridBagConstraints.HORIZONTAL;


        add(usernameTextField, constraints);



    }

    //Confirm the Host Address text field is properly filled
    boolean validateHostAddress(){

        //Check if the text field is not empty
        if(!hostTextField.getText().isEmpty()){

            this.hostAddress = hostTextField.getText();
            return true;

        }

        //If empty, show error prompt
        JOptionPane.showMessageDialog(null,
                "The 'Host Address' text field is empty. \nPlease enter a valid host address. ",
                "ERROR", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    //Confirm the Port Number text field is correct
    boolean validatePortNumber(){


        int port;

        //Check if the port text field is not empty
        if(!portTextField.getText().isEmpty()){

            //See if the port text field only consist of numbers
            try{
                port = Math.abs(Integer.parseInt(portTextField.getText()));
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Port Number: Only numbers 0-9 is allowed. \n Please try again", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            //Check if the number does not exceed 65535
            if( port > 0xFFFF){
                JOptionPane.showMessageDialog(null, "Port number is out of range. \nLargest port allowed: 65534", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;

            }

            this.port = port;

            return true;

        }else{
            JOptionPane.showMessageDialog(null, "'Port Number' text field is empty. \n Please enter a valid port.", "ERROR", JOptionPane.ERROR_MESSAGE);

            portTextField.requestFocus();
            return false;
        }

    }

    //Confirm the Username text field follows all rules
    boolean validateUsername(){


        String username;

        //Check if the username text field is not empty
        if(!usernameTextField.getText().isEmpty()){

            username = usernameTextField.getText();

            //The username cannot exceed 20
            if(username.length() > 20){
                JOptionPane.showMessageDialog(null, "Username is too long. \nMaximum amount of characters is 20.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            //Remove any special characters, only latin characters, numbers and spaces are allowed
            if(!username.equals(username.replaceAll("[^a-zA-Z0-9\\s]",""))){

                JOptionPane.showMessageDialog(null,
                        "Username uses illegal characters\n" +
                                "Acceptable characters: \n" +
                                "•Latin alphabet (A-Z and a-z)\n" +
                                "•Numbers (0-9) \n" +
                                "•White spaces\n" +
                                "Do not use these characters: \n" +
                                showIllegalCharacters(username) ,
                        "ERROR", JOptionPane.ERROR_MESSAGE);

                return false;
            }

            this.username = username;
            return true;


        //Text field is empty
        }else{
            JOptionPane.showMessageDialog(null, "'Username' text field is empty. \n Please enter a username.", "ERROR", JOptionPane.ERROR_MESSAGE);

            usernameTextField.requestFocus();
            return false;
        }

    }


    public void changeButtonColor(boolean fade){
        if(fade){
            connectButton.setBackground(fadeColor);
        }else{
            connectButton.setBackground(normalColor);
        }
    }


    private String showIllegalCharacters(String username){

        String s = username;

        //Replace any characters which are not in the latin alphabet, numbers or white spaces
        s = s.replaceAll("[a-zA-Z0-9\\s]","");

        char[] chars = s.toCharArray();

        String illegalChars = "";

        //Store all illegal characters used in the username as a string
        for(int x = 0; x < chars.length; x++){

            if(illegalChars.indexOf(chars[x]) == -1){
                illegalChars = illegalChars + (chars[x]);

                if(x != (chars.length -1)){
                    illegalChars = illegalChars + ", ";
                }

            }
        }

        return illegalChars;



    }



}
