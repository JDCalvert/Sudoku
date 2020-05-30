package calvert.jd.sudoku.game.logic;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;

import static calvert.jd.sudoku.game.logic.LogicStage.LogicStageIdentifier.SINGLE_CELL_ELIMINATION;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Any cell that is visible to this cell by any rule
 */
public class SingleCellElimination extends LogicStage {

    @Override
    public void processCellUpdate(GameState gameState, Cell cell) {
        if (isValidForCell(cell)) {
            gameState.addToProcessQueue(cell, SINGLE_CELL_ELIMINATION);
        }
    }

    @Override
    public boolean isValidForCell(Cell cell) {
        return nonNull(cell.getValue());
    }

    @Override
    public void runLogic(GameState gameState, Cell cell) {
        // If somehow this cell doesn't have a value, then we can skip this logic.
        if (isNull(cell.getValue())) {
            return;
        }

        gameState.setSelectedCell(cell);
        gameState.update();

        gameState.getRules().forEach(rule -> {
            List<Cell> visibleCells = rule.getVisibleCells(gameState, cell);
            gameState.setCalculationCells(visibleCells);
            gameState.update();

            boolean updatedCells = visibleCells.stream()
                .map(visibleCell -> visibleCell.removePossibleValues(rule.getPossibilitiesToEliminate(cell)))
                .reduce(false, (a, b) -> a || b);

            if (updatedCells) {
                gameState.update();
            }
        });
    }
}
