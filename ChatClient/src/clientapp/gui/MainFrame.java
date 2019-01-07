package clientapp.gui;

import clientapp.client.Client;
import clientapp.client.StartClientThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {

    ClientConnectPanel connectPanel;

    ChatPanel chatPanel;

    GridBagConstraints constraints;

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

        connectPanel = new ClientConnectPanel(this);


        container = getContentPane();

        constraints = new GridBagConstraints();


        constraints.gridx = 0;
        constraints.gridy = 0;

        container.add(connectPanel, constraints);
    }



    private void createChatGUI(){
        container.remove(connectPanel);

        chatPanel = new ChatPanel(this);

        constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;

        constraints.anchor = GridBagConstraints.PAGE_END;

        constraints.insets = new Insets(0,0,10,0);

        container.add(chatPanel, constraints);


        updatechat = true;


        updateChatThread = new Thread(new UpdateChat(this));
        updateChatThread.start();



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

            }




        }


    }
}
