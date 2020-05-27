package calvert.jd.sudoku.panels;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.Color.LIGHT_GRAY;

public class SudokuFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public SudokuFrame() {
        setLayout(new BorderLayout());

        SudokuSurface sudokuSurface = new SudokuSurface();
        addKeyListener(sudokuSurface);
        add(sudokuSurface, CENTER);

        setTitle("Sudoku");
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setBackground(LIGHT_GRAY);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
