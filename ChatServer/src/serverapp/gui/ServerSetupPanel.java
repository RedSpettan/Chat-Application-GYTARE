package serverapp.gui;

import serverapp.server.Server;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerSetupPanel extends JPanel implements ActionListener {

    JButton submitButton;

    private JLabel maximumUserLabel;
    private JLabel portLabel;

    public int port;

    private JTextField portTextField;
    private JTextField maximumUserField;

    int maximumUsers;

    MainFrame frame;


    public ServerSetupPanel(MainFrame currentFrame){

        frame = currentFrame;

        //Set the panel size half of the whole window
        Dimension size = getPreferredSize();
        size.width = (currentFrame.getSize().width / 2);
        size.height = (currentFrame.getSize().height / 2);

        //Set the size
        setPreferredSize(size);
        setSize(size);

        //Set a border
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        //Submit submitButton
        submitButton = new JButton("Start Server");
        submitButton.addActionListener((ActionListener) currentFrame);

        //Create a label an text field for port number

        portLabel = new JLabel("Port Number: ");
        portTextField = new JTextField(0);

        //Create a label and text field for maximum users

        maximumUserLabel = new JLabel("Maximum Users: ");
        maximumUserField = new JTextField(10);

        //Set the layout to GridBagLayout
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();



        // <--- First Column ---------------->

        // <--- First Row ----->
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;

        add(portLabel, constraints);

        // <--- Second Row ----->

        constraints.weighty = 0.0;
        constraints.gridx = 0;
        constraints.gridy = 1;

        constraints.insets = new Insets(10,0,10,5);
        add(maximumUserLabel, constraints);


        // <--- Third Row ----->
        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.gridx = 0;
        constraints.gridy = 2;

        constraints.gridwidth = 3;
        add(submitButton, constraints);

        // <--- Second Column ---------------->

        // <--- First Row ----->

        constraints.insets =  new Insets(0,0,0,0);

        constraints.anchor = GridBagConstraints.LINE_START;

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 1;
        constraints.gridy = 0;

        constraints.gridwidth = 1;

        add(portTextField, constraints);

        // <--- Second Row ----->

        constraints.insets = new Insets(10,0,10,0);

        constraints.gridx = 1;
        constraints.gridy = 1;


        add(maximumUserField, constraints);
    }

    //Validate that the text field "Port Number" is correctly filled out
    public boolean validatePortField(){

        int port;

        //Make sure the field is not empty
        if(!portTextField.getText().isEmpty()){

            //Parse the string to int, NumberFormatException will be called if the text field does not contain only numbers
            try{
                port = Math.abs(Integer.parseInt(portTextField.getText()));
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Port Number: Only numbers 0-9 is allowed. \n Please try again", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            //See if the port is available
            if(Server.CheckRemotePortAvailability(port)){

            }else{
                System.out.println("Not a valid port!");

                JOptionPane.showMessageDialog(null, "Please enter a valid port!", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        }else{
            System.out.println("Enter a valid port!");

            JOptionPane.showMessageDialog(null, "'Port Number' text field is empty. \n Please enter a valid port.", "ERROR", JOptionPane.ERROR_MESSAGE);

            portTextField.requestFocus();
            return false;
        }

        this.port = port;

        return true;
    }

    //Validate that the text field "Maximum Users" is correctly filled out
    public boolean validateMaximumUsersField(){


        //Check that the field is not empty
        if(!maximumUserField.getText().isEmpty()){

            int maximumUsers;

            //Parse the string to int, NumberFormatException will be called if the string does not only contain numbers
            try{
                maximumUsers = Math.abs(Integer.parseInt(maximumUserField.getText()));
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Maximum Users: Only numbers 0-9 is allowed. \n Please try again", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            this.maximumUsers = maximumUsers;

            return true;

        }else{
            JOptionPane.showMessageDialog(null, "'Maximum Users' text field is empty. \n Please enter a number.", "ERROR", JOptionPane.ERROR_MESSAGE);

            portTextField.requestFocus();

            return false;
        }

    }


    @Override
    public void actionPerformed(ActionEvent e) {



        /*if(e.getSource() == submitButton){
            System.out.println("Button has been pressed");

        }*/

    }
}
