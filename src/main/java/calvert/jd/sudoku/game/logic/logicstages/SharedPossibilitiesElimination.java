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

import static calvert.jd.sudoku.game.logic.LogicStageIdentifier.SHARED_POSSIBILITIES_ELIMINATION;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

/**
 * For an inclusive rule, if we have X probabilities and we find X cells with those same probabilities (even if they have more), then we can do two things:
 * 1. Remove the other probabilities from those cells.
 * 2. Remove the shared probabilities from all other cells visible to all X cells.
 */
public class SharedPossibilitiesElimination extends LogicStage {

    @Override
    public void processCellUpdate(GameState gameState, CellUpdate cellUpdate) {
        Cell cell = cellUpdate.getCell();
        if (!isValidForCell(cell)) {
            return;
        }

        gameState.addToProcessQueue(SHARED_POSSIBILITIES_ELIMINATION, new LogicConstraint(cell));

        gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .map(rule -> rule.getVisibleCells(gameState, cell))
            .flatMap(Collection::stream)
            .distinct()
            .filter(visibleCell -> visibleCell.hasAnyPossibility(cellUpdate.getRemovedPossibilities()))
            .forEach(visibleCell -> gameState.addToProcessQueue(SHARED_POSSIBILITIES_ELIMINATION, new LogicConstraint(cell)));
    }

    @Override
    public boolean isValidForCell(Cell cell) {
        return isNull(cell.getValue());
    }

    @Override
    public void runLogic(GameState gameState, LogicConstraint constraint) {
        Cell cell = constraint.getCell();
        if (!isValidForCell(cell)) {
            return;
        }

        gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .filter(Rule::isInclusive)
            .forEach(inclusiveRule -> {
                gameState.setSelectedCell(cell);
                gameState.setCalculationCells(emptyList());
                gameState.update();

                List<Cell> cellsInRule = inclusiveRule.getVisibleCells(gameState, cell);
                gameState.setCalculationCells(cellsInRule);
                gameState.update();

                // For this rule, find all cells that have all the same possibilities of this cell (even if they have more possibilities)
                List<Cell> cellsSharingPossibilities = getCellsSharingPossibilities(cell, cellsInRule);

                // If the number of cells we found (including this cell) equals the number of possibilities we have, then those possibilities must exist in these cells
                if (cellsSharingPossibilities.size() == cell.getPossibleValues().size()) {
                    gameState.setSelectedCells(cellsSharingPossibilities);
                    gameState.setCalculationCells(emptyList());
                    gameState.update();

                    // Remove all other possibilities from these cells
                    Boolean updatedCellsSharing = cellsSharingPossibilities.stream().map(visibleCell -> {
                        List<Integer> possibleValuesToRemove = visibleCell.getPossibleValues().stream()
                            .filter(possibleValue -> !cell.getPossibleValues().contains(possibleValue))
                            .collect(Collectors.toList());
                        return visibleCell.removePossibleValues(possibleValuesToRemove);
                    })
                        .reduce(false, (a, b) -> a || b);

                    if (updatedCellsSharing) {
                        gameState.update();
                    }

                    // Find any other cells that all these cells can see, and remove our possibilities from them
                    List<Cell> cellsVisibleByAll = cellsSharingPossibilities.stream()
                        .map(visibleCell -> getVisibleCellsForAllRules(gameState, visibleCell))
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

                        boolean updated = cellsVisibleByAll.stream()
                            .map(visibleCell -> visibleCell.removePossibleValues(cell.getPossibleValues()))
                            .reduce(false, (a, b) -> a || b);
                        if (updated) {
                            gameState.update();
                        }
                    }
                }
            });
    }

    private List<Cell> getCellsSharingPossibilities(Cell cell, List<Cell> cellsInRule) {
        List<Cell> visibleCellsWithSamePossibilities = cellsInRule.stream()
            .filter(cellInRule -> cell.getPossibleValues().stream().anyMatch(possibleValue -> cellInRule.getPossibleValues().contains(possibleValue)))
            .collect(Collectors.toList());

        return Stream.of(visibleCellsWithSamePossibilities, singletonList(cell))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<Cell> getVisibleCellsForAllRules(GameState gameState, Cell cell) {
        return gameState.getRules().stream()
            .filter(rule -> rule.appliesToCell(cell))
            .map(rule -> rule.getVisibleCells(gameState, cell))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }
}
