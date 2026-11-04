package survivalgame.model;

/** Zombie rapide, plus fragile que la variante standard. */
public final class FastZombie extends Enemy {
    public FastZombie(double x, double y) { super(x, y, 22, 1.65); }
}
