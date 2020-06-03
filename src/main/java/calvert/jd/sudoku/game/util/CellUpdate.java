package calvert.jd.sudoku.game.util;

import calvert.jd.sudoku.game.Cell;

import java.util.List;

public class CellUpdate {
    private final Cell cell;
    private final List<Integer> removedPossibilities;

    public CellUpdate(Cell cell, List<Integer> removedPossibilities) {
        this.cell = cell;
        this.removedPossibilities = removedPossibilities;
    }

    public Cell getCell() {
        return this.cell;
    }

    public List<Integer> getRemovedPossibilities() {
        return this.removedPossibilities;
    }
}
