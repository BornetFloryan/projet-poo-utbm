package survivalgame.model;

/** Barricade de fenêtre que les tirs et les zombies peuvent détruire. */
public final class Barricade extends Obstacle {
    private int durability = 4;

    public Barricade(int column, int row) { super(column, row); }
    @Override public boolean isDestructible() { return true; }
    public int getDurability() { return durability; }
    @Override public boolean takeDamage(int damage) {
        durability = Math.max(0, durability - damage);
        return durability == 0;
    }
}
