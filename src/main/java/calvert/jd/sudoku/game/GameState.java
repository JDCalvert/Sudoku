package calvert.jd.sudoku.game;

import calvert.jd.sudoku.actioncontrol.GameLoggingListener;
import calvert.jd.sudoku.actioncontrol.GameStateListener;
import calvert.jd.sudoku.game.logic.LogicConstraint;
import calvert.jd.sudoku.game.logic.LogicStage;
import calvert.jd.sudoku.game.logic.LogicStageIdentifier;
import calvert.jd.sudoku.game.rules.Rule;
import calvert.jd.sudoku.game.rules.Rule.RuleIdentifier;
import calvert.jd.sudoku.game.util.CellUpdate;
import calvert.jd.sudoku.game.util.LogicQueue;
import calvert.jd.sudoku.game.util.LogicQueue.LogicQueueEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

public class GameState {

    private final List<Cell> cells = new ArrayList<>();

    private List<Cell> errorCells = emptyList();

    private boolean running = false;
    private boolean shouldPause = false;
    private boolean shouldStop = false;

    private boolean doUpdates = true;
    private long updateDelay = 100;

    private int numQueueProcesses = 0;
    private int numUpdates = 0;

    private final Queue<LogicQueueEntry> processQueue = new LogicQueue();

    private List<Rule> rules = new ArrayList<>();
    private List<LogicStage> logicStages = new ArrayList<>();

    private List<Cell> selectedCells = emptyList();
    private List<Cell> calculationCells = emptyList();
    private List<Integer> calculationValues = emptyList();

    private final List<GameStateListener> gameStateListeners = new ArrayList<>();
    private final List<GameLoggingListener> gameLoggingListeners = new ArrayList<>();

