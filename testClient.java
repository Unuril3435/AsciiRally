import java.net.*;
import java.util.Scanner;

public class testClient{
    public static void main(String[] args) throws Exception{
        Socket s = new Socket("localhost", 1729);
        System.out.println("Connection created");
        Scanner in = new Scanner(s.getInputStream());
        System.out.println(in.nextLine());
        System.out.println(in.nextLine());
    }
}
