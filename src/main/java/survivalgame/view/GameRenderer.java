package survivalgame.view;

import survivalgame.model.Barricade;
import survivalgame.model.Enemy;
import survivalgame.model.GameMap;
import survivalgame.model.GameState;
import survivalgame.model.GameStatus;
import survivalgame.model.Obstacle;
import survivalgame.model.Player;
import survivalgame.model.Projectile;
import survivalgame.persistence.ScoreEntry;
import survivalgame.view.SpriteAtlas.Sprite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

/** Affiche les écrans et le terrain sans contenir de règles de jeu. */
final class GameRenderer {
    static final int WIDTH = 960;
    static final int HEIGHT = 640;
    private final SpriteAtlas sprites = new SpriteAtlas();

    void draw(Graphics2D graphics, GameStatus status, GameState state,
              List<ScoreEntry> scores, String scoreError, String notice) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(12, 16, 24));
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        switch (status) {
            case MENU -> drawMenu(graphics);
            case PLAYING -> drawGame(graphics, state);
            case SHOP -> {
                drawGame(graphics, state);
                drawShop(graphics, state);
            }
            case PAUSED -> {
                drawGame(graphics, state);
                drawOverlay(graphics, "PAUSE", "La sauvegarde reprend au début de la vague actuelle");
                drawButtons(graphics, new String[]{"Reprendre", "Recommencer", "Sauvegarder", "Menu"},
                        365, 270, 65);
            }
            case GAME_OVER -> drawGameOver(graphics, state);
            case SCORES -> drawScores(graphics, scores, scoreError);
            case HELP -> drawHelp(graphics);
        }
        if (notice != null && !notice.isBlank()) drawCentered(graphics, notice, 615, 15, new Color(255, 218, 126));
    }

    int getMapOffsetX(GameMap map) { return (WIDTH - map.getPixelWidth()) / 2; }
    int getMapOffsetY(GameMap map) { return (HEIGHT - map.getPixelHeight()) / 2; }

    private void drawGame(Graphics2D g, GameState state) {
        if (state == null) return;
        GameMap map = state.getMap();
        int mapX = getMapOffsetX(map);
        int mapY = getMapOffsetY(map);
        for (int column = 0; column < GameMap.COLUMNS; column++) {
            for (int row = 0; row < GameMap.ROWS; row++) {
                drawSprite(g, Sprite.FLOOR, mapX + column * GameMap.TILE_SIZE,
                        mapY + row * GameMap.TILE_SIZE, GameMap.TILE_SIZE, GameMap.TILE_SIZE);
            }
        }
        for (GameMap.SpawnPoint entrance : map.getEntrances()) {
            if (entrance.type() == GameMap.SpawnType.WINDOW) {
                drawSprite(g, Sprite.WINDOW, mapX + entrance.column() * GameMap.TILE_SIZE - 4,
                        mapY + entrance.row() * GameMap.TILE_SIZE - 4, 40, 40);
            } else {
                drawLadder(g, mapX + entrance.column() * GameMap.TILE_SIZE,
                        mapY + entrance.row() * GameMap.TILE_SIZE);
            }
        }
        for (Obstacle obstacle : map.getObstacles()) drawObstacle(g, obstacle, mapX, mapY);
        for (Projectile projectile : state.getProjectiles()) {
            drawEntity(g, Sprite.BULLET, projectile, mapX, mapY, 18, 18);
        }
        for (Enemy enemy : state.getEnemies()) {
            Sprite sprite = enemy instanceof survivalgame.model.FastZombie ? Sprite.FAST_ZOMBIE : Sprite.ZOMBIE;
            drawEntity(g, sprite, enemy, mapX, mapY, 38, 38);
        }
        Player player = state.getPlayer();
        drawEntity(g, Sprite.PLAYER, player, mapX, mapY, 42, 42);
        drawAim(g, player, mapX, mapY);
        drawHud(g, state);
    }

    private void drawObstacle(Graphics2D g, Obstacle obstacle, int mapX, int mapY) {
        int x = mapX + obstacle.getColumn() * GameMap.TILE_SIZE;
        int y = mapY + obstacle.getRow() * GameMap.TILE_SIZE;
        Sprite sprite = obstacle instanceof Barricade ? Sprite.WINDOW
                : obstacle.isBoundary() ? Sprite.BRICK : Sprite.SANDBAG;
        drawSprite(g, sprite, x - 2, y - 2, GameMap.TILE_SIZE + 4, GameMap.TILE_SIZE + 4);
        if (obstacle instanceof Barricade barricade) {
            g.setColor(new Color(12, 15, 20, 180));
            g.fillRoundRect(x + 4, y + 25, 24, 5, 4, 4);
            g.setColor(new Color(255, 205, 103));
            g.fillRoundRect(x + 4, y + 25, 24 * barricade.getDurability() / 4, 5, 4, 4);
        }
    }

    private void drawEntity(Graphics2D g, Sprite sprite, survivalgame.model.Entity entity,
                            int mapX, int mapY, int width, int height) {
        int centerX = mapX + (int) entity.getX();
        int centerY = mapY + (int) entity.getY();
        Graphics2D rotated = (Graphics2D) g.create();
        rotated.translate(centerX, centerY);
        rotated.rotate(entity.getFacingAngle());
        drawSprite(rotated, sprite, -width / 2, -height / 2, width, height);
        rotated.dispose();
    }

    private void drawLadder(Graphics2D g, int x, int y) {
        g.setColor(new Color(7, 10, 15, 150));
        g.fillRoundRect(x + 5, y + 4, 22, 25, 5, 5);
        g.setColor(new Color(126, 76, 43));
        g.fillRoundRect(x + 9, y + 3, 4, 26, 3, 3);
        g.fillRoundRect(x + 21, y + 3, 4, 26, 3, 3);
        g.setColor(new Color(222, 166, 96));
        for (int rungY = y + 7; rungY < y + 28; rungY += 6) {
            g.fillRoundRect(x + 11, rungY, 12, 3, 2, 2);
        }
        g.setColor(new Color(255, 220, 143));
        g.drawLine(x + 16, y + 7, x + 16, y + 25);
    }

    private void drawAim(Graphics2D g, Player player, int mapX, int mapY) {
        double angle = Math.atan2(lastAimY - player.getY(), lastAimX - player.getX());
        int x = mapX + (int) player.getX();
        int y = mapY + (int) player.getY();
        g.setColor(Color.WHITE);
        g.drawLine(x, y, x + (int) (Math.cos(angle) * 24), y + (int) (Math.sin(angle) * 24));
    }

    private double lastAimX;
    private double lastAimY;
    void setAim(double x, double y) { lastAimX = x; lastAimY = y; }

    private void drawHud(Graphics2D g, GameState state) {
        Player player = state.getPlayer();
        g.setColor(new Color(8, 12, 19, 230));
        g.fillRoundRect(15, 14, 930, 62, 12, 12);
        drawText(g, "VIE", 27, 42, 14, Color.WHITE, false);
        g.setColor(new Color(82, 39, 52));
        g.fillRoundRect(74, 30, 150, 16, 7, 7);
        g.setColor(new Color(226, 75, 96));
        g.fillRoundRect(74, 30, 150 * Math.max(0, player.getHealth()) / player.getMaxHealth(), 16, 7, 7);
        drawText(g, "Score  " + player.getScore(), 260, 43, 16, Color.WHITE, false);
        drawText(g, "Crédits  " + player.getCredits(), 410, 43, 16, new Color(255, 215, 105), false);
        drawText(g, "Vague  " + state.getWave(), 570, 43, 16, Color.WHITE, false);
        drawText(g, "Zombies  " + state.getEnemies().size(), 680, 43, 16, Color.WHITE, false);
        drawText(g, player.getWeapon().name(), 825, 43, 16, new Color(130, 211, 255), false);
        drawText(g, "Graine : " + state.getMap().getSeed(), 24, 620, 12, new Color(165, 178, 195), false);
        drawCentered(g, "ZQSD / WASD : bouger     Souris : viser et tirer     Échap : pause", 620, 13, new Color(165, 178, 195));
    }

    private void drawMenu(Graphics2D g) {
        drawCentered(g, "SURVIVAL GAME", 140, 42, Color.WHITE);
        drawCentered(g, "ÉDITION ARÈNE", 180, 20, new Color(128, 168, 198));
        drawButtons(g, new String[]{"Continuer", "Jouer", "Meilleurs scores", "Commandes", "Quitter"},
                365, 225, 60);
        drawCentered(g, "Survivez. Renforcez les barricades. Tenez le siège.", 560, 17, new Color(155, 171, 190));
    }

    private void drawShop(Graphics2D g, GameState state) {
        drawOverlay(g, "LA VAGUE EST TERMINÉE", "Crédits disponibles : " + state.getPlayer().getCredits());
        button(g, "Mitraillette — 35 crédits", 180, 290, 280, 54);
        button(g, "Fusil puissant — 55 crédits", 500, 290, 280, 54);
        button(g, "+ dégâts — 30 crédits", 180, 360, 280, 54);
        button(g, "+ cadence — 30 crédits", 500, 360, 280, 54);
        button(g, "Soin — 25 crédits", 340, 430, 280, 54);
        button(g, "Vague suivante", 340, 500, 280, 54);
    }

    private void drawGameOver(Graphics2D g, GameState state) {
        drawGame(g, state);
        drawOverlay(g, "GAME OVER", "Score : " + state.getPlayer().getScore() + "  •  Vague : " + state.getWave());
        drawButtons(g, new String[]{"Enregistrer le score", "Recommencer", "Menu"}, 340, 390, 65);
    }

    private void drawScores(Graphics2D g, List<ScoreEntry> scores, String error) {
        drawCentered(g, "MEILLEURS SCORES", 135, 36, Color.WHITE);
        if (error != null) drawCentered(g, error, 220, 16, new Color(255, 160, 150));
        int y = 225;
        for (ScoreEntry entry : scores) {
            drawText(g, entry.name(), 300, y, 20, Color.WHITE, false);
            drawText(g, String.valueOf(entry.score()), 630, y, 20, new Color(255, 215, 105), false);
            y += 38;
        }
        if (scores.isEmpty() && error == null) drawCentered(g, "Aucun score enregistré.", 240, 18, Color.LIGHT_GRAY);
        button(g, "Retour", 365, 530, 230, 52);
    }

    private void drawHelp(Graphics2D g) {
        drawCentered(g, "COMMANDES", 135, 36, Color.WHITE);
        String[] lines = {"ZQSD ou WASD : déplacement", "Souris : viser — clic gauche maintenu : tirer",
                "Échap : pause et reprise", "Les barricades de fenêtres peuvent être détruites",
                "Entre les vagues : achetez armes, bonus et soins"};
        int y = 260;
        for (String line : lines) { drawCentered(g, line, y, 18, new Color(220, 229, 240)); y += 42; }
        button(g, "Retour", 365, 530, 230, 52);
    }

    private void drawOverlay(Graphics2D g, String title, String subtitle) {
        g.setColor(new Color(8, 12, 20, 225));
        g.fillRoundRect(100, 175, 760, 390, 18, 18);
        drawCentered(g, title, 235, 30, Color.WHITE);
        drawCentered(g, subtitle, 270, 17, new Color(180, 195, 214));
    }

    private void drawButtons(Graphics2D g, String[] labels, int x, int y, int gap) {
        for (int i = 0; i < labels.length; i++) button(g, labels[i], x, y + i * gap, 230, 52);
    }

    private void button(Graphics2D g, String text, int x, int y, int width, int height) {
        g.setColor(new Color(45, 79, 111));
        g.fillRoundRect(x, y, width, height, 12, 12);
        g.setColor(new Color(99, 154, 198));
        g.drawRoundRect(x, y, width, height, 12, 12);
        drawText(g, text, x + width / 2, y + height / 2 + 6, 17, Color.WHITE, true);
    }

    private void drawSprite(Graphics2D g, Sprite sprite, int x, int y, int width, int height) {
        BufferedImage image = sprites.get(sprite);
        Object oldInterpolation = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(image, x, y, width, height, null);
        if (oldInterpolation != null) g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterpolation);
    }

    private void drawCentered(Graphics2D g, String text, int y, int size, Color color) {
        drawText(g, text, WIDTH / 2, y, size, color, true);
    }

    private void drawText(Graphics2D g, String text, int x, int y, int size, Color color, boolean centered) {
        g.setFont(new Font("SansSerif", Font.BOLD, size));
        g.setColor(color);
        int drawX = centered ? x - g.getFontMetrics().stringWidth(text) / 2 : x;
        g.drawString(text, drawX, y);
    }
}
