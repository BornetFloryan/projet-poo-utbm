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

    /** Crée une partie et place le joueur au centre de la carte.
     * @param map carte initiale
     */
    public GameState(GameMap map) {
        this.map = map;
        player = new Player(map.getPixelWidth() / 2.0, map.getPixelHeight() / 2.0);
    }
    /** Retourne le joueur de la partie.
     * @return joueur
     */
    public Player getPlayer() { return player; }
    /** Retourne la carte active.
     * @return carte
     */
    public GameMap getMap() { return map; }
    /** Retourne les ennemis actifs.
     * @return liste modifiable des ennemis
     */
    public List<Enemy> getEnemies() { return enemies; }
    /** Retourne les projectiles actifs.
     * @return liste modifiable des projectiles
     */
    public List<Projectile> getProjectiles() { return projectiles; }
    /** Retourne le numéro de la vague.
     * @return numéro de vague, à partir de un
     */
    public int getWave() { return wave; }
    /** Passe à la vague suivante. */
    public void advanceWave() { wave++; }

    /** Définit le numéro de vague lors du chargement d'une partie.
     * @param wave numéro de vague strictement positif
     */
    public void setWave(int wave) {
        if (wave < 1) throw new IllegalArgumentException("La vague doit être positive.");
        this.wave = wave;
    }

    /** Remplace la carte et recentre le joueur en supprimant les tirs actifs.
     * @param map nouvelle carte
     */
    public void changeMap(GameMap map) {
        this.map = map;
        player.setPosition(map.getPixelWidth() / 2.0, map.getPixelHeight() / 2.0);
        projectiles.clear();
    }
}
