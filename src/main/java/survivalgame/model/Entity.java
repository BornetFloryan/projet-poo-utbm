package survivalgame.model;

import java.awt.geom.Rectangle2D;

/**
 * Élément mobile de la partie, exprimé dans les coordonnées locales de l'arène.  *
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public abstract class Entity {
    private double x;
    private double y;
    private double facingAngle;
    private final int width;
    private final int height;

    /** Initialise une entité positionnée dans l'arène.
     * @param x abscisse centrale
     * @param y ordonnée centrale
     * @param width largeur de collision
     * @param height hauteur de collision
     */
    protected Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Retourne l'abscisse centrale.
     * @return abscisse locale à la carte
     */
    public double getX() { return x; }
    /** Retourne l'ordonnée centrale.
     * @return ordonnée locale à la carte
     */
    public double getY() { return y; }
    /** Retourne l'orientation de l'entité.
     * @return angle en radians
     */
    public double getFacingAngle() { return facingAngle; }
    /** Retourne la largeur de collision.
     * @return largeur en pixels
     */
    public int getWidth() { return width; }
    /** Retourne la hauteur de collision.
     * @return hauteur en pixels
     */
    public int getHeight() { return height; }
    /** Modifie l'abscisse centrale.
     * @param x nouvelle abscisse
     */
    protected void setX(double x) { this.x = x; }
    /** Modifie l'ordonnée centrale.
     * @param y nouvelle ordonnée
     */
    protected void setY(double y) { this.y = y; }
    /** Modifie la position centrale.
     * @param x nouvelle abscisse
     * @param y nouvelle ordonnée
     */
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    /** Modifie l'orientation de l'entité.
     * @param angle nouvelle orientation en radians
     */
    public void setFacingAngle(double angle) { facingAngle = angle; }
    /** Retourne le rectangle de collision centré sur l'entité.
     * @return rectangle des limites de collision
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x - width / 2.0, y - height / 2.0, width, height);
    }

    /** Met à jour le comportement de l'entité pour un pas de simulation.
     * @param state état courant de la partie
     * @param distanceField distances utilisées pour naviguer
     */
    public abstract void update(GameState state, int[][] distanceField);
}
