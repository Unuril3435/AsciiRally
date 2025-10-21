import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class ClientPlayer {
    public int id;
    public static void main(String[] args) throws Exception{
        String IP = "192.168.2.113";
        Socket s = new Socket(IP, ServerMap.PORT);
        Scanner in = new Scanner(s.getInputStream());
        int id = in.nextInt();
        System.out.println("I am " + Map.COLORS[id%(Map.COLORS.length-1)] + "player " + id + Map.COLORS[Map.COLORS.length-1]);
        
        s = new Socket(IP, ServerMap.PORT);
        PrintWriter out = new PrintWriter(s.getOutputStream());
        in = new Scanner(s.getInputStream());
        out.print(id);
        out.flush();

        //Recieve info from server
    }
}
