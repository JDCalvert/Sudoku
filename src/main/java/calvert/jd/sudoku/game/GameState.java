package calvert.jd.sudoku.game;

import calvert.jd.sudoku.game.rules.CellRule;
import calvert.jd.sudoku.game.rules.SudokuEliminatePossibilitiesRule;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

public class GameState {

    private final Cell[][] cells = new Cell[9][9];
    private final Cell[][] initialCells = new Cell[9][9];

    private final List<Cell> cellsList = new ArrayList<>();
    private final List<Cell> initialCellsList = new ArrayList<>();

    private boolean running = false;
    private boolean shouldStop = false;
    private boolean shouldReset = false;

    private Queue<Cell> cellsToProcessCellRules = new ArrayDeque<>();
    private List<CellRule> cellRules = new ArrayList<>();

    private List<Cell> selectedCells;
    private List<Cell> calculationCells = emptyList();

    private Thread gameRunningThread;
    private final List<ActionListener> actionListeners = new ArrayList<>();

    public GameState() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell cell = new Cell(this, i, j);
                this.cells[i][j] = cell;
                this.cellsList.add(cell);

                Cell initialCell = new Cell(this, i, j);
                this.initialCells[i][j] = initialCell;
                this.initialCellsList.add(initialCell);
            }
        }

        this.selectedCells = singletonList(this.cells[0][0]);
    }

    public void start() {
        this.gameRunningThread = new Thread(() -> {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    this.initialCells[i][j].setValue(this.cells[i][j].getValue());
                }
            }

            this.running = true;
            this.shouldStop = false;
            this.shouldReset = false;
            this.cellsToProcessCellRules = new ArrayDeque<>();

            this.cellRules = new ArrayList<>();
            this.cellRules.add(new SudokuEliminatePossibilitiesRule());

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    Cell cell = this.cells[j][i];
                    setSelectedCells(singletonList(cell));
                    update();
                    if (nonNull(cell.getValue())) {
                        addCellToProcess(cell);
                    } else {
                        cell.resetPossibleValues();
                        update();
                    }
                    if (this.shouldReset) {
                        reinitialise();
                        done();
                        return;
                    } else if (this.shouldStop) {
                        done();
                        return;
                    }
                }
            }

            setSelectedCells(emptyList());
            update();

            while (nonNull(this.cellsToProcessCellRules.peek())) {
                Cell cell = this.cellsToProcessCellRules.poll();
                setSelectedCells(singletonList(cell));
                update();
                this.cellRules.forEach(cellRule -> cellRule.runRule(this, cell));
                if (checkStateDone()) {
                    done();
                    return;
                } else if (this.shouldReset) {
                    reinitialise();
                    done();
                    return;
                } else if (this.shouldStop) {
                    done();
                    return;
                }
            }

            setSelectedCells(emptyList());
            update();
            done();
        });
        this.gameRunningThread.start();
    }

    public void stop() {
        this.shouldStop = true;
    }

    public void addCellToProcess(Cell cell) {
        this.cellsToProcessCellRules.add(cell);
    }

    private boolean checkStateDone() {
        return false;
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
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.cells[i][j].setValue(this.initialCells[i][j].getValue());
            }
        }
        updateNoWait();
    }

    public void done() {
        boolean complete = Arrays.stream(this.cells).allMatch(row -> Arrays.stream(row).allMatch(cell -> nonNull(cell.getValue())));
        this.running = false;
        this.actionListeners.forEach(actionListener -> actionListener.actionPerformed(new ActionEvent(this, 0, "done")));
        this.selectedCells = singletonList(this.cells[0][0]);
    }

    public Cell[][] getCells() {
        return this.cells;
    }

    public List<Cell> getSelectedCells() {
        return this.selectedCells;
    }

    public void setSelectedCells(List<Cell> selectedCells) {
        this.selectedCells = selectedCells;
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
        this.selectedCells = this.selectedCells.stream()
            .map(cell -> this.cells
                [min(max(cell.getI() + iDiff, 0), 8)]
                [min(max(cell.getJ() + jDiff, 0), 8)]
            )
            .collect(Collectors.toList());
    }

    public void clear() {
        this.cellsList.forEach(cell -> cell.setValue(null));
    }

    public List<Cell> getCalculationCells() {
        return this.calculationCells;
    }

    public void setCalculationCells(List<Cell> calculationCells) {
        this.calculationCells = calculationCells;
    }

    public void addActionListener(ActionListener actionListener) {
        this.actionListeners.add(actionListener);
    }
}
