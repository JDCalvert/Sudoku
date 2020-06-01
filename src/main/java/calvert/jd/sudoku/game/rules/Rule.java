package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;

import static java.util.Collections.singletonList;

public abstract class Rule {

    /**
     * Find the cells visible to the given cell by this rule
     *
     * @param gameState The current game state
     * @param cell      The cell to be processed
     * @return A list of cells visible to the cell being processed by this rule
     */
    public abstract List<Cell> getVisibleCells(GameState gameState, Cell cell);

    /**
     * Whether the rule is inclusive. It is inclusive if, from any given cell, any cell visible by this rule can see all other visible cells.
     *
     * @return Whether the rule is inclusive
     */
    public abstract boolean isInclusive();

    /**
     * Whether the rule applies to a given cell. For example, the leading diagonal rule only applies to cells on the leading diagonal.
     *
     * @param cell The cell being processed
     * @return Whether the rule applies to the given cell
     */
    public abstract boolean appliesToCell(Cell cell);

    /**
     * Calculate the possibilities to be eliminated by this rule.
     * By default, this is the same as the values passed in (i.e. a cell should not be able to see any cell with the same value). This can be overridden for rules that differ,
     *
     * @param valuesToCheck The values to check the rule against.
     * @return Which possibilities should be eliminated from visible cells
     */
    public List<Integer> getPossibilitiesToEliminate(List<Integer> valuesToCheck) {
        return valuesToCheck;
    }

    public final List<Integer> getPossibilitiesToEliminate(Integer valueToCheck) {
        return getPossibilitiesToEliminate(singletonList(valueToCheck));
    }

    public enum RuleIdentifier {
        SUDOKU_ROW_RULE(new SudokuRowRule()),
        SUDOKU_COLUMN_RULE(new SudokuColumnRule()),
        SUDOKU_REGION_RULE(new SudokuRegionRule()),
        LEADING_DIAGONAL_DOWN_RULE(new LeadingDiagonalDownRule()),
        LEADING_DIAGONAL_UP_RULE(new LeadingDiagonalUpRule()),
        KNIGHTS_MOVE_RULE(new KnightsMoveRule()),
        KINGS_MOVE_RULE(new KingsMoveRule());

        private final Rule rule;

        RuleIdentifier(Rule rule) {
            this.rule = rule;
        }

        public Rule getRule() {
            return this.rule;
        }
    }
}
