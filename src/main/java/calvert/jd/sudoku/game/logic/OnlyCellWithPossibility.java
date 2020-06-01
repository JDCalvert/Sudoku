package calvert.jd.sudoku.game.logic;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.rules.Rule;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static calvert.jd.sudoku.game.logic.LogicStage.LogicStageIdentifier.ONLY_CELL_WITH_POSSIBILITY;
import static java.util.Objects.isNull;

/**
 * If a cell is the only one in an inclusive rule with a particular possible value, then the cell must have that value.
 */
public class OnlyCellWithPossibility extends LogicStage {

    @Override
    public void processCellUpdate(GameState gameState, Cell cell) {
        if (!isValidForCell(cell)) {
            return;
        }

        gameState.addToProcessQueue(cell, ONLY_CELL_WITH_POSSIBILITY);

        gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .map(rule -> rule.getVisibleCells(gameState, cell))
            .flatMap(Collection::stream)
            .distinct()
            .forEach(visibleCell -> gameState.addToProcessQueue(visibleCell, ONLY_CELL_WITH_POSSIBILITY));
    }

    @Override
    public boolean isValidForCell(Cell cell) {
        return isNull(cell.getValue());
    }

    @Override
    public void runLogic(GameState gameState, Cell cell) {
        // If this cell already has a value, we can skip this logic.
        if (!isValidForCell(cell)) {
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

            List<Integer> values = cell.getPossibleValues().stream()
                .filter(possibleValue ->
                    visibleCells.stream().noneMatch(
                        visibleCell -> visibleCell.getPossibleValues().contains(possibleValue) || possibleValue.equals(visibleCell.getValue())
                    )
                )
                .collect(Collectors.toList());

            // We should have found one or zero values. If we got more than one, something is wrong. If we got exactly one value, then this cell must have that value
            if (values.size() > 1) {
                gameState.setErrorCell(cell);
                return;
            } else if (!values.isEmpty()) {
                cell.setValue(values.get(0));
                gameState.update();
                return;
            }
        }
    }
}
