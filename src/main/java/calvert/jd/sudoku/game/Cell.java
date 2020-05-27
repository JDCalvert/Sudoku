package calvert.jd.sudoku.game;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class Cell {
    private final int i;
    private final int j;

    private final GameState gameState;

    private Integer value;
    private List<Integer> possibleValues;

    public Cell(GameState gameState, int i, int j) {
        this.i = i;
        this.j = j;

        this.gameState = gameState;

        this.value = null;
        this.possibleValues = new ArrayList<>();
    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue(Integer value) {
        this.value = value;
        this.possibleValues = emptyList();
        this.gameState.addCellToProcess(this);
    }

    public List<Integer> getPossibleValues() {
        return this.possibleValues;
    }

    public void resetPossibleValues() {
        this.possibleValues = new ArrayList<>(asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    public int getI() {
        return this.i;
    }

    public int getJ() {
        return this.j;
    }

    public void removePossibleValue(Integer value) {
        this.possibleValues.remove(value);
        if (this.possibleValues.size() == 1) {
            setValue(this.possibleValues.get(0));
        }
    }
}
