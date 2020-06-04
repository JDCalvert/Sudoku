package calvert.jd.sudoku.game.logic;

import calvert.jd.sudoku.game.Cell;
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
    public abstract boolean isValidForCell(Cell cell);

    /**
     * Run the piece of logic on the given cell.
     *
     * @param cell the cell to start our logic on
     */
    public abstract void runLogic(GameState gameState, Cell cell);

    /**
     * Enumerate the logic stages.
     */
    public enum LogicStageIdentifier {
        SINGLE_CELL_ELIMINATION(new SingleCellElimination(), false),
        CENTRE_REGION_MAGIC_SQUARE(new CentreRegionMagicSquare(), true),
        SHARED_POSSIBILITIES_ELIMINATION(new SharedPossibilitiesElimination(), false),
        SAME_POSSIBILITIES_ELIMINATION(new SamePossibilitiesElimination(), false),
        ONLY_CELL_WITH_POSSIBILITY(new OnlyCellWithPossibility(), false),
        MULTIPLE_CELL_ELIMINATION(new MultipleCellElimination(), false);

        private final LogicStage logicStage;
        private final boolean isConstraint;

        LogicStageIdentifier(LogicStage logicStage, boolean isConstraint) {
            this.logicStage = logicStage;
            this.isConstraint = isConstraint;
        }

        public LogicStage getLogicStage() {
            return this.logicStage;
        }

        public boolean isConstraint() {
            return this.isConstraint;
        }
    }
}
