package survivalgame.model;

/** Élément fixe de décor positionné sur la grille. */
public abstract class Element {
    private final int column;
    private final int row;

    protected Element(int column, int row) { this.column = column; this.row = row; }
    public int getColumn() { return column; }
    public int getRow() { return row; }
}
