package calvert.jd.sudoku.game.logic;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.rules.Rule;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Constraint to put on a piece of logic.
 */
public class LogicConstraint {
    private final Cell cell;
    private final Integer value; // The possible value to run the piece of logic on.
    private final Rule rule; // The rule that found this cell to add.

    private LogicConstraint(Cell cell, Integer value, Rule rule) {
        this.cell = cell;
        this.value = value;
        this.rule = rule;
    }

    public static LogicConstraintBuilder builder() {
        return new LogicConstraintBuilder();
    }

    public Cell getCell() {
        return this.cell;
    }

    public Integer getValue() {
        return this.value;
    }

    public Rule getRule() {
        return rule;
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
            .append(this.rule, that.rule)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.cell)
            .append(this.value)
            .append(this.rule)
            .toHashCode();
    }

    public static class LogicConstraintBuilder {
        private Cell cell;
        private Integer value;
        private Rule rule;

        public LogicConstraintBuilder cell(Cell cell) {
            this.cell = cell;
            return this;
        }

        public LogicConstraintBuilder value(Integer value) {
            this.value = value;
            return this;
        }

        public LogicConstraintBuilder rule(Rule rule) {
            this.rule = rule;
            return this;
        }

        public LogicConstraint build() {
            return new LogicConstraint(this.cell, this.value, this.rule);
        }
    }
}
