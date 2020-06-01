package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;
import java.util.stream.Collectors;

public class LeadingDiagonalDownRule extends Rule {

    @Override
    public List<Cell> getVisibleCells(GameState gameState, Cell cell) {
        List<Cell> visibleCells = gameState.getCells().stream()
            .filter(visibleCell -> visibleCell.getI() == visibleCell.getJ())
            .collect(Collectors.toList());

        visibleCells.remove(cell);
        return visibleCells;
    }

    @Override
    public boolean isInclusive() {
        return true;
    }

    @Override
    public boolean appliesToCell(Cell cell) {
        return cell.getI() == cell.getJ();
    }
}