    public GameState() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.cells.add(new Cell(this, i, j));
            }
        }

        this.cells.sort(Cell::compareTo);
    }

    public int getNumQueueProcesses() {
        return this.numQueueProcesses;
    }

    public int getNumUpdates() {
        return this.numUpdates;
    }

    public void addToProcessQueue(LogicStageIdentifier logicStageIdentifier, LogicConstraint logicConstraint) {
        this.processQueue.add(new LogicQueueEntry(logicStageIdentifier, logicConstraint));
    }

    public void start(GameParameters gameParameters) {
        this.rules = gameParameters.getRules().stream()
            .sorted()
            .map(RuleIdentifier::getRule)
            .collect(Collectors.toList());

        this.logicStages = gameParameters.getLogicStages().stream()
            .sorted()
            .map(LogicStageIdentifier::getLogicStage)
            .collect(Collectors.toList());

        reinitialise();
        this.numQueueProcesses = 0;
        this.numUpdates = 0;

        new Thread(this::start).start();
    }

    public void togglePause() {
        if (this.running) {
            this.shouldPause = true;
        } else {
            this.running = true;
            this.shouldPause = false;
            new Thread(this::processCells).start();
        }
    }

    private void start() {
        this.running = true;
        this.shouldPause = false;
        this.shouldStop = false;

        this.processQueue.clear();

        setErrorCells(emptyList());

        clearLogging();
        log("Initialising...");

        // Build initial list of cells to process, and reset possible values for blank cells.
        this.cells.forEach(cell -> {
            if (nonNull(cell.getInitialValue())) {
                cell.setValue(cell.getInitialValue());
            } else {
                cell.resetPossibleValues();
            }
        });

        processCells();
    }

    private void processCells() {
        while (!this.processQueue.isEmpty()) {
            LogicQueueEntry logicQueueEntry = this.processQueue.poll();

            LogicStage logicStage = logicQueueEntry.getLogicStageIdentifier().getLogicStage();
            LogicConstraint logicConstraint = logicQueueEntry.getLogicConstraint();

            if (logicStage.isValidForCell(logicConstraint)) {
                this.log("About to process cell=" + logicConstraint.getCell() + " for logic " + logicQueueEntry.getLogicStageIdentifier());

                logicStage.runLogic(this, logicConstraint);
                setSelectedCells(emptyList());
                setCalculationCells(emptyList());
                this.numQueueProcesses++;

                if (checkStatus()) {
                    return;
                }
            }
        }

        done();
    }

    private boolean checkStatus() {
        if (this.shouldStop || isError() || isComplete()) {
            done();
            return true;
        } else if (this.shouldPause) {
            paused();
            return true;
        }
        return false;
    }

    public boolean isComplete() {
        return this.cells.stream().allMatch(cell -> nonNull(cell.getValue()));
    }

    public boolean isError() {
        if (this.errorCells.isEmpty()) {
            this.errorCells = this.cells.stream()
                .filter(cell -> nonNull(cell.getValue()))
                .filter(cell ->
                    this.rules.stream()
                        .filter(rule -> rule.appliesToCell(cell))
                        .anyMatch(rule -> {
                                List<Integer> illegalValues = rule.getPossibilitiesToEliminate(cell.getValue());
                                return rule.getVisibleCells(this, cell).stream()
                                    .filter(visibleCell -> visibleCell != cell)
                                    .filter(visibleCell -> nonNull(visibleCell.getValue()))
                                    .anyMatch(visibleCell -> illegalValues.contains(visibleCell.getValue()));
                            }
                        )
                )
                .collect(Collectors.toList());
        }
        return !this.errorCells.isEmpty();
    }

    public void stop() {
        this.shouldStop = true;
        if (!this.running) {
            done();
        }
    }

    public void update() {
        this.numUpdates++;
        if (this.doUpdates) {
            sendUpdate();
            try {
                Thread.sleep(this.updateDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendUpdate() {
        this.gameStateListeners.forEach(GameStateListener::update);
    }

    public void clearLogging() {
        this.gameLoggingListeners.forEach(GameLoggingListener::clear);
    }

    public void log(String text) {
        this.gameLoggingListeners.forEach(listener -> listener.log(text));
    }

    public void reset() {
        reinitialise();
    }

    private void reinitialise() {
        this.cells.forEach(Cell::resetValue);
        this.errorCells = emptyList();

        this.processQueue.clear();

        sendUpdate();
    }

    private void paused() {
        this.running = false;
        setSelectedCells(emptyList());
        setCalculationCells(emptyList());

        sendUpdate();
    }

    public void done() {
        log("Total queue entries processed: " + this.numQueueProcesses);
        log("Total updates: " + this.numUpdates);

        this.running = false;
        setSelectedCells(emptyList());
        setCalculationCells(emptyList());

        this.gameStateListeners.forEach(GameStateListener::done);
        sendUpdate();
    }

    public void handleCellUpdate(CellUpdate cellUpdate) {
        this.logicStages.forEach(logicStage -> logicStage.processCellUpdate(this, cellUpdate));
    }

    public List<Cell> getCells() {
        return this.cells;
    }

    public List<Cell> getSelectedCells() {
        return this.selectedCells;
    }

    public void setSelectedCell(Cell selectedCell) {
        setSelectedCells(singletonList(selectedCell));
    }

    public void setSelectedCells(List<Cell> selectedCells) {
        this.selectedCells = selectedCells;
    }

    public List<Cell> getCalculationCells() {
        return this.calculationCells;
    }

    public void setCalculationCells(List<Cell> calculationCells) {
        this.calculationCells = calculationCells;
    }

    public List<Integer> getCalculationValues() {
        return this.calculationValues;
    }

    public void setCalculationValue(Integer calculationValue) {
        setCalculationValues(singletonList(calculationValue));
    }

    public void setCalculationValues(List<Integer> calculationValues) {
        this.calculationValues = calculationValues;
    }

    public List<Cell> getErrorCells() {
        return this.errorCells;
    }

    public void setErrorCell(Cell errorCell) {
        this.errorCells = singletonList(errorCell);
    }

    public void setErrorCells(List<Cell> errorCells) {
        this.errorCells = errorCells;
    }

    public List<Rule> getRules() {
        return this.rules;
    }

    public void setDoUpdates(boolean doUpdates) {
        this.doUpdates = doUpdates;
    }

    public void setUpdateDelay(long updateDelay) {
        this.updateDelay = updateDelay;
    }

    public void moveSelectedCellUp() {
        moveSelectedCell(0, -1);
    }

    public void moveSelectedCellDown() {
        moveSelectedCell(0, 1);
    }

    public void moveSelectedCellLeft() {
        moveSelectedCell(-1, -0);
    }

    public void moveSelectedCellRight() {
        moveSelectedCell(1, 0);
    }

    private void moveSelectedCell(int iDiff, int jDiff) {
        if (this.selectedCells.isEmpty()) {
            setSelectedCell(this.cells.get(0));
        } else {
            this.selectedCells = this.selectedCells.stream()
                .map(cell -> this.cells.stream()
                    .filter(
                        newCell -> newCell.getI() == min(max(cell.getI() + iDiff, 0), 8)
                            && newCell.getJ() == min(max(cell.getJ() + jDiff, 0), 8))
                    .findFirst()
                    .orElse(null)
                )
                .collect(Collectors.toList());
        }
    }

    public void clear() {
        this.cells.forEach(cell -> {
            cell.setValue(null);
            cell.setInitialValue(null);
        });
    }

    public void addGameStateListener(GameStateListener listener) {
        this.gameStateListeners.add(listener);
    }

    public void addGameLoggingListener(GameLoggingListener listener) {
        this.gameLoggingListeners.add(listener);
    }
}
