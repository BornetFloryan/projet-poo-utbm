package survivalgame.view;

import javax.swing.JFrame;
import java.awt.Dimension;

/** Fenêtre Swing principale. */
public final class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    /** Crée la fenêtre principale du jeu. */
    public MainWindow() {
        super("Survival Game 2D — Édition Arène");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new GamePanel());
        setMinimumSize(new Dimension(760, 560));
        setSize(1120, 760);
        setLocationRelativeTo(null);
    }
}
