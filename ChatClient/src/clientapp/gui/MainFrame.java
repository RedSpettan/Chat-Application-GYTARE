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

    JOptionPane pane;


    Container container;

    Client client;

    Thread updateChatThread;


    public boolean updatechat = false;

    public boolean runClient = false;


    public MainFrame(String title)  {
        super(title);

        setVisible(true);

        setSize(800,600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation((dim.width/2) - (getSize().width /2), (dim.height /2) - getSize().height / 2);

        connectPanel = new UserSetUpPanel(this);


        container = getContentPane();

        constraints = new GridBagConstraints();


        constraints.gridx = 0;
        constraints.gridy = 0;

        container.add(connectPanel, constraints);
    }



    public void createChatGUI(){
        container.remove(connectPanel);

        createChatPanel();
        createUsersPanel();
        createDisconnectButton();

        container.revalidate();
        container.repaint();

        updatechat = true;


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


    public void displayServerRespondError(){
        JOptionPane.showMessageDialog(null, "Server failed to respond.\nMake sure all information is correct and try again.", "HOST ADDRESS ERROR", JOptionPane.ERROR_MESSAGE);
        runClient = false;


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



    @Override
    public void actionPerformed(ActionEvent e) {


        if(e.getSource() == connectPanel.connectButton){



            if(connectPanel.validateHostAddress() && connectPanel.validatePortNumber() && connectPanel.validateUsername()){


                client = null;

                client = new Client(connectPanel.hostAddress, connectPanel.port, connectPanel.username);
                runClient = true;
                new Thread(new StartClientThread(this,client)).start();

                createChatGUI();

                container.revalidate();
                container.repaint();

                //TODO add a Swing timer here!


                /*long initializedTime = System.currentTimeMillis();

                while((System.currentTimeMillis() - initializedTime) < client.socketTimeoutTime){


                    //System.out.println((System.currentTimeMillis() - initializedTime));

                    if(client.clientConnected){
                        createChatGUI();

                        container.revalidate();
                        container.repaint();
                        break;
                    }

                }*/


                /*createChatGUI();

                container.revalidate();
                container.repaint();*/

            }


        }


        if(e.getSource() == disconnectButton){
            runClient = false;

            System.out.println("Coolio a button got pressed!");
        }


    }
}
