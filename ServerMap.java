import java.io.PrintWriter;
import java.net.*;
import java .util.Scanner;

public class ServerMap {
    public static final int PORT = 1729;

    private Map map;
    private ServerSocket ss;
    private Socket cs;
    private PrintWriter out;
    private Scanner in;

    public void drawHand(){
        for(int i = 0; i<this.map.getPlayers()+1; i++){
            this.map.drawHand(i);
        }
    }
    
    public ServerMap(){
        try{
            this.ss = new ServerSocket(PORT);
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
    public String print(int id){
        String ans = Map.COLORS[id%(Map.COLORS.length-1)] + "PLAYER " + id + Map.COLORS[Map.COLORS.length-1];
        ans += ":\n" + this.map.toString();
        ans += "FLAGS: ";
        if(this.map.getFlag(id)==0){
            ans += "none";
        } else {
            for(int i = 1; i<this.map.getFlag(id);i++){
                ans += "-->\u001B[32m " + i + "\u001B[0m ";
            }
        }
        
        ans += "\nHAND: ";
        String[] hand = this.map.getHand(id);
        for(int i = 0; i<hand.length; i++){ans += hand[i] + " ";}
        ans +="\n";
        return ans;
    }

    public void searchPlayer() throws Exception{
        System.out.println("Waiting for player " + (this.map.getPlayers()+1) +"...");
        
        this.cs = ss.accept();
        this.addPlayer();
        this.out = new PrintWriter(this.cs.getOutputStream());
        this.out.print(this.map.getPlayers());
        this.out.flush();
        this.cs.close();
        
    }
    //Protocol:
    // - Accept(client connects)
    // - Client sends it's id
    // - Server checks if client's id == id
    // - if (client's id == id){send s}
    //   else {repeat}
    public void send(String s, int id) throws Exception{
        this.cs = ss.accept();
        this.in = new Scanner(this.cs.getInputStream());
        this.out = new PrintWriter(this.cs.getOutputStream());
        System.out.println("Hello World");
        String playerId = this.in.nextLine();
        System.out.println(playerId);
        
        this.out.print(s);
        this.out.flush();

        /*if(playerId == id){
            
        } else {
            this.send(s,id);
        }*/

        this.close();

    }
    public void close() throws Exception{
        //this.ss.close();
        this.cs.close();
    }

    public static void main(String[] args) throws Exception{
        Socket google = new Socket("8.8.8.8", 53);
        System.out.println("Server launched (ip = " + google.getLocalAddress().toString().substring(1) + ")");
        ServerMap sm = new ServerMap();
        Scanner kbd = new Scanner(System.in);
        sm.addPlayer();
        System.out.println("You are " + Map.COLORS[0] + "player 0" + Map.COLORS[Map.COLORS.length-1]);
        System.out.print("How many players do you want to host?: ");
        for(int n = kbd.nextInt();n>0;n--){
            sm.searchPlayer();
        }
        sm.drawHand();
        System.out.println(sm.print(0));

        sm.send(sm.print(1), 1);
        /*for(int i = 1; i<sm.map.getPlayers()+1; i++){
            sm.out.print(sm.print(i));
            sm.out.flush();
            sm.close();
        }*/
    }
}
