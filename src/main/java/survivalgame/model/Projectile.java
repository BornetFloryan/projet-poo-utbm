package survivalgame.model;

/**
 * Projectile rectiligne avec une portée maximale.  *
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public final class Projectile extends Entity {
    private final double velocityX;
    private final double velocityY;
    private final int damage;
    private double remainingRange = 520;

    /** Crée un projectile avec sa portée par défaut.
     * @param x position horizontale
     * @param y position verticale
     * @param velocityX vitesse horizontale
     * @param velocityY vitesse verticale
     * @param damage dégâts infligés
     */
    public Projectile(double x, double y, double velocityX, double velocityY, int damage) {
        this(x, y, velocityX, velocityY, damage, 520);
    }

    /** Recrée un projectile avec sa portée restante lors du chargement.
     * @param x abscisse initiale
     * @param y ordonnée initiale
     * @param velocityX vitesse horizontale
     * @param velocityY vitesse verticale
     * @param damage dégâts infligés
     * @param remainingRange portée restante en pixels
     */
    public Projectile(double x, double y, double velocityX, double velocityY,
                      int damage, double remainingRange) {
        super(x, y, 8, 8);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.damage = damage;
        if (!Double.isFinite(remainingRange) || remainingRange < 0) {
            throw new IllegalArgumentException("Portée de projectile invalide.");
        }
        this.remainingRange = remainingRange;
        setFacingAngle(Math.atan2(velocityY, velocityX));
    }

    /** Retourne la vitesse horizontale.
     * @return vitesse en pixels par pas
     */
    public double getVelocityX() { return velocityX; }
    /** Retourne la vitesse verticale.
     * @return vitesse en pixels par pas
     */
    public double getVelocityY() { return velocityY; }
    /** Retourne les dégâts du projectile.
     * @return dégâts infligés à une cible
     */
    public int getDamage() { return damage; }
    /** Retourne la portée qu'il reste à parcourir.
     * @return portée restante en pixels
     */
    public double getRemainingRange() { return remainingRange; }
    @Override public void update(GameState state, int[][] distanceField) {
        setX(getX() + velocityX);
        setY(getY() + velocityY);
        remainingRange -= Math.hypot(velocityX, velocityY);
    }
}
