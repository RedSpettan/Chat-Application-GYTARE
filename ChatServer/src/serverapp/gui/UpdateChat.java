package serverapp.gui;

import serverapp.server.User;

import java.util.List;

public class UpdateChat implements Runnable {


    private MainFrame frame;

    int scrollInitialMaximum = 0;
    int scrollValue;

    long timeInitilaized;

    //Font font = new Font("Comic Sans MS", Font.BOLD, 30);
    public UpdateChat(MainFrame mainFrame){

        this.frame = mainFrame;

        timeInitilaized = System.currentTimeMillis();

    }

    private void updateInformationPanel(){
        if(System.currentTimeMillis() - timeInitilaized > 2000){

            frame.informationPanel.clearTextAreas();

            for(User user : frame.server.userList){

                frame.informationPanel.usernameTextArea.append(user.username + "\n");
                frame.informationPanel.ipAddressTextArea.append(user.inetAddress.toString()+ "\n");
                frame.informationPanel.portTextArea.append(user.socket.getPort() + "\n");
                frame.informationPanel.timeTextArea.append(user.formattedTimeConnected + "\n");

            }

            //frame.informationPanel.usernameTextArea.append("Hello\n");

            timeInitilaized = System.currentTimeMillis();
        }
    }

    @Override
    public void run() {

        System.out.println("Runnable is running!");

        while(frame.updateChat){

            updateInformationPanel();

            if(!frame.server.messagesToBeDisplayed.isEmpty()){

                String messageToBeSent = frame.server.messagesToBeDisplayed.poll();

                System.out.println("Message to be sent: " + messageToBeSent);

                if(messageToBeSent != null){

                    if(scrollInitialMaximum == 0){
                        scrollInitialMaximum = frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum();
                    }

                    scrollValue = frame.chatPanel.scrollPane.getVerticalScrollBar().getValue();

                    boolean moveScrollBar = false;

                    if((scrollValue ==(frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum() - scrollInitialMaximum)) || (scrollValue == 1)){
                        moveScrollBar = true;
                        System.out.println("Scroll bar will be moved!");

                    }

                    /*System.out.println("Scroll value: " + scrollValue);
                    System.out.println("Maximum: " + frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum());
                    System.out.println("Maximum - inital: " + (frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum() - scrollInitialMaximum));*/

                    frame.chatPanel.textArea.append("\n"+ messageToBeSent);
                    //frame.chatPanel.textArea.setFont(font);

                    if(moveScrollBar){
                        frame.chatPanel.textArea.setCaretPosition(frame.chatPanel.textArea.getDocument().getLength());
                    }

                    //frame.chatPanel.scrollPane.getVerticalScrollBar().setValue(frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum());



                    /*System.out.println("Scrollbars value: " + frame.chatPanel.scrollPane.getVerticalScrollBar().getValue());

                    System.out.println("Scrollbar max: " + frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum());*/



                    //System.out.println("The amount of Rows: " +frame.chatPanel.textArea.getRows() );
                }else{
                    System.out.println("Nullllll: " + messageToBeSent);
                }



            }


        }
    }
}
