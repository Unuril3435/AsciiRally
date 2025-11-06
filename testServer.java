
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class testServer{
    public static final int PORT = 1729;
    private ServerSocket ss;
    private Socket[] s;
    private Scanner[] in;
    private PrintWriter[] out; 

    public testServer() throws Exception{
        this.ss = new ServerSocket(PORT);
        this.s = new Socket[1];
        this.in = new Scanner[1];
        this.out = new PrintWriter[1];
    }

    public static <T> T[] add(T[] array, T item) {
        T[] arr = Arrays.copyOf(array, array.length + 1);
        arr[arr.length-1] = item;
        return arr;
    }

    public void accept() throws Exception{
        this.s = add(this.s, this.ss.accept());
        this.out = add(this.out, new PrintWriter(this.s[this.s.length-1].getOutputStream()));
        this.in = add(this.in, new Scanner(this.s[this.s.length-1].getInputStream()));
    }
    public void send(String s, int id) throws Exception{
        this.out[id].println(s);
        this.out[id].flush();
    }
    public String recieve(int id){
        return this.in[id].nextLine();
    }
    
    public static void main(String[] args) throws Exception{
        testServer test = new testServer();

        test.accept();
        /*test.accept();
        test.send("Hello",1);*/
        test.send("bye",1);

        System.out.println(test.recieve(1));
        test.send("Something",1);
    }

    
}