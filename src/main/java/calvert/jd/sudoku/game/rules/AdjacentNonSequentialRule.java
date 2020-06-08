package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static calvert.jd.sudoku.game.rules.Rule.RuleIdentifier.ADJACENT_SEQUENTIAL_RULE;
import static java.lang.Math.abs;

public class AdjacentNonSequentialRule extends Rule {

    @Override
    public List<Cell> getVisibleCells(GameState gameState, Cell cell) {
        return gameState.getCells().stream()
            .filter(visibleCell -> {
                int iDiff = abs(visibleCell.getI() - cell.getI());
                int jDiff = abs(visibleCell.getJ() - cell.getJ());
                return (iDiff == 1 && jDiff == 0) || (jDiff == 1 && iDiff == 0);
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
        return ADJACENT_SEQUENTIAL_RULE;
    }

    @Override
    public List<Integer> getPossibilitiesToEliminate(List<Integer> valuesToCheck) {
        return valuesToCheck.stream()
            .flatMap(value -> Stream.of(value - 1, value + 1))
            .distinct()
            .filter(value -> value >= 1 && value <= 9)
            .collect(Collectors.toList());
    }
}
