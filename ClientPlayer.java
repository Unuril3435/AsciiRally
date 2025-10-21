import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class ClientPlayer {

    public static String IP;
    private static int id;
    private ServerSocket ss;
    private Socket s;
    private PrintWriter out;
    private Scanner in;

    public ClientPlayer(String ip) throws Exception{
        this.IP = ip;
        this.s = new Socket(this.IP, ServerMap.PORT);
        System.out.println("Connection successfull");
        this.in = new Scanner(s.getInputStream());
        this.id = in.nextInt();
    }

    public String ask() throws Exception{
        try{
            String ans = "";

            this.ss = new ServerSocket(ServerMap.PORT);
            this.s = ss.accept();
            this.in = new Scanner(s.getInputStream());
            while(in.hasNext()){
                ans += in.nextLine();
            }
            ss.close();
            s.close();

            return ans;
        } catch (BindException e){
            Map.wait(100);
            return this.ask();
        }
    }
    public static void main(String[] args) throws Exception{
        
        ClientPlayer cp = new ClientPlayer("10.236.56.87");
        
        System.out.println(cp.ask());
        
    }
}
