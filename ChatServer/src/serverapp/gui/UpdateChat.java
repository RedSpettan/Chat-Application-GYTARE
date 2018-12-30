package serverapp.gui;

public class UpdateChat implements Runnable {


    private MainFrame frame;

    int scrollInitialMaximum;
    int scrollValue;


    //Font font = new Font("Comic Sans MS", Font.BOLD, 30);
    public UpdateChat(MainFrame mainFrame){

        this.frame = mainFrame;

    }

    @Override
    public void run() {

        System.out.println("Runnable is running!");




        while(frame.updateChat){

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
}
