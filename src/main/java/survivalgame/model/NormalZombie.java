package survivalgame.model;

/** Zombie standard : vitesse modérée et résistance moyenne. */
public final class NormalZombie extends Enemy {
    public NormalZombie(double x, double y) { super(x, y, 36, 1.05); }
}
