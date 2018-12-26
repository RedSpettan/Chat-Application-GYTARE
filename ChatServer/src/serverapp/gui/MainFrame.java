package serverapp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {

    GridBagConstraints constraints;

    ServerSetupPanel serverSetupPanel;
    ChatPanel chatPanel;
    Container container;

    //Server

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

    private void CreateChatPanel(){
        chatPanel = new ChatPanel(this);

        constraints = new GridBagConstraints();

        constraints.weighty = 0.5;

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;

        //constraints.anchor = GridBagConstraints.LINE_END;

        constraints.anchor = GridBagConstraints.PAGE_END;

        constraints.insets = new Insets(0,0,10,0);

        container.add(chatPanel, constraints);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == serverSetupPanel.submitButton){
            System.out.println("The button got pressed!");
            //serverSetupPanel.setVisible(false);
            container.remove(serverSetupPanel);

            CreateChatPanel();

            container.revalidate();
            container.repaint();




        }
    }
}
