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
    public String ask() throws Exception{return this.ask(true);}
    public String ask(boolean print) throws Exception{
        try{
            String ans = "";

            this.ss = new ServerSocket(ServerMap.PORT);
            this.s = ss.accept();
            this.in = new Scanner(s.getInputStream());
            while(in.hasNext()){
                ans += in.nextLine() + "\n";
            }   
            ss.close();
            s.close();

            return ans;
        } catch (BindException e){
            Map.wait(100);
            if(print){System.out.println("Waiting for other players...");}
            return this.ask(false);
        }
    }

    public void send(String s) throws Exception{

        this.s = new Socket(this.IP, ServerMap.PORT);
        this.out = new PrintWriter(this.s.getOutputStream());
        this.out.print(s);
        this.out.flush();
        this.s.close();
    }
    public static void main(String[] args) throws Exception{
        
        ClientPlayer cp = new ClientPlayer("10.236.56.87");
        
        System.out.println(cp.ask());
        
    }
}
