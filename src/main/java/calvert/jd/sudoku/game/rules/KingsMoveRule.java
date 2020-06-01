package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class KingsMoveRule extends Rule {

    @Override
    public List<Cell> getVisibleCells(GameState gameState, Cell cell) {
        return gameState.getCells().stream()
            .filter(someCell -> {
                int iDiff = abs(cell.getI() - someCell.getI());
                int jDiff = abs(cell.getJ() - someCell.getJ());
                return iDiff == 1 || jDiff == 1;
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
}
