import java.io.PrintWriter;
import java.net.*;
import java .util.Scanner;

public class ServerMap {
    private Map map;
    private ServerSocket ss;
    private Socket s;
    private PrintWriter in;
    private Scanner out;

    public ServerMap(){
        try{
            this.ss = new ServerSocket(3435);
        } catch (Exception e){System.out.println("Couldn't create the server.");}
        this.map = new Map(7, 10);
        this.map.print();
        this.map.addHole(0, 0);
        this.map.print();
    }

    public static void main(String[] args){
        ServerMap sm = new ServerMap();


    }
}
