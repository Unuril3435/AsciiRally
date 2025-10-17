public class Player {

    public static final String[] INITIALHAND = {"move1", "move1", "move1", "move1", "move1",
                                                "move2", "move2", "move2", "move3", "recede",
                                                "turnRight", "turnRight", "turnRight", "turnLeft", "turnLeft", "turnLeft",
                                                "fullTurn", "repeat", "repeat"};
    private int heigth;
    private int width;
    private int pointing; //0 => Right
                          //1 => Down
                          //2 => Left
                          //3 => Up
    private String[] deck;
    private String[] hand;
    private String[] discard;

    public int getHeigth(){return this.heigth;}
    public void setHeigth(int h){this.heigth = h;}
    public int getWidth(){return this.width;}
    public void setWidth(int w){this.width = w;}
    public int getPointing(){return this.pointing;}
    public void setPointing(int n){this.pointing = n;}
    public String[] getHand(){return this.hand;}
    

    public Player(int h, int w){
        this.heigth = h;
        this.width = w;
        this.pointing = 0;
        this.deck = INITIALHAND;
    }

    public void drawCard(){
        if(this.deck.length==0){
            this.shuffle();
        }
        String[] newHand = new String[this.hand.length+1];
        for(int i = 0; i<this.hand.length;i++){
            newHand[i] = this.hand[i];
        }
        int newCard = (int)(Math.random()*this.deck.length);
        newHand[this.hand.length] = this.deck[newCard];

        String[] newDeck = new String[this.deck.length-1];
        for(int i = 0, skip = 0; i<newDeck.length; i++){
            if(i == newCard){skip++;}
            newDeck[i]=this.deck[i+skip];
        }
        this.hand = newHand;
        this.deck = newDeck;
    }
    public void shuffle(){
        this.discard = this.deck;
    }
    public void discard(){
        String[] newDiscard = new String[this.discard.length+this.hand.length];
        for(int i = 0; i<this.discard.length; i++){
            newDiscard[i] = this.discard[i];
        }
        for(int i = 0; i<this.hand.length; i++){
            newDiscard[i+this.discard.length] = this.hand[i];
        }

        this.discard = newDiscard;
        this.hand = new String[0];
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
