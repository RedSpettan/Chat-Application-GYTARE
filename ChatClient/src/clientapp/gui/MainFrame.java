package clientapp.gui;

import clientapp.client.Client;
import clientapp.client.StartClientThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {

    UserSetUpPanel connectPanel;

    UsersPanel usersPanel;

    ChatPanel chatPanel;

    JButton disconnectButton;

    GridBagConstraints constraints;


    String host = "";

    int port = 0;

    String username = "";


    Timer connectTimer;

    Timer checkConnectionTimer;


    Container container;

    Client client;

    Thread updateChatThread;


    public boolean updateChat = false;

    public boolean runClient = false;


    public MainFrame(String title)  {
        super(title);

        setVisible(true);

        setSize(800,600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation((dim.width/2) - (getSize().width /2), (dim.height /2) - getSize().height / 2);

        container = getContentPane();

        /*connectPanel = new UserSetUpPanel(this);

        constraints = new GridBagConstraints();


        constraints.gridx = 0;
        constraints.gridy = 0;

        container.add(connectPanel, constraints);*/


        createSetupGUI();

        connectTimer = new Timer(2000, drawChatGUITask);
        connectTimer.setRepeats(false);


        checkConnectionTimer = new Timer(1000, checkUserConnection);
        connectTimer.setRepeats(false);
    }



    private void createSetupPanel(){
        connectPanel = new UserSetUpPanel(this);

        constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;

        container.add(connectPanel, constraints);
    }

    private void createSetupGUI(){
        createSetupPanel();

        container.revalidate();
        container.repaint();



    }

    public void createChatGUI(){
        container.remove(connectPanel);

        createChatPanel();
        createUsersPanel();
        createDisconnectButton();

        container.revalidate();
        container.repaint();

        updateChat = true;


        updateChatThread = new Thread(new UpdateChat(this));
        updateChatThread.start();



    }


    private void createUsersPanel(){

        usersPanel = new UsersPanel(this);
        constraints = new GridBagConstraints();


        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        constraints.gridx = 0;
        constraints.gridy = 0;

        constraints.anchor = GridBagConstraints.CENTER;

        constraints.insets = new Insets(10,0,5,0);

        container.add(usersPanel, constraints);

    }

    private void createChatPanel(){
        chatPanel = new ChatPanel(this);

        constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 1;

        constraints.anchor = GridBagConstraints.PAGE_END;

        constraints.insets = new Insets(0,0,10,0);

        container.add(chatPanel, constraints);
    }

    private void createDisconnectButton(){
        disconnectButton = new JButton("Disconnect");

        disconnectButton.addActionListener(this);

        constraints = new GridBagConstraints();

        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        constraints.gridx = 0;
        constraints.gridy = 2;

        constraints.anchor = GridBagConstraints.PAGE_END;

        constraints.insets = new Insets(0,0,10,0);

        container.add(disconnectButton, constraints);

    }


    public void displayServerRespondError(Client client){
        JOptionPane.showMessageDialog(null,
                "Server failed to respond.\nMake sure all information is correct and try again.",
                "HOST ADDRESS ERROR",
                JOptionPane.ERROR_MESSAGE);


        runClient = false;

        /*int respons = JOptionPane.showConfirmDialog(null,
                "Server failed to respond.\nDo you want to retry?",
                "ERROR",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);

        if(respons == JOptionPane.YES_OPTION){
            System.out.println("Yes, retry sounds perfect!\n");

            //connectClient(client.serverHost, client.remotePort, client.username);

            connectPanel.connectButton.doClick();


        }else{
            System.out.println("Nope, I don't want that sweet retry\n");
        }*/




    }

    public void displayUnknownHostError(){
        JOptionPane.showMessageDialog(null, "Host Address is invalid.\nMake sure all information is correct and try again.", "HOST ADDRESS ERROR", JOptionPane.ERROR_MESSAGE);
        runClient = false;
    }

    public void displayUsernameTakenError(){
        JOptionPane.showMessageDialog(null, "Username is already taken.\nChange your username and re-connect again", "HOST ADDRESS ERROR", JOptionPane.ERROR_MESSAGE);
        runClient = false;
    }

    public void displayServerFullError(){
        JOptionPane.showMessageDialog(null, "Server is currently full.\nAttempt a reconnect in a while.", "HOST ADDRESS ERROR", JOptionPane.ERROR_MESSAGE);
        runClient = false;
    }




    ActionListener drawChatGUITask = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println("Attempt to draw GUI");

            if(client.clientConnected){



                createChatGUI();
                System.out.println("Drew the chat GUI!");

                container.revalidate();
                container.repaint();

                host = client.serverHost;
                port = client.remotePort;
                username = client.username;

                checkConnectionTimer.start();

            }
        }
    };


    ActionListener checkUserConnection = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {


            if(!runClient){
                checkConnectionTimer.stop();
                System.out.println("Timer stoppppped!");

            }else {
                if(client.socket.isClosed() || !client.clientConnected){

                    runClient = false;


                    JOptionPane.showMessageDialog(null,
                            "The server connection has been disrupted.",
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);

                    container.remove(usersPanel);
                    container.remove(chatPanel);
                    container.remove(disconnectButton);

                    createSetupGUI();

                }else{
                    checkConnectionTimer.start();
                    System.out.println("Start a new timer!");
                }
            }
        }
    };


    private void connectClient(){

        client = null;

        client = new Client(connectPanel.hostAddress, connectPanel.port, connectPanel.username);
        runClient = true;
        new Thread(new StartClientThread(this,client)).start();

        System.out.println("Client has been started, tries to connect...");

        connectTimer.setDelay(client.socketTimeoutTime);

        connectTimer.start();
    }

    private void connectClient(String host, int port, String username){
        client = null;

        client = new Client(host, port, username);
        runClient = true;
        new Thread(new StartClientThread(this,client)).start();

        System.out.println("Client has been started, tries to connect...");

        connectTimer.setDelay(client.socketTimeoutTime);

        connectTimer.start();
    }


    private void validateClientShutdown(){

        int response = JOptionPane.showConfirmDialog(null,
                "The Client will be disconnected.\nDo you want to proceed?",
                "Disconnect?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if(response == JOptionPane.YES_OPTION){
            runClient = false;

            container.remove(usersPanel);
            container.remove(chatPanel);
            container.remove(disconnectButton);

            createSetupGUI();
        }

    }


    @Override
    public void actionPerformed(ActionEvent e) {


        if(e.getSource() == connectPanel.connectButton){


            if(connectTimer.isRunning()){
                System.out.println("The timer is running");
                return;
            }

            System.out.println("The timer is not running!");

            if(connectPanel.validateHostAddress() && connectPanel.validatePortNumber() && connectPanel.validateUsername()){
                //connectClient();
                connectClient(connectPanel.hostAddress, connectPanel.port, connectPanel.username);
            }


        }


        if(e.getSource() == disconnectButton){


            validateClientShutdown();

            //System.out.println("Coolio a button got pressed!");



        }


    }
}
