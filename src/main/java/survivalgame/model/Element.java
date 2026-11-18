package survivalgame.model;

/**
 * Élément fixe de décor positionné sur la grille.  *
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public abstract class Element {
    private final int column;
    private final int row;

    /** Place un élément fixe sur une case.
     * @param column colonne de la grille
     * @param row ligne de la grille
     */
    protected Element(int column, int row) { this.column = column; this.row = row; }
    /** Retourne la colonne de l'élément.
     * @return colonne de grille
     */
    public int getColumn() { return column; }
    /** Retourne la ligne de l'élément.
     * @return ligne de grille
     */
    public int getRow() { return row; }
}
