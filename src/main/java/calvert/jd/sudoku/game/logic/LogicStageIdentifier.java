package calvert.jd.sudoku.game.logic;

import calvert.jd.sudoku.game.logic.logicstages.*;

/**
 * Enumerate the logic stages.
 */
public enum LogicStageIdentifier {
    SINGLE_CELL_ELIMINATION(new SingleCellElimination(), false),
    CENTRE_REGION_MAGIC_SQUARE(new CentreRegionMagicSquare(), true),
    SHARED_POSSIBILITIES_ELIMINATION(new SharedPossibilitiesElimination(), false),
    SAME_POSSIBILITIES_ELIMINATION(new SamePossibilitiesElimination(), false),
    ONLY_CELL_WITH_POSSIBILITY(new OnlyCellWithPossibility(), false),
    MULTIPLE_CELL_ELIMINATION(new MultipleCellElimination(), false);

    private final LogicStage logicStage;
    private final boolean isConstraint;

    LogicStageIdentifier(LogicStage logicStage, boolean isConstraint) {
        this.logicStage = logicStage;
        this.isConstraint = isConstraint;
    }

    public LogicStage getLogicStage() {
        return this.logicStage;
    }

    public boolean isConstraint() {
        return this.isConstraint;
    }
}
