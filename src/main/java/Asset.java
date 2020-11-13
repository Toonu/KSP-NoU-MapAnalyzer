import java.util.ArrayList;

/**
 * @author Toonu NoU
 */
public class Asset {
    private ArrayList<String> parts;
    private String craftName;


    public Asset(ArrayList<String> parts, String craftName) {
        this.parts = parts;
        this.craftName = craftName;
    }

    @Override
    public String toString() {
        return String.format("%s: Missile: %s Gun: %s Hardpoints: %s Avionics: %s Parts: [%s]", craftName);
    }
}
