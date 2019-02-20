package clientapp.gui;

import javax.swing.*;
import java.awt.*;

public class UsersPanel extends JPanel {


    private JTextField usersTextField;

    JTextArea usersTextArea;

    private JScrollPane usersScrollPane;


    public UsersPanel(MainFrame mainFrame) {

        Dimension size = getPreferredSize();

        //Set width half of the current window size and height as 250
        size.width = (int)(mainFrame.getWidth() * 0.5f);
        size.height = 250;

        //Set size
        setPreferredSize(size);
        setSize(size);


        //Create text field, text area and scroll pane
        createComponents();

        //Set layout as GridBagLayout
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

        //Text field
        usersTextField = new JTextField(10);
        usersTextField.setEditable(false);

        //Text area
        usersTextArea = new JTextArea(2, 20);
        //usersTextArea.setPreferredSize(new Dimension(200,0));
        usersTextArea.setEditable(false);

        //The scrubbers will only appear as they are needed
        usersScrollPane = new JScrollPane(usersTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //usersScrollPane.setPreferredSize(new Dimension(200,0));



    }

}
