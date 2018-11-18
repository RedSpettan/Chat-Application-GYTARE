import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here

        System.out.println("Enter your username");

        //Read username from console
        Scanner scanner = new Scanner(System.in);
        String line;
        String username = null;

        if((line = scanner.nextLine()) != null){
            username = line;
        }

        Client mainClient = new Client("LAPTOP-9V1U1J17", 9543, username);

        mainClient.startClient();

    }
}
