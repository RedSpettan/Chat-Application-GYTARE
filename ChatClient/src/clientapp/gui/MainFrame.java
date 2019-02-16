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

    UserSetUpPanel connectPanel;

    UsersPanel usersPanel;

    ChatPanel chatPanel;

    JButton disconnectButton;


    GridBagConstraints constraints;
    Container container;

    String connectedUsers = "";

    String host = "";

    int port = 0;

    String username = "";


    Timer connectTimer;

    Timer checkConnectionTimer;

    Timer updateUserListTimer;

    //How often the Userlist should update (in millis)
    int updateUserList = 2000;


    Client client;

    Thread updateChatThread;



    public boolean updateChat = false;

    public boolean runClient = false;


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

        createSetupGUI();

            //Instantiate the timers

        connectTimer = new Timer(2000, drawChatGUITask);
        connectTimer.setRepeats(false);

        checkConnectionTimer = new Timer(1000, checkUserConnection);
        checkConnectionTimer.setRepeats(false);
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


        //Methods to display different error the client can encounter

    public void displayServerRespondError(Client client){
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


    private void connectClient(String host, int port, String username){
        client = null;

        //Instantiate a new client and start a new thread to run the client
        client = new Client(host, port, username);
        runClient = true;
        new Thread(new StartClientThread(this,client)).start();

        System.out.println("Client has been started, tries to connect...");

        //Start a timer which checks if the client has successfully connected to the server
        connectTimer = new Timer(client.socketTimeoutTime, drawChatGUITask);
        connectTimer.setRepeats(false);
        connectTimer.start();
    }


    //Get the usernames of all connected clients
    private void getConnectedUsers(){


        try(DatagramSocket socket = new DatagramSocket(0)){

            //Set socket time out
            socket.setSoTimeout(updateUserList);

            String message = "%USERS%";

            byte[] messageInBytes = message.getBytes(StandardCharsets.ISO_8859_1);

            DatagramPacket request = new DatagramPacket(messageInBytes, messageInBytes.length, InetAddress.getByName(client.serverHost), client.remotePort);
            DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

            //Send and receive a response
            socket.send(request);
            socket.receive(response);

            String responseMessage = new String(response.getData(), StandardCharsets.ISO_8859_1);

            //Assign the incoming usernames to the connectedUsers string variable
            if(!responseMessage.isEmpty()){
                responseMessage = responseMessage.replace(",", "\n");

                connectedUsers = responseMessage;
            }else{
                connectedUsers = "";
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

            container.remove(usersPanel);
            container.remove(chatPanel);
            container.remove(disconnectButton);

            createSetupGUI();
        }

    }

    //Draws the chat GUI if the client has connected
    ActionListener drawChatGUITask = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println("Attempt to draw GUI");

            //Check if the client has connected
            if(client.clientConnected){

                createChatGUI();
                System.out.println("Drew the chat GUI!");

                container.revalidate();
                container.repaint();

                //Save current values
                host = client.serverHost;
                port = client.remotePort;
                username = client.username;

                //Start new timers for updating the currently connected users
                //and a timer to check if the server is still running

                checkConnectionTimer.start();

                updateUserListTimer = new Timer(updateUserList, updateConnectedUsers);
                updateUserListTimer.setRepeats(false);
                updateUserListTimer.start();

            }
        }
    };

    //Check if the server connection hasn't been disrupted
    ActionListener checkUserConnection = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            //Stop the timer if the client is no longer running
            if(!runClient){
                checkConnectionTimer.stop();
                System.out.println("Timer stoppppped!");

            }else {

                //Check if the socket is no longer connected
                if(client.socket.isClosed() || !client.clientConnected){

                    runClient = false;


                    JOptionPane.showMessageDialog(null,
                            "The server connection has been disrupted.",
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);

                    //Remove the chat GUI and draw the Set up GUI

                    container.remove(usersPanel);
                    container.remove(chatPanel);
                    container.remove(disconnectButton);

                    createSetupGUI();

                }else{

                    //Restart the timer
                    checkConnectionTimer.start();

                }
            }
        }
    };

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

    @Override
    public void actionPerformed(ActionEvent e) {

        //Check if the connected button has been pressed in the set up GUI
        if(e.getSource() == connectPanel.connectButton){

            //Prevent spam presses
            if(connectTimer.isRunning()){
                System.out.println("The timer is running");
                return;
            }

            System.out.println("The timer is not running!");

            //Validate the 3 different text fields
            if(connectPanel.validateHostAddress() && connectPanel.validatePortNumber() && connectPanel.validateUsername()){
                connectClient(connectPanel.hostAddress, connectPanel.port, connectPanel.username);
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

            int response = JOptionPane.showConfirmDialog(null,
                    "The client will be disconnected and the program will shut down.\nDo you want to proceed?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if(response == JOptionPane.YES_OPTION){
                System.exit(0);
            }

        }
    };

}
