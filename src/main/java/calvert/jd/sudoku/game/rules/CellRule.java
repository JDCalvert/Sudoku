package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

public abstract class CellRule {

    /**
     * Run the rule, updating the game state as it goes.
     *
     * @param gameState The current game state
     * @param cell      The cell to be processed
     * @return whether the rule changed the game state in any way
     */
    public abstract boolean runRule(GameState gameState, Cell cell);
}
