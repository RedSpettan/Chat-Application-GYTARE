package clientapp.gui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class ClientConnectPanel extends JPanel {


    JLabel hostLabel;
    JLabel portLabel;
    JLabel usernameLabel;

    JTextField hostTextField;
    JTextField portTextField;
    JTextField usernameTextField;


    JButton connectButton;

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






        // <--- First Column ---------------->

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
}
