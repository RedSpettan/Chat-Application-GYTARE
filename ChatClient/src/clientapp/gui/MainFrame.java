package clientapp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {

    ClientConnectPanel connectPanel;

    ChatPanel chatPanel;

    GridBagConstraints constraints;

    Container container;


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
    }



    @Override
    public void actionPerformed(ActionEvent e) {


        if(e.getSource() == connectPanel.connectButton){

            if(connectPanel.validateHostAddress() && connectPanel.validatePortNumber() && connectPanel.validateUsername()){
                createChatGUI();
            }

            container.revalidate();
            container.repaint();


        }


    }
}
