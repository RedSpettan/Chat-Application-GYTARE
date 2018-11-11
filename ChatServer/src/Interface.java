import javax.swing.*;
import java.awt.*;

public class Interface extends JFrame {

    public Interface(){
        JFrame frame = new JFrame("Hello!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Hello there Loggboken!", SwingConstants.CENTER);
        frame.getContentPane().add(label);

        JButton button = new JButton("Knapp");
        button.setVerticalTextPosition(AbstractButton.BOTTOM);
        button.setHorizontalTextPosition(AbstractButton.CENTER);
        button.setBounds(0,0,500,50);
        frame.getContentPane().add(button);

        JButton button1 = new JButton("Press me");
        button.setVerticalTextPosition(AbstractButton.BOTTOM);
        button.setHorizontalTextPosition(AbstractButton.RIGHT);
        button.setBounds(0,100, 500, 50);
        frame.getContentPane().add(button1);




        frame.pack();
        frame.setVisible(true);
    }



}
