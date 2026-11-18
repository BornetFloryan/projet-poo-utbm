package survivalgame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Données détenues par une partie. */
public final class GameState {
    private final Player player;
    private GameMap map;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private int wave = 1;

    public GameState(GameMap map) {
        this.map = map;
        player = new Player(map.getPixelWidth() / 2.0, map.getPixelHeight() / 2.0);
    }
    public Player getPlayer() { return player; }
    public GameMap getMap() { return map; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Projectile> getProjectiles() { return projectiles; }
    public int getWave() { return wave; }
    public void advanceWave() { wave++; }

    /** Définit la vague lors de la restauration d'une progression. */
    public void setWave(int wave) {
        if (wave < 1) throw new IllegalArgumentException("La vague doit être positive.");
        this.wave = wave;
    }
    public void changeMap(GameMap map) {
        this.map = map;
        player.setPosition(map.getPixelWidth() / 2.0, map.getPixelHeight() / 2.0);
        projectiles.clear();
    }
}
