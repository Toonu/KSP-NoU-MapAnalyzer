import java.util.ArrayList;

/**
 * @author Tomas Novotny
 */
public class AnalysedCraft {
    private ArrayList<String> parts;
    private int counter = 0;
    private int missileCounter = 0;
    private int gunCounter = 0;
    private int hardpoints = 0;
    private int avionics = 0;
    private String craftName;


    public AnalysedCraft(ArrayList<String> parts, int counter, int missileCounter, int gunCounter, int hardpoints, int avionics, String craftName) {
        this.parts = parts;
        this.counter = counter;
        this.missileCounter = missileCounter;
        this.gunCounter = gunCounter;
        this.hardpoints = hardpoints;
        this.avionics = avionics;
        this.craftName = craftName;
    }

    @Override
    public String toString() {
        return String.format("%s: Missile: %s Gun: %s Hardpoints: %s Avionics: %s Parts: [%s]", craftName,missileCounter,gunCounter,hardpoints,avionics,counter);
    }

    public String toPartString() {
        return String.format("%s: Missile: %s Gun: %s Hardpoints: %s Avionics: %s Parts: [%s] %s", craftName,missileCounter,gunCounter,hardpoints,avionics,counter,parts);
    }
}
