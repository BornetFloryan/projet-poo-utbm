package survivalgame.view;

import survivalgame.controller.GameEngine;
import survivalgame.input.KeyboardInput;
import survivalgame.input.MouseInput;
import survivalgame.model.GameMap;
import survivalgame.model.GameState;
import survivalgame.model.GameStatus;
import survivalgame.model.ShopManager;
import survivalgame.persistence.ScoreEntry;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

/**
 * Vue du jeu et point d'entrée des événements Swing.
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
@SuppressWarnings("serial")
public final class GamePanel extends JPanel implements MouseListener {
    private static final long serialVersionUID = 1L;

    /** Contrôleur de la session affichée. */
    private final GameEngine engine = new GameEngine();
    /** Dessine les écrans et le monde. */
    private final GameRenderer renderer = new GameRenderer();
    /** Gestionnaire des raccourcis clavier. */
    private final KeyboardInput keyboard = new KeyboardInput();
    /** Gestionnaire de visée et de tir. */
    private final MouseInput mouse = new MouseInput();
    /** Horloge de rafraîchissement du jeu. */
    private final javax.swing.Timer timer;
    /** Dernier message d'action affiché. */
    private String notice = "";
    /** Erreur éventuelle de lecture du classement. */
    private String scoreError;
    /** Scores mis en cache pendant l'affichage. */
    private List<ScoreEntry> scoreCache = List.of();
    /** Indique si le classement doit être relu. */
    private boolean scoresNeedReload = true;
    /** Date d'expiration du message temporaire. */
    private long noticeExpires;
    /** Échelle logique de la fenêtre. */
    private double scale = 1;
    /** Décalage horizontal de l'affichage logique. */
    private double offsetX;
    /** Décalage vertical de l'affichage logique. */
    private double offsetY;
    /** Décalage horizontal de la carte. */
    private int mapOffsetX;
    /** Décalage vertical de la carte. */
    private int mapOffsetY;

    /** Initialise le panneau, les entrées et l'horloge de jeu. */
    public GamePanel() {
        setFocusable(true);
        setBackground(new java.awt.Color(12, 16, 24));
        keyboard.attach(this, this::togglePause);
        addMouseListener(this);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        timer = new javax.swing.Timer(16, event -> updateGame());
        timer.start();
    }

    private void updateGame() {
        Point2D.Double direction = keyboard.getDirection();
        Point2D.Double aim = mouse.getAim();
        engine.update(direction.x, direction.y, aim, mouse.isShooting());
        if (System.currentTimeMillis() > noticeExpires) notice = "";
        repaint();
    }

    private void togglePause() {
        if (engine.getStatus() == GameStatus.PLAYING) engine.setStatus(GameStatus.PAUSED);
        else if (engine.getStatus() == GameStatus.PAUSED) engine.setStatus(GameStatus.PLAYING);
    }

    @Override protected void paintComponent(Graphics rawGraphics) {
        super.paintComponent(rawGraphics);
        Graphics2D graphics = (Graphics2D) rawGraphics.create();
        scale = Math.max(0.1, Math.min(getWidth() / (double) GameRenderer.WIDTH,
                getHeight() / (double) GameRenderer.HEIGHT));
        offsetX = (getWidth() - GameRenderer.WIDTH * scale) / 2;
        offsetY = (getHeight() - GameRenderer.HEIGHT * scale) / 2;

        GameState state = engine.getState();
        mapOffsetX = state == null ? 0 : renderer.getMapOffsetX(state.getMap());
        mapOffsetY = state == null ? 0 : renderer.getMapOffsetY(state.getMap());
        mouse.setTransform(scale, offsetX, offsetY, mapOffsetX, mapOffsetY);

        graphics.translate(offsetX, offsetY);
        graphics.scale(scale, scale);
        renderer.setAim(mouse.getAim().x, mouse.getAim().y);
        renderer.draw(graphics, engine.getStatus(), state, loadScores(), scoreError, notice);
        graphics.dispose();
    }

    private List<ScoreEntry> loadScores() {
        if (engine.getStatus() != GameStatus.SCORES) return List.of();
        if (!scoresNeedReload) return scoreCache;
        try {
            scoreError = null;
            scoreCache = engine.getScoreManager().getTopScores();
        } catch (IOException exception) {
            scoreError = "Lecture du classement impossible : " + exception.getMessage();
            scoreCache = List.of();
        }
        scoresNeedReload = false;
        return scoreCache;
    }

    @Override public void mouseClicked(MouseEvent event) { handleClick(toLogicalX(event), toLogicalY(event)); }
    @Override public void mousePressed(MouseEvent event) { }
    @Override public void mouseReleased(MouseEvent event) { }
    @Override public void mouseEntered(MouseEvent event) { }
    @Override public void mouseExited(MouseEvent event) { }

    private int toLogicalX(MouseEvent event) { return (int) ((event.getX() - offsetX) / scale); }
    private int toLogicalY(MouseEvent event) { return (int) ((event.getY() - offsetY) / scale); }

    private void handleClick(int x, int y) {
        switch (engine.getStatus()) {
            case MENU -> handleMenuClick(x, y);
            case PAUSED -> handlePauseClick(x, y);
            case SHOP -> handleShopClick(x, y);
            case SCORES, HELP -> { if (inside(x, y, 365, 530, 230, 52)) engine.setStatus(GameStatus.MENU); }
            case GAME_OVER -> handleGameOverClick(x, y);
            default -> { }
        }
        requestFocusInWindow();
    }

    private void handleMenuClick(int x, int y) {
        if (inside(x, y, 365, 225, 230, 52)) {
            loadGame();
        } else if (inside(x, y, 365, 285, 230, 52)) {
            engine.startNewGame();
            updateMouseMapOffset();
        } else if (inside(x, y, 365, 345, 230, 52)) {
            scoresNeedReload = true;
            engine.setStatus(GameStatus.SCORES);
        } else if (inside(x, y, 365, 405, 230, 52)) {
            engine.setStatus(GameStatus.HELP);
        } else if (inside(x, y, 365, 465, 230, 52)) {
            System.exit(0);
        }
    }

    private void loadGame() {
        try {
            engine.loadGame();
            updateMouseMapOffset();
            notice = "Partie chargée.";
        } catch (IOException exception) {
            notice = exception.getMessage();
        }
        noticeExpires = System.currentTimeMillis() + 3000;
    }

    private void handlePauseClick(int x, int y) {
        if (inside(x, y, 365, 270, 230, 52)) engine.setStatus(GameStatus.PLAYING);
        else if (inside(x, y, 365, 335, 230, 52)) engine.startNewGame();
        else if (inside(x, y, 365, 400, 230, 52)) saveGame();
        else if (inside(x, y, 365, 465, 230, 52)) engine.setStatus(GameStatus.MENU);
    }

    private void saveGame() {
        try {
            engine.saveGame();
            notice = "Partie sauvegardée dans savegame.properties.";
        } catch (IOException exception) {
            notice = "Sauvegarde impossible : " + exception.getMessage();
        }
        noticeExpires = System.currentTimeMillis() + 3000;
    }

    private void handleShopClick(int x, int y) {
        if (inside(x, y, 180, 290, 280, 54)) buy(ShopManager.Item.SMG);
        else if (inside(x, y, 500, 290, 280, 54)) buy(ShopManager.Item.RIFLE);
        else if (inside(x, y, 180, 360, 280, 54)) buy(ShopManager.Item.DAMAGE);
        else if (inside(x, y, 500, 360, 280, 54)) buy(ShopManager.Item.RATE);
        else if (inside(x, y, 340, 430, 280, 54)) buy(ShopManager.Item.HEAL);
        else if (inside(x, y, 340, 500, 280, 54)) engine.startNextWave();
    }

    private void handleGameOverClick(int x, int y) {
        if (inside(x, y, 340, 390, 280, 52)) saveScore();
        else if (inside(x, y, 340, 455, 280, 52)) engine.startNewGame();
        else if (inside(x, y, 340, 520, 280, 52)) engine.setStatus(GameStatus.MENU);
    }

    private void buy(ShopManager.Item item) {
        notice = engine.purchase(item);
        noticeExpires = System.currentTimeMillis() + 1800;
    }

    private void saveScore() {
        String name = JOptionPane.showInputDialog(this, "Pseudonyme (1 à 16 caractères) :");
        if (name == null) return;
        try {
            engine.getScoreManager().addScore(name, engine.getState().getPlayer().getScore());
            notice = "Score enregistré.";
            scoresNeedReload = true;
        } catch (IllegalArgumentException | IOException exception) {
            notice = exception.getMessage();
        }
        noticeExpires = System.currentTimeMillis() + 2500;
    }

    private void updateMouseMapOffset() {
        GameMap map = engine.getState().getMap();
        mapOffsetX = renderer.getMapOffsetX(map);
        mapOffsetY = renderer.getMapOffsetY(map);
        mouse.setTransform(scale, offsetX, offsetY, mapOffsetX, mapOffsetY);
    }

    private static boolean inside(int x, int y, int left, int top, int width, int height) {
        return x >= left && x <= left + width && y >= top && y <= top + height;
    }
}
