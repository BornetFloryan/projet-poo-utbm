package survivalgame.model;

import java.util.Random;

/** Produit des cartes petites, reproductibles et accessibles. */
public final class MapGenerator {
    public GameMap generate() { return generate(new Random().nextLong()); }

    public GameMap generate(long seed) {
        Random random = new Random(seed);
        for (int attempt = 0; attempt < 40; attempt++) {
            GameMap map = new GameMap(seed);
            buildBoundaryAndWindows(map, random);
            addRandomCover(map, random);
            addLadderSpawn(map, random);
            if (isConnected(map)) return map;
        }
        GameMap fallback = new GameMap(seed);
        buildBoundaryAndWindows(fallback, random);
        addLadderSpawn(fallback, random);
        return fallback;
    }

    private void buildBoundaryAndWindows(GameMap map, Random random) {
        int topColumn = random.nextInt(3, GameMap.COLUMNS - 3);
        int bottomColumn = differentValue(random, 3, GameMap.COLUMNS - 3, topColumn);
        int leftRow = random.nextInt(3, GameMap.ROWS - 3);
        int rightRow = differentValue(random, 3, GameMap.ROWS - 3, leftRow);

        for (int column = 0; column < GameMap.COLUMNS; column++) {
            if (column != topColumn) {
                map.addBoundaryWall(column, 0);
            }
            if (column != bottomColumn) {
                map.addBoundaryWall(column, GameMap.ROWS - 1);
            }
        }
        for (int row = 0; row < GameMap.ROWS; row++) {
            if (row != leftRow) {
                map.addBoundaryWall(0, row);
            }
            if (row != rightRow) {
                map.addBoundaryWall(GameMap.COLUMNS - 1, row);
            }
        }

        map.addEntrance(topColumn, 0);
        map.addEntrance(bottomColumn, GameMap.ROWS - 1);
        map.addEntrance(0, leftRow);
        map.addEntrance(GameMap.COLUMNS - 1, rightRow);
        map.addBarricade(topColumn, 1);
        map.addBarricade(bottomColumn, GameMap.ROWS - 2);
        map.addBarricade(1, leftRow);
        map.addBarricade(GameMap.COLUMNS - 2, rightRow);

        // Side walls make each boarded window a narrow breach point.
        map.addBoundaryWall(topColumn - 1, 1);
        map.addBoundaryWall(topColumn + 1, 1);
        map.addBoundaryWall(bottomColumn - 1, GameMap.ROWS - 2);
        map.addBoundaryWall(bottomColumn + 1, GameMap.ROWS - 2);
        map.addBoundaryWall(1, leftRow - 1);
        map.addBoundaryWall(1, leftRow + 1);
        map.addBoundaryWall(GameMap.COLUMNS - 2, rightRow - 1);
        map.addBoundaryWall(GameMap.COLUMNS - 2, rightRow + 1);
    }

    private int differentValue(Random random, int minimum, int maximum, int excluded) {
        int value;
        do { value = random.nextInt(minimum, maximum); }
        while (value == excluded);
        return value;
    }

    private void addRandomCover(GameMap map, Random random) {
        for (int count = 0; count < 30; count++) {
            int column = 2 + random.nextInt(GameMap.COLUMNS - 4);
            int row = 2 + random.nextInt(GameMap.ROWS - 4);
            if (Math.abs(column - GameMap.COLUMNS / 2) < 3
                    && Math.abs(row - GameMap.ROWS / 2) < 2) continue;
            map.addWall(column, row);
        }
    }

    private void addLadderSpawn(GameMap map, Random random) {
        for (int attempt = 0; attempt < 100; attempt++) {
            int column = random.nextInt(3, GameMap.COLUMNS - 3);
            int row = random.nextInt(3, GameMap.ROWS - 3);
            if (Math.abs(column - GameMap.COLUMNS / 2) < 4
                    && Math.abs(row - GameMap.ROWS / 2) < 3) continue;
            if (!map.isBlocked((column + 0.5) * GameMap.TILE_SIZE,
                    (row + 0.5) * GameMap.TILE_SIZE, 10)) {
                map.addLadderSpawn(column, row);
                return;
            }
        }
        for (int row = 3; row < GameMap.ROWS - 3; row++) {
            for (int column = 3; column < GameMap.COLUMNS - 3; column++) {
                if (!map.isBlocked((column + 0.5) * GameMap.TILE_SIZE,
                        (row + 0.5) * GameMap.TILE_SIZE, 10)) {
                    map.addLadderSpawn(column, row);
                    return;
                }
            }
        }
    }

    private boolean isConnected(GameMap map) {
        int[][] distances = map.distanceField(GameMap.COLUMNS / 2, GameMap.ROWS / 2);
        for (int row = 1; row < GameMap.ROWS - 1; row++) {
            for (int column = 1; column < GameMap.COLUMNS - 1; column++) {
                if (!map.isWall(column, row) && distances[row][column] < 0) return false;
            }
        }
        return true;
    }
}
