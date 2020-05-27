package calvert.jd.sudoku.panels;

import calvert.jd.sudoku.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import static calvert.jd.sudoku.panels.PuzzleSurface.CELL_SIZE;
import static java.awt.BorderLayout.CENTER;
import static java.awt.Color.LIGHT_GRAY;
import static java.util.Arrays.asList;
import static javax.swing.BoxLayout.Y_AXIS;

public class SudokuSurface extends JPanel implements ActionListener, KeyListener {

    private static final int SIZE = CELL_SIZE * 9 + 10;

    private final GameState gameState;

    private PuzzleSurface puzzleSurface;
    private JPanel controlPanel;
    private JPanel inputPanel;
    private JPanel rulesPanel;

    public SudokuSurface() {
        this.gameState = new GameState();
        this.gameState.addActionListener(this);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBackground(LIGHT_GRAY);

        this.puzzleSurface = new PuzzleSurface(this.gameState);
        this.puzzleSurface.setLocation(20, 20);
        this.puzzleSurface.setSize(SIZE, SIZE);
        add(this.puzzleSurface, CENTER);

        this.controlPanel = new JPanel();
        BoxLayout controlPanelLayout = new BoxLayout(this.controlPanel, Y_AXIS);
        this.controlPanel.setLayout(controlPanelLayout);
        add(this.controlPanel, BorderLayout.EAST);

        this.rulesPanel = new JPanel();
        BoxLayout rulesPanelLayout = new BoxLayout(this.rulesPanel, Y_AXIS);
        this.rulesPanel.setLayout(rulesPanelLayout);

        this.controlPanel.add(this.rulesPanel);

        JCheckBox standardRulesCheckBox = new JCheckBox("Standard Sudoku");
        standardRulesCheckBox.setEnabled(false);
        this.rulesPanel.add(standardRulesCheckBox);

        JCheckBox knightsMoveRulesCheckBox = new JCheckBox("Knight's Move");
        knightsMoveRulesCheckBox.setEnabled(true);
        this.rulesPanel.add(knightsMoveRulesCheckBox);

        this.inputPanel = new JPanel();
        GridLayout inputLayout = new GridLayout();
        inputLayout.setRows(6);
        this.inputPanel.setLayout(inputLayout);
        this.controlPanel.add(this.inputPanel);

        for (int i = 1; i <= 9; i++) {
            JButton numberButton = new JButton(Integer.toString(i));
            numberButton.setActionCommand(Integer.toString(i));
            numberButton.addActionListener(this);
            this.inputPanel.add(numberButton);
        }

        JButton clearButton = new JButton("Clear");
        clearButton.setActionCommand("clear");
        clearButton.addActionListener(this);
        this.inputPanel.add(clearButton);

        for (String str : asList("Up", "Left", "Right", "Down")) {
            JButton directionButton = new JButton(str);
            directionButton.setActionCommand(str.toLowerCase());
            directionButton.addActionListener(this);
            this.inputPanel.add(directionButton);
            this.inputPanel.add(Box.createHorizontalBox());
        }

        JPanel buttonPanel = new JPanel();
        FlowLayout buttonPanelLayout = new FlowLayout();
        buttonPanelLayout.setHgap(0);
        buttonPanel.setLayout(buttonPanelLayout);
        this.controlPanel.add(buttonPanel);

        JButton startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
        buttonPanel.add(startButton);

        JButton stopButton = new JButton("Stop");
        stopButton.setActionCommand("stop");
        stopButton.addActionListener(this);
        buttonPanel.add(stopButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);

        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.setActionCommand("clearAll");
        clearAllButton.addActionListener(this);
        buttonPanel.add(clearAllButton);

        makeNotFocusable(this.controlPanel);
    }

    private void makeNotFocusable(JPanel panel) {
        Arrays.stream(panel.getComponents())
            .forEach(component -> {
                component.setFocusable(false);
                if (component instanceof JPanel) {
                    makeNotFocusable((JPanel) component);
                }
            });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("start")) {
            this.gameState.start();
            this.rulesPanel.setEnabled(false);
            Arrays.stream(this.rulesPanel.getComponents()).forEach(c -> c.setEnabled(false));
        } else if (actionCommand.equals("stop")) {
            this.gameState.stop();
        } else if (actionCommand.equals("reset")) {
            this.gameState.reset();
        } else if (actionCommand.equals("done")) {
            this.rulesPanel.setEnabled(true);
            Arrays.stream(this.rulesPanel.getComponents()).forEach(c -> c.setEnabled(true));
        } else if (actionCommand.equals("up")) {
            this.gameState.moveSelectedCellUp();
        } else if (actionCommand.equals("down")) {
            this.gameState.moveSelectedCellDown();
        } else if (actionCommand.equals("left")) {
            this.gameState.moveSelectedCellLeft();
        } else if (actionCommand.equals("right")) {
            this.gameState.moveSelectedCellRight();
        } else if (actionCommand.equals("clear")) {
            this.gameState.getSelectedCells().forEach(cell -> cell.setValue(null));
        } else if (actionCommand.equals("clearAll")) {
            this.gameState.clear();
        }

        char[] chars = actionCommand.toCharArray();
        if (chars.length > 0) {
            char aChar = chars[0];
            if (aChar >= '1' && aChar <= '9') {
                this.gameState.getSelectedCells().forEach(cell -> cell.setValue(Integer.valueOf(String.valueOf(aChar))));
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            this.gameState.moveSelectedCellUp();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            this.gameState.moveSelectedCellDown();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            this.gameState.moveSelectedCellLeft();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.gameState.moveSelectedCellRight();
        } else if (e.getKeyChar() >= '1' && e.getKeyChar() <= '9') {
            this.gameState.getSelectedCells().forEach(cell -> cell.setValue(Integer.valueOf(String.valueOf(e.getKeyChar()))));
        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            this.gameState.getSelectedCells().forEach(cell -> cell.setValue(null));
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
