package serverapp.gui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatPanel extends JPanel implements ActionListener {

    JTextField textField;
    JTextArea textArea;
    JScrollPane scrollPane;

    public ChatPanel(JFrame currentFrame) {


        Dimension size = getPreferredSize();

        //Set the size 90% of the current window width
        size.width = (int) (currentFrame.getWidth() * 0.9f);
        size.height = 250;

        setPreferredSize(size);
        setSize(size);

        //Set border
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        //Create Text field
        textField = new JTextField(20);
        textField.addActionListener(this);

        //Create text area and use it in a scroll pane
        textArea = new JTextArea(2, 20);
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);


        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();


        // <--- Text Area ---------------->

        constraints.gridwidth = GridBagConstraints.REMAINDER;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 0;

        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        add(scrollPane, constraints);


        // <--- Text Field ---------------->

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        constraints.gridx = 0;
        constraints.gridy = 1;

        constraints.anchor = GridBagConstraints.EAST;

        add(textField, constraints);


        //textArea.append("AnotherOne: Hello there \nTestPerson13: Wow, it sure is empty in here");

        textArea.append("\nj\nj\nj\nj\nj\nj\nj\nj\nj\nj\nj\nj");


    }



    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
