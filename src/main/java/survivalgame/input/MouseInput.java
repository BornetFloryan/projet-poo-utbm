package survivalgame.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;

/**
 * Mémorise la visée et l'état maintenu du clic gauche.  *
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public final class MouseInput extends MouseAdapter {
    private double x;
    private double y;
    private boolean shooting;
    private double scale = 1;
    private double offsetX;
    private double offsetY;
    private double mapOffsetX;
    private double mapOffsetY;

    /** Crée un gestionnaire de visée et de tir. */
    public MouseInput() { }

    /** Met à jour l'échelle et les décalages entre écran et carte.
     * @param scale échelle d'affichage
     * @param offsetX marge horizontale
     * @param offsetY marge verticale
     * @param mapOffsetX décalage horizontal de la carte
     * @param mapOffsetY décalage vertical de la carte
     */
    public void setTransform(double scale, double offsetX, double offsetY,
                             double mapOffsetX, double mapOffsetY) {
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.mapOffsetX = mapOffsetX;
        this.mapOffsetY = mapOffsetY;
    }

    /** Retourne la position de visée dans le repère de la carte.
     * @return coordonnées de visée
     */
    public Point2D.Double getAim() { return new Point2D.Double(x, y); }
    /** Indique si le bouton gauche est maintenu.
     * @return vrai pendant le tir
     */
    public boolean isShooting() { return shooting; }

    @Override public void mouseMoved(MouseEvent event) { update(event); }
    @Override public void mouseDragged(MouseEvent event) { update(event); }
    @Override public void mousePressed(MouseEvent event) {
        update(event);
        if (SwingUtilities.isLeftMouseButton(event)) shooting = true;
    }
    @Override public void mouseReleased(MouseEvent event) {
        if (SwingUtilities.isLeftMouseButton(event)) shooting = false;
    }

    private void update(MouseEvent event) {
        x = (event.getX() - offsetX) / scale - mapOffsetX;
        y = (event.getY() - offsetY) / scale - mapOffsetY;
    }
}
