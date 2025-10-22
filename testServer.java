
import java.io.*;
import java.net.*;

public class testServer{
    public static final int INITIAL_PORT = 1729;
    private ServerSocket[] ss;
    private Socket s;
    private int currentPort;

    public testServer() throws Exception{
        this.ss = new ServerSocket[1];
        this.ss[0] = new ServerSocket(INITIAL_PORT);
        this.currentPort = INITIAL_PORT;
    }
    public void addSS(){addSS(1729);}
    public void addSS(int port){
        ServerSocket[] ans = new ServerSocket[this.ss.length+1];
        for(int i = 0; i<this.ss.length; i++){
            ans[i]=this.ss[i];
        }
        try{
            ans[this.ss.length]= new ServerSocket(port);
            System.out.println("Socket created in port " + port);
            this.ss = ans;
            this.s = this.ss[0].accept();
            PrintWriter out = new PrintWriter(this.s.getOutputStream());
            out.print("Hello World");
            out.close();
        }catch (IOException e){
            this.currentPort++;
            addSS(currentPort);
        }
    }
    public static void main(String[] args) throws Exception{
        testServer t = new testServer();
        t.addSS();

    }

    
}