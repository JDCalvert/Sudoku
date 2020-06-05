package calvert.jd.sudoku.game.logic.logicstages;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.logic.LogicConstraint;
import calvert.jd.sudoku.game.logic.LogicStage;
import calvert.jd.sudoku.game.util.CellUpdate;

import java.util.List;

import static calvert.jd.sudoku.game.logic.LogicStageIdentifier.SINGLE_CELL_ELIMINATION;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

/**
 * Any cell that is visible to this cell by any rule
 */
public class SingleCellElimination extends LogicStage {

    @Override
    public void processCellUpdate(GameState gameState, CellUpdate cellUpdate) {
        Cell cell = cellUpdate.getCell();
        if (nonNull(cell)) {
            gameState.addToProcessQueue(SINGLE_CELL_ELIMINATION, LogicConstraint.builder().cell(cell).build());
        }
    }

    @Override
    public boolean isValidForCell(LogicConstraint constraint) {
        return nonNull(constraint.getCell().getValue());
    }

    @Override
    public void runLogic(GameState gameState, LogicConstraint constraint) {
        Cell cell = constraint.getCell();

        gameState.setSelectedCell(cell);

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
