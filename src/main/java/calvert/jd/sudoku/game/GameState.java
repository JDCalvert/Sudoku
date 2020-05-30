package calvert.jd.sudoku.game;

import calvert.jd.sudoku.game.logic.LogicStage.LogicStageIdentifier;
import calvert.jd.sudoku.game.rules.Rule;
import calvert.jd.sudoku.game.rules.SudokuColumnRule;
import calvert.jd.sudoku.game.rules.SudokuRegionRule;
import calvert.jd.sudoku.game.rules.SudokuRowRule;
import calvert.jd.sudoku.game.util.LogicQueue;
import calvert.jd.sudoku.game.util.LogicQueue.LogicQueueEntry;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

public class GameState {

    private final List<Cell> cells = new ArrayList<>();

    private List<Cell> errorCells = emptyList();

    private boolean running = false;
    private boolean shouldStop = false;
    private boolean shouldReset = false;

    private final AtomicBoolean hasUpdated = new AtomicBoolean();

    private final Queue<LogicQueueEntry> processQueue = new LogicQueue();

    private List<Rule> rules = new ArrayList<>();
    private List<LogicStageIdentifier> logicStages = new ArrayList<>();

    private List<Cell> selectedCells;
    private List<Cell> calculationCells = emptyList();

    private final List<ActionListener> actionListeners = new ArrayList<>();

    public GameState() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.cells.add(new Cell(this, i, j));
            }
        }

        this.cells.sort(Cell::compareTo);
        this.selectedCells = emptyList();
    }

    public void addToProcessQueue(Cell cell, LogicStageIdentifier logicStageIdentifier) {
        this.processQueue.add(new LogicQueueEntry(cell, logicStageIdentifier));
    }

    public void start() {
        new Thread(this::solve).start();
    }

    private void solve() {
        this.running = true;
        this.shouldStop = false;
        this.shouldReset = false;

        this.rules = asList(
            new SudokuRowRule(),
            new SudokuColumnRule(),
            new SudokuRegionRule()
        );

        this.logicStages = asList(LogicStageIdentifier.values());

        setErrorCells(emptyList());

        // Build initial list of cells to process, and reset possible values for blank cells.
        for (Cell cell : this.cells) {
            setSelectedCell(cell);
            update();
            if (nonNull(cell.getInitialValue())) {
                cell.setValue(cell.getInitialValue());
            } else {
                cell.resetPossibleValues();
                update();
            }
            if (checkStatus()) {
                return;
            }
        }

        setSelectedCells(emptyList());
        update();

        this.hasUpdated.set(true);
        while (this.hasUpdated.get()) {
            resetHasUpdated();

            while (!this.processQueue.isEmpty()) {
                LogicQueueEntry logicQueueEntry = this.processQueue.poll();

                logicQueueEntry.getLogicStageIdentifier()
                    .getLogicStage()
                    .runLogic(this, logicQueueEntry.getCell());

                if (checkStatus()) {
                    return;
                }
            }

            setSelectedCells(emptyList());
            setCalculationCells(emptyList());
        }

        done();
    }

    private boolean checkStatus() {
        if (this.shouldStop || isError() || isComplete()) {
            done();
            return true;
        } else if (this.shouldReset) {
            reinitialise();
            done();
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
                .filter(cell ->
                    this.rules.stream()
                        .map(rule -> rule.getVisibleCells(this, cell))
                        .flatMap(Collection::stream)
                        .filter(visibleCell -> visibleCell != cell)
                        .filter(visibleCell -> nonNull(visibleCell.getValue()))
                        .anyMatch(visibleCell -> Objects.equals(visibleCell.getValue(), cell.getValue()))
                )
                .collect(Collectors.toList());
        }
        return !this.errorCells.isEmpty();
    }

    public void stop() {
        this.shouldStop = true;
    }

    public void update() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updateNoWait();
    }

    private void updateNoWait() {
        this.actionListeners.forEach(actionListener -> actionListener.actionPerformed(new ActionEvent(this, 0, "update")));
    }

    public void reset() {
        this.shouldReset = true;
        if (!this.running) {
            reinitialise();
        }
    }

    private void reinitialise() {
        this.cells.forEach(Cell::resetValue);
        this.errorCells = emptyList();
        updateNoWait();
    }

    public void done() {
        this.running = false;
        this.actionListeners.forEach(actionListener -> actionListener.actionPerformed(new ActionEvent(this, 0, "done")));

        setSelectedCell(this.cells.get(0));
        setCalculationCells(emptyList());
        updateNoWait();
    }

    public void handleCellUpdated(Cell cell) {
        setHasUpdated();
        this.logicStages.stream()
            .map(LogicStageIdentifier::getLogicStage)
            .forEach(logicStage -> logicStage.processCellUpdate(this, cell));
    }

    private void resetHasUpdated() {
        this.hasUpdated.set(false);
    }

    public void setHasUpdated() {
        this.hasUpdated.set(true);
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

    public void addActionListener(ActionListener actionListener) {
        this.actionListeners.add(actionListener);
    }
}
