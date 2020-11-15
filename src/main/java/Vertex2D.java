import java.io.Serializable;
import java.util.Objects;

/**
 * @author Toonu
 * <p>
 * Class representing 2D coordinate system.
 */
public class Vertex2D implements Serializable {
    private final double x;
    private final double y;

    /**
     * Constructor.
     *
     * @param x double x coordinate
     * @param y double y coordinate
     */
    public Vertex2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Method returns distance between two sets of coordinates.
     *
     * @param vertex battleGui.utils.Vertex2D second set of coordinates
     * @return double distance
     */
    public double distance(Vertex2D vertex) {
        if (vertex != null) {
            return Math.sqrt(Math.pow(getX() - vertex.getX(), 2) + Math.pow(getY() - vertex.getY(), 2));
        }
        return -1;
    }

    /**
     * Method to check if two objects equals.
     *
     * @param o Object to compare.
     * @return boolean result.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vertex2D vertex2D = (Vertex2D) o;
        return Double.compare(vertex2D.x, x) == 0 &&
                Double.compare(vertex2D.y, y) == 0;
    }

    /**
     * hashCode method for equals.
     *
     * @return int result.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Method returns String representing the object.
     *
     * @return String of formatted x and y coordinates
     */
    @Override
    public String toString() {
        return String.format("[%.5f, %.5f]", x, y);
    }
}
