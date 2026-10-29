package model;

/**
 * Classe de base abstraite pour tous les objets physiques du jeu.
 * Elle définit la position et la taille de l'objet pour gérer les collisions.
 */
public abstract class Entity {
    protected double x;
    protected double y;
    protected int width;
    protected int height;

    /**
     * Constructeur pour initialiser une entité sur la carte.
     * 
     * @param x      Position horizontale de départ
     * @param y      Position verticale de départ
     * @param width  Largeur de l'objet
     * @param height Hauteur de l'objet
     */
    public Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * @return La coordonnée X actuelle
     */
    public double getX() {
        return x;
    }

    /**
     * @return La coordonnée Y actuelle
     */
    public double getY() {
        return y;
    }

    /**
     * @return La largeur de l'entité
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return La hauteur de l'entité
     */
    public int getHeight() {
        return height;
    }

    /**
     * Modifie les coordonnées de l'entité.
     * 
     * @param x Nouvelle position horizontale
     * @param y Nouvelle position verticale
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Méthode abstraite mise à jour à chaque image du jeu.
     */
    public abstract void update();
}