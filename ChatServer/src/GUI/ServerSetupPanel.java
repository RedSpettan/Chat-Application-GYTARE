package GUI;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class ServerSetupPanel extends JPanel {

    public ServerSetupPanel(JFrame currentFrame){
        Dimension size = getPreferredSize();
        size.width = (currentFrame.getSize().width / 2);
        size.height = (currentFrame.getSize().height / 2);


        setPreferredSize(size);
        setSize(size);

        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        //Submit button
        JButton button = new JButton("Start Server");

        //Create a label an text field for port number
        JLabel portLabel = new JLabel("Port Number: ");
        JTextField portTextField = new JTextField(10);

        JLabel maximumUserLabel = new JLabel("Maximum Users: ");
        JTextField maximumUserField = new JTextField(10);

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
        add(button, constraints);

        // <--- Second Column ---------------->

        // <--- First Row ----->

        constraints.insets =  new Insets(0,0,0,0);

        constraints.anchor = GridBagConstraints.LINE_START;

        constraints.gridx = 1;
        constraints.gridy = 0;

        constraints.gridwidth = 1;

        add(maximumUserField, constraints);

        // <--- Second Row ----->

        constraints.insets = new Insets(10,0,10,0);

        constraints.gridx = 1;
        constraints.gridy = 1;

        add(portTextField, constraints);





    }



}
