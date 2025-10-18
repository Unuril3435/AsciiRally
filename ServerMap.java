import java.io.PrintWriter;
import java.net.*;
import java .util.Scanner;

public class ServerMap {
    private Map map;
    private ServerSocket ss;
    private Socket cs;
    private PrintWriter out;
    private Scanner in;
    private String clientPlayer;

    public int getLastPlayer(){return this.map.getLastPlayer();}
    public String getClientPlayer(){return this.clientPlayer;}

    public ServerMap(){
        try{
            this.ss = new ServerSocket(3435);
        } catch (Exception e){System.out.println("Couldn't create the server.");}
        //Generate a map with different things
        this.map = new Map(7, 10);
        for(int i = 0; i<5; i++){
            this.map.addHole(0, i+3);
        }
        for(int i = 0; i<7;i++){
            this.map.addHole(3, i+3);
        }
        this.map.addHole(4, 3);
        this.map.addHole(4, 4);
        this.map.addFlag(6, 9);
        this.map.addFlag(2, 7);
    }

    public void addPlayer(){
        int x = (int)(Math.random()*7);
        int y = (int)(Math.random()*2);
        if(this.map.isFree(x, y)){
            this.map.addPlayer(x, y);
        } else {
            this.addPlayer();
        }
    }

    public void accept() throws Exception{
        this.cs = this.ss.accept();
        this.clientPlayer = cs.getInetAddress().toString();
        this.out = new PrintWriter(cs.getOutputStream());
        this.in = new Scanner(cs.getInputStream());
    }
    //Sending protocol (everything divided by \n)
    // - player id
    // - map in a string
    public void send(String s){
        this.out.print(s);
        this.out.flush();
    }
    public void close() throws Exception{
        this.ss.close();
        this.cs.close();
    }

    public static void main(String[] args) throws Exception{
        Socket google = new Socket("8.8.8.8", 53);
        System.out.println("Server launched (ip = " + google.getLocalAddress().toString().substring(1) + ")");
        ServerMap sm = new ServerMap();
        sm.ss = new ServerSocket(1729);
        sm.addPlayer();
        System.out.println("You are Player " + sm.getLastPlayer());
        System.out.println("Waiting for Players...");
        sm.accept();
        sm.addPlayer();
        sm.send(String.valueOf(sm.getLastPlayer()));

        sm.close();
    }
}
