package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;
import java.util.stream.Collectors;

import static calvert.jd.sudoku.game.rules.Rule.RuleIdentifier.SUDOKU_REGION_RULE;

public class SudokuRegionRule extends SudokuRule {

    @Override
    public List<Cell> getVisibleCells(GameState gameState, Cell cell) {
        int regionI = cell.getI() / 3;
        int regionJ = cell.getJ() / 3;

        return gameState.getCells().stream()
            .filter(possibleCell -> possibleCell != cell)
            .filter(possibleCell -> possibleCell.getI() / 3 == regionI && possibleCell.getJ() / 3 == regionJ)
            .collect(Collectors.toList());
    }

    @Override
    public RuleIdentifier getRuleIdentifier() {
        return SUDOKU_REGION_RULE;
    }
}
