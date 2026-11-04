package survivalgame.model;

import java.awt.geom.Rectangle2D;

/** Élément mobile de la partie, exprimé dans les coordonnées locales de l'arène. */
public abstract class Entity {
    private double x;
    private double y;
    private double facingAngle;
    private final int width;
    private final int height;

    protected Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getFacingAngle() { return facingAngle; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    protected void setX(double x) { this.x = x; }
    protected void setY(double y) { this.y = y; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    public void setFacingAngle(double angle) { facingAngle = angle; }
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x - width / 2.0, y - height / 2.0, width, height);
    }

    /** Met à jour le comportement de l'entité pour un pas de simulation. */
    public abstract void update(GameState state, int[][] distanceField);
}
