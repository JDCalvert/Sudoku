package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractRuleTest<R extends Rule> {

    @Mock
    protected GameState gameState;

    protected Cell[][] cells;

    protected R rule;

    public AbstractRuleTest() {
        this.rule = getRule();
    }

    @Before
    public void init() {
        this.cells = new Cell[9][9];

        List<Cell> cellsList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell cell = new Cell(this.gameState, i, j);
                this.cells[i][j] = cell;
                cellsList.add(cell);
            }
        }

        given(this.gameState.getCells()).willReturn(cellsList);
    }

    protected abstract R getRule();

    protected void assertVisibleCells(Cell processCell, List<Cell> expectedVisibleCells) {
        assertThat(this.rule.getVisibleCells(this.gameState, processCell), containsInAnyOrder(expectedVisibleCells.toArray()));
    }
}
