package view;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import controller.KeyboardInput;
import model.Player;

/**
 * Panneau principal du jeu (l'arène).
 * Gère la boucle de rafraîchissement, les textures et le dessin.
 */
public class GamePanel extends JPanel {
    private Player player;
    private Timer gameLoop;

    private Image backgroundTexture;
    private Image playerTexture;

    /**
     * Constructeur du panneau de jeu.
     * 
     * @param player Le joueur à afficher
     */
    public GamePanel(Player player) {
        this.player = player;

        // 1. Chargement des fichiers images avec gestion des exceptions
        try {
            backgroundTexture = ImageIO.read(new File("data/images/background.png"));
            playerTexture = ImageIO.read(new File("data/images/player.png"));
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des textures : " + e.getMessage());
        }

        // 2. Connecter le clavier au panneau
        this.addKeyListener(new KeyboardInput(player));
        this.setFocusable(true);
        this.requestFocusInWindow();

        // 3. Lancer la boucle de jeu (60 FPS)
        gameLoop = new Timer(16, e -> {
            player.update(); // Calcule la nouvelle position
            repaint(); // Redessine l'écran
        });
        gameLoop.start();
    }

    /**
     * Méthode appelée automatiquement pour redessiner l'écran.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Dessiner le fond (étiré sur toute la taille de la fenêtre)
        if (backgroundTexture != null) {
            g.drawImage(backgroundTexture, 0, 0, this.getWidth(), this.getHeight(), this);
        } else {
            // Sécurité : fond gris si l'image n'est pas trouvée
            g.setColor(new java.awt.Color(40, 40, 40));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        // 2. Dessiner le joueur à ses coordonnées
        if (playerTexture != null) {
            g.drawImage(
                    playerTexture,
                    (int) player.getX(),
                    (int) player.getY(),
                    player.getWidth(),
                    player.getHeight(),
                    this);
        } else {
            // Sécurité : cercle vert si l'image n'est pas trouvée
            g.setColor(java.awt.Color.GREEN);
            g.fillOval((int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight());
        }
    }
}