package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;
import java.util.stream.Collectors;

import static calvert.jd.sudoku.game.rules.Rule.RuleIdentifier.KINGS_MOVE_RULE;
import static java.lang.Math.abs;

public class KingsMoveRule extends Rule {

    @Override
    public List<Cell> getVisibleCells(GameState gameState, Cell cell) {
        return gameState.getCells().stream()
            .filter(possibleCell -> possibleCell != cell)
            .filter(possibleCell -> {
                int iDiff = abs(cell.getI() - possibleCell.getI());
                int jDiff = abs(cell.getJ() - possibleCell.getJ());
                return iDiff <= 1 && jDiff <= 1;
            })
            .collect(Collectors.toList());
    }

    @Override
    public boolean isInclusive() {
        return false;
    }

    @Override
    public boolean appliesToCell(Cell cell) {
        return true;
    }

    @Override
    public RuleIdentifier getRuleIdentifier() {
        return KINGS_MOVE_RULE;
    }
}
