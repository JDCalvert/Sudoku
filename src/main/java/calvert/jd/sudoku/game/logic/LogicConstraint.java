package calvert.jd.sudoku.game.logic;

import calvert.jd.sudoku.game.Cell;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Constraint to put on a piece of logic.
 */
public class LogicConstraint {
    private final Cell cell;
    private final Integer value; // The possible value to run the piece of logic on.

    public LogicConstraint(Cell cell) {
        this.cell = cell;
        this.value = null;
    }

    public LogicConstraint(Cell cell, Integer value) {
        this.cell = cell;
        this.value = value;
    }

    public Cell getCell() {
        return this.cell;
    }

    public Integer getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LogicConstraint)) {
            return false;
        }

        LogicConstraint that = (LogicConstraint) o;

        return new EqualsBuilder()
            .append(this.cell, that.cell)
            .append(this.value, that.value)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.cell)
            .append(this.value)
            .toHashCode();
    }
}
