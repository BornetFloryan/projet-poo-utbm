package survivalgame.model;

/** Zombie rapide, plus fragile que la variante standard. */
public final class FastZombie extends Enemy {
    /** Crée un zombie rapide et fragile.
     * @param x position horizontale
     * @param y position verticale
     */
    public FastZombie(double x, double y) { super(x, y, 22, 1.65); }
}
