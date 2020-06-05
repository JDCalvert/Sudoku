package calvert.jd.sudoku.game.logic;

import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.util.CellUpdate;

/**
 * Abstract class to represent some logic to solve the puzzle. Each piece of logic uses one cell as a base to either
 * calculate the values or eliminate possible values from the current cell or other cells.
 */
public abstract class LogicStage {

    /**
     * Given the cell was updated, add cells to the process queue to be processed by this logic.
     *
     * @param gameState  the current game state
     * @param cellUpdate information about the cell update
     */
    public abstract void processCellUpdate(GameState gameState, CellUpdate cellUpdate);

    /**
     * Some logic should only be run on certain cells. The cell might have become invalid for the logic stage by the time it's reached in the queue.
     */
    public abstract boolean isValidForCell(LogicConstraint constraint);

    /**
     * Run the piece of logic on the given logicConstraint.
     *
     * @param constraint information about how to run the logic
     */
    public abstract void runLogic(GameState gameState, LogicConstraint constraint);
}
