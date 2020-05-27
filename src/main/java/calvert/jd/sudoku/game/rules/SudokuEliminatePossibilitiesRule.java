package calvert.jd.sudoku.game.rules;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

public class SudokuEliminatePossibilitiesRule extends CellRule {

    @Override
    public boolean runRule(GameState gameState, Cell cell) {
        Integer value = cell.getValue();
        if (isNull(value)) {
            return false;
        }

        Cell[][] cells = gameState.getCells();

        renderAndEliminate(gameState, value, getCellsInRow(cell, cells));
        renderAndEliminate(gameState, value, getCellsInColumn(cell, cells));
        renderAndEliminate(gameState, value, getCellsInRegion(cell, cells));

        gameState.setCalculationCells(emptyList());
        gameState.update();

        return true;
    }

    private List<Cell> getCellsInRegion(Cell cell, Cell[][] cells) {
        List<Cell> calculationCells;
        calculationCells = new ArrayList<>();
        int regionI = cell.getI() / 3;
        int regionJ = cell.getJ() / 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                calculationCells.add(cells[regionI * 3 + i][regionJ * 3 + j]);
            }
        }
        return calculationCells;
    }

    private List<Cell> getCellsInColumn(Cell cell, Cell[][] cells) {
        List<Cell> calculationCells;
        calculationCells = new ArrayList<>();
        for (int jInColumn = 0; jInColumn < 9; jInColumn++) {
            calculationCells.add(cells[cell.getI()][jInColumn]);
        }
        return calculationCells;
    }

    private List<Cell> getCellsInRow(Cell cell, Cell[][] cells) {
        List<Cell> calculationCells = new ArrayList<>();
        for (int iInRow = 0; iInRow < 9; iInRow++) {
            calculationCells.add(cells[iInRow][cell.getJ()]);
        }
        return calculationCells;
    }

    private void renderAndEliminate(GameState gameState, Integer value, List<Cell> calculationCells) {
        gameState.setCalculationCells(calculationCells);
        gameState.update();

        calculationCells.forEach(currentCell -> currentCell.removePossibleValue(value));
        gameState.update();
    }
}
