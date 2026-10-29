package view;

import javax.swing.JFrame;

/**
 * Fenêtre principale de l'application.
 */
public class MainWindow extends JFrame {

    /**
     * Constructeur de la fenêtre.
     * 
     * @param panel Le panneau de jeu à intégrer dans la fenêtre
     */
    public MainWindow(GamePanel panel) {
        this.setTitle("Survival Game 2D - Arène");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600); // Taille de la fenêtre
        this.setResizable(false); // Empêche le redimensionnement
        this.setLocationRelativeTo(null); // Centre la fenêtre sur l'écran

        // Ajoute le panneau de jeu dans la fenêtre
        this.add(panel);
    }
}