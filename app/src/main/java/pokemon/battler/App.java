package pokemon.battler;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;
/*TODO Add Items and Selection Screen
Add Pokemon Selection screen
Add run away dialogue
Add post and pre battle dialouge
Add new pokemon
Changable ASCII Art
*/
public class App {
    public static Scanner scanner = new Scanner(System.in);
    public static Random random = new Random();
    public static void main (String[] args) throws InterruptedException {
        // soundPlayer.playSound("app/src/main/resources/title-screen.midi");
        textManager.Title_Screen();
        System.out.println("");
        scanner.next();
        try {textManager.prefightScreen();}
        catch (FileNotFoundException e) {e.printStackTrace();}
        System.out.println("");
        scanner.next();
        textManager.initialize();
        //TODO uncomment sound when ready
        // soundPlayer.stop();
        // soundPlayer.playSound("app/src/main/resources/trainer-battle.midi");
        battleManager.setUp();
        battleManager.startInput();
    }
}
