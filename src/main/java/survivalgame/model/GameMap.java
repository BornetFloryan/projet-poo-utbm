package survivalgame.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Arène en grille, obstacles, entrées de fenêtres et navigation BFS. */
public final class GameMap {
    public static final int TILE_SIZE = 32;
    public static final int COLUMNS = 22;
    public static final int ROWS = 14;

    private final long seed;
    private final boolean[][] walls = new boolean[ROWS][COLUMNS];
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<SpawnPoint> entrances = new ArrayList<>();

    public GameMap(long seed) { this.seed = seed; }
    public long getSeed() { return seed; }
    public int getPixelWidth() { return COLUMNS * TILE_SIZE; }
    public int getPixelHeight() { return ROWS * TILE_SIZE; }
    public List<Obstacle> getObstacles() { return Collections.unmodifiableList(obstacles); }
    public List<SpawnPoint> getEntrances() { return Collections.unmodifiableList(entrances); }

    public void addWall(int column, int row) {
        addWall(column, row, false);
    }

    public void addBoundaryWall(int column, int row) {
        addWall(column, row, true);
    }

    private void addWall(int column, int row, boolean boundary) {
        if (!inside(column, row) || walls[row][column]) return;
        walls[row][column] = true;
        obstacles.add(new Obstacle(column, row, boundary));
    }

    public void addBarricade(int column, int row) {
        if (inside(column, row)) obstacles.add(new Barricade(column, row));
    }

    public void addEntrance(int column, int row) {
        entrances.add(new SpawnPoint(column, row, SpawnType.WINDOW));
    }

    public void addLadderSpawn(int column, int row) {
        entrances.add(new SpawnPoint(column, row, SpawnType.LADDER));
    }

    public boolean isWall(int column, int row) {
        return !inside(column, row) || walls[row][column];
    }

    public boolean isBlocked(double x, double y, double radius) {
        return solidAt(x - radius, y - radius) || solidAt(x + radius, y - radius)
                || solidAt(x - radius, y + radius) || solidAt(x + radius, y + radius);
    }

    private boolean solidAt(double x, double y) {
        int column = (int) (x / TILE_SIZE);
        int row = (int) (y / TILE_SIZE);
        if (isWall(column, row)) return true;
        return obstacles.stream().anyMatch(o -> o.getColumn() == column && o.getRow() == row);
    }

    public boolean hasBarricade(int column, int row) {
        return obstacles.stream().anyMatch(o -> o.getColumn() == column && o.getRow() == row
                && o instanceof Barricade);
    }

    public void damageBarricade(int column, int row, int damage) {
        obstacles.removeIf(o -> o.getColumn() == column && o.getRow() == row
                && o instanceof Barricade barricade && barricade.takeDamage(damage));
    }

    /** Construit les distances depuis la position du joueur, en traitant les barricades comme franchissables. */
    public int[][] distanceField(int startColumn, int startRow) {
        int[][] distances = new int[ROWS][COLUMNS];
        for (int[] row : distances) java.util.Arrays.fill(row, -1);
        if (isWall(startColumn, startRow)) return distances;

        ArrayDeque<SpawnPoint> queue = new ArrayDeque<>();
        queue.add(new SpawnPoint(startColumn, startRow, SpawnType.WINDOW));
        distances[startRow][startColumn] = 0;
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            SpawnPoint current = queue.removeFirst();
            for (int[] direction : directions) {
                int x = current.column() + direction[0];
                int y = current.row() + direction[1];
                if (isWall(x, y) || hasNonBarricadeObstacle(x, y) || distances[y][x] >= 0) continue;
                distances[y][x] = distances[current.row()][current.column()] + 1;
                queue.addLast(new SpawnPoint(x, y, SpawnType.WINDOW));
            }
        }
        return distances;
    }

    private boolean hasNonBarricadeObstacle(int x, int y) {
        return obstacles.stream().anyMatch(o -> o.getColumn() == x && o.getRow() == y
                && !(o instanceof Barricade));
    }

    private boolean inside(int column, int row) {
        return column >= 0 && row >= 0 && column < COLUMNS && row < ROWS;
    }

    /** Coordonnées en cases pour un point d'apparition. */
    public enum SpawnType { WINDOW, LADDER }

    /** Coordonnées et type d'un point d'apparition. */
    public record SpawnPoint(int column, int row, SpawnType type) { }
}
