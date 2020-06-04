package calvert.jd.sudoku.game.save;

import calvert.jd.sudoku.game.logic.LogicStage.LogicStageIdentifier;
import calvert.jd.sudoku.game.rules.Rule.RuleIdentifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SaveGame {
    private final List<SaveCell> cells;
    private final List<RuleIdentifier> rules;
    private final List<LogicStageIdentifier> constraints;

    @JsonCreator
    public SaveGame(
        @JsonProperty("cells") List<SaveCell> cells,
        @JsonProperty("rules") List<RuleIdentifier> rules,
        @JsonProperty("constraints") List<LogicStageIdentifier> constraints
    ) {
        this.cells = cells;
        this.rules = rules;
        this.constraints = constraints;
    }

    public List<SaveCell> getCells() {
        return this.cells;
    }

    public List<RuleIdentifier> getRules() {
        return this.rules;
    }

    public List<LogicStageIdentifier> getConstraints() {
        return this.constraints;
    }
}
