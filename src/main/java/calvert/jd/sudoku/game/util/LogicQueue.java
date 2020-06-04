package calvert.jd.sudoku.game.util;

import calvert.jd.sudoku.game.logic.LogicConstraint;
import calvert.jd.sudoku.game.logic.LogicStageIdentifier;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Queue to keep track of which pieces of logic should be run on which cells. Prioritises by logic stage, but otherwise
 * keeps cells in the same order they were inserted.
 */
public class LogicQueue extends LinkedList<LogicQueue.LogicQueueEntry> {

    @Override
    public boolean add(LogicQueueEntry logicQueueEntry) {

        // Traverse through the elements in the list. If the new element is already in the list, then don't add it. If
        // we reach an element with a later logic stage, then add the new entry before it.
        ListIterator<LogicQueueEntry> it = listIterator();
        while (it.hasNext()) {
            LogicQueueEntry next = it.next();
            if (Objects.equals(next, logicQueueEntry)) {
                return false;
            } else if (Objects.compare(logicQueueEntry, next, Comparator.comparing(LogicQueueEntry::getLogicStageIdentifier)) < 0) {
                it.previous();
                it.add(logicQueueEntry);
                return true;
            }
        }
        it.add(logicQueueEntry);
        return true;
    }

    public static class LogicQueueEntry {
        private final LogicStageIdentifier logicStageIdentifier;
        private final LogicConstraint logicConstraint;

        public LogicQueueEntry(LogicStageIdentifier logicStageIdentifier, LogicConstraint logicConstraint) {
            this.logicStageIdentifier = logicStageIdentifier;
            this.logicConstraint = logicConstraint;
        }

        public LogicStageIdentifier getLogicStageIdentifier() {
            return this.logicStageIdentifier;
        }

        public LogicConstraint getLogicConstraint() {
            return this.logicConstraint;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof LogicQueueEntry)) {
                return false;
            }

            LogicQueueEntry that = (LogicQueueEntry) o;

            return new EqualsBuilder()
                .append(this.logicStageIdentifier, that.logicStageIdentifier)
                .append(this.logicConstraint, that.logicConstraint)
                .build();

        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                .append(this.logicStageIdentifier)
                .append(this.logicConstraint)
                .toHashCode();
        }
    }
}
