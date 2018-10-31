import java.util.logging.FileHandler;

public class ShutdownHook implements Runnable {

    FileHandler fileHandler;

    public ShutdownHook(FileHandler fileHandler){

        this.fileHandler = fileHandler;

    }

    @Override
    public void run() {
        try{
            fileHandler.close();
            System.out.println("Yeet exit!");
        }catch(SecurityException e){
            e.printStackTrace();
        }
    }
}
