/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package pokemon.battler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;

public class soundPlayer {
    public static Sequencer sequencer;
    public static void playSound(String pathway) {
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(pathway)));
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(inputStream);
            sequencer.start();
            sequencer.setLoopCount(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } public static void stop() {
        sequencer.stop();
    }
}
