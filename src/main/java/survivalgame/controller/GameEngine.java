package survivalgame.controller;

import survivalgame.model.Enemy;
import survivalgame.model.FastZombie;
import survivalgame.model.GameMap;
import survivalgame.model.GameState;
import survivalgame.model.GameStatus;
import survivalgame.model.MapGenerator;
import survivalgame.model.Player;
import survivalgame.model.Projectile;
import survivalgame.model.ShopManager;
import survivalgame.model.WaveManager;
import survivalgame.persistence.ScoreManager;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

/** Coordonne les entrées de jeu, la simulation, les vagues et les achats. */
public final class GameEngine {
    private final MapGenerator mapGenerator = new MapGenerator();
    private final WaveManager waveManager = new WaveManager();
    private final ShopManager shopManager = new ShopManager();
    private final ScoreManager scoreManager = new ScoreManager();
    private GameState state;
    private GameStatus status = GameStatus.MENU;
    private long lastShotTime;

    public GameStatus getStatus() { return status; }
    public GameState getState() { return state; }
    public ScoreManager getScoreManager() { return scoreManager; }
    public void setStatus(GameStatus status) { this.status = status; }

    public void startNewGame() {
        state = new GameState(mapGenerator.generate());
        waveManager.startWave(state);
        status = GameStatus.PLAYING;
        lastShotTime = 0;
    }

    public void startNextWave() {
        state.advanceWave();
        if (state.getWave() % 3 == 0) {
            state.changeMap(generateDifferentMap(state.getMap()));
        }
        waveManager.startWave(state);
        status = GameStatus.PLAYING;
    }

    private GameMap generateDifferentMap(GameMap previousMap) {
        long seed = previousMap.getSeed() + 1;
        GameMap replacement;
        do {
            replacement = mapGenerator.generate(seed++);
        } while (hasSameSpawnLocations(previousMap, replacement));
        return replacement;
    }

    private boolean hasSameSpawnLocations(GameMap first, GameMap second) {
        return first.getEntrances().equals(second.getEntrances());
    }

    public String purchase(ShopManager.Item item) { return shopManager.buy(state, item); }

    /** Avance une image de jeu à partir des contrôles maintenus. */
    public void update(double directionX, double directionY, Point2D aim, boolean shooting) {
        if (status != GameStatus.PLAYING || state == null) return;
        Player player = state.getPlayer();
        player.setFacingAngle(Math.atan2(aim.getY() - player.getY(), aim.getX() - player.getX()));
        movePlayer(player, directionX * player.getSpeed(), directionY * player.getSpeed());
        updateShooting(player, aim, shooting);
        waveManager.update(state);
        int[][] distances = state.getMap().distanceField(
                (int) (player.getX() / GameMap.TILE_SIZE),
                (int) (player.getY() / GameMap.TILE_SIZE));
        updateEnemies(distances);
        updateProjectiles(distances);
        updateStatus(player);
    }

    private void movePlayer(Player player, double dx, double dy) {
        moveOnAxis(player, dx, 0);
        moveOnAxis(player, 0, dy);
    }

    private void moveOnAxis(Player player, double dx, double dy) {
        double oldX = player.getX();
        double oldY = player.getY();
        player.setPosition(oldX + dx, oldY + dy);
        if (state.getMap().isBlocked(player.getX(), player.getY(), player.getRadius())) {
            player.setPosition(oldX, oldY);
        }
    }

    private void updateShooting(Player player, Point2D aim, boolean shooting) {
        long now = System.currentTimeMillis();
        if (!shooting || now - lastShotTime < player.getWeapon().cooldownMs()
                || state.getProjectiles().size() >= 100) return;
        double dx = aim.getX() - player.getX();
        double dy = aim.getY() - player.getY();
        double length = Math.hypot(dx, dy);
        if (length == 0) return;
        state.getProjectiles().add(new Projectile(player.getX(), player.getY(),
                dx / length * 8, dy / length * 8, player.getWeapon().damage()));
        lastShotTime = now;
    }

    private void updateEnemies(int[][] distances) {
        Player player = state.getPlayer();
        long now = System.currentTimeMillis();
        for (Enemy enemy : state.getEnemies()) {
            enemy.update(state, distances);
            if (enemy.getBounds().intersects(player.getBounds())) player.receiveContactDamage(now);
        }
    }

    private void updateProjectiles(int[][] distances) {
        List<Projectile> projectiles = state.getProjectiles();
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update(state, distances);
            if (outsideMap(projectile) || projectile.getRemainingRange() <= 0) {
                iterator.remove();
                continue;
            }
            if (damageObstacle(projectile)) {
                iterator.remove();
                continue;
            }
            Enemy target = findHitEnemy(projectile);
            if (target != null) {
                target.takeDamage(projectile.getDamage());
                iterator.remove();
                if (target.isDead()) rewardAndRemove(target);
            }
        }
    }

    private boolean outsideMap(Projectile projectile) {
        return projectile.getX() < 0 || projectile.getY() < 0
                || projectile.getX() >= state.getMap().getPixelWidth()
                || projectile.getY() >= state.getMap().getPixelHeight();
    }

    private boolean damageObstacle(Projectile projectile) {
        GameMap map = state.getMap();
        int column = (int) (projectile.getX() / GameMap.TILE_SIZE);
        int row = (int) (projectile.getY() / GameMap.TILE_SIZE);
        if (map.hasBarricade(column, row)) {
            map.damageBarricade(column, row, projectile.getDamage());
            return true;
        }
        return map.isBlocked(projectile.getX(), projectile.getY(), 2);
    }

    private Enemy findHitEnemy(Projectile projectile) {
        for (Enemy enemy : state.getEnemies()) {
            if (Math.hypot(projectile.getX() - enemy.getX(),
                    projectile.getY() - enemy.getY()) < 19) return enemy;
        }
        return null;
    }

    private void rewardAndRemove(Enemy enemy) {
        state.getPlayer().addReward(enemy instanceof FastZombie ? 20 : 10,
                enemy instanceof FastZombie ? 12 : 8);
        state.getEnemies().remove(enemy);
    }

    private void updateStatus(Player player) {
        if (player.isDead()) status = GameStatus.GAME_OVER;
        else if (state.getEnemies().isEmpty() && waveManager.getRemainingToSpawn() == 0) {
            status = GameStatus.SHOP;
        }
    }
}
