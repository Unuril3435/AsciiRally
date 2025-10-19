import java.io.PrintWriter;
import java.net.*;
import java .util.Scanner;

public class ClientPlayer {
    public int playerID;
    public static void main(String[] args) throws Exception{
        Socket s = new Socket("192.168.1.135", 1729);
        PrintWriter out = new PrintWriter(s.getOutputStream());
        Scanner in = new Scanner(s.getInputStream());
        int playerID = Integer.parseInt(in.nextLine());
        System.out.println("I am " + Map.COLORS[playerID%(Map.COLORS.length-1)] + "player " + playerID + Map.COLORS[Map.COLORS.length-1]);
        while(in.hasNext()){
            System.out.println(in.nextLine());
        }
    }
}
