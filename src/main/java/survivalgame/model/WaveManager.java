package survivalgame.model;

import java.util.Random;

/** Gère les vagues et espace les apparitions pour éviter les groupes trop denses. */
public final class WaveManager {
    private Random random;
    private int remainingToSpawn;
    private long lastSpawnTime;

    public void startWave(GameState state) {
        random = new Random(state.getMap().getSeed() + state.getWave() * 7919L);
        remainingToSpawn = Math.min(5 + state.getWave() * 2, 32);
        lastSpawnTime = 0;
    }

    public int getRemainingToSpawn() { return remainingToSpawn; }

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
