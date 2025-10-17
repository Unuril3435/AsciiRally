import java.util.concurrent.TimeUnit;

//There is a bug, if 2 or more players have to respawn they all end up at the spawnpoint without pushing each other
public class Map {
    public static final String[] pointString = {">", "V", "<", "^"};

    private final boolean PRINT;
    private final int[] SPAWN = {3,2};
    private final String[] COLORS = {"\u001B[31m", "\u001B[34m", "\u001B[33m", "\u001B[35m", "\u001B[36m", "\u001B[37m", "\u001B[0m"};
    private final int heigth;
    private final int width;
    private int[][] holes;
    private int[][] flags;
    private Player[] players;

    public Map(int h, int w){
        this.heigth = h;
        this.width = w;
        this.holes = new int[0][2];
        this.flags = new int[0][2];
        this.players =  new Player[0];
        this.PRINT = true;
    }

    public Map(int h, int w, boolean p){
        this.heigth = h;
        this.width = w;
        this.holes = new int[0][2];
        this.flags = new int[0][2];
        this.players =  new Player[0];
        this.PRINT = p;
    }

    public int getHeigth(){return this.heigth;}
    public int getWidth(){return this.width;}

    //Adds a hole at the given coordinates
    public void addHole(int x, int y) throws ArrayIndexOutOfBoundsException{
        if(isFree(x, y)){
            int[][] ans = new int[this.holes.length+1][2];
            for(int i = 0; i<this.holes.length; i++){
                ans[i][0] = this.holes[i][0];
                ans[i][1] = this.holes[i][1];
            }
            ans[this.holes.length][0] = x;
            ans[this.holes.length][1] = y;
            this.holes = ans;
        }
    }
    //Return true if the coordinates are a hole
    public boolean isHole(int x, int y){
        for(int i = 0; i<this.holes.length; i++){
            if(this.holes[i][0]==x&&this.holes[i][1]==y ||
              ( x<0 || x>=this.heigth ||
               y<0 || y>=this.width) ){
                return true;
            }
        }
        return false;
    }

    //Adds a flag at the given coordinates
    public void addFlag(int x, int y) throws ArrayIndexOutOfBoundsException{
        if(isFree(x, y)){
            int[][] ans = new int[this.flags.length+1][2];
            for(int i = 0; i<this.flags.length; i++){
                ans[i][0] = this.flags[i][0];
                ans[i][1] = this.flags[i][1];
            }
            ans[this.flags.length][0] = x;
            ans[this.flags.length][1] = y;
            this.flags = ans;
        }
    }
    //Checks if said coordinates are a flag and returns witch flag it is (-1 if there is no flag)
    public int isFlag(int x, int y){
        for(int i = 0; i<this.flags.length; i++){
            if(this.flags[i][0]==x&&this.flags[i][1]==y){
                return i;
            }
        }
        return -1;
    }

    //Checks if the coordinates are not a hole, flag or the spawnpoint
    public boolean isFree(int x, int y){
        return!(isHole(x, y)||isFlag(x, y)>=0||(x==SPAWN[0]&&y==SPAWN[1]));
    }

    //Creates a player with an id at the spawnpoint
    public void addPlayer() throws ArrayIndexOutOfBoundsException{
        Player[] ans = new Player[this.players.length+1];
        for(int i = 0; i<this.players.length; i++){
            ans[i] = this.players[i];
        }
        if(isPlayer(SPAWN[0], SPAWN[1])>=0){
            this.movePlayer(isPlayer(SPAWN[0], SPAWN[1]),1);
        }
        ans[this.players.length] = new Player(SPAWN[0],SPAWN[1]);
        this.players = ans;
    }
    //Creates a player with an id at the given coordinates
    public void addPlayer(int x, int y) throws ArrayIndexOutOfBoundsException{
        Player[] ans = new Player[this.players.length+1];
        for(int i = 0; i<this.players.length; i++){
            ans[i] = this.players[i];
        }
        ans[this.players.length] = new Player(x, y);
        this.players = ans;
    }
    //Returns whether there is a player in given coordinates
    public int isPlayer(int x, int y){
        for(int i = 0; i<this.players.length; i++){
            if(isPlayer(i, x, y)){
                return i;
            }
        }
        return -1;
    }
    //Returns if player n is in that position
    public boolean isPlayer(int id, int x, int y){
        return this.players[id].getHeigth()==x&&this.players[id].getWidth()==y;
    }

