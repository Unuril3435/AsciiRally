import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class testClient{

    private Socket s;
    private Scanner in;
    private PrintWriter out;

    public testClient() throws Exception{
        this.s = new Socket("localhost", testServer.PORT);
        this.in = new Scanner(this.s.getInputStream());
        this.out = new PrintWriter(this.s.getOutputStream());
    }

    public String recieve(){
        return this.in.nextLine();
    }

    public void send(String s){
        this.out.println(s);
        this.out.flush();
    }
    public static void main(String[] args) throws Exception{
        testClient test = new testClient();
        System.out.println("Connection created");
        
        System.out.println(test.recieve());
        test.send("Hello World");
        System.out.println(test.recieve());
    }
}
