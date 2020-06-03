package calvert.jd.sudoku.game.logic;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.util.CellUpdate;

import java.util.List;

import static calvert.jd.sudoku.game.logic.LogicStage.LogicStageIdentifier.SINGLE_CELL_ELIMINATION;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

/**
 * Any cell that is visible to this cell by any rule
 */
public class SingleCellElimination extends LogicStage {

    @Override
    public void processCellUpdate(GameState gameState, CellUpdate cellUpdate) {
        Cell cell = cellUpdate.getCell();
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
        if (!isValidForCell(cell)) {
            return;
        }

        gameState.setSelectedCell(cell);
        gameState.update();

        gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .forEach(rule -> {
                List<Cell> visibleCells = rule.getVisibleCells(gameState, cell);
                gameState.setCalculationCells(visibleCells);
                gameState.update();

                boolean updatedCells = visibleCells.stream()
                    .map(visibleCell -> visibleCell.removePossibleValues(rule.getPossibilitiesToEliminate(cell.getValue())))
                    .reduce(false, (a, b) -> a || b);

                if (updatedCells) {
                    gameState.update();
                }
            });

        gameState.setCalculationCells(emptyList());
    }
}
