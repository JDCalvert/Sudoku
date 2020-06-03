package calvert.jd.sudoku.game.rules;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

class AdjacentNonSequentialRuleTest extends AbstractRuleTest<AdjacentNonSequentialRule> {

    @Override
    protected AdjacentNonSequentialRule getRule() {
        return new AdjacentNonSequentialRule();
    }

    @Test
    public void checkVisibleCellsCentre() {
        assertVisibleCells(
            this.cells[4][4],
            asList(
                this.cells[4][3],
                this.cells[4][5],
                this.cells[3][4],
                this.cells[5][4]
            )
        );
    }

    @Test
    public void checkVisibleCellsTopLeft() {
        assertVisibleCells(
            this.cells[0][0],
            asList(
                this.cells[0][1],
                this.cells[1][0]
            )
        );
    }
}