package survivalgame.model;

/**
 * Barricade de fenêtre que les tirs et les zombies peuvent détruire.  *
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public final class Barricade extends Obstacle {
    private int durability = 4;

    /** Crée une barricade intacte.
     * @param column colonne de la case
     * @param row ligne de la case
     */
    public Barricade(int column, int row) { super(column, row); }
    @Override public boolean isDestructible() { return true; }
    /** Retourne la résistance restante.
     * @return durabilité comprise entre zéro et quatre
     */
    public int getDurability() { return durability; }
    @Override public boolean takeDamage(int damage) {
        durability = Math.max(0, durability - damage);
        return durability == 0;
    }
}
