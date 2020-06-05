package calvert.jd.sudoku.game.logic.logicstages;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.logic.LogicConstraint;
import calvert.jd.sudoku.game.logic.LogicStage;
import calvert.jd.sudoku.game.rules.Rule;
import calvert.jd.sudoku.game.util.CellUpdate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static calvert.jd.sudoku.game.logic.LogicStageIdentifier.MULTIPLE_CELL_ELIMINATION;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * For an inclusive rule, find all the cells that share a possible value. That possible value can be eliminated from
 * cells that all those cells can see (via all rules).
 */
public class MultipleCellElimination extends LogicStage {

    @Override
    public void processCellUpdate(GameState gameState, CellUpdate cellUpdate) {
        Cell cell = cellUpdate.getCell();
        gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .filter(Rule::isInclusive)
            .forEach(rule -> {
                List<Cell> visibleCells = rule.getVisibleCells(gameState, cell);

                cellUpdate.getRemovedPossibilities().forEach(possibleValue ->
                    visibleCells.stream()
                        .filter(visibleCell -> visibleCell.getPossibleValues().contains(possibleValue))
                        .findFirst()
                        .ifPresent(visibleCell -> gameState.addToProcessQueue(
                            MULTIPLE_CELL_ELIMINATION,
                            LogicConstraint.builder()
                                .cell(visibleCell)
                                .value(possibleValue)
                                .rule(rule)
                                .build()
                        ))
                );
            });
    }

    @Override
    public boolean isValidForCell(LogicConstraint constraint) {
        return constraint.getCell().getPossibleValues().contains(constraint.getValue());
    }

    @Override
    public void runLogic(GameState gameState, LogicConstraint constraint) {
        Cell cell = constraint.getCell();
        Integer possibleValue = constraint.getValue();
        Rule rule = constraint.getRule();

        List<Cell> cellsInRule = rule.getVisibleCells(gameState, cell);

        gameState.setSelectedCell(cell);
        gameState.setCalculationCells(cellsInRule);
        gameState.setCalculationValue(possibleValue);
        gameState.update();

        List<Cell> cellsSharingPossibleValue = getCellsSharingValue(cellsInRule, possibleValue);

        if (cellsSharingPossibleValue.isEmpty()) {
            cell.setValue(possibleValue);
            gameState.update();
        } else if (cellsSharingPossibleValue.stream().allMatch(visibleCell -> cell.compareTo(visibleCell) < 0)) {
            cellsSharingPossibleValue = Stream.of(cellsSharingPossibleValue, singletonList(cell))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

            gameState.setSelectedCells(cellsSharingPossibleValue);
            gameState.setCalculationCells(emptyList());
            gameState.update();

            List<Cell> cellsVisibleByAll = cellsSharingPossibleValue.stream()
                .map(visibleCell -> getVisibleCellsForAllRules(gameState, visibleCell))
                .map(visibleCells ->
                    visibleCells.stream()
                        .filter(visibleCell -> !cellsInRule.contains(visibleCell))
                        .collect(Collectors.toList())
                )
                .reduce(
                    new ArrayList<>(gameState.getCells()),
                    (allCells, someCells) -> {
                        allCells.retainAll(someCells);
                        return allCells;
                    }
                );

            if (!cellsVisibleByAll.isEmpty()) {
                gameState.setCalculationCells(cellsVisibleByAll);
                gameState.update();

                Boolean updated = cellsVisibleByAll.stream()
                    .map(cellToRemovePossibility -> cellToRemovePossibility.removePossibleValue(possibleValue))
                    .reduce(false, (a, b) -> a || b);
                if (updated) {
                    gameState.update();
                }
            }
        }

        gameState.setCalculationValues(emptyList());
    }

    /**
     * Find the cells that share the possible value.
     *
     * @param cellsInRule   The cell that is visible to the process cell by a particular rule
     * @param possibleValue The possible value to join on
     * @return A list of cells that all have the required possible value, including the cell being processed
     */
    private List<Cell> getCellsSharingValue(List<Cell> cellsInRule, Integer possibleValue) {
        return cellsInRule.stream()
            .filter(visibleCell -> visibleCell.getPossibleValues().contains(possibleValue))
            .collect(Collectors.toList());
    }

    /**
     * Find all the cells that are visible to this cell via any rule.
     *
     * @param gameState The current state of the game
     * @param cell      The cell for which to find all visible cells
     * @return A list of cells visible to the cell passed in
     */
    private List<Cell> getVisibleCellsForAllRules(GameState gameState, Cell cell) {
        return gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .map(rule -> rule.getVisibleCells(gameState, cell))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }
}