    //Generates the map for printing
    //  - green # is the spawnpoint
    //  - green nubers are the flags
    //  - black spaces are holes
    //  - the different arrows are the players and the direction they are pointing
    //  - the white # is normal terrain
    @Override
    public String toString(){
        String ans = "";

        for(int i = 0; i<this.heigth; i++){
            for(int j = 0; j<this.width;j++){
                if(isHole(i, j)){
                    ans += " ";
                } else{
                    if(isPlayer(i, j)>=0){
                        if(isFlag(i, j)>=0||(i==this.SPAWN[0]&&this.SPAWN[1]==j)){
                            ans+="\u001B[42m";
                        }
                        ans += COLORS[this.isPlayer(i, j)%(COLORS.length-1)];
                        
                        ans += pointString[this.players[isPlayer(i, j)].getPointing()];
                        ans += COLORS[6];
                    } else if (isFlag(i, j)>=0){
                        ans += "\u001B[32m" + (isFlag(i, j)+1) + "\u001B[0m";
                    } else if(i==this.SPAWN[0]&&this.SPAWN[1]==j){
                        ans+="\u001B[32m#\u001B[0m";
                    }else {
                        ans += "#";
                    }
                }
            }
            ans += "\n";
        }

        return ans;
    }

    //this two methods move the player n position toward the direction it is pointing (if print == true then it prints else, it doesn't) print is true by default
    public void movePlayer(int id, int n){
        this.movePlayer(id, n, this.PRINT);
    }
    public void movePlayer(int id, int n, boolean print){
        for(int i = 0; i<n;i++){
            //generate a phantom player to check if there is a player at the front
            int tempX = this.players[id].getHeigth();
            int tempY = this.players[id].getWidth();
            int tempP = this.players[id].getPointing();
            switch (tempP) {
                case 0:
                    tempY+=1;
                    break;
                case 1:
                    tempX+=1;
                    break;
                case 2:
                    tempY-=1;
                    break;
                case 3:
                    tempX-=1;
                    break;
            }
            //push the player if there is one
            int pushPlayer = isPlayer(tempX, tempY);
            if(pushPlayer>=0){
                int pushedP = this.players[pushPlayer].getPointing();
                this.players[pushPlayer].setPointing(tempP);
                this.movePlayer(pushPlayer, 1,false);
                this.players[pushPlayer].setPointing(pushedP);
            }

            this.players[id].move();
            //If it falls
            if(isHole(this.players[id].getHeigth(),this.players[id].getWidth())){
                int inSpawnPlayer = isPlayer(SPAWN[0], SPAWN[1]);
                this.players[id].setHeigth(SPAWN[0]);
                this.players[id].setWidth(SPAWN[1]);
                this.players[id].setPointing(0);
                if(inSpawnPlayer>=0){
                    this.movePlayer(inSpawnPlayer, 1);
                }
                break;
            }
        if(print){this.print();}
        }
    }
    //Move the player backwards (full turn, move one, full turn)
    public void movePlayerBack(int id){
        this.movePlayerBack(id, this.PRINT);
    }
    public void movePlayerBack(int id, boolean print){
        int prevX = this.players[id].getHeigth();
        int prevY = this.players[id].getWidth();
        this.turnLeftPlayer(id,false);
        this.turnLeftPlayer(id,false);
        this.movePlayer(id,1,false);
        this.turnLeftPlayer(id,false);
        this.turnLeftPlayer(id,false);
        int currX = this.players[id].getHeigth();
        int currY = this.players[id].getWidth();
        if(prevX==currX && prevY==currY && currX==SPAWN[0] && currY==SPAWN[1]){
            this.players[id].setPointing(0);
        }
        if(print){this.print();}
    }

    //Turn the player to the Right and Left or make a full turn (if print == true it prints else, it doesn't) print is true by default
    public void turnRightPlayer(int id){
        this.turnRightPlayer(id,this.PRINT);
    }
    public void turnRightPlayer(int id, boolean  print){
        this.players[id].turnRight();
        if(print){this.print();}
    }
    public void turnLeftPlayer(int id){
        this.turnLeftPlayer(id,this.PRINT);
    }
    public void turnLeftPlayer(int id, boolean  print){
        this.players[id].turnLeft();
        if(print){this.print();}
    }
    public void fullTurnPlayer(int id){
        this.turnRightPlayer(id);
        this.turnRightPlayer(id);
    }

    //Various methods to create the illusion of movement
    //  - clear()  => clears screen
    //  - wait(ms) => wait x ms
    //  - print()  => clear screen, print the current map and wait 500 ms
    public static void clear(){
        System.out.print("\033[H\033[2J");
    }
    public static void wait(int ms){
        try{
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (Exception e){}
    }
    public void print(){
	    clear();
        System.out.println(this.toString());
        wait(300);
    }

    public static void main(String[] args){
        Map map = new Map(5,8, true);
        map.addPlayer(2,0);
        map.addPlayer(1,0);
        map.addPlayer();
        map.addHole(1, 2);
        map.addHole(1, 3);
        map.addFlag(4,5);
        map.addFlag(3,4);
        map.print();
        map.movePlayer(2, 2);
        map.movePlayer(1, 3);
        map.fullTurnPlayer(0);
        map.movePlayerBack(0);
        map.fullTurnPlayer(0);
        map.movePlayer(2, 5);
    }


}
