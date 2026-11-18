package survivalgame.model;

/** Entité vivante dotée de points de vie, d'une vitesse et d'un rayon de collision. */
public abstract class Character extends Entity {
    private int health;
    private final int maxHealth;
    private final double speed;
    private final double radius;

    /** Initialise un personnage avec ses caractéristiques physiques.
     * @param x abscisse initiale
     * @param y ordonnée initiale
     * @param health vie initiale et maximale
     * @param speed vitesse par pas de simulation
     * @param radius rayon de collision
     */
    protected Character(double x, double y, int health, double speed, double radius) {
        super(x, y, (int) (radius * 2), (int) (radius * 2));
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.radius = radius;
    }

    /** Retourne la vie restante.
     * @return points de vie actuels
     */
    public int getHealth() { return health; }
    /** Retourne la vie maximale.
     * @return points de vie maximum
     */
    public int getMaxHealth() { return maxHealth; }
    /** Retourne la vitesse de déplacement.
     * @return vitesse en pixels par mise à jour
     */
    public double getSpeed() { return speed; }
    /** Retourne le rayon de collision.
     * @return rayon en pixels
     */
    public double getRadius() { return radius; }
    /** Indique si le personnage n'a plus de vie.
     * @return vrai si les points de vie sont à zéro
     */
    public boolean isDead() { return health <= 0; }
    /** Retire des points de vie sans passer sous zéro.
     * @param amount nombre de dégâts
     */
    public void takeDamage(int amount) { health = Math.max(0, health - amount); }
    /** Soigne le personnage sans dépasser sa vie maximale.
     * @param amount points de vie à restaurer
     */
    protected void heal(int amount) { health = Math.min(maxHealth, health + amount); }

    /** Restaure la vie depuis une sauvegarde validée.
     * @param health points de vie compris entre un et la vie maximale
     */
    protected void restoreHealth(int health) {
        if (health < 1 || health > maxHealth) {
            throw new IllegalArgumentException("Valeur de vie invalide.");
        }
        this.health = health;
    }
}
