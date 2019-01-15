package serverapp.gui;

import serverapp.server.Server;
import serverapp.server.ShutdownHook;
import serverapp.server.StartServerThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame implements ActionListener {

    public Server server;




    ServerSetupPanel serverSetupPanel;

    ChatPanel chatPanel;
    UserInformationPanel informationPanel;
    JButton shutDownServerButton;

    GridBagConstraints constraints;

    Container container;


    private Thread chatUpdateThread;


    boolean updateChat = false;

    public boolean runServer = false;




    public MainFrame(String title){
        super(title);

        setVisible(true);

        //Set size and the program will close upon pressing the X
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


        //Set Layout as GridBagLayout
        setLayout(new GridBagLayout());


        addWindowListener(wa);


        //Place the program window in the middle of the active screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width/2) - (getSize().width /2), (dim.height /2) - getSize().height / 2);


        //Create the set up GUI
        createSetupPanel();


    }


    //Create the panel used for set up
    private void createSetupPanel(){
        serverSetupPanel = new ServerSetupPanel(this);

        constraints = new GridBagConstraints();

        container = getContentPane();

        constraints.weightx = 0;
        constraints.weighty = 0;

        constraints.gridx = 0;
        constraints.gridy = 0;

        container.add(serverSetupPanel, constraints);
    }


        //Method for constructing the different elements of the chat GUI

    private void createInformationPanel(){
        informationPanel = new UserInformationPanel(this);

        constraints = new GridBagConstraints();


        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        constraints.gridx = 0;
        constraints.gridy = 0;

        constraints.anchor = GridBagConstraints.LINE_START;

        constraints.insets = new Insets(10,0,0,0);

        container.add(informationPanel, constraints);

    }

    private void createChatPanel(){
        chatPanel = new ChatPanel(this);

        constraints = new GridBagConstraints();

        constraints.weighty = 0.5;

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 1;

        //constraints.anchor = GridBagConstraints.LINE_END;

        constraints.anchor = GridBagConstraints.PAGE_END;

        constraints.insets = new Insets(0,0,10,0);

        container.add(chatPanel, constraints);

        updateChat = true;

        chatUpdateThread = new Thread(new UpdateChat(this));
        chatUpdateThread.start();

    }

    private void createShutDownButton(){
        shutDownServerButton = new JButton("Shut down Server");

        shutDownServerButton.addActionListener(this);

        constraints = new GridBagConstraints();

        constraints.weightx = 0.0;
        constraints.weighty = 0.0;


        constraints.gridx = 0;
        constraints.gridy = 2;

        constraints.anchor = GridBagConstraints.PAGE_END;

        constraints.insets = new Insets(0,0,10,0);

        container.add(shutDownServerButton, constraints);

    }


        //Method for drawing the Set up or chat GUI

    private void createSetupGUI(){
        createSetupPanel();

        //Update the GUI
        container.revalidate();
        container.repaint();
    }
    private void createChatGUI(){
        createChatPanel();
        createInformationPanel();
        createShutDownButton();

        //Update the GUI
        container.revalidate();
        container.repaint();
    }



    //Prompt to confirm server shutdown
    private void validateServerShutdown(){
        int responseMessage = JOptionPane.showConfirmDialog(null,
                "The server will be shut down. \n Do you want to proceed?",
                "Confirm",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if(responseMessage == JOptionPane.YES_OPTION){
            runServer = false;

            //Remove the Chat GUI
            container.remove(informationPanel);
            container.remove(chatPanel);
            container.remove(shutDownServerButton);

            createSetupGUI();
        }

    }


    //Validate input information and then attempts to start the server
    private void validateStartServer(){

        //See that the two text fields in the set up is properly set up
        if(serverSetupPanel.validatePortField() && serverSetupPanel.validateMaximumUsersField()){
            //serverSetupPanel.setVisible(false);

            //Prompt to confirm server start
            int responseMessage = JOptionPane.showConfirmDialog(null,
                    "Are these values correct? \n Port Number: " + serverSetupPanel.port + "\n Maximum Users: " + serverSetupPanel.maximumUsers + "\n Press 'Yes' to proceed",
                    "Information",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if(responseMessage == JOptionPane.YES_OPTION){

                //Remove set up GUI
                container.remove(serverSetupPanel);

                //Instantiate a new Server
                server = null;
                server = new Server(serverSetupPanel.port, serverSetupPanel.maximumUsers);

                runServer = true;

                //Run the server in a separate thread
                new Thread(new StartServerThread(this, server)).start();

                createChatGUI();


            }


        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == serverSetupPanel.submitButton){
            System.out.println("The button got pressed!");

            validateStartServer();

        }

        if(e.getSource() == shutDownServerButton){
            validateServerShutdown();

        }


    }

    WindowAdapter wa = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);

            int response = JOptionPane.showConfirmDialog(null,
                    "The server and program will shut down.\nDo you want to proceed?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if(response == JOptionPane.YES_OPTION){
                System.exit(0);
            }
        }
    };
}
