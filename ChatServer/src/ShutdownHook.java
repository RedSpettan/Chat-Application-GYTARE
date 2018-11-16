import java.util.logging.FileHandler;

public class ShutdownHook implements Runnable {

    FileHandler errorFileHandler;
    FileHandler requestFileHandler;

    public ShutdownHook(FileHandler requestFileHandler, FileHandler errorFileHandler){

        this.requestFileHandler = requestFileHandler;
        this.errorFileHandler = errorFileHandler;

    }

    @Override
    public void run() {
        try{
            requestFileHandler.close();
            errorFileHandler.close();
            System.out.println("Shutdown hook exit!");
        }catch(SecurityException e){
            e.printStackTrace();
        }
    }
}
