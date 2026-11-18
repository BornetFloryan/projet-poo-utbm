package survivalgame.model;

/** Personnage contrôlé par le joueur : vie, score, crédits et arme. */
public final class Player extends Character {
    private int score;
    private int credits;
    private Weapon weapon = Weapon.pistol();
    private long lastDamageTime;

    public Player(double x, double y) { super(x, y, 100, 3.2, 12); }
    public int getScore() { return score; }
    public int getCredits() { return credits; }
    public Weapon getWeapon() { return weapon; }
    public void addReward(int points, int credits) { score += points; this.credits += credits; }
    public boolean spendCredits(int amount) {
        if (amount < 0 || credits < amount) return false;
        credits -= amount;
        return true;
    }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }
    public void healPlayer(int amount) { heal(amount); }
    public void receiveContactDamage(long now) {
        if (now - lastDamageTime >= 850) {
            takeDamage(10);
            lastDamageTime = now;
        }
    }
    @Override public void update(GameState state, int[][] distanceField) { }
}
