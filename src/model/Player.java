package model;

/**
 * Classe représentant le joueur principal contrôlé par l'utilisateur.
 * Elle gère le score, l'équipement et les déplacements.
 */
public class Player extends Character {
    private int score;
    private int ammo;

    private boolean up, down, left, right;

    /**
     * Constructeur pour créer le joueur.
     * 
     * @param startX Position de départ en X
     * @param startY Position de départ en Y
     */
    public Player(double startX, double startY) {
        // Le joueur mesure 30x30, a 100 PV et une vitesse de 5.0
        super(startX, startY, 30, 30, 100, 5.0);
        this.score = 0;
        this.ammo = 50;
    }

    public int getScore() {
        return score;
    }

    public int getAmmo() {
        return ammo;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    /**
     * Met à jour la position du joueur en fonction des touches pressées.
     */
    @Override
    public void update() {
        if (up)
            y -= speed;
        if (down)
            y += speed;
        if (left)
            x -= speed;
        if (right)
            x += speed;
    }
}