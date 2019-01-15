package serverapp.gui;

import serverapp.server.User;

public class UpdateChat implements Runnable {


    private MainFrame frame;

    int scrollInitialMaximum = 0;
    int scrollValue;

    long timeInitialized;

    public UpdateChat(MainFrame mainFrame){

        this.frame = mainFrame;

        //Save the time this class was initialized
        timeInitialized = System.currentTimeMillis();

    }

    //Update all the connected users information
    private void updateInformationPanel(){
        if(System.currentTimeMillis() - timeInitialized > 2000){

            frame.informationPanel.clearTextAreas();

            for(User user : frame.server.userList){

                frame.informationPanel.usernameTextArea.append(user.username + "\n");
                frame.informationPanel.ipAddressTextArea.append(user.inetAddress.toString()+ "\n");
                frame.informationPanel.portTextArea.append(user.socket.getPort() + "\n");
                frame.informationPanel.timeTextArea.append(user.formattedTimeConnected + "\n");

            }

            //frame.informationPanel.usernameTextArea.append("Hello\n");

            timeInitialized = System.currentTimeMillis();
        }
    }

    @Override
    public void run() {

        System.out.println("Runnable is running!");

        while(frame.updateChat){

            updateInformationPanel();

            //Check if any message is pending to be sent
            if(!frame.server.messagesToBeDisplayed.isEmpty()){

                //Get the message at the front of the queue
                String messageToBeSent = frame.server.messagesToBeDisplayed.poll();

                System.out.println("Message to be sent: " + messageToBeSent);

                //Confirm the message is not a null value
                if(messageToBeSent != null){

                    //Get the scroll panes current initial scroll maximum, when no message are present
                    if(scrollInitialMaximum == 0){
                        scrollInitialMaximum = frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum();
                    }

                    //Store the current scroll value
                    scrollValue = frame.chatPanel.scrollPane.getVerticalScrollBar().getValue();

                    boolean moveScrollBar = false;

                    //If the current maximum - the initial maximum is equal to current scroll value, that means the scroll bar is at bottom and the scroll pane should auto scroll
                    if((scrollValue ==(frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum() - scrollInitialMaximum)) || (scrollValue == 1)){
                        moveScrollBar = true;
                        System.out.println("Scroll bar will be moved!");

                    }

                    //Post the message
                    frame.chatPanel.textArea.append("\n"+ messageToBeSent);

                    //Move the scroll bar to the bottom
                    if(moveScrollBar){
                        frame.chatPanel.textArea.setCaretPosition(frame.chatPanel.textArea.getDocument().getLength());
                    }

                }
            }
        }
    }
}
