package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KnightsMoveRuleTest {

    @Mock
    private GameState gameState;

    private Cell[][] cells;

    private final KnightsMoveRule knightsMoveRule = new KnightsMoveRule();

    @BeforeEach
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

    @Test
    public void checkVisibleCellsCentre() {
        assertThat(
            this.knightsMoveRule.getVisibleCells(this.gameState, this.cells[4][4]),
            Matchers.containsInAnyOrder(
                this.cells[3][2],
                this.cells[2][3],
                this.cells[5][2],
                this.cells[6][3],
                this.cells[2][5],
                this.cells[3][6],
                this.cells[6][5],
                this.cells[5][6]
            )
        );
    }

    @Test
    public void checkVisibleCellsTopLeft() {
        assertThat(
            this.knightsMoveRule.getVisibleCells(this.gameState, this.cells[0][0]),
            Matchers.containsInAnyOrder(
                this.cells[2][1],
                this.cells[1][2]
            )
        );
    }
}
