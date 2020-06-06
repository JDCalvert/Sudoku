package calvert.jd.sudoku.game.util;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.logic.LogicConstraint;
import calvert.jd.sudoku.game.logic.LogicStageIdentifier;
import calvert.jd.sudoku.game.util.LogicQueue.LogicQueueEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static calvert.jd.sudoku.game.logic.LogicStageIdentifier.MULTIPLE_CELL_ELIMINATION;
import static calvert.jd.sudoku.game.logic.LogicStageIdentifier.SINGLE_CELL_ELIMINATION;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LogicQueueTest {

    @Mock
    private GameState gameState;

    private LogicQueue logicQueue;

    @Before
    public void init() {
        this.logicQueue = new LogicQueue();
    }

    @Test
    public void addSameLogic() {
        LogicQueueEntry queueEntry1 = buildQueueEntry(SINGLE_CELL_ELIMINATION, 1, 1);
        LogicQueueEntry queueEntry2 = buildQueueEntry(SINGLE_CELL_ELIMINATION, 0, 0);

        this.logicQueue.add(queueEntry1);
        this.logicQueue.add(queueEntry2);

        assertThat(this.logicQueue, contains(queueEntry1, queueEntry2));
    }

    @Test
    public void addDifferentLogic() {
        LogicQueueEntry queueEntry1 = buildQueueEntry(SINGLE_CELL_ELIMINATION, 0, 0);
        LogicQueueEntry queueEntry2 = buildQueueEntry(MULTIPLE_CELL_ELIMINATION, 0, 0);
        LogicQueueEntry queueEntry3 = buildQueueEntry(SINGLE_CELL_ELIMINATION, 1, 0);

        this.logicQueue.add(queueEntry1);
        this.logicQueue.add(queueEntry2);
        this.logicQueue.add(queueEntry3);

        assertThat(this.logicQueue, contains(queueEntry1, queueEntry3, queueEntry2));
    }

    private LogicQueueEntry buildQueueEntry(LogicStageIdentifier logicStage, int i, int j) {
        return new LogicQueueEntry(logicStage, LogicConstraint.builder().cell(new Cell(this.gameState, i, j)).build());
    }
}
