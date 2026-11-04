package survivalgame;

import javax.swing.SwingUtilities;
import survivalgame.view.MainWindow;

/** Point d'entrée de Survival Game 2D — Édition Arène. */
public final class Main {
    private Main() { }

    /** Lance l'interface Swing sur son thread graphique. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
