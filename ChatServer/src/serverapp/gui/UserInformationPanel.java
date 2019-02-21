package serverapp.gui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class UserInformationPanel extends JPanel {

    private MainFrame mainFrame;

    private JTextField usernameHeader;
    private JTextField ipAddressHeader;
    private JTextField portHeader;
    private JTextField timeHeader;

    JTextArea usernameTextArea;
    JTextArea ipAddressTextArea;
    JTextArea portTextArea;
    JTextArea timeTextArea;

    private JScrollPane usernameScrollPane;
    private JScrollPane ipAddressScrollPane;
    private JScrollPane portScrollPane;
    private JScrollPane timeScrollPane;


    void clearTextAreas(){
        usernameTextArea.setText("");
        ipAddressTextArea.setText("");
        portTextArea.setText("");
        timeTextArea.setText("");

    }

    public UserInformationPanel(MainFrame mainFrame) {

        this.mainFrame = mainFrame;

        Dimension size = getPreferredSize();

        //Set the size 90% of the current window width and 250 in height
        size.width = (int)(mainFrame.getWidth() * 0.9f);
        size.height = 250;

        //Set the size
        setPreferredSize(size);
        setSize(size);

        //Set the border
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        //Create the different components
        createHeaders();
        createTextAreas();
        createScrollPanes();


        setLayout(new GridBagLayout());
        GridBagConstraints constraints;




        // <--- First Row (Headers) ------------------------------------->



        // <--- First Column (Username) ----->

        constraints = new GridBagConstraints();

        constraints.weightx = 0.1;
        constraints.weighty = 0.0;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 0;

        usernameHeader.setText("Username");

        add(usernameHeader, constraints);


        // <--- First Column (IP address) ----->

        constraints = new GridBagConstraints();

        constraints.weightx = 0.1;
        constraints.weighty = 0.0;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 1;
        constraints.gridy = 0;

        ipAddressHeader.setText("IP-Address");

        add(ipAddressHeader, constraints);


        // <--- First Column (Port) ----->

        constraints = new GridBagConstraints();

        constraints.weightx = 0.1;
        constraints.weighty = 0.0;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 2;
        constraints.gridy = 0;

        portHeader.setText("Port");

        add(portHeader, constraints);


        // <--- First Column (Time) ----->

        constraints = new GridBagConstraints();

        constraints.weightx = 0.1;
        constraints.weighty = 0.0;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 3;
        constraints.gridy = 0;

        timeHeader.setText("Time Connected");

        add(timeHeader, constraints);





        // <--- Second Row (Text Areas) ------------------------------------->


        // <--- First Column (Username) ----->
        constraints = new GridBagConstraints();

        constraints.weightx = 0.3;
        constraints.weighty = 1.0;

        constraints.gridheight = GridBagConstraints.REMAINDER;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 1;

        add(usernameScrollPane, constraints);


        // <--- First Column (IP address) ----->
        constraints = new GridBagConstraints();

        constraints.weightx = 0.4;
        constraints.weighty = 1.0;

        constraints.gridheight = GridBagConstraints.REMAINDER;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 1;
        constraints.gridy = 1;

        add(ipAddressScrollPane, constraints);


        // <--- First Column (Port) ----->
        constraints = new GridBagConstraints();

        constraints.weightx = 0.05;
        constraints.weighty = 1.0;

        constraints.gridheight = GridBagConstraints.REMAINDER;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 2;
        constraints.gridy = 1;

        add(portScrollPane, constraints);


        // <--- First Column (Time) ----->
        constraints = new GridBagConstraints();

        constraints.weightx = 0.2;
        constraints.weighty = 1.0;

        constraints.gridheight = GridBagConstraints.REMAINDER;

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 3;
        constraints.gridy = 1;

        add(timeScrollPane, constraints);




    }

    public void createHeaders(){

        usernameHeader = new JTextField(10);
        usernameHeader.setEditable(false);

        ipAddressHeader = new JTextField(10);
        ipAddressHeader.setEditable(false);

        portHeader = new JTextField(10);
        portHeader.setEditable(false);

        timeHeader = new JTextField(10);
        timeHeader.setEditable(false);

    }

    public void createTextAreas(){
        usernameTextArea = new JTextArea(mainFrame.server.maximumUsers, 10);
        usernameTextArea.setEditable(false);

        ipAddressTextArea = new JTextArea(mainFrame.server.maximumUsers, 10);
        ipAddressTextArea.setEditable(false);

        portTextArea = new JTextArea(mainFrame.server.maximumUsers, 10);
        portTextArea.setEditable(false);

        timeTextArea = new JTextArea(mainFrame.server.maximumUsers, 10);
        timeTextArea.setEditable(false);
    }

    public void createScrollPanes(){
        usernameScrollPane = new JScrollPane(usernameTextArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ipAddressScrollPane = new JScrollPane(ipAddressTextArea,JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        portScrollPane = new JScrollPane(portTextArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        timeScrollPane = new JScrollPane(timeTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        //Set timeScrollPanes scroll bar as the others scrollPane scroll bar. This means that scrolling one scroll bar, scrolls all scroll panes
        usernameScrollPane.getVerticalScrollBar().setModel(timeScrollPane.getVerticalScrollBar().getModel());
        ipAddressScrollPane.getVerticalScrollBar().setModel(timeScrollPane.getVerticalScrollBar().getModel());
        portScrollPane.getVerticalScrollBar().setModel(timeScrollPane.getVerticalScrollBar().getModel());

        Dimension scrollPaneSize = new Dimension(100,0);

        usernameScrollPane.setPreferredSize(scrollPaneSize);
        ipAddressScrollPane.setPreferredSize(scrollPaneSize);
        portScrollPane.setPreferredSize(scrollPaneSize);
        timeScrollPane.setPreferredSize(scrollPaneSize);





    }
}
