package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;

public abstract class Rule {

    /**
     * Find the cells visible to the given cell by this rule
     *
     * @param gameState The current game state
     * @param cell      The cell to be processed
     * @return A list of cells visible to the cell being processed by this rule
     */
    public abstract List<Cell> getVisibleCells(GameState gameState, Cell cell);

    /**
     * Whether the rule is inclusive. It is inclusive if, from any given cell, any cell visible by this rule can see
     * all other visible cells.
     *
     * @return whether the rule is inclusive.
     */
    public abstract boolean isInclusive();

    /**
     * Calculate the possibilities to be eliminated by this rule
     *
     * @param cell The cell to be processed
     * @return Which possibilities should be eliminated from visible cells
     */
    public abstract List<Integer> getPossibilitiesToEliminate(Cell cell);
}
