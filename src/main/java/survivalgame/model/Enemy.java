package survivalgame.model;

/** Base polymorphe des zombies qui suivent le joueur sur la grille. */
public abstract class Enemy extends Character {
    private long lastBarricadeAttack;

    protected Enemy(double x, double y, int health, double speed) {
        super(x, y, health, speed, 12);
    }

    @Override public void update(GameState state, int[][] distances) {
        int column = (int) (getX() / GameMap.TILE_SIZE);
        int row = (int) (getY() / GameMap.TILE_SIZE);
        int[] next = closestNeighbour(distances, column, row);
        if (next == null) return;

        double targetX = (next[0] + 0.5) * GameMap.TILE_SIZE;
        double targetY = (next[1] + 0.5) * GameMap.TILE_SIZE;
        double dx = targetX - getX();
        double dy = targetY - getY();
        double length = Math.hypot(dx, dy);
        if (length > 1) {
            setFacingAngle(Math.atan2(dy, dx));
            double moveX = dx / length * getSpeed();
            double moveY = dy / length * getSpeed();
            moveOnAxis(state.getMap(), moveX, 0);
            moveOnAxis(state.getMap(), 0, moveY);
        }
        attackBarricadeIfBlocked(state.getMap(), next[0], next[1]);
    }

    private int[] closestNeighbour(int[][] distances, int column, int row) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int bestDistance = distanceAt(distances, column, row);
        int[] best = null;
        for (int[] direction : directions) {
            int x = column + direction[0];
            int y = row + direction[1];
            int distance = distanceAt(distances, x, y);
            if (distance >= 0 && (bestDistance < 0 || distance < bestDistance)) {
                bestDistance = distance;
                best = new int[]{x, y};
            }
        }
        return best;
    }

    private int distanceAt(int[][] distances, int x, int y) {
        if (y < 0 || y >= distances.length || x < 0 || x >= distances[0].length) return -1;
        return distances[y][x];
    }

    private void moveOnAxis(GameMap map, double dx, double dy) {
        double oldX = getX();
        double oldY = getY();
        setX(oldX + dx);
        setY(oldY + dy);
        if (map.isBlocked(getX(), getY(), getRadius())) {
            setX(oldX);
            setY(oldY);
        }
    }

    private void attackBarricadeIfBlocked(GameMap map, int targetColumn, int targetRow) {
        if (!map.hasBarricade(targetColumn, targetRow)) return;
        long now = System.currentTimeMillis();
        if (now - lastBarricadeAttack < 500) return;
        map.damageBarricade(targetColumn, targetRow, 1);
        lastBarricadeAttack = now;
    }
}
