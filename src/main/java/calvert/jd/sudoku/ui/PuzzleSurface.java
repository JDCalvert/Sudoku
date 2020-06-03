package calvert.jd.sudoku.ui;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import static java.awt.Color.*;
import static java.util.Objects.nonNull;

public class PuzzleSurface extends JPanel {

    public static final int CELL_SIZE = 50;
    private static final int MAIN_BORDER_WIDTH = 3;
    private static final int REGION_BORDER_WIDTH = 2;
    private static final int CELL_BORDER_WIDTH = 1;

    private static final int SIZE = CELL_SIZE * 9;

    private static final Color LIGHT_YELLOW = new Color(255, 255, 192);
    private static final Color LIGHT_RED = new Color(255, 192, 192);
    private static final Color DARK_BLUE = new Color(0, 0, 128);

    private final GameState gameState;

    public PuzzleSurface(GameState gameState) {
        this.gameState = gameState;

        setSize(SIZE + 10, SIZE + 10);
        setBackground(WHITE);
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(BLACK);

        //Highlight selected cells
        highlightCells(g2d, this.gameState.getErrorCells(), LIGHT_RED);
        highlightCells(g2d, this.gameState.getCalculationCells(), LIGHT_YELLOW);
        highlightCells(g2d, this.gameState.getSelectedCells(), YELLOW);

        //Outline
        g2d.setColor(BLACK);
        g2d.setStroke(new BasicStroke(MAIN_BORDER_WIDTH));
        g2d.drawRect(5, 5, SIZE, SIZE);

        //Regions
        g2d.setStroke(new BasicStroke(REGION_BORDER_WIDTH));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                g2d.drawRect(5 + i * (CELL_SIZE * 3), 5 + j * (CELL_SIZE * 3), CELL_SIZE * 3, CELL_SIZE * 3);
            }
        }

        //Cells
        g2d.setStroke(new BasicStroke(CELL_BORDER_WIDTH));
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                g2d.drawRect(5 + i * CELL_SIZE, 5 + j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        //Numbers
        this.gameState.getCells().forEach(cell -> {
            int cellPositionX = 5 + cell.getI() * CELL_SIZE;
            int cellPositionY = 5 + cell.getJ() * CELL_SIZE;

            if (nonNull(cell.getInitialValue())) {
                g2d.setColor(BLACK);
            } else {
                g2d.setColor(DARK_BLUE);
            }

            g2d.setFont(g2d.getFont().deriveFont(40.0f));
            Integer value = Optional.ofNullable(cell.getInitialValue()).orElse(cell.getValue());
            if (nonNull(value)) {
                g2d.drawString(
                    value.toString(),
                    cellPositionX + CELL_SIZE / 2 - 10,
                    cellPositionY + CELL_SIZE / 2 + 15
                );
            }

            g2d.setColor(GRAY);
            g2d.setFont(g2d.getFont().deriveFont(10.0f));
            List<Integer> possibleValues = cell.getPossibleValues();
            for (Integer possibleValue : possibleValues) {
                g2d.drawString(
                    possibleValue.toString(),
                    cellPositionX + 20 * ((possibleValue - 1) % 3) + 3,
                    cellPositionY + 16 * ((possibleValue - 1) / 3) + 13
                );
            }
        });
    }

    private void highlightCells(Graphics2D g2d, List<Cell> calculationCells, Color colour) {
        g2d.setColor(colour);
        calculationCells.forEach(cell -> {
            g2d.fillRect(5 + cell.getI() * CELL_SIZE, 5 + cell.getJ() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}
