package survivalgame.model;

/** Projectile rectiligne avec une portée maximale. */
public final class Projectile extends Entity {
    private final double velocityX;
    private final double velocityY;
    private final int damage;
    private double remainingRange = 520;

    public Projectile(double x, double y, double velocityX, double velocityY, int damage) {
        super(x, y, 8, 8);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.damage = damage;
        setFacingAngle(Math.atan2(velocityY, velocityX));
    }
    public int getDamage() { return damage; }
    public double getRemainingRange() { return remainingRange; }
    @Override public void update(GameState state, int[][] distanceField) {
        setX(getX() + velocityX);
        setY(getY() + velocityY);
        remainingRange -= Math.hypot(velocityX, velocityY);
    }
}
