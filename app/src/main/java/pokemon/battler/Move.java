package pokemon.battler;



public class Move {
    public String moveName;
    public String moveElement;
    public int dmg;
    public int acc;
    public String moveType;
    public Status[] effects = new Status[] {};
    public double[] effectPer;
    public moveTarget target;
    public String[] description;
    public boolean disabled = false;

    public void setDescrpt(String[] descrpt) {this.description = descrpt;}

    public Move(String name, String element, int dmg, int acc, moveTarget target, Status[] effects) {
        this.moveName = name;
        this.effects = effects;
        this.moveElement = element;
        this.acc = acc;
        this.target = target;
        if (dmg == 0) {
            moveType = "STA";
        } else {
            moveType = "PHY";
        }
    }
    public Move(String name, String element, int dmg, int acc, moveTarget target, Status[] effects, boolean isSpecial) {
        this.moveName = name;
        this.effects = effects;
        this.moveElement = element;
        this.dmg = dmg;
        this.acc = acc;
        this.target = target;
        moveType = "SPE";
    }
    
    public Move(String name, String element, int dmg, int acc, moveTarget target, Status[] effects, double[] effectPer) {
        this.moveName = name;
        this.effects = effects;
        this.effectPer = effectPer;
        this.moveElement = element;
        this.dmg = dmg;
        this.acc = acc;
        this.target = target;
        if (dmg == 0) {
            moveType = "STA";
        } else {
            moveType = "PHY";
        }
    }
    public Move(String name, String element, int dmg, int acc, moveTarget target, Status[] effects, double[] effectPer, boolean isSpecial) {
        this.moveName = name;
        this.effects = effects;
        this.effectPer = effectPer;
        this.moveElement = element;
        this.dmg = dmg;
        this.acc = acc;
        this.target = target;
        moveType = "SPE";
    }

    public Move(String name, String element, int dmg, int acc, moveTarget target) {
        this.moveName = name;
        this.moveElement = element;
        this.dmg = dmg;
        this.acc = acc;
        this.target = target;
        if (dmg == 0) {
            moveType = "STA";
        } else {
            moveType = "PHY";
        }
    }
    public Move(String name, String element, int dmg, int acc, moveTarget target, boolean isSpecial) {
        this.moveName = name;
        this.moveElement = element;
        this.dmg = dmg;
        this.acc = acc;
        this.target = target;
        moveType = "SPE";
    }
}

