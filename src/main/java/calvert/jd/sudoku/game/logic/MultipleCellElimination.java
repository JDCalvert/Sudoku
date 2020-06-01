package calvert.jd.sudoku.game.logic;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.rules.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static calvert.jd.sudoku.game.logic.LogicStage.LogicStageIdentifier.MULTIPLE_CELL_ELIMINATION;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * For an inclusive rule, find all the cells that share a possible value. That possible value can be eliminated from
 * cells that all those cells can see (via all rules).
 */
public class MultipleCellElimination extends LogicStage {

    @Override
    public void processCellUpdate(GameState gameState, Cell cell) {
        if (nonNull(cell.getValue())) {
            return;
        }

        gameState.addToProcessQueue(cell, MULTIPLE_CELL_ELIMINATION);

        gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .map(rule -> rule.getVisibleCells(gameState, cell))
            .flatMap(Collection::stream)
            .distinct()
            .forEach(visibleCell -> gameState.addToProcessQueue(visibleCell, MULTIPLE_CELL_ELIMINATION));
    }

    @Override
    public boolean isValidForCell(Cell cell) {
        return isNull(cell.getValue());
    }

    @Override
    public void runLogic(GameState gameState, Cell cell) {
        gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .filter(Rule::isInclusive)
            .forEach(inclusiveRule -> {
                gameState.setSelectedCell(cell);
                gameState.update();

                List<Cell> cellsInRule = inclusiveRule.getVisibleCells(gameState, cell);

                cell.getPossibleValues().forEach(possibleValue -> {
                    gameState.setSelectedCell(cell);
                    gameState.setCalculationCells(cellsInRule);
                    gameState.update();

                    List<Cell> cellsSharingPossibleValue = getCellsSharingValue(cell, cellsInRule, possibleValue);

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

                    gameState.setCalculationCells(cellsVisibleByAll);
                    gameState.update();

                    Boolean updated = cellsVisibleByAll.stream()
                        .map(cellToRemovePossibility -> cellToRemovePossibility.removePossibleValue(possibleValue))
                        .reduce(false, (a, b) -> a || b);
                    if (updated) {
                        gameState.update();
                    }
                });
            });
    }

    /**
     * Find the cells that share the possible value.
     *
     * @param cell          The cell being processed
     * @param cellsInRule   The cell that is visible to the process cell by a particular rule
     * @param possibleValue The possible value to join on
     * @return A list of cells that all have the required possible value, including the cell being processed
     */
    private List<Cell> getCellsSharingValue(Cell cell, List<Cell> cellsInRule, Integer possibleValue) {
        List<Cell> visibleCellsSharingPossibleValue = cellsInRule.stream()
            .filter(visibleCell -> visibleCell.getPossibleValues().contains(possibleValue))
            .collect(Collectors.toList());

        return Stream.of(visibleCellsSharingPossibleValue, singletonList(cell))
            .flatMap(Collection::stream)
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
