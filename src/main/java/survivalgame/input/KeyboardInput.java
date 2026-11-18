package survivalgame.input;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

/**
 * Suit l'état des touches sans dépendre de leur répétition par le système.  *
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public final class KeyboardInput {
    private final Set<Integer> pressedKeys = new HashSet<>();

    /** Crée un gestionnaire de touches sans touche active. */
    public KeyboardInput() { }

    /** Associe les touches de déplacement et de pause au composant.
     * @param component composant Swing recevant les raccourcis
     * @param pauseAction action déclenchée par Échap
     */
    public void attach(JComponent component, Runnable pauseAction) {
        int[] codes = {KeyEvent.VK_W, KeyEvent.VK_Z, KeyEvent.VK_A, KeyEvent.VK_Q,
                KeyEvent.VK_S, KeyEvent.VK_D};
        String[] labels = {"W", "Z", "A", "Q", "S", "D"};
        for (int i = 0; i < codes.length; i++) bindKey(component, labels[i], codes[i]);
        bind(component, "pressed ESCAPE", "pause", pauseAction);
    }

    /** Calcule le vecteur de déplacement normalisé en diagonale.
     * @return direction courante
     */
    public Point2D.Double getDirection() {
        double x = 0;
        double y = 0;
        if (pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_Q)) x--;
        if (pressedKeys.contains(KeyEvent.VK_D)) x++;
        if (pressedKeys.contains(KeyEvent.VK_W) || pressedKeys.contains(KeyEvent.VK_Z)) y--;
        if (pressedKeys.contains(KeyEvent.VK_S)) y++;
        if (x != 0 && y != 0) {
            x *= Math.sqrt(0.5);
            y *= Math.sqrt(0.5);
        }
        return new Point2D.Double(x, y);
    }

    private void bindKey(JComponent component, String label, int keyCode) {
        bind(component, "pressed " + label, "press-" + label, () -> pressedKeys.add(keyCode));
        bind(component, "released " + label, "release-" + label, () -> pressedKeys.remove(keyCode));
    }

    private void bind(JComponent component, String stroke, String name, Runnable action) {
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(stroke), name);
        component.getActionMap().put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { action.run(); }
        });
    }
}
