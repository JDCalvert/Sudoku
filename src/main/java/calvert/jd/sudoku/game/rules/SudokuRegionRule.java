package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.List;
import java.util.stream.Collectors;

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
}
