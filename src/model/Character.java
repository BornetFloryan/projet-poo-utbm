package model;

/**
 * Classe abstraite pour les entités vivantes (comme le Joueur ou l'Ennemi).
 * Elle hérite de Entity et ajoute la gestion de la vie et de la vitesse.
 */
public abstract class Character extends Entity {
    protected int health;
    protected double speed;

    /**
     * Constructeur pour un personnage vivant.
     * 
     * @param x         Position X de départ
     * @param y         Position Y de départ
     * @param width     Largeur du personnage
     * @param height    Hauteur du personnage
     * @param maxHealth Points de vie de départ
     * @param speed     Vitesse de déplacement
     */
    public Character(double x, double y, int width, int height, int maxHealth, double speed) {
        super(x, y, width, height); // Appel du constructeur parent (Entity)
        this.health = maxHealth;
        this.speed = speed;
    }

    /**
     * @return Les points de vie restants
     */
    public int getHealth() {
        return health;
    }

    /**
     * Fait perdre des points de vie au personnage.
     * 
     * @param amount Le nombre de dégâts subis
     */
    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    /**
     * Vérifie si le personnage n'a plus de vie.
     * 
     * @return Vrai si la vie est à 0 ou moins, faux sinon
     */
    public boolean isDead() {
        return this.health <= 0;
    }
}