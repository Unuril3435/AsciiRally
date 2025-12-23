import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
	public static final int PORT = 1729;
	public static void main(String[] args) throws Exception{
		ServerMap sm = new ServerMap();

		Scanner kbd = new Scanner(System.in);
		System.out.print("Number of players in the game: ");
		
		Thread[] t = new Thread[2/*kbd.nextInt()*/];
		for(int i = 0; i<t.length; i++){
			sm.addPlayer();
			t[i] = new Thread(() -> {sm.talk();});
			t[i].start();
		}
		while(true){	
			sm.deletePrograms();
			sm.drawHand();
			sm.sendMapToAll();
			while(!sm.getExec()){Map.wait(100);}
			System.out.println("Next Phase");
			for(int i = 0; i<t.length; i++){
				t[i].interrupt();
			}

			sm.execute();
			
			for(int i = 0; i<t.length; i++){
				try{
					t[i].start();
				}catch(Exception e){}
			}
		}

		//for(int i = 0;i<t.length;i++){t[i].join();}

	}
}

class ServerMap{
	
	private static final int[] MAPSIZE = {5,8};
	private Map map = new Map(MAPSIZE[0], MAPSIZE[1], false);
	
	private Socket[] s = new Socket[0];
	private PrintWriter[] out = new PrintWriter[0];
	private Scanner[] in = new Scanner[0];
	private ServerSocket ss;

	private String[][] program;
	private boolean toExec = false;

	public boolean getExec(){return this.toExec;}

	
	public ServerMap(){
		try{
			ss = new ServerSocket(Server.PORT);
		}catch(Exception e){}
		createMap();
	}
	public ServerMap(int port){
		try{
			ss = new ServerSocket(port);
		}catch(Exception e){}
		createMap();
	}

	public void createMap(){
		this.map.addHole(3,3);
		this.map.addHole(2,3);
		this.map.addHole(2,4);
		this.map.addHole(2,5);
		this.map.addHole(2,6);
		this.map.addHole(2,7);
		this.map.addFlag(4,7);
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
		this.send(this.s.length-1, "Waiting for other players...");
	}

	public void talk(){
		int id = this.s.length-1;

		while(!Thread.currentThread().isInterrupted()){
			String ans = "";
			if(!Thread.currentThread().isInterrupted()){
				ans = this.in[id].nextLine();
			}
			
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
	public void sendMap(int id){this.sendMap(id, true);}
	public void sendMap(int id, boolean printHand){
		this.out[id].print("\033[H\033[2J");
		this.out[id].print(Map.COLORS[id]);
		this.out[id].print("Player " + id + ":\n");
		this.out[id].print(Map.COLORS[Map.COLORS.length-1]);
		this.out[id].print(this.map);
		if(printHand){
			this.out[id].print("\nHand:\n");
			String[] hand = this.map.getHand(id);
			for(int i = 0; i<hand.length; i++){this.out[id].print(hand[i] + " ");}
		}
		this.out[id].print("\n");
		this.out[id].flush();
	}
	
	public void sendMapToAll(){this.sendMapToAll(true);}
	public void sendMapToAll(boolean printHand){
		for(int i = 0; i<this.out.length; i++){
			this.sendMap(i, printHand);
		}
	}

	public void execute(){
		int waitTime = 400;
		for(int i = 0; i<this.program[0].length; i++){
			for(int p = 0; p<this.program.length; p++){
				switch(this.program[p][i]){
					case "move3":
						this.map.movePlayer(p, 1);
						Map.wait(waitTime);
						this.sendMapToAll(false);
					case "move2":
						this.map.movePlayer(p, 1);
						Map.wait(waitTime);
						this.sendMapToAll(false);
					case "move1":
						this.map.movePlayer(p, 1);
						Map.wait(waitTime);
						this.sendMapToAll(false);
						break;
					case "recede":
						this.map.movePlayerBack(p);
						Map.wait(waitTime);
						this.sendMapToAll(false);
						break;
					case "turnRight":
						this.map.turnRightPlayer(p);
						Map.wait(waitTime);
						this.sendMapToAll(false);
						break;
					case "turnLeft":
						this.map.turnLeftPlayer(p);
						Map.wait(waitTime);
						this.sendMapToAll(false);
						break;
					case "fullTurn":
						this.map.fullTurnPlayer(p);
						Map.wait(waitTime);
						this.sendMapToAll(false);
						break;
					case "spam":
						this.map.spam(p);
						Map.wait(waitTime);
						this.sendMapToAll(false);
						break;
				}
			}
		}
	}

	
}
