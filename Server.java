import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
	public static final int PORT = 1729;
	public static void main(String[] args) throws Exception{
		ServerMap sm = new ServerMap();
		
		Thread[] t = new Thread[2];
		for(int i = 0; i<t.length; i++){
			sm.addPlayer();
			t[i] = new Thread(() -> {sm.talk();});
			t[i].start();
		}
		
		sm.deletePrograms();
		sm.drawHand();
		sm.sendMapToAll();


		for(int i = 0;i<t.length;i++){t[i].join();}

	}
}

class ServerMap{
	
	private static final int[] MAPSIZE = {5,8};
	private Map map = new Map(MAPSIZE[0], MAPSIZE[1]);
	
	private Socket[] s = new Socket[0];
	private PrintWriter[] out = new PrintWriter[0];
	private Scanner[] in = new Scanner[0];
	private ServerSocket ss;

	private String[][] program;
	private boolean toExec = false;

	
	public ServerMap(){
		try{
			ss = new ServerSocket(Server.PORT);
		}catch(Exception e){}
	}
	public ServerMap(int port){
		try{
			ss = new ServerSocket(port);
		}catch(Exception e){}
	}
	
	
	//Add a player at a random position
	public synchronized void addPlayer() throws Exception{
		Socket[] tempS = new Socket[this.s.length+1];
		for(int i = 0; i<this.s.length; i++){
			tempS[i] = this.s[i];
		}
		tempS[this.s.length]=this.ss.accept();

		Scanner[] tempIn = new Scanner[this.in.length+1];
		for(int i = 0; i<this.in.length; i++){
			tempIn[i] = this.in[i];
		}
		tempIn[this.in.length] = new Scanner(tempS[this.s.length].getInputStream());
		
		PrintWriter[] tempOut = new PrintWriter[this.out.length+1];
		for(int i = 0; i<this.out.length; i++){
			tempOut[i] = this.out[i];
		}
		tempOut[this.out.length] = new PrintWriter(tempS[this.s.length].getOutputStream());

		this.s = tempS;
		this.in = tempIn;
		this.out = tempOut;
		this.program = new String[this.s.length][5];
		
		int x;
		int y;
		do{
			y = (int) (Math.random()*MAPSIZE[1]/3);
			x = (int) (Math.random()*MAPSIZE[0]);
		}while(!this.map.isFree(x,y));
		this.map.addPlayer(x,y);
		System.out.println("Player " + (this.s.length-1) + " added");
		System.out.println("At: " + y + ", " + x);
	}

	public void talk(){
		int id = this.s.length-1;

		while(true){
			String ans = this.in[id].nextLine();
			
			String[] arr = ans.split(" ");
			if(this.addProgram(id, arr)){
				this.send(id, "Program accepted");
			}else{
				this.send(id, "Program not accepted");
			}

		}
	}
	private synchronized boolean addProgram(int id, String[] program){
		if(!this.map.isProgram(id, program)){
			return false;
		}
		this.program[id] = program;
		boolean b = true;
		for(int i = 0; i<this.s.length; i++){
			b = b && !this.program[i][0].equals("");
			System.out.println(b);
		}
		this.toExec = b;
		return true;
	}
	
	public void deletePrograms(){
		for(int i = 0; i<this.s.length; i++){
			this.program[i]=new String[5];
			for(int j = 0; j<5; j++){this.program[i][j] = "";}
		}
		this.toExec = false;
	}
		
	
	//Every players draws a hand
	public void drawHand(){
		for(int i = 0; i<this.s.length; i++){
			this.map.drawHand(i);
		}
	}

	public void send(int id, String str){
		this.out[id].print(str);
		this.out[id].print("\n");
		this.out[id].flush();
	}
	
	//Send current map to all with player id's hand
	public void sendMap(int id){
		this.out[id].print("\033[H\033[2J");
		this.out[id].print(Map.COLORS[id]);
		this.out[id].print("Player " + id + ":\n");
		this.out[id].print(Map.COLORS[Map.COLORS.length-1]);
		this.out[id].print(this.map);
		this.out[id].print("\nHand:\n");
		String[] hand = this.map.getHand(id);
		for(int i = 0; i<hand.length; i++){this.out[id].print(hand[i] + " ");}
		this.out[id].print("\n");
		this.out[id].flush();
	}

	public void sendMapToAll(){
		for(int i = 0; i<this.out.length; i++){
			this.sendMap(i);
		}
	}

	
}
