package survivalgame;

import javax.swing.SwingUtilities;
import survivalgame.view.MainWindow;

/** Point d'entrée de Survival Game 2D — Édition Arène. */
public final class Main {
    private Main() { }

    /** Lance l'interface Swing sur son thread graphique. */
    /** Lance l'interface Swing sur son thread d'événements.
     * @param args arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
