package calvert.jd.sudoku.game.logic.logicstages;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.logic.LogicConstraint;
import calvert.jd.sudoku.game.logic.LogicStage;
import calvert.jd.sudoku.game.rules.Rule;
import calvert.jd.sudoku.game.util.CellUpdate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static calvert.jd.sudoku.game.logic.LogicStageIdentifier.CENTRE_REGION_MAGIC_SQUARE;
import static calvert.jd.sudoku.game.rules.Rule.RuleIdentifier.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

/**
 * The centre region is a magic square, meaning that all values are distinct, and each row, column, and leading diagonal add up to the same number. Since the numbers must be
 * 1 to 9, each row, column, and leading diagonal must add up to 15.
 * <p>
 * First, we enumerate how many ways there are to make 15 using three distinct numbers from 1 to 9. Each cell can then only contain possibilities where that possibility appears
 * in the same number of ways to make 15 as the number of rows, columns, and diagonals it appears in (for example, a corner square is in a row, a column, and one leading diagonal,
 * so it can only contain possibilities that appear in exactly three of the ways to make 15.
 * <p>
 * Also, if a cell has a set value then we can eliminate any possibilities in cells that cannot add up to 15.
 */
public class CentreRegionMagicSquare extends LogicStage {

    public final List<List<Integer>> enumerations = new ArrayList<>();

    public CentreRegionMagicSquare() {
        for (int i = 1; i <= 9; i++) {
            for (int j = i + 1; j <= 9; j++) {
                for (int k = j + 1; k <= 9; k++) {
                    if (i + j + k == 15) {
                        this.enumerations.add(asList(i, j, k));
                    }
                }
            }
        }
    }

    @Override
    public void processCellUpdate(GameState gameState, CellUpdate cellUpdate) {
        Cell cell = cellUpdate.getCell();
        if (isValidForCell(cell)) {
            Stream.of(SUDOKU_ROW_RULE, SUDOKU_COLUMN_RULE, LEADING_DIAGONAL_DOWN_RULE, LEADING_DIAGONAL_UP_RULE)
                .map(Rule.RuleIdentifier::getRule)
                .map(rule -> rule.getVisibleCells(gameState, cell))
                .flatMap(Collection::stream)
                .distinct()
                .filter(this::isValidForCell)
                .forEach(magicSquareCell -> gameState.addToProcessQueue(CENTRE_REGION_MAGIC_SQUARE, new LogicConstraint(magicSquareCell)));
        }
    }

    @Override
    public boolean isValidForCell(Cell cell) {
        return cellInMagicSquare(cell) && isNull(cell.getValue());
    }

    @Override
    public void runLogic(GameState gameState, LogicConstraint logicConstraint) {
        Cell cell = logicConstraint.getCell();
        if (!isValidForCell(cell)) {
            return;
        }

        gameState.setSelectedCell(cell);
        gameState.update();

        // Find the rules that apply to this cell
        List<Rule> rules = Stream.of(SUDOKU_ROW_RULE, SUDOKU_COLUMN_RULE, LEADING_DIAGONAL_DOWN_RULE, LEADING_DIAGONAL_UP_RULE)
            .map(Rule.RuleIdentifier::getRule)
            .filter(rule -> rule.appliesToCell(cell))
            .collect(Collectors.toList());


        // Remove any possibilities that don't appear in the same number of enumerations as rules
        List<Integer> possibilitiesToRemove = cell.getPossibleValues().stream()
            .filter(possibleValue -> this.enumerations.stream().filter(enumeration -> enumeration.contains(possibleValue)).count() != rules.size())
            .collect(Collectors.toList());

        if (cell.removePossibleValues(possibilitiesToRemove)) {
            gameState.update();
        }

        // For each rule, check the possibilities (or value) in the other cells within the rule. We can eliminate any possibilities from this cell that cannot be used to add up
        // to 15 with any of the other cells in the rule.
        List<Integer> possibilitiesThatCannotAddUp = rules.stream()
            .flatMap(rule -> {
                    List<Cell> visibleCells = rule.getVisibleCells(gameState, cell).stream()
                        .filter(this::cellInMagicSquare)
                        .collect(Collectors.toList());

                    gameState.setCalculationCells(visibleCells);
                    gameState.update();

                    List<List<Integer>> visibleCellsPossibilities = visibleCells.stream()
                        .map(visibleCell ->
                            Optional.ofNullable(visibleCell.getValue())
                                .map(Collections::singletonList)
                                .orElse(visibleCell.getPossibleValues())
                        )
                        .collect(Collectors.toList());

                    return cell.getPossibleValues().stream()
                        .filter(possibleValue -> {
                            // Start with one possibility, zero. For each visible cell, and for each possible value in that cell, create a new total which is the previous total
                            // plus the visible cell possibility. This gives us a list of all the possible totals of those cells, so we can eliminate any possibilities from this
                            // cell that cannot make 15 when added to any of those possible totals.
                            List<Integer> totals = singletonList(0);
                            for (List<Integer> visibleCellPossibilities : visibleCellsPossibilities) {
                                List<Integer> newTotals = new ArrayList<>();
                                for (Integer totalAddition : totals) {
                                    for (Integer visibleCellPossibility : visibleCellPossibilities) {
                                        newTotals.add(totalAddition + visibleCellPossibility);
                                    }
                                }
                                totals = newTotals;
                            }

                            return totals.stream().distinct().noneMatch(totalAddition -> totalAddition + possibleValue == 15);
                        });
                }
            )
            .collect(Collectors.toList());

        if (cell.removePossibleValues(possibilitiesThatCannotAddUp)) {
            gameState.update();
        }
    }

    private boolean cellInMagicSquare(Cell cell) {
        return cell.getI() / 3 == 1 && cell.getJ() / 3 == 1;
    }
}
