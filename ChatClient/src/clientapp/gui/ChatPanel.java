package clientapp.gui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatPanel extends JPanel implements ActionListener {


    JTextField textField;
    JTextArea textArea;
    JScrollPane scrollPane;

    JButton sendButton;

    MainFrame frame;


    public ChatPanel(MainFrame currentFrame) {


        this.frame = currentFrame;

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

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);

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


        // <--- Send button ---------------->

        constraints = new GridBagConstraints();

        constraints.gridx = 1;
        constraints.gridy = 1;

        constraints.weightx = 0.0;
        constraints.anchor = GridBagConstraints.LINE_END;

        add(sendButton, constraints);


        // <--- Text Field ---------------->

        constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.weightx = 1.0;
        constraints.weighty = 0.0;

        constraints.gridx = 0;
        constraints.gridy = 1;

        constraints.anchor = GridBagConstraints.EAST;

        add(textField, constraints);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(!textField.getText().isEmpty()){
            String message = textField.getText();

            frame.client.messageToBeSentQueue.add(message);

            textField.setText("");

            textField.requestFocus();
        }
    }
}
