package survivalgame.model;

import java.util.Random;

/** Gère les vagues et espace les apparitions pour éviter les groupes trop denses. */
public final class WaveManager {
    private Random random;
    private int remainingToSpawn;
    private long lastSpawnTime;

    /** Crée le gestionnaire des vagues. */
    public WaveManager() { }

    /** Initialise la séquence d'apparition d'une vague.
     * @param state état de partie actif
     */
    public void startWave(GameState state) {
        random = new Random(state.getMap().getSeed() + state.getWave() * 7919L);
        remainingToSpawn = Math.min(5 + state.getWave() * 2, 32);
        lastSpawnTime = 0;
    }

    /** Retourne le nombre d'ennemis encore attendus dans la vague.
     * @return nombre d'apparitions restantes
     */
    public int getRemainingToSpawn() { return remainingToSpawn; }

    /** Restaure le compteur d'apparitions après le chargement d'une partie.
     * @param remainingToSpawn nombre d'ennemis restant à faire apparaître
     */
    public void restoreRemainingToSpawn(int remainingToSpawn) {
        if (remainingToSpawn < 0 || remainingToSpawn > 32) {
            throw new IllegalArgumentException("Compteur d'apparitions invalide.");
        }
        this.remainingToSpawn = remainingToSpawn;
        lastSpawnTime = 0;
    }

    /** Fait apparaître un ennemi lorsque la cadence et la limite le permettent.
     * @param state état de partie à modifier
     */
    public void update(GameState state) {
        long now = System.currentTimeMillis();
        if (remainingToSpawn == 0 || state.getEnemies().size() >= 14
                || (lastSpawnTime != 0 && now - lastSpawnTime < 430)) return;
        spawnEnemy(state);
        lastSpawnTime = now;
        remainingToSpawn--;
    }

    private void spawnEnemy(GameState state) {
        GameMap map = state.getMap();
        GameMap.SpawnPoint entrance = map.getEntrances().get(random.nextInt(map.getEntrances().size()));
        double x = (entrance.column() + 0.5) * GameMap.TILE_SIZE;
        double y = (entrance.row() + 0.5) * GameMap.TILE_SIZE;
        if (random.nextDouble() < Math.min(0.18 + state.getWave() * 0.035, 0.55)) {
            state.getEnemies().add(new FastZombie(x, y));
        } else {
            state.getEnemies().add(new NormalZombie(x, y));
        }
    }
}
