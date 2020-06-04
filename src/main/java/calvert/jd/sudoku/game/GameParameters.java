package calvert.jd.sudoku.game;

import calvert.jd.sudoku.game.logic.LogicStageIdentifier;
import calvert.jd.sudoku.game.rules.Rule.RuleIdentifier;

import java.util.List;

public class GameParameters {
    private final List<RuleIdentifier> rules;
    private final List<LogicStageIdentifier> logicStages;

    public GameParameters(List<RuleIdentifier> rules, List<LogicStageIdentifier> logicStages) {
        this.rules = rules;
        this.logicStages = logicStages;
    }

    public List<RuleIdentifier> getRules() {
        return this.rules;
    }

    public List<LogicStageIdentifier> getLogicStages() {
        return this.logicStages;
    }
}
