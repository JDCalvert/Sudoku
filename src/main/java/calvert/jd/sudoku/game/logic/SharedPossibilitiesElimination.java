package calvert.jd.sudoku.game.logic;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.rules.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static calvert.jd.sudoku.game.logic.LogicStage.LogicStageIdentifier.SHARED_POSSIBILITIES_ELIMINATION;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

/**
 * For an inclusive rule, if X cells all have the same X possibilities, those possibilities can be eliminated from any cells they can all see via any rule.
 */
public class SharedPossibilitiesElimination extends LogicStage {

    @Override
    public void processCellUpdate(GameState gameState, Cell cell) {
        if (!isValidForCell(cell)) {
            return;
        }

        gameState.addToProcessQueue(cell, SHARED_POSSIBILITIES_ELIMINATION);

        gameState.getRules().stream()
            .map(rule -> rule.getVisibleCells(gameState, cell))
            .flatMap(Collection::stream)
            .distinct()
            .forEach(visibleCell -> gameState.addToProcessQueue(visibleCell, SHARED_POSSIBILITIES_ELIMINATION));
    }

    @Override
    public boolean isValidForCell(Cell cell) {
        return isNull(cell.getValue());
    }

    @Override
    public void runLogic(GameState gameState, Cell cell) {
        if (!isValidForCell(cell)) {
            return;
        }

        gameState.getRules().stream()
            .filter(Rule::isInclusive)
            .forEach(inclusiveRule -> {
                /*gameState.setSelectedCell(cell);
                gameState.setCalculationCells(emptyList());
                gameState.update();*/

                List<Cell> cellsInRule = inclusiveRule.getVisibleCells(gameState, cell);
                /*gameState.setCalculationCells(cellsInRule);
                gameState.update();*/

                List<Cell> cellsSharingPossibilities = getCellsSharingPossibilities(cell, cellsInRule);
                if (cellsSharingPossibilities.size() == cell.getPossibleValues().size()) {
                    gameState.setSelectedCells(cellsSharingPossibilities);
                    gameState.setCalculationCells(emptyList());
                    gameState.update();

                    List<Cell> cellsVisibleByAll = cellsSharingPossibilities.stream()
                        .map(visibleCell -> getVisibleCellsForAllRules(gameState, visibleCell))
                        /*.map(visibleCells ->
                            visibleCells.stream()
                                .filter(visibleCell -> !cellsInRule.contains(visibleCell))
                                .collect(Collectors.toList())
                        )*/
                        .reduce(
                            new ArrayList<>(gameState.getCells()),
                            (allCells, someCells) -> {
                                allCells.retainAll(someCells);
                                return allCells;
                            }
                        );

                    gameState.setCalculationCells(cellsVisibleByAll);
                    gameState.update();

                    boolean updated = cellsVisibleByAll.stream()
                        .map(visibleCell -> visibleCell.removePossibleValues(cell.getPossibleValues()))
                        .reduce(false, (a, b) -> a || b);
                    if (updated) {
                        gameState.update();
                    }
                }
            });
    }

    private List<Cell> getCellsSharingPossibilities(Cell cell, List<Cell> cellsInRule) {
        List<Cell> visibleCellsWithSamePossibilities = cellsInRule.stream()
            .filter(cellInRule -> Objects.equals(cell.getPossibleValues(), cellInRule.getPossibleValues()))
            .collect(Collectors.toList());

        return Stream.of(visibleCellsWithSamePossibilities, singletonList(cell))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<Cell> getVisibleCellsForAllRules(GameState gameState, Cell cell) {
        return gameState.getRules().stream()
            .map(rule -> rule.getVisibleCells(gameState, cell))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }
}
