package pokemon.battler;
import java.util.ArrayList;

public class Pokemon {
    private String ASCII_FILEPATH;
    public String name;
    public String typeA,typeB;
    public int MAX_HP;
    public double hp;
    public int atk,def,sp_atk,sp_def,spd;
    private Move[] moves;
    public ArrayList<Status> effect = new ArrayList<Status>();
    public boolean skipTurn;
    public int[] stageStats = new int[5];
    public Pokemon (String name,String ASCII_FILEPATH,int[] stats,String typeA, String typeB, Move[] moves) {
        this.name = name;
        this.typeA = typeA;
        this.typeB = typeB;
        this.MAX_HP = stats[0];
        this.hp = (double) stats[0];
        this.atk = stats[1];
        this.def = stats[2];
        this.sp_atk = stats[3];
        this.sp_def = stats[4];
        this.spd = stats[5];
        this.moves = moves;
        this.ASCII_FILEPATH = ASCII_FILEPATH;
        //TODO this is debug, remove this
        // effect.add(Status.CONFUSED);
    }
    public Move getMove(int num) {return moves[num];}
    public String getASCIIPath() {return ASCII_FILEPATH;}
    public void activateStatuses() {}
}
