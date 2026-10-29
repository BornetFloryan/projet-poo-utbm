package main;

import javax.swing.SwingUtilities;

import model.Player;
import view.GamePanel;
import view.MainWindow;

/**
 * Point d'entrée principal du programme.
 */
public class Main {
    public static void main(String[] args) {
        // Exécute l'interface graphique dans le thread dédié de Swing
        SwingUtilities.invokeLater(() -> {

            // 1. Initialiser le Modèle (Le joueur au centre de l'écran 800x600)
            // (800/2) - (30/2) = 385 pour le centrer parfaitement
            Player player = new Player(385, 285);

            // 2. Initialiser la Vue en lui passant le modèle
            GamePanel panel = new GamePanel(player);
            MainWindow window = new MainWindow(panel);

            // 3. Afficher le tout
            window.setVisible(true);
        });
    }
}