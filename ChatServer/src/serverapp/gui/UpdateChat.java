package serverapp.gui;

import serverapp.server.User;

import java.util.List;

public class UpdateChat implements Runnable {


    private MainFrame frame;

    int scrollInitialMaximum;
    int scrollValue;

    long timeInitilaized;

    //Font font = new Font("Comic Sans MS", Font.BOLD, 30);
    public UpdateChat(MainFrame mainFrame){

        this.frame = mainFrame;

        timeInitilaized = System.currentTimeMillis();

    }

    @Override
    public void run() {

        System.out.println("Runnable is running!");



        while(frame.updateChat){

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

            if(!frame.server.messagesToBeDisplayed.isEmpty()){

                String messageToBeSent = frame.server.messagesToBeDisplayed.poll();

                if(scrollInitialMaximum == 0){
                    scrollInitialMaximum = frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum();
                }

                scrollValue = frame.chatPanel.scrollPane.getVerticalScrollBar().getValue();

                boolean moveScrollBar = false;

                if((scrollValue ==(frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum() - scrollInitialMaximum)) || (scrollValue == 1)){
                    moveScrollBar = true;
                    System.out.println("Scroll bar will be moved!");

                }

                System.out.println("Scroll value: " + scrollValue);
                System.out.println("Maximum: " + frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum());
                System.out.println("Maximum - inital: " + (frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum() - scrollInitialMaximum));

                frame.chatPanel.textArea.append("\n"+ messageToBeSent);
                //frame.chatPanel.textArea.setFont(font);

                /*if(moveScrollBar){

                }*/

                frame.chatPanel.scrollPane.getVerticalScrollBar().setValue(frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum());

                /*System.out.println("Scrollbars value: " + frame.chatPanel.scrollPane.getVerticalScrollBar().getValue());

                System.out.println("Scrollbar max: " + frame.chatPanel.scrollPane.getVerticalScrollBar().getMaximum());*/



                //System.out.println("The amount of Rows: " +frame.chatPanel.textArea.getRows() );

            }


        }
    }


   /* static void quickSort(List<User> userList, int minimum, int maximum){

        long[] userTimes = new long[userList.size()];


        for(int x = 0; x < userList.size(); x++){
            userTimes[x] = userList.get(x).amountOfTimeConnected;
        }

        //Get the middle pivot point
        long pivot = userTimes[(minimum+maximum) / 2];


        int leftHold = minimum;
        int rightHold = maximum;

        while(leftHold < rightHold){

            while((userTimes[leftHold] < pivot) && (leftHold <= rightHold)){
                leftHold++;
            }
            while((userTimes[rightHold] > pivot) && (rightHold >= leftHold)){
                rightHold--;
            }

            if(leftHold < rightHold){

                long tmp = userTimes[leftHold];
                userTimes[leftHold] = userTimes[rightHold];
                userTimes[rightHold] = tmp;

                if((userTimes[leftHold] == pivot) && (userTimes[rightHold] == pivot)){
                    leftHold++;
                }
            }

            if((minimum < (leftHold - 1))

        }
    }*/
}
