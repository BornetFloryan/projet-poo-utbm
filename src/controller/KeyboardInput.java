package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import model.Player;

/**
 * Contrôleur gérant les entrées clavier du joueur.
 * Permet un déplacement fluide en mémorisant l'état des touches.
 */
public class KeyboardInput extends KeyAdapter {
    private Player player;

    /**
     * Constructeur liant le clavier au joueur.
     * 
     * @param player Le joueur à contrôler
     */
    public KeyboardInput(Player player) {
        this.player = player;
    }

    /**
     * Méthode appelée quand une touche est enfoncée.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_Z || key == KeyEvent.VK_UP)
            player.setUp(true);
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
            player.setDown(true);
        if (key == KeyEvent.VK_Q || key == KeyEvent.VK_LEFT)
            player.setLeft(true);
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT)
            player.setRight(true);
    }

    /**
     * Méthode appelée quand une touche est relâchée.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_Z || key == KeyEvent.VK_UP)
            player.setUp(false);
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
            player.setDown(false);
        if (key == KeyEvent.VK_Q || key == KeyEvent.VK_LEFT)
            player.setLeft(false);
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT)
            player.setRight(false);
    }
}