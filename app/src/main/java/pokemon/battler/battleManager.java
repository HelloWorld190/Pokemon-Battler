package pokemon.battler;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
public class battleManager {
    public static Scanner scanner = new Scanner(System.in);
    public static Random random = new Random();

    public static boolean finish = false;
    public static int pos = 0;
    public static Pokemon Blastoise;
    public static Pokemon Pidgey;
    public static Pokemon[] pokemonList = new Pokemon[2];
    public static Pokemon[] playerTeam;
    public static int currentPokemon = 0;
    public static int enemyPokemon = 1;
    public static boolean returnToMain = false;
    public static boolean setUpVatk = false;
    public static void setUp() {
        //TODO add pokemon randomizer + better set up;
        Move ShellSmash = new Move("Shell Smash", "NOR", 00, 00, moveTarget.EFFonSELF,
        new Status[] {Status.MINUS_DEF,Status.MINUS_SP_DEF,Status.PLUS_ATK,Status.PLUS_ATK,
            Status.PLUS_SP_ATK,Status.PLUS_SP_ATK,Status.PLUS_SPD,Status.PLUS_SPD});
        ShellSmash.setDescrpt(new String[] {
            "Self: Sp. Atk ↑↑,",
            "Atk ↑↑, Spd ↑↑,",
            "Sp. Def ↓, Def ↓"
        });
        Move IceBeam = new Move("Ice Beam","ICE",90,100,moveTarget.EFFonENEMYnATK,
            new Status[] {Status.FROZEN}, new double[] {0.1}, true);
        IceBeam.setDescrpt(new String[] {
            "ICE Dmg,",
            "10% Freeze Enemy",
            ""
        });
        Move HydroCannon = new Move("Hydro Cannon", "WAT", 150, 90,moveTarget.EFFonSELFnATK,
            new Status[] {Status.DISABLED},true);
        HydroCannon.setDescrpt(new String[] {
            "WATER Dmg,",
            "Cooldown: 1 turn",
            ""
        });
        Move FlashCannon = new Move("Flash Cannon", "STE", 80, 100,moveTarget.EFFonENEMYnATK,
            new Status[] {Status.MINUS_SP_DEF}, new double[] {0.1},true);
        FlashCannon.setDescrpt(new String[] {
            "STEEL Dmg,",
            "Enemy: 10% Sp. ",
            "Def ↓"
        });
        Blastoise = new Pokemon(
            "Blastoise", "app/src/main/resources/blastoiseASCII.txt",
            new int[] {79,83,100,85,105,78},"WAT",null,
            new Move[] {ShellSmash,IceBeam,HydroCannon,FlashCannon}
        );

        Move QuickAttack = new Move("Quick Attack", "NOR", 40, 100,moveTarget.EFFonSELFnATK,
            new Status[] {Status.PRIORITY});
        QuickAttack.setDescrpt(new String[] {
            "NORMAL Dmg,",
            "SELF: Move",
            "priority"
        });
        Move Tackle = new Move("Tackle","NOR",40,100,moveTarget.ATK);
        Tackle.setDescrpt(new String[] {
            "NORMAL Dmg,",
            "No other ",
            "effects"
        });
        Move AerialAce = new Move("Aerial Ace", "FLY", 60, 100,moveTarget.ATK);
        AerialAce.setDescrpt(new String[] {
            "FLYING Dmg,",
            "No other",
            "effects"
        });
        Move Twister = new Move("Twister", "DRA", 40, 100,moveTarget.EFFonENEMYnATK,
        new Status[] {Status.FLINCH},true);
        Twister.setDescrpt(new String[] {
            "DRAGON Dmg,",
            "ENEMY: 30%",
            "FLINCH"
        });
        Pidgey = new Pokemon(
            "Pidgey", "app/src/main/resources/pidgeyASCII.txt",
            new int[] {40,45,40,35,35,56},"NOR","FLY",
            new Move[] {QuickAttack,Tackle,AerialAce,Twister}
        );

        Move HydroGun = new Move("Hydro Gun","WAT",40,100,moveTarget.ATK,true);
        HydroGun.setDescrpt(new String[] {
            "WATER Dmg.",
            "No other", 
            "effects"
        });

        Pokemon pidgey2 = new Pokemon(
            "Pidgey2", "app/src/main/resources/pidgeyASCII.txt",
            new int[] {40,45,40,35,35,56},"NOR","FLY",
            new Move[] {QuickAttack,Tackle,AerialAce,Twister}
        );
        pokemonList = new Pokemon[] {Blastoise,Pidgey};
        playerTeam = new Pokemon[] {Blastoise,Blastoise,pidgey2};
        textManager.setHealth("green",Pidgey.MAX_HP, Pidgey.MAX_HP,false);
        textManager.setHealth("green",Blastoise.MAX_HP, Blastoise.MAX_HP,true);
        try {
            textManager.changeASCII(playerTeam[currentPokemon].getASCIIPath(),true);
            textManager.changeASCII(pokemonList[enemyPokemon].getASCIIPath(), false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        textManager.screen = CurrentScreen.MAIN_BATTLE;
        textManager.print();
    }
    public static void startInput() {
        scanner = new Scanner(System.in);
            int[][] positions = {
                {-1,-1},
                {0,1},
                {2,3}
            };
            int posX = 0,posY = 1;
        while (!finish) {
            while (true) {
                String input = scanner.next();
                if (input.equals(">")) {posX++;}
                else if (input.equals("<")) {posX--;}
                else if (input.equals("v")) {posY++;}
                else if (input.equals("^")) {posY--;}
                else {break;}
                if (posX >= 2) {posX -= 2;} if (posX<0) {posX+=2;}
                if (posY >= 3) {posY -= 3;} if (posY<0) {posY+=3;}
                if (textManager.screen == CurrentScreen.MAIN_BATTLE && posY==0) {
                    if (input.equals("^")) {posY=2;} else {posY=1;}
                }
                if (textManager.screen == CurrentScreen.MOVES) {
                    try {textManager.getTerminal(playerTeam[currentPokemon],positions[posY][posX]);
                    } catch (FileNotFoundException e) {e.printStackTrace();}}
                textManager.cursorPos(positions[posY][posX]);
                textManager.print();
            }
            if (setUpVatk) {setUpVatk = false; playerTeam[currentPokemon].skipTurn=false;}
            if (textManager.screen == CurrentScreen.MAIN_BATTLE && positions[posY][posX] == 0) {
                try {textManager.getTerminal(playerTeam[currentPokemon], positions[posY][posX]);
                } catch (FileNotFoundException e) { e.printStackTrace();}
                textManager.screen = CurrentScreen.MOVES;
                textManager.cursorPos(positions[posY][posX]);
            } else if (textManager.screen == CurrentScreen.MAIN_BATTLE && positions[posY][posX] == 1) {
                try {textManager.getTerminal(playerTeam);
                } catch (FileNotFoundException e) { e.printStackTrace();}
                textManager.print();
                textManager.screen = CurrentScreen.POKEMON_SWITCH;
                int selection = textManager.cursorPosList();
                if (selection == -1 || selection == currentPokemon) {returnToMain = true;}
                else {
                    currentPokemon = selection;
                    playerTeam[currentPokemon].skipTurn=true;
                    setUpVatk = true;
                    try {textManager.changeASCII(playerTeam[currentPokemon].getASCIIPath(),true);
                    } catch (FileNotFoundException e) {e.printStackTrace();}
                }
            } else if (textManager.screen == CurrentScreen.MOVES) {
                if (positions[posY][posX] == -1) {returnToMain=true;}
                else if (playerTeam[currentPokemon].getMove(positions[posY][posX]).disabled) {
                    ArrayList<String> notification = new ArrayList<String>();
                    notification.add("This move is on cooldown!");
                    try {textManager.printLogs(notification);
                    textManager.getTerminal(playerTeam[currentPokemon], positions[posY][posX]);}
                    catch (FileNotFoundException e) {e.printStackTrace();}
                    textManager.cursorPos(positions[posY][posX]);
                } else {
                    textManager.screen = CurrentScreen.POST_MOVE;
                    try{battleCalculator.battleAndLogs(positions[posY][posX]);}
                    catch (FileNotFoundException e) {e.printStackTrace();}
                }
            }
            if (setUpVatk) {
                try {textManager.getTerminal();}
                catch (FileNotFoundException e) {e.printStackTrace();}
                textManager.screen = CurrentScreen.POST_MOVE;
                try{battleCalculator.battleAndLogs(positions[posY][posX]);}
                catch (FileNotFoundException e) {e.printStackTrace();}
            }
            if (textManager.screen == CurrentScreen.POST_MOVE || returnToMain) {
                returnToMain = false;
                textManager.screen = CurrentScreen.MAIN_BATTLE;
                try {textManager.getTerminal();} 
                catch (FileNotFoundException e) {e.printStackTrace();}
                effectTimer.turnNum++;
                posX = 0; posY = 1;
            }
            textManager.print();
        }
    }
}
