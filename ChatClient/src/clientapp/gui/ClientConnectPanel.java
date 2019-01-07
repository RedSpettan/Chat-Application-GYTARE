package clientapp.gui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientConnectPanel extends JPanel {

    public String hostAddress;
    public int port;
    public String username;


    JLabel hostLabel;
    JLabel portLabel;
    JLabel usernameLabel;

    JTextField hostTextField;
    JTextField portTextField;
    JTextField usernameTextField;


    public JButton connectButton;

    MainFrame frame;


    public ClientConnectPanel(MainFrame currentFrame) {


        this.frame = currentFrame;


        Dimension size = getPreferredSize();

        size.width = (currentFrame.getSize().width / 2);
        size.height = (currentFrame.getSize().height / 2);


        setPreferredSize(size);
        setSize(size);

        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));


        hostLabel = new JLabel("Host Address: ");
        hostTextField = new JTextField(20);

        portLabel = new JLabel("Port Number: ");
        portTextField = new JTextField(5);

        usernameLabel = new JLabel("Username: ");
        usernameTextField = new JTextField(15);


        connectButton = new JButton("Connect");
        connectButton.addActionListener(currentFrame);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();


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


    public boolean validateHostAddress(){

        if(!hostTextField.getText().isEmpty()){

            this.hostAddress = hostTextField.getText();
            return true;

        }


        JOptionPane.showMessageDialog(null,
                "The 'Host Address' text field is empty. \nPlease enter a valid host address. ",
                "ERROR", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    public boolean validatePortNumber(){


        int port;

        if(!portTextField.getText().isEmpty()){


            try{
                port = Math.abs(Integer.parseInt(portTextField.getText()));
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Port Number: Only numbers 0-9 is allowed. \n Please try again", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }

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

    public boolean validateUsername(){


        String username;

        if(!usernameTextField.getText().isEmpty()){

            username = usernameTextField.getText();

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

               /* System.err.println("Username uses illegal characters, try again!");
                System.out.println("Previous username: " + username + "\nNew usersame: " + username.replaceAll("[^a-zA-Z0-9\\s]",""));*/
            }/*else{
                //username = username.replaceAll("[^a-zA-Z0-9\\s]","");
            }*/

            this.username = username;
            return true;



        }else{
            JOptionPane.showMessageDialog(null, "'Username' text field is empty. \n Please enter a username.", "ERROR", JOptionPane.ERROR_MESSAGE);

            usernameTextField.requestFocus();
            return false;
        }

    }


    private String showIllegalCharacters(String username){

        String s = username;

        s = s.replaceAll("[a-zA-Z0-9\\s]","");

        char[] chars = s.toCharArray();

        String illegalChars = "";

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
