package clientapp.gui;

import clientapp.client.Client;
import clientapp.client.StartClientThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MainFrame extends JFrame implements ActionListener {

    private UserSetUpPanel connectPanel;

    private UsersPanel usersPanel;

    ChatPanel chatPanel;

    private JButton disconnectButton;


    private GridBagConstraints constraints;
    private Container container;


    //Store connected users
    private String connectedUsers = "";


    //Client information

    private String host = "";

    private int port = 0;

    private String username = "";


    private Timer updateUserListTimer;

    //How often the User list should update (in millis)
    private int updateUserList = 2000;

    Client client;

    private ChatManager chatManager;


    private boolean connectPressed = false;

    public boolean runClient = false;






    // Getter/Setter methods for the connect button

    public boolean isConnectPressed() {
        return connectPressed;
    }
    public void setConnectPressed(boolean connectPressed) {
        this.connectPressed = connectPressed;

        if(connectPressed){
            connectPanel.changeButtonColor(true);
            connectPanel.connectButton.setText("Connecting...");
        }else{
            connectPanel.changeButtonColor(false);
            connectPanel.connectButton.setText("Connect");
        }
    }



    public MainFrame(String title)  {
        super(title);

        setVisible(true);

        //Set window size
        setSize(800,600);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(wa);

        //Set GridBagLayout
        setLayout(new GridBagLayout());

        //Place the window in the middle of the main screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width/2) - (getSize().width /2), (dim.height /2) - getSize().height / 2);

        container = getContentPane();

        //Create the set up GUI
        createSetupGUI();
    }



    //Create the Set up screen GUI
    private void createSetupPanel(){
        connectPanel = new UserSetUpPanel(this);

        constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;

        container.add(connectPanel, constraints);
    }

        //Create the different panels of the chat GUI

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


        //Create the different GUIs, either the set up or the chat GUI

    //This is only used starting the program, refer to drawSetupGUI otherwise
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

    }


        //Methods to display different error the client can encounter

    public void displayServerRespondError(){
        JOptionPane.showMessageDialog(null,
                "Server failed to respond.\nMake sure all information is correct and try again.",
                "Server ERROR",
                JOptionPane.ERROR_MESSAGE);


        runClient = false;

    }

    public void displayUnknownHostError(){
        JOptionPane.showMessageDialog(null, "Host Address is invalid.\nMake sure all information is correct and try again.", "Unknown Host", JOptionPane.ERROR_MESSAGE);
        runClient = false;
    }

    public void displayUsernameTakenError(){
        JOptionPane.showMessageDialog(null, "Username is already taken.\nChange your username and re-connect again", "Username unavailable", JOptionPane.ERROR_MESSAGE);
        runClient = false;
    }

    public void displayServerFullError(){
        JOptionPane.showMessageDialog(null, "Server is currently full.\nAttempt a reconnect in a while.", "Server full", JOptionPane.ERROR_MESSAGE);
        runClient = false;
    }

    private void displayConnectionDisruptedError(){
        JOptionPane.showMessageDialog(null,
                "The server connection has been disrupted.",
                "ERROR",
                JOptionPane.ERROR_MESSAGE);
    }


    //Creates a new Client and runs it in a seperate thread
    private void connectClient(String host, int port, String username){

        //Override the previous client
        client = null;

        //Instantiate a new client and start a new thread to run the client
        client = new Client(host, port, username);
        runClient = true;
        new Thread(new StartClientThread(this,client)).start();

        System.out.println("Client has been started, tries to connect...");
    }


    //Asks if the users are sure shutting down the client
    private void validateClientShutdown(){

        int response = JOptionPane.showConfirmDialog(null,
                "The Client will be disconnected.\nDo you want to proceed?",
                "Disconnect?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        //Remove chat GUI and draw set up GUI
        if(response == JOptionPane.YES_OPTION){
            runClient = false;
        }

    }

    //Draw the Chat GUI
    public void drawChatGUI(){
        createChatGUI();

        //Refresh GUI
        container.revalidate();
        container.repaint();

        //Save client values
        host = client.serverHost;
        port = client.remotePort;
        username = client.username;

        //Update the chat
        chatManager = new ChatManager(this);

        //Update connected users
        updateUserListTimer = new Timer(updateUserList, updateConnectedUsers);
        updateUserListTimer.setRepeats(false);
        updateUserListTimer.start();

    }

    //Draw the Set up GUI
    public void drawSetupGUI(boolean showError){

        //Don't update users
        updateUserListTimer.stop();

        runClient = false;

        //Remove Chat GUI
        container.remove(usersPanel);
        container.remove(chatPanel);
        container.remove(disconnectButton);

        //Don't update the chat
        chatManager.stopTimer();

        createSetupGUI();

        //Show error if the server shut down
        if(showError){
            displayConnectionDisruptedError();
        }

    }

    //Get the currently connected clients' username
    ActionListener updateConnectedUsers = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            //If the client is still running, try and get usernames
            if(runClient){
                getConnectedUsers();
                if(!connectedUsers.isEmpty()){

                    //Display the usernames
                    usersPanel.usersTextArea.setText("");
                    usersPanel.usersTextArea.append(connectedUsers);
                }else{
                    //usersPanel.usersTextArea.append("*** FAILED TO GET USERS ***");
                    usersPanel.usersTextArea.setText("");
                }

                updateUserListTimer.start();

            }else{
                updateUserListTimer.stop();
            }
        }
    };


    //Get the usernames of all connected clients
    private void getConnectedUsers(){


        try(DatagramSocket socket = new DatagramSocket(0)){

            //Set socket time out
            socket.setSoTimeout(updateUserList);

            //Code word for retrieving usernames from the server
            String message = "%USERS%";

            byte[] messageInBytes = message.getBytes(StandardCharsets.ISO_8859_1);

            //Send and receive packets
            DatagramPacket request = new DatagramPacket(messageInBytes, messageInBytes.length, InetAddress.getByName(client.serverHost), client.remotePort);
            DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

            //Send and receive a response
            socket.send(request);
            socket.receive(response);

            String responseMessage = new String(response.getData(), StandardCharsets.ISO_8859_1);

            //Assign the incoming usernames to the connectedUsers string variable
            if(!responseMessage.isEmpty()){
                responseMessage = responseMessage.replace(",", "\n");

                responseMessage = responseMessage.trim();

                connectedUsers = responseMessage;
            }else{
                connectedUsers = "";
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Timed out...");
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Check if the connected button has been pressed in the set up GUI
        if(e.getSource() == connectPanel.connectButton){

            //Prevent spam
            if(isConnectPressed()){
                return;
            }

            //Validate the 3 different text fields
            if(connectPanel.validateHostAddress() && connectPanel.validatePortNumber() && connectPanel.validateUsername()){
                connectClient(connectPanel.hostAddress, connectPanel.port, connectPanel.username);
                setConnectPressed(true);
            }
        }

        //Disconnect button
        if(e.getSource() == disconnectButton){

            validateClientShutdown();

        }
    }


    //Display a prompt when pressing the X in the top right corner
    WindowAdapter wa = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);

            int response;

            if(runClient){
                response = JOptionPane.showConfirmDialog(null,
                        "The client will be disconnected and the program will shut down.\nDo you want to proceed?",
                        "Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
            }else{
                response = JOptionPane.showConfirmDialog(null,
                        "The program will shut down.\nDo you want to proceed?",
                        "Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
            }



            if(response == JOptionPane.YES_OPTION){
                System.exit(0);
            }

        }
    };


}
