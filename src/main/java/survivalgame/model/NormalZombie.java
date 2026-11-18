package survivalgame.model;

/** Zombie standard : vitesse modérée et résistance moyenne. */
public final class NormalZombie extends Enemy {
    /** Crée un zombie standard.
     * @param x position horizontale
     * @param y position verticale
     */
    public NormalZombie(double x, double y) { super(x, y, 36, 1.05); }
}
