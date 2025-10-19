import java.io.PrintWriter;
import java.net.*;
import java .util.Scanner;

public class ServerMap {
    private Map map;
    private ServerSocket ss;
    private Socket[] cs;
    private PrintWriter[] out;
    private Scanner[] in;

    public int getLastPlayer(){return this.map.getLastPlayer();}
    
    public ServerMap(){
        this.cs = new Socket[1];
        this.out = new PrintWriter[1];
        this.in = new Scanner[1];
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

    public void searchPlayer() throws Exception{
        System.out.println("Waiting for player " + (this.cs.length) +"...");
        Socket temp = ss.accept();
        Socket[] newClient = new Socket[this.cs.length+1];
        for(int i = 0; i<this.cs.length;i++){
            newClient[i] = this.cs[i];
        }
        int newID = this.cs.length;
        newClient[newID] = temp;
        this.cs = newClient;
        this.addPlayer();
        
        PrintWriter[] newOut = new PrintWriter[this.out.length+1];
        for(int i = 0; i<this.out.length;i++){
            newOut[i] = this.out[i];
        }
        newOut[newID] = new PrintWriter(this.cs[newID].getOutputStream());
        this.out = newOut;
        Scanner[] newIn = new Scanner[this.in.length+1];
        for(int i = 0; i<this.in.length;i++){
            newIn[i] = this.in[i];
        }
        newIn[newID] = new Scanner(this.cs[newID].getInputStream());
        this.in = newIn;

        this.send(String.valueOf(newID),newID);


    }
    public void send(String s, int id){
        this.out[id].print(s);
        this.out[id].flush();
    }
    public void close() throws Exception{
        this.ss.close();
        for(int i = 1; i<this.cs.length;i++){
            this.cs[i].close();
        }
    }

    public static void main(String[] args) throws Exception{
        Socket google = new Socket("8.8.8.8", 53);
        System.out.println("Server launched (ip = " + google.getLocalAddress().toString().substring(1) + ")");
        ServerMap sm = new ServerMap();
        Scanner kbd = new Scanner(System.in);
        sm.ss = new ServerSocket(1729);
        sm.addPlayer();
        System.out.println("You are " + Map.COLORS[0] + "player 0" + Map.COLORS[Map.COLORS.length-1]);
        System.out.println("How many players do you want to host?");
        for(int n = kbd.nextInt();n>0;n--){
            sm.searchPlayer();
        }

        sm.close();
    }
}
