package GUI;

import javax.swing.*;
import java.awt.*;

public class InformationFrame extends JFrame {

    ServerSetupPanel serverSetupPanel;

    public InformationFrame(String title){
        super(title);

        setVisible(true);

        //Set size and the program will close upon pressing the X
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation((dim.width/2) - (getSize().width /2), (dim.height /2) - getSize().height / 2);

        serverSetupPanel = new ServerSetupPanel(this);

        Container c = getContentPane();

        constraints.weightx = 0;
        constraints.weighty = 0;

        constraints.gridx = 0;
        constraints.gridy = 0;

        c.add(serverSetupPanel, constraints);


    }


}
