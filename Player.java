public class Player {

    private int heigth;
    private int width;
    private int pointing; //0 => Right
                          //1 => Down
                          //2 => Left
                          //3 => Up

    public int getHeigth(){return this.heigth;}
    public void setHeigth(int h){this.heigth = h;}
    public int getWidth(){return this.width;}
    public void setWidth(int w){this.width = w;}
    public int getPointing(){return this.pointing;}
    public void setPointing(int n){this.pointing = n;}
    

    public Player(int h, int w){
        this.heigth = h;
        this.width = w;
        this.pointing = 0;
    }

    public void move(){
            switch (this.pointing) {
                case 0:
                    this.width+=1;
                    break;
                case 1:
                    this.heigth+=1;
                    break;
                case 2:
                    this.width-=1;
                    break;
                case 3:
                    this.heigth-=1;
                    break;
            }
    }
    public void turnRight(){
        this.pointing=(this.pointing+1)%4;
    }
    public void turnLeft(){
        this.pointing=((this.pointing+3)%4);
    }
    
}
