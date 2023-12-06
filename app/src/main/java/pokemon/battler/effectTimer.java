package pokemon.battler;

public class effectTimer {
    public static int turnNum = 1;
    public Status effect;
    public Move move;
    public int onTurn;
    public effectTimer(Status effect,int onTurn) {
        this.effect=effect;
        this.onTurn=onTurn;
    }
    public effectTimer(Status effect,int onTurn, Move move) {
        this.effect=effect;
        this.onTurn=onTurn;
        this.move = move;
    }
    public boolean endEffect() {if (turnNum==onTurn) {return true;} return false;}
}
