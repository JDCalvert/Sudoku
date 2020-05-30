package calvert.jd.sudoku.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class Cell implements Comparable<Cell> {
    private final int i;
    private final int j;

    private final GameState gameState;

    private Integer value;
    private Integer initialValue;

    private List<Integer> possibleValues;

    public Cell(GameState gameState, int i, int j) {
        this.i = i;
        this.j = j;

        this.gameState = gameState;

        this.value = null;
        this.possibleValues = emptyList();
    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue(Integer value) {
        this.value = value;
        this.possibleValues = emptyList();
        this.gameState.handleCellUpdated(this);
    }

    public Integer getInitialValue() {
        return this.initialValue;
    }

    public void setInitialValue(Integer value) {
        this.initialValue = value;
    }

    public void resetValue() {
        this.value = null;
        this.possibleValues = emptyList();
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

    public boolean removePossibleValue(Integer value) {
        return removePossibleValues(singletonList(value));
    }

    public boolean removePossibleValues(List<Integer> values) {
        boolean removed = this.possibleValues.removeAll(values);
        if (removed) {
            this.gameState.handleCellUpdated(this);

            if (this.possibleValues.isEmpty()) {
                this.gameState.setErrorCell(this);
            } else if (this.possibleValues.size() == 1) {
                setValue(this.possibleValues.get(0));
            }
        }

        return removed;
    }

    @Override
    public int compareTo(Cell o) {
        return Objects.compare(this, o, Comparator.comparingInt(Cell::getJ).thenComparingInt(Cell::getI));
    }
}
