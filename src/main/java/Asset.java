import java.util.ArrayList;

/**
 * @author Toonu NoU
 */
public class Asset {
    private String value;
    private String id;
    private String style;
    private String parent;
    private String vertex;
    private String other;
    private Vertex2D coordinates;

    public Asset(String value, String id, String style, String parent, String vertex, String other) {
        this.value = value;
        this.id = id;
        this.style = style;
        this.parent = parent;
        this.vertex = vertex;
        this.other = other;
    }

    public String toXMLString() {
        return String.format("");
    }

    @Override
    public String toString() {
        return String.format("[%s]", id);
    }
}
