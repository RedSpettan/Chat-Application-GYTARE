package serverapp.gui;

import serverapp.server.Server;
import serverapp.server.StartServerThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {

    GridBagConstraints constraints;

    ServerSetupPanel serverSetupPanel;
    ChatPanel chatPanel;
    UserInformationPanel informationPanel;

    JButton shutDownServerButton;

    Container container;

    private Thread chatUpdateThread;

    boolean updateChat = false;

    public boolean runServer = false;

    public Server server;


    public MainFrame(String title){
        super(title);

        setVisible(true);

        //Set size and the program will close upon pressing the X
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());

        constraints = new GridBagConstraints();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation((dim.width/2) - (getSize().width /2), (dim.height /2) - getSize().height / 2);

        serverSetupPanel = new ServerSetupPanel(this);

        container = getContentPane();

        constraints.weightx = 0;
        constraints.weighty = 0;

        constraints.gridx = 0;
        constraints.gridy = 0;

        container.add(serverSetupPanel, constraints);




    }


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

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == serverSetupPanel.submitButton){
            System.out.println("The button got pressed!");

            if(serverSetupPanel.validatePortField() && serverSetupPanel.validateMaximumUsersField()){
                //serverSetupPanel.setVisible(false);

                int responsMessage = JOptionPane.showConfirmDialog(null,
                        "Are these values correct? \n Port Number: " + serverSetupPanel.port + "\n Maximum Users: " + serverSetupPanel.maximumUsers + "\n Press 'Yes' to proceed",
                        "Information",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                if(responsMessage == JOptionPane.YES_OPTION){
                    container.remove(serverSetupPanel);

                    server = new Server(serverSetupPanel.port, serverSetupPanel.maximumUsers);

                    runServer = true;

                    new Thread(new StartServerThread(this, server)).start();

                    createChatPanel();
                    createInformationPanel();
                    createShutDownButton();

                    container.revalidate();
                    container.repaint();
                }


            }

        }

        if(e.getSource() == shutDownServerButton){
            runServer = false;
            System.out.println("Run Server");
        }
    }
}
