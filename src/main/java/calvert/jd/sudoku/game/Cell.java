package calvert.jd.sudoku.game;

import calvert.jd.sudoku.game.util.CellUpdate;

import java.util.*;
import java.util.stream.Collectors;

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
        logUpdate("set value=" + value);

        this.value = value;
        this.possibleValues = emptyList();
        this.gameState.handleCellUpdate(new CellUpdate(this, emptyList()));
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

    public boolean hasAnyPossibility(List<Integer> possibleValues) {
        return !Collections.disjoint(this.possibleValues, possibleValues);
    }

    public boolean removePossibleValue(Integer value) {
        return removePossibleValues(singletonList(value));
    }

    public boolean removePossibleValues(List<Integer> values) {
        // Filter to values that are actually possibilities currently.
        List<Integer> valuesToRemove = values.stream()
            .filter(this.possibleValues::contains)
            .collect(Collectors.toList());

        if (!valuesToRemove.isEmpty()) {
            logUpdate("remove possibilities=" + valuesToRemove);

            this.possibleValues.removeAll(valuesToRemove);
            this.gameState.handleCellUpdate(new CellUpdate(this, valuesToRemove));

            if (this.possibleValues.isEmpty()) {
                this.gameState.setErrorCell(this);
            } else if (this.possibleValues.size() == 1) {
                setValue(this.possibleValues.get(0));
            }
            return true;
        }

        return false;
    }

    private void logUpdate(String update) {
        this.gameState.log("Update cell=" + this + " " + update);
    }

    @Override
    public int compareTo(Cell o) {
        return Objects.compare(this, o, Comparator.comparingInt(Cell::getJ).thenComparingInt(Cell::getI));
    }

    @Override
    public String toString() {
        return new StringBuilder(80)
            .append(asList(this.i, this.j))
            .append(" value=")
            .append(Optional.ofNullable(this.value).map(String::valueOf).orElse(""))
            .append(" possibleValues=")
            .append(this.possibleValues)
            .toString();
    }
}
