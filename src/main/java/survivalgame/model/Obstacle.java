package survivalgame.model;

/** Obstacle infranchissable et indestructible. */
public class Obstacle extends Element {
    private final boolean boundary;

    /** Crée un obstacle intérieur.
     * @param column colonne de l'obstacle
     * @param row ligne de l'obstacle
     */
    public Obstacle(int column, int row) { this(column, row, false); }
    /** Crée un obstacle avec son statut de limite.
     * @param column colonne de l'obstacle
     * @param row ligne de l'obstacle
     * @param boundary vrai si l'obstacle borde l'arène
     */
    public Obstacle(int column, int row, boolean boundary) {
        super(column, row);
        this.boundary = boundary;
    }
    /** Indique si l'obstacle se trouve sur la limite.
     * @return vrai pour un mur extérieur
     */
    public boolean isBoundary() { return boundary; }
    /** Indique si cet obstacle peut être détruit.
     * @return faux pour un obstacle ordinaire
     */
    public boolean isDestructible() { return false; }
    /** Applique des dégâts si le sous-type est destructible.
     * @param damage dégâts reçus
     * @return vrai si l'obstacle est détruit
     */
    public boolean takeDamage(int damage) { return false; }
}
