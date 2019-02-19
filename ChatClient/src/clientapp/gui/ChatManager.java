package clientapp.gui;

import java.util.Timer;
import java.util.TimerTask;

public class ChatManager {


    private MainFrame frame;

    private int scrollInitialMaximum = 0;
    private int scrollValue;

    private Timer updateChatTimer = new Timer();


    public ChatManager(MainFrame mainFrame) {

        this.frame = mainFrame;

        //Update chat every 100 milliseconds
        updateChatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateChat();
            }
        }, 1000, 100);

    }

    //Post new messages and scrolls the text pane
    public void updateChat(){
        //Check if any message is pending to be sent
        if(!frame.client.messageToBeDisplayedList.isEmpty()){

            //Get the message at the front of the queue
            String messageToBeSent = frame.client.messageToBeDisplayedList.poll();

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
                frame.chatPanel.textArea.append(messageToBeSent + "\n");

                //Move the scroll bar to the bottom
                if(moveScrollBar){
                    frame.chatPanel.textArea.setCaretPosition(frame.chatPanel.textArea.getDocument().getLength());
                }

            }
        }
    }


    public void stopTimer(){
        updateChatTimer.cancel();
    }

}
