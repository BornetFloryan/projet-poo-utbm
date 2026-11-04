package survivalgame.model;

/** Obstacle infranchissable et indestructible. */
public class Obstacle extends Element {
    private final boolean boundary;

    public Obstacle(int column, int row) { this(column, row, false); }
    public Obstacle(int column, int row, boolean boundary) {
        super(column, row);
        this.boundary = boundary;
    }
    public boolean isBoundary() { return boundary; }
    public boolean isDestructible() { return false; }
    public boolean takeDamage(int damage) { return false; }
}
