package survivalgame.model;

/** Entité vivante dotée de points de vie, d'une vitesse et d'un rayon de collision. */
public abstract class Character extends Entity {
    private int health;
    private final int maxHealth;
    private final double speed;
    private final double radius;

    protected Character(double x, double y, int health, double speed, double radius) {
        super(x, y, (int) (radius * 2), (int) (radius * 2));
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.radius = radius;
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public double getSpeed() { return speed; }
    public double getRadius() { return radius; }
    public boolean isDead() { return health <= 0; }
    public void takeDamage(int amount) { health = Math.max(0, health - amount); }
    protected void heal(int amount) { health = Math.min(maxHealth, health + amount); }

    /** Restaure les points de vie validés lors du chargement d'une partie. */
    protected void restoreHealth(int health) {
        if (health < 1 || health > maxHealth) throw new IllegalArgumentException("Vie invalide.");
        this.health = health;
    }
}
