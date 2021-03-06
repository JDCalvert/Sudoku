package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;
import java.util.stream.Collectors;

import static calvert.jd.sudoku.game.rules.Rule.RuleIdentifier.SUDOKU_COLUMN_RULE;

public class SudokuColumnRule extends SudokuRule {

    @Override
    public List<Cell> getVisibleCells(GameState gameState, Cell cell) {
        return gameState.getCells().stream()
            .filter(possibleCell -> possibleCell != cell)
            .filter(possibleCell -> possibleCell.getI() == cell.getI())
            .collect(Collectors.toList());
    }

    @Override
    public RuleIdentifier getRuleIdentifier() {
        return SUDOKU_COLUMN_RULE;
    }
}
