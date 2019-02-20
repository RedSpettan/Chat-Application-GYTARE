package serverapp.gui;

import serverapp.server.Server;
import serverapp.server.StartServerThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Enumeration;

public class MainFrame extends JFrame implements ActionListener {

    public Server server;

    ServerSetupPanel serverSetupPanel;

    ChatPanel chatPanel;
    UserInformationPanel informationPanel;
    private JButton shutDownServerButton;
    private JButton informationButton;

    private GridBagConstraints constraints;

    private Container container;


    private ChatManager chatManager;

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

        chatManager = new ChatManager(this);


        /*chatUpdateThread = new Thread(new ChatManager(this));
        chatUpdateThread.start();*/

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

    private void createInformationButton(){
        informationButton = new JButton("Server Information");

        informationButton.addActionListener(this);

        constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 2;

        //Attach it to the far left
        constraints.anchor = GridBagConstraints.LINE_START;

        constraints.insets = new Insets(0,0,10,0);

        container.add(informationButton, constraints);

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
        createInformationButton();
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

            updateChat = false;

            chatManager.StopUpdate();

            //Remove the Chat GUI
            container.remove(informationPanel);
            container.remove(chatPanel);
            container.remove(informationButton);
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

        if(e.getSource() == informationButton){

            //Display server information prompt
            JOptionPane.showMessageDialog(null, getNetworkInterfaces() + "Port: " + server.remotePort + "\nMaximum Users: " + server.maximumUsers);

        }


    }


    //Get the current IP-address associated with the host machine
    public static String getNetworkInterfaces(){

        StringBuilder addressString = new StringBuilder();

        try {
            //Get the network interfaces on computer the program is executing on
            Enumeration<NetworkInterface> eNI = NetworkInterface.getNetworkInterfaces();

            while(eNI.hasMoreElements()){

                //Get the IP-addresses associated with the network interface
                NetworkInterface n = eNI.nextElement();
                Enumeration eIA = n.getInetAddresses();

                while(eIA.hasMoreElements()){
                    InetAddress i = (InetAddress) eIA.nextElement();

                    //Only IPv4 address
                    if(!(i.getAddress().length > 4)){

                        String address = i.getHostAddress();

                        //Array to store the ip-address sliced
                        int[] intAddress = new int[4];

                        int beginIndex = 0;
                        int endIndex;

                        //Divide the IP-address into 4 slices
                        for(int x = 0; x < 4; x++){

                            endIndex = address.indexOf(".", beginIndex);

                            if(endIndex != -1){
                                intAddress[x] = Integer.parseInt(address.substring(beginIndex, endIndex));

                                beginIndex = endIndex + 1;
                            }else{
                                intAddress[x] = Integer.parseInt(address.substring(beginIndex));
                            }
                        }


                        //No loopback address
                        if(!address.equalsIgnoreCase("127.0.0.1")){

                            //Check if it is a local IP-address
                            if(intAddress[0] == 192 && intAddress[1] == 168 ){

                                System.out.println("Local address: " + address);

                                addressString.append("Local address: " + address + "\n");


                            }else{
                                System.out.println("IP address: " + address);

                                addressString.append("IP address: " + address + "\n");


                            }

                            System.out.println(i.getHostAddress());
                            //System.out.println("Length: " + i.getAddress().length);
                        }
                    }
                    //System.out.println(i.getHostAddress());
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }

        //Return
        return String.valueOf(addressString);
    }



    //Used to show a confirmation prompt if the player pressed the close button in the top corner
    WindowAdapter wa = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);

            int response;

            if(runServer){
                //Show prompt
                response = JOptionPane.showConfirmDialog(null,
                        "The server and program will shut down.\nDo you want to proceed?",
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
