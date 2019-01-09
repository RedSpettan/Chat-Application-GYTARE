package clientapp.gui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class UsersPanel extends JPanel {

    MainFrame frame;


    JTextField usersTextField;

    JTextArea usersTextArea;

    JScrollPane usersScrollPane;


    public UsersPanel(MainFrame mainFrame) {

        this.frame = mainFrame;

        Dimension size = getPreferredSize();

        size.width = (int)(mainFrame.getWidth() * 0.5f);
        size.height = 250;

        setPreferredSize(size);

        setSize(size);

        //setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));



        createComponents();


        setLayout(new GridBagLayout());
        GridBagConstraints constraints;


        // <--- First Row (Header) ------------------>

        constraints = new GridBagConstraints();

        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 0;

        usersTextField.setText("Connected Users");
        usersTextField.setHorizontalAlignment(JTextField.CENTER);

        add(usersTextField, constraints);

        // <--- Second Row (Text Area) ------------------>

        constraints = new GridBagConstraints();

        constraints.weightx = 0.0;
        constraints.weighty = 1.0;

        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 1;

        add(usersScrollPane, constraints);


    }


    private void createComponents(){

        usersTextField = new JTextField(10);
        usersTextField.setEditable(false);

        usersTextArea = new JTextArea();
        usersTextArea.setPreferredSize(new Dimension(200,0));
        usersTextArea.setEditable(false);

        usersScrollPane = new JScrollPane(usersTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //usersScrollPane.setPreferredSize(new Dimension(200,0));



    }

}
