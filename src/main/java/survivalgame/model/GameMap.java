package survivalgame.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Arène en grille, obstacles, entrées de fenêtres et navigation BFS.  *
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public final class GameMap {
    /** Taille d'une case en pixels. */
    public static final int TILE_SIZE = 32;
    /** Nombre de colonnes de la grille. */
    public static final int COLUMNS = 22;
    /** Nombre de lignes de la grille. */
    public static final int ROWS = 14;

    private final long seed;
    private final boolean[][] walls = new boolean[ROWS][COLUMNS];
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<SpawnPoint> entrances = new ArrayList<>();

    /** Crée une carte vide liée à une graine.
     * @param seed graine permettant de reproduire sa génération
     */
    public GameMap(long seed) { this.seed = seed; }
    /** Retourne la graine de la carte.
     * @return graine de génération
     */
    public long getSeed() { return seed; }
    /** Retourne la largeur de la carte en pixels.
     * @return largeur en pixels
     */
    public int getPixelWidth() { return COLUMNS * TILE_SIZE; }
    /** Retourne la hauteur de la carte en pixels.
     * @return hauteur en pixels
     */
    public int getPixelHeight() { return ROWS * TILE_SIZE; }
    /** Retourne une vue non modifiable des obstacles.
     * @return obstacles de la carte
     */
    public List<Obstacle> getObstacles() { return Collections.unmodifiableList(obstacles); }
    /** Retourne une vue non modifiable des points d'apparition.
     * @return entrées de la carte
     */
    public List<SpawnPoint> getEntrances() { return Collections.unmodifiableList(entrances); }

    /** Ajoute un mur intérieur si la case est libre.
     * @param column colonne de la case
     * @param row ligne de la case
     */
    public void addWall(int column, int row) {
        addWall(column, row, false);
    }

    /** Ajoute un mur au bord extérieur de l'arène.
     * @param column colonne de la case
     * @param row ligne de la case
     */
    public void addBoundaryWall(int column, int row) {
        addWall(column, row, true);
    }

    private void addWall(int column, int row, boolean boundary) {
        if (!inside(column, row) || walls[row][column]) return;
        walls[row][column] = true;
        obstacles.add(new Obstacle(column, row, boundary));
    }

    /** Ajoute une barricade destructible.
     * @param column colonne de la barricade
     * @param row ligne de la barricade
     */
    public void addBarricade(int column, int row) {
        if (inside(column, row)) obstacles.add(new Barricade(column, row));
    }

    /** Ajoute une entrée de fenêtre.
     * @param column colonne de la fenêtre
     * @param row ligne de la fenêtre
     */
    public void addEntrance(int column, int row) {
        entrances.add(new SpawnPoint(column, row, SpawnType.WINDOW));
    }

    /** Ajoute un point d'apparition d'échelle.
     * @param column colonne de l'échelle
     * @param row ligne de l'échelle
     */
    public void addLadderSpawn(int column, int row) {
        entrances.add(new SpawnPoint(column, row, SpawnType.LADDER));
    }

    /** Indique si une case contient un mur ou est hors de la grille.
     * @param column colonne examinée
     * @param row ligne examinée
     * @return vrai si la case est un mur ou hors limites
     */
    public boolean isWall(int column, int row) {
        return !inside(column, row) || walls[row][column];
    }

    /** Indique si un cercle de collision touche un obstacle solide.
     * @param x abscisse du centre
     * @param y ordonnée du centre
     * @param radius rayon du cercle
     * @return vrai si la position est bloquée
     */
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

    /** Indique si une case contient une barricade non détruite.
     * @param column colonne examinée
     * @param row ligne examinée
     * @return vrai si une barricade est présente
     */
    public boolean hasBarricade(int column, int row) {
        return obstacles.stream().anyMatch(o -> o.getColumn() == column && o.getRow() == row
                && o instanceof Barricade);
    }

    /** Applique des dégâts et retire la barricade si elle est détruite.
     * @param column colonne de la barricade
     * @param row ligne de la barricade
     * @param damage quantité de dégâts
     */
    public void damageBarricade(int column, int row, int damage) {
        obstacles.removeIf(o -> o.getColumn() == column && o.getRow() == row
                && o instanceof Barricade barricade && barricade.takeDamage(damage));
    }

    /** Calcule les distances depuis une case, en laissant traverser les barricades.
     * @param startColumn colonne de départ
     * @param startRow ligne de départ
     * @return distances des cases accessibles, -1 pour les autres
     */
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

    /** Origine d'un point d'apparition. */
    public enum SpawnType {
        /** Fenêtre du bord de l'arène. */
        WINDOW,
        /** Échelle intérieure. */
        LADDER
    }

    /** Coordonnées et type d'un point d'apparition.
     * @param column colonne de la grille
     * @param row ligne de la grille
     * @param type origine de l'apparition
     */
    public record SpawnPoint(int column, int row, SpawnType type) { }
}
