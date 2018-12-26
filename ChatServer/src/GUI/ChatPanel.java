package GUI;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.tools.Tool;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatPanel extends JPanel implements ActionListener {




    JTextField textField;
    JTextArea textArea;

    public ChatPanel(JFrame currentFrame) {


        Dimension size = getPreferredSize();

        size.width = (int) (currentFrame.getWidth() * 0.9f);
        size.height = 250;

        setPreferredSize(size);

        setSize(size);


        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        textField = new JTextField(20);
        textField.addActionListener(this);

        textArea = new JTextArea(20, 20);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);


        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();


        ///// Text Area ///////////////////////////////////////

        constraints.gridwidth = GridBagConstraints.REMAINDER;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 0;

        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        add(scrollPane, constraints);


        ////// TEXT FIELD ////////////////////////////////////////////

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        constraints.gridx = 0;
        constraints.gridy = 1;

        constraints.anchor = GridBagConstraints.EAST;

        add(textField, constraints);


        textArea.append("AnotherOne: Hello there \nTestPerson13: Wow, it sure is empty in here");


    }



    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
