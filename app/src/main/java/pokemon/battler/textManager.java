package pokemon.battler;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class textManager {
    public static Scanner wait = new Scanner(System.in);

    private static int TEXT_LENGTH=0;
    private static String[] file;
    private static final int HEALTH_TICKS = 25;
    private static final int[] enemyHealthLines = {6,7,8};
    private static final int[] selfHealthLines = {22,23,24};
    private static final int selfHealthStart=67;
    private static final int enemyHealthStart=22;

    private static final int midColumnEn = 26;
    private static final int[] linesEn = {0,1,2,3,4};
    private static final int midColumnFr = 68;
    private static final int[] linesFr = {16,17,18,19,20};
    private static final int ASCII_START_COLUMN = 37;

    private static final int terminalStart = 28;
    private static final int terminalLength = 11;
    private static final int statLine = 2;
    private static final int[] effectLines = {6,7,8};
    private static final int effectLineLength = 16;
    private static final int effectsStart = 5;
    private static final int[] moveLines = {3,7};
    private static final int[] moveCol = {31,64};

    private static final int[] pokemonLines = {5,12,19,26};
    private static final int switchMiddleColumn = 60;
    private static final int pokemonListColumn = 3;

    public static final String ANSI_BLACK = "\u001B[90m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static CurrentScreen screen = CurrentScreen.TITLE_SCREEN;
    
    public static void Title_Screen() {
        System.out.println("");

        try {
            Scanner scanner = new Scanner(new File("app/src/main/resources/Title_Screen.txt"));
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void initialize() {
        try {
            Scanner scanner = new Scanner(new File("app/src/main/resources/Battle_Screen.txt"));
            while (scanner.hasNextLine()) {
                TEXT_LENGTH++;
                scanner.nextLine();
            }
            file = new String[TEXT_LENGTH];
            scanner = new Scanner(new File("app/src/main/resources/Battle_Screen.txt"));
            for (int i = 0; i < TEXT_LENGTH; i++) {
                file[i] = scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static String setHealth(String color, int hp, int maxHP, boolean isSelf) {
        String healthBar = ANSI_BLACK;
        int healthTicksOff = (int) (HEALTH_TICKS*(maxHP-(double)hp)/maxHP);
        if (healthTicksOff>HEALTH_TICKS) {healthTicksOff = HEALTH_TICKS;}
        for (int i = 0; i < HEALTH_TICKS; i++) {healthBar += "█";}
        String middleHealthBar = healthBar.substring(0,ANSI_BLACK.length()+(HEALTH_TICKS/2)-2) 
            + (((hp>0&&hp<10)) ? "0" : "") + hp + "/" + maxHP + 
            healthBar.substring(ANSI_BLACK.length()+(HEALTH_TICKS/2)+3);
        int num = 0;
        int[] targetLines = new int[] {};
        if (isSelf) {num = selfHealthStart; targetLines = selfHealthLines;}
        else {num = enemyHealthStart; targetLines = enemyHealthLines;}
        for (int i : targetLines) {
            if (i != targetLines[1]) {
                file[i] = file[i].substring(0,num) + healthBar + file[i].substring(file[i].lastIndexOf("█"));
            } else {
                file[i] = file[i].substring(0,num) + middleHealthBar + file[i].substring(file[i].lastIndexOf("█"));
            }
            if (healthTicksOff==25) {healthTicksOff--;}
            file[i] = file[i].substring(0,num+ANSI_BLACK.length()+healthTicksOff)+((hp<0)?"":
                (color=="green")?ANSI_GREEN:(color=="yellow")?ANSI_YELLOW:(color=="red")?ANSI_RED:"ERROR")
                + file[i].substring(num+ANSI_BLACK.length()+healthTicksOff,file[i].lastIndexOf("█")) 
                + ANSI_WHITE + file[i].substring(file[i].lastIndexOf("█")+1);
        }
        return file[targetLines[1]].substring(num,file[targetLines[1]].lastIndexOf("█")+ANSI_WHITE.length()+1);
    }
    public static void cursorPos(int pos) {
        int[][] positions = new int[4][2];
        if (screen == CurrentScreen.MAIN_BATTLE) {positions = new int[][] {{31,36},{31,65},{36,36},{36,65}};}
        if (screen == CurrentScreen.MOVES) {positions = new int[][] {{31,27},{31,61},{35,27},{35,61},{28,27}};}
        for (int[] i : positions) {
            file[i[0]] = file[i[0]].substring(0,i[1]) + "  " + file[i[0]].substring(i[1]+2);
        }
        if (pos!=-1) {
            if (screen == CurrentScreen.MOVES) {try {
                getTerminal(battleManager.playerTeam[battleManager.currentPokemon],pos);
            } catch (FileNotFoundException e) {e.printStackTrace();}}
            file[positions[pos][0]] = file[positions[pos][0]].substring(0,positions[pos][1]) 
                + ">>" + file[positions[pos][0]].substring(positions[pos][1]+2);
        } else {
            pos = 4;
            file[positions[pos][0]] = file[positions[pos][0]].substring(0,positions[pos][1]) 
                + "<<" + file[positions[pos][0]].substring(positions[pos][1]+2);
        }
    }

    public static int cursorPosList() {
        int[] positions = new int[0];
        if (screen==CurrentScreen.POKEMON_SWITCH) {positions = new int[] {1,5,12,19,26};}
        int pos = 1;
        while (true) {
            for (int i : positions) {
                file[i] = file[i].substring(0,pokemonListColumn) + "  " + 
                    file[i].substring(pokemonListColumn+2);
            }
            String input = wait.next();
            if (input.equals("^")) {pos--;} else if (input.equals("v")) {pos++;}
            else {break;}
            if (pos==5) {pos=0;} else if (pos==-1) {pos=4;}
            String arrow = ">>";
            if (pos==0) {arrow = "<<";}
                file[positions[pos]] = file[positions[pos]].substring(0,pokemonListColumn) 
                    + arrow + file[positions[pos]].substring(pokemonListColumn+2);
            print();
        }
        return pos-1;
    }

    public static void changeASCII(String ASCII_FILEPATH, boolean isSelf) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(ASCII_FILEPATH));;
        String[] display = new String[5];
        for (int i = 0; i < 5; i++) {display[i] = scanner.nextLine();}
        if (isSelf) {
            for (int i : linesFr) {file[i] = file[i].substring(0,ASCII_START_COLUMN)+"                                       ";}
            int start = midColumnFr-(display[3].length()/2);
            for (int i = 0; i < linesFr.length; i++) {
                file[linesFr[i]] = file[linesFr[i]].substring(0,start) + display[i];
            }
        } else {
            for (int i : linesEn) {file[i] = "                                       ";}
            int start = midColumnEn-(display[3].length()/2);
            for (int i = 0; i < linesEn.length; i++) {
                file[linesEn[i]] = file[linesEn[i]].substring(0,start) + display[i];
            }
        }
    }
    public static void changeASCIICondition(String status,boolean isSelf) {
        if (isSelf) {
            file[linesFr[3]] = file[linesFr[3]].substring(0,file[linesFr[3]].length()-4) + "_____";
            file[linesFr[4]] = file[linesFr[4]].substring(0,file[linesFr[4]].length()-4) + "|" + status + "|";
        } else {
            file[linesEn[3]] = file[linesEn[3]].substring(0,file[linesEn[3]].length()-4) + "_____";
            file[linesEn[4]] = file[linesEn[4]].substring(0,file[linesEn[4]].length()-4) + "|" + status + "|";
        }
    }
    public static void getTerminal(Pokemon pokemon,int pos) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("app/src/main/resources/moveTerminal.txt"));
        String[] terminal = new String[terminalLength];
        scanner = new Scanner(new File("app/src/main/resources/moveTerminal.txt"));
        for (int i = 0; i < terminalLength; i++) {terminal[i] = scanner.nextLine();}
        Move move0 = pokemon.getMove(0),move1 = pokemon.getMove(1),move2 = pokemon.getMove(2),move3 = pokemon.getMove(3);
        Move displayMove = move0;
        if (pos == 1) {displayMove=move1;} else if (pos == 2) {displayMove=move2;} else if (pos == 3) {displayMove=move3;}
        terminal[statLine] = terminal[statLine].substring(0,terminal[statLine].indexOf("K")+3)
            + displayMove.dmg + ((displayMove.dmg == 0) ? " " : "") + ((displayMove.dmg < 100) ? " " : "") +
            terminal[statLine].substring(terminal[statLine].indexOf("K")+6,terminal[statLine].indexOf("U")+3)
            + displayMove.acc + ((displayMove.acc == 0) ? " " : "") + ((displayMove.acc < 100) ? " " : "") +
            terminal[statLine].substring(terminal[statLine].indexOf("U")+6);
        for (String i : displayMove.description) {while (i.length() < effectLineLength) {i += " ";}}
        for (int i = 0; i < 3; i++) {
            terminal[effectLines[i]] = terminal[effectLines[i]].substring(0,effectsStart)
                +displayMove.description[i]+ terminal[effectLines[i]].substring(effectsStart+displayMove.description[i].length());
        }
        terminal[moveLines[0]] = terminal[moveLines[0]].substring(0,moveCol[0]) + 
                terminal[moveLines[0]].substring(moveCol[0],moveCol[0]+12).replaceFirst("TYP", move0.moveType)
                .replaceFirst("ELE", move0.moveElement) + " " + move0.moveName +
                terminal[moveLines[0]].substring(moveCol[0]+13+move0.moveName.length(),moveCol[1]) +
                terminal[moveLines[0]].substring(moveCol[1],moveCol[1]+12).replaceFirst("TYP", move1.moveType)
                .replaceFirst("ELE", move1.moveElement) + " " + move1.moveName +
                terminal[moveLines[0]].substring(moveCol[1]+13+move1.moveName.length());
        terminal[moveLines[1]] = terminal[moveLines[1]].substring(0,moveCol[0]) + 
                terminal[moveLines[1]].substring(moveCol[0],moveCol[0]+12).replaceFirst("TYP", move2.moveType)
                .replaceFirst("ELE", move2.moveElement) + " " + move2.moveName +
                terminal[moveLines[1]].substring(moveCol[0]+13+move2.moveName.length(),moveCol[1]) +
                terminal[moveLines[1]].substring(moveCol[1],moveCol[1]+12).replaceFirst("TYP", move3.moveType)
                .replaceFirst("ELE", move3.moveElement) + " " + move3.moveName +
                terminal[moveLines[1]].substring(moveCol[1]+13+move3.moveName.length());
        for (int i = 0; i < terminalLength; i++) {
            file[terminalStart+i] = terminal[i];
        }
    }
    public static void getTerminal(Pokemon[] pokemons) throws FileNotFoundException {
        String[] terminal = new String[file.length-1];
        Scanner scanner = new Scanner(new File("app/src/main/resources/pokemonSwitchScreen.txt"));
        for (int i = 0; i < terminal.length; i++) {
            terminal[i] = scanner.nextLine();
        }
        for (int i = 0; i < pokemons.length; i++) {
            terminal[pokemonLines[i]-2] = terminal[pokemonLines[i]-2]
                .replace("NAME", pokemons[i].name);
            terminal[pokemonLines[i]] = terminal[pokemonLines[i]]
                .replace("EA", fullType(pokemons[i].typeA));
            if (pokemons[i].typeB!=null) {
                terminal[pokemonLines[i]] = terminal[pokemonLines[i]]
                    .replace("TB", fullType(pokemons[i].typeB));}
            else {
                terminal[pokemonLines[i]] = terminal[pokemonLines[i]]
                    .replace("|TB|", "");}
            if (pokemons[i].effect.contains(Status.FAINTED)) {
                terminal[pokemonLines[i]] = terminal[pokemonLines[i]]
                    .replace("STS", "FAINTED");}
            else if (!pokemons[i].effect.isEmpty()) {
                terminal[pokemonLines[i]] = terminal[pokemonLines[i]]
                    .replace("STS", pokemons[i].effect.get(0).name());}
            else {
                terminal[pokemonLines[i]] = terminal[pokemonLines[i]]
                    .replace("STS", "NORMAL");
            }
            boolean fillBox = false;
            String topBox = "", bottomBox = "";
            for (char c : terminal[pokemonLines[i]].substring(terminal[pokemonLines[i]+2].indexOf("H"),switchMiddleColumn-1).toCharArray()) {
                if (c == '|') {fillBox = !fillBox;}
                if (fillBox) {topBox += "_"; bottomBox += "‾";} 
                else {topBox += " "; bottomBox += " ";}
                if (c == '|' && !fillBox) {topBox = topBox.substring(0,topBox.length()-1)+"_"; 
                bottomBox = bottomBox.substring(0,bottomBox.length()-1)+"‾";}
            }
            terminal[pokemonLines[i]-1] = terminal[pokemonLines[i]-1].substring(0,terminal[pokemonLines[i]+2].indexOf("H"))
                + topBox + "                     ";
            terminal[pokemonLines[i]+1] = terminal[pokemonLines[i]+1].substring(0,terminal[pokemonLines[i]+2].indexOf("H"))
                + bottomBox + "                     ";
            String healthBar = setHealth(((pokemons[i].hp/pokemons[i].hp>=0.50)?"green":(pokemons[i].hp/pokemons[i].hp>=0.25)
                ?"yellow":"red"),(int)pokemons[i].hp, pokemons[i].MAX_HP,true);
            terminal[pokemonLines[i]+2] = terminal[pokemonLines[i]+2]
                .replace("██████████**/**██████████", healthBar);
            for (int j = -2; j < 2; j++) {
                terminal[pokemonLines[i]+j] = terminal[pokemonLines[i]+j].substring(0,switchMiddleColumn)+
                    "|                                |";
            }
        }
        for (int i = 0; i < terminal.length;i++) {file[i] = terminal[i];} file[file.length-1] = "";
    }
    private static String fullType(String type) {
        if (type == "NOR") {return "NORMAL";} else if (type == "FIR") {return "FIRE";} else if (type == "WAT") {return "WATER";}
        else if (type == "ELE") {return "ELECTRIC";} else if (type == "GRA") {return "GRASS";} else if (type == "ICE") {return "ICE";}
        else if (type == "FIG") {return "FIGHTING";} else if (type == "POI") {return "POISON";} else if (type == "GRO") {return "GROUND";}
        else if (type == "FLY") {return "FLYING";} else if (type == "PSY") {return "PSYCHIC";} else if (type == "BUG") {return "BUG";}
        else if (type == "ROC") {return "ROCK";} else if (type == "GHO") {return "GHOST";} else if (type == "DRA") {return "DRAGON";}
        else if (type == "DAR") {return "DARK";} else if (type == "STE") {return "STEEL";} else if (type == "FAI") {return "FAIRY";}
        else {return "ERROR";}
    }
    public static void getTerminal(Item[] items) {}
    public static void printLogs(ArrayList<String> logs) throws FileNotFoundException {
        int currentLine = 0;
        Scanner scanner = new Scanner(new File("app/src/main/resources/terminal.txt"));
        String[] terminal = new String[terminalLength];
        for (int i = 0; i < terminal.length; i++) {terminal[i] = scanner.nextLine();}
        for (int i = 0; i < logs.size(); i++) {
            currentLine += 2;
            if (currentLine >= terminalLength) {
                currentLine=terminal.length-1;
                terminal[currentLine] = terminal[currentLine].substring(0,terminal[currentLine].length()-7) 
                     + ">>" + terminal[currentLine].substring(terminal[currentLine].length()-5);
                for (int j = terminalStart; j < terminalStart+terminalLength; j++) {file[j] = terminal[j-terminalStart];}
                print();
                String s = wait.next();
                terminal = new String[terminalLength];
                scanner = new Scanner(new File("app/src/main/resources/terminal.txt"));
                for (int j = 0; j < terminal.length; j++) {terminal[j] = scanner.nextLine();}
                currentLine = 2;
            } 
            terminal[currentLine] = terminal[currentLine].substring(0,7) + logs.get(i) + 
                terminal[currentLine].substring(7+logs.get(i).length());
        }  
        currentLine = terminal.length-1;
        terminal[currentLine] = terminal[currentLine].substring(0,terminal[currentLine].length()-7) 
            + ">>" + terminal[currentLine].substring(terminal[currentLine].length()-5);
        for (int j = terminalStart; j < terminalStart+terminalLength; j++) {file[j] = terminal[j-terminalStart];}
        print();
        String s = wait.next();
    }
    public static void getTerminal() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("app/src/main/resources/Battle_Screen.txt"));
        for (int i = 0; i < 0; i++) {scanner.nextLine();}
        for (int i = 0; i < terminalStart+terminalLength; i++) {file[i] = scanner.nextLine();}
        textManager.changeASCII(battleManager.playerTeam[battleManager.currentPokemon].getASCIIPath(),true);
        textManager.changeASCII(battleManager.pokemonList[battleManager.enemyPokemon].getASCIIPath(), false);
        double healthPercentS = battleManager.playerTeam[battleManager.currentPokemon].hp/
            battleManager.playerTeam[battleManager.currentPokemon].MAX_HP;
        textManager.setHealth(((healthPercentS>=0.50)?"green":(healthPercentS>=0.25)?"yellow":"red")
            ,(int) battleManager.playerTeam[battleManager.currentPokemon].hp, 
            battleManager.playerTeam[battleManager.currentPokemon].MAX_HP, true);
        double healthPercentE = battleManager.pokemonList[battleManager.enemyPokemon].hp/
            battleManager.pokemonList[battleManager.enemyPokemon].MAX_HP;
        textManager.setHealth(((healthPercentE>=0.50)?"green":(healthPercentE>=0.25)?"yellow":"red")
            ,(int) battleManager.pokemonList[battleManager.enemyPokemon].hp,
            battleManager.pokemonList[battleManager.enemyPokemon].MAX_HP, false);
    }
    public static void prefightScreen() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("app/src/main/resources/prefightScreen.txt"));
        while(scanner.hasNextLine()) {System.out.println(scanner.nextLine());}
    }

    public static void print() {
        for (int i = 0; i < TEXT_LENGTH; i++) {
            System.out.println(file[i]);
        }
        System.out.println("");
    }
}

