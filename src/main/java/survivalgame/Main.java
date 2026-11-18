package survivalgame;

import javax.swing.SwingUtilities;
import survivalgame.view.MainWindow;

/**
 * Point d'entrée de Survival Game 2D — Édition Arène.
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public final class Main {
    /** Empêche l’instanciation de la classe contenant uniquement le point d’entrée. */
    private Main() { }

    /** Lance l'interface Swing sur son thread d'événements.
     * @param args arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
