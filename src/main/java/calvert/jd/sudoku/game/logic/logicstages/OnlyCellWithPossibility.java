package calvert.jd.sudoku.game.logic.logicstages;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.logic.LogicConstraint;
import calvert.jd.sudoku.game.logic.LogicStage;
import calvert.jd.sudoku.game.rules.Rule;
import calvert.jd.sudoku.game.util.CellUpdate;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static calvert.jd.sudoku.game.logic.LogicStageIdentifier.ONLY_CELL_WITH_POSSIBILITY;
import static java.util.Objects.isNull;

/**
 * If a cell is the only one in an inclusive rule with a particular possible value, then the cell must have that value.
 */
public class OnlyCellWithPossibility extends LogicStage {

    @Override
    public void processCellUpdate(GameState gameState, CellUpdate cellUpdate) {
        Cell cell = cellUpdate.getCell();
        if (!isValidForCell(cell)) {
            return;
        }

        gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .map(rule -> rule.getVisibleCells(gameState, cell))
            .flatMap(Collection::stream)
            .distinct()
            .filter(visibleCell -> visibleCell.hasAnyPossibility(cellUpdate.getRemovedPossibilities()))
            .forEach(visibleCell ->
                visibleCell.getPossibleValues().stream()
                    .filter(possibleValue -> cellUpdate.getRemovedPossibilities().contains(possibleValue))
                    .forEach(possibleValue -> gameState.addToProcessQueue(ONLY_CELL_WITH_POSSIBILITY, new LogicConstraint(visibleCell, possibleValue)))
            );
    }

    @Override
    public boolean isValidForCell(Cell cell) {
        return isNull(cell.getValue());
    }

    @Override
    public void runLogic(GameState gameState, LogicConstraint constraint) {
        Cell cell = constraint.getCell();
        Integer possibleValue = constraint.getValue();

        // If this cell already has a possibleValue, we can skip this logic.
        if (!isValidForCell(cell) || !cell.getPossibleValues().contains(possibleValue)) {
            return;
        }

        gameState.setSelectedCell(cell);
        gameState.update();

        List<Rule> rules = gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .filter(Rule::isInclusive)
            .collect(Collectors.toList());

        for (Rule rule : rules) {
            List<Cell> visibleCells = rule.getVisibleCells(gameState, cell);
            gameState.setCalculationCells(visibleCells);
            gameState.update();

            if (visibleCells.stream().noneMatch(visibleCell -> visibleCell.getPossibleValues().contains(possibleValue) || possibleValue.equals(visibleCell.getValue()))) {
                cell.setValue(possibleValue);
                gameState.update();
                return;
            }
        }
    }
}
