package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.util.Collections.emptyList;

public class KnightsMoveRule extends Rule {

    @Override
    public List<Cell> getVisibleCells(GameState gameState, Cell cell) {
        return gameState.getCells().stream()
            .filter(someCell -> {
                int iDiff = abs(cell.getI() - someCell.getI());
                int jDiff = abs(cell.getJ() - someCell.getJ());
                return iDiff * jDiff == 2;
            })
            .collect(Collectors.toList());
    }

    @Override
    public boolean isInclusive() {
        return false;
    }

    @Override
    public List<Integer> getPossibilitiesToEliminate(Cell cell) {
        return Optional.ofNullable(cell.getValue())
            .map(Collections::singletonList)
            .orElse(emptyList());
    }
}
