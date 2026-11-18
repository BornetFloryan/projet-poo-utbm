package survivalgame.model;

/**
 * Personnage contrôlé par le joueur : vie, score, crédits et arme.
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public final class Player extends Character {
    private int score;
    private int credits;
    private Weapon weapon = Weapon.pistol();
    private long lastDamageTime;

    /** Crée le joueur à la position de départ.
     * @param x abscisse initiale
     * @param y ordonnée initiale
     */
    public Player(double x, double y) { super(x, y, 100, 3.2, 12); }
    /** Retourne le score accumulé.
     * @return score courant
     */
    public int getScore() { return score; }
    /** Retourne le nombre de crédits disponibles.
     * @return solde courant
     */
    public int getCredits() { return credits; }
    /** Retourne l'arme équipée.
     * @return arme active
     */
    public Weapon getWeapon() { return weapon; }
    /** Ajoute les points et crédits gagnés.
     * @param points points à ajouter au score
     * @param credits crédits à ajouter
     */
    public void addReward(int points, int credits) { score += points; this.credits += credits; }
    /** Débite les crédits si le solde est suffisant.
     * @param amount montant à dépenser
     * @return vrai si les crédits ont été débités
     */
    public boolean spendCredits(int amount) {
        if (amount < 0 || credits < amount) return false;
        credits -= amount;
        return true;
    }
    /** Équipe une arme.
     * @param weapon nouvelle arme
     */
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }

    /** Restaure les attributs du joueur depuis une sauvegarde validée.
     * @param health points de vie à restaurer
     * @param score score à restaurer
     * @param credits crédits à restaurer
     * @param weapon arme à équiper
     */
    public void restoreProgress(int health, int score, int credits, Weapon weapon) {
        if (score < 0 || credits < 0 || weapon == null) {
            throw new IllegalArgumentException("Progression invalide.");
        }
        restoreHealth(health);
        this.score = score;
        this.credits = credits;
        this.weapon = weapon;
    }

    /** Soigne le joueur sans dépasser la vie maximale.
     * @param amount points de vie à restaurer
     */
    public void healPlayer(int amount) { heal(amount); }

    /** Applique les dégâts de contact après le délai d'invulnérabilité.
     * @param now heure courante en millisecondes
     */
    public void receiveContactDamage(long now) {
        if (now - lastDamageTime >= 850) {
            takeDamage(10);
            lastDamageTime = now;
        }
    }
    @Override public void update(GameState state, int[][] distanceField) { }
}
