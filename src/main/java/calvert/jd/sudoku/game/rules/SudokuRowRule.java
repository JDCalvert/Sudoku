package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;
import java.util.stream.Collectors;

import static calvert.jd.sudoku.game.rules.Rule.RuleIdentifier.SUDOKU_ROW_RULE;

/**
 * One of the fundamental Sudoku rules. A cell can see all cells on the same row.
 */
public class SudokuRowRule extends SudokuRule {

    @Override
    public List<Cell> getVisibleCells(GameState gameState, Cell cell) {
        return gameState.getCells().stream()
            .filter(possibleCell -> possibleCell != cell)
            .filter(possibleCell -> possibleCell.getJ() == cell.getJ())
            .collect(Collectors.toList());
    }

    @Override
    public RuleIdentifier getRuleIdentifier() {
        return SUDOKU_ROW_RULE;
    }
}
