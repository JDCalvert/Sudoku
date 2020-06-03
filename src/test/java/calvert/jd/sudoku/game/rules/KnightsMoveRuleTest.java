package calvert.jd.sudoku.game.rules;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

class KnightsMoveRuleTest extends AbstractRuleTest<KnightsMoveRule> {

    @Override
    protected KnightsMoveRule getRule() {
        return new KnightsMoveRule();
    }

    @Test
    public void checkVisibleCellsCentre() {
        assertVisibleCells(
            this.cells[4][4],
            asList(
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
        assertVisibleCells(
            this.cells[0][0],
            asList(
                this.cells[2][1],
                this.cells[1][2]
            )
        );
    }
}
