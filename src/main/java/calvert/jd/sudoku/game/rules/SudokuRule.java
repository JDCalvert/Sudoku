package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Abstract class for standard Sudoku rules. These rules are inclusive and eliminate possible values with the same value
 * as the cell.
 */
public abstract class SudokuRule extends Rule {

    @Override
    public boolean isInclusive() {
        return true;
    }

    @Override
    public List<Integer> getPossibilitiesToEliminate(Cell cell) {
        return Optional.ofNullable(cell.getValue())
            .map(Collections::singletonList)
            .orElse(emptyList());
    }
}
