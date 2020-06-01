package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;

/**
 * Abstract class for standard Sudoku rules. These rules are inclusive and eliminate possible values with the same value
 * as the cell.
 */
public abstract class SudokuRule extends Rule {

    @Override
    public boolean isInclusive() {
        return true;
    }

    @Override
    public boolean appliesToCell(Cell cell) {
        return true;
    }
}
