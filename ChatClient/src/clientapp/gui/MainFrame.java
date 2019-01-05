package clientapp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {

    ClientConnectPanel connectPanel;

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


        GridBagConstraints constraints = new GridBagConstraints();


        constraints.gridx = 0;
        constraints.gridy = 0;

        container.add(connectPanel, constraints);






    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
