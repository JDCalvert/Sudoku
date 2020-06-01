package calvert.jd.sudoku.panels;

import calvert.jd.sudoku.game.Cell;
import calvert.jd.sudoku.game.GameParameters;
import calvert.jd.sudoku.game.GameState;
import calvert.jd.sudoku.game.logic.LogicStage.LogicStageIdentifier;
import calvert.jd.sudoku.game.rules.Rule.RuleIdentifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static calvert.jd.sudoku.game.logic.LogicStage.LogicStageIdentifier.*;
import static calvert.jd.sudoku.game.rules.Rule.RuleIdentifier.*;
import static calvert.jd.sudoku.panels.PuzzleSurface.CELL_SIZE;
import static java.awt.BorderLayout.CENTER;
import static java.awt.Color.LIGHT_GRAY;
import static javax.swing.GroupLayout.DEFAULT_SIZE;

public class SudokuSurface extends JPanel implements ActionListener, KeyListener {

    private static final int SIZE = CELL_SIZE * 9 + 10;

    private final GameState gameState;

    private PuzzleSurface puzzleSurface;

    private JPanel controlPanel;

    private JPanel rulesPanel;
    private JCheckBox standardRulesCheckbox;
    private JCheckBox leadingDiagonalsRuleCheckbox;
    private JCheckBox knightsMoveRuleCheckbox;
    private JCheckBox kingsMoveRuleCheckbox;
    private JCheckBox magicCentreSquareCheckbox;

    private JPanel logicStagesPanel;
    private JCheckBox singleCellEliminationCheckbox;
    private JCheckBox sharedPossibilitiesEliminationCheckbox;
    private JCheckBox multipleCellEliminationCheckbox;
    private JCheckBox onlyCellWithPossibilityCheckbox;

    private JPanel inputPanel;

    private JPanel buttonPanel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton resetButton;
    private JButton clearAllButton;
    private JButton saveSetupButton;
    private JButton loadSetupButton;

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

        GroupLayout controlPanelLayout = createGroupLayout(this.controlPanel);
        add(this.controlPanel, BorderLayout.EAST);

        this.rulesPanel = new JPanel();
        this.rulesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        GroupLayout rulesPanelLayout = createGroupLayout(this.rulesPanel);

        this.standardRulesCheckbox = new JCheckBox("Standard Sudoku");
        this.leadingDiagonalsRuleCheckbox = new JCheckBox("Leading Diagonals");
        this.knightsMoveRuleCheckbox = new JCheckBox("Knight's Move");
        this.kingsMoveRuleCheckbox = new JCheckBox("King's Move");
        this.magicCentreSquareCheckbox = new JCheckBox("Centre Region Magic Square");

        this.standardRulesCheckbox.setSelected(true);

        rulesPanelLayout.setHorizontalGroup(
            rulesPanelLayout.createParallelGroup()
                .addComponent(this.standardRulesCheckbox, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.leadingDiagonalsRuleCheckbox, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.knightsMoveRuleCheckbox, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.kingsMoveRuleCheckbox, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.magicCentreSquareCheckbox, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
        );
        rulesPanelLayout.setVerticalGroup(
            rulesPanelLayout.createSequentialGroup()
                .addComponent(this.standardRulesCheckbox)
                .addComponent(this.leadingDiagonalsRuleCheckbox)
                .addComponent(this.knightsMoveRuleCheckbox)
                .addComponent(this.kingsMoveRuleCheckbox)
                .addComponent(this.magicCentreSquareCheckbox)
        );

        this.logicStagesPanel = new JPanel();
        this.logicStagesPanel.setBorder(BorderFactory.createTitledBorder("Logic Stages"));
        GroupLayout logicStagesGroupLayout = createGroupLayout(this.logicStagesPanel);

        this.singleCellEliminationCheckbox = new JCheckBox("Single Cell Elimination");
        this.sharedPossibilitiesEliminationCheckbox = new JCheckBox("Shared Possibilities");
        this.multipleCellEliminationCheckbox = new JCheckBox("Multiple Cell Elimination");
        this.onlyCellWithPossibilityCheckbox = new JCheckBox("Only Cell with Possibility");

        this.singleCellEliminationCheckbox.setSelected(true);
        this.sharedPossibilitiesEliminationCheckbox.setSelected(true);
        this.multipleCellEliminationCheckbox.setSelected(true);
        this.onlyCellWithPossibilityCheckbox.setSelected(true);

        logicStagesGroupLayout.setHorizontalGroup(
            logicStagesGroupLayout.createParallelGroup()
                .addComponent(this.singleCellEliminationCheckbox, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.sharedPossibilitiesEliminationCheckbox, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.multipleCellEliminationCheckbox, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.onlyCellWithPossibilityCheckbox, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
        );
        logicStagesGroupLayout.setVerticalGroup(
            logicStagesGroupLayout.createSequentialGroup()
                .addComponent(this.singleCellEliminationCheckbox)
                .addComponent(this.sharedPossibilitiesEliminationCheckbox)
                .addComponent(this.multipleCellEliminationCheckbox)
                .addComponent(this.onlyCellWithPossibilityCheckbox)
        );

        this.inputPanel = new JPanel();
        this.inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
        GroupLayout inputPanelLayout = createGroupLayout(this.inputPanel);

        List<JButton> numberButtons = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            numberButtons.add(createButton(String.valueOf(i)));
        }

        JButton clearButton = new JButton("Clear");
        clearButton.setActionCommand("clear");
        clearButton.addActionListener(this);

        JButton upButton = createButton("Up");
        JButton downButton = createButton("Down");
        JButton leftButton = createButton("Left");
        JButton rightButton = createButton("Right");

        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createSequentialGroup()
                .addGroup(
                    inputPanelLayout.createParallelGroup()
                        .addComponent(numberButtons.get(0), DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(numberButtons.get(3), DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(numberButtons.get(6), DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(clearButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(leftButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, Short.MAX_VALUE)
                )
                .addGroup(
                    inputPanelLayout.createParallelGroup()
                        .addComponent(numberButtons.get(1), DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(numberButtons.get(4), DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(numberButtons.get(7), DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(upButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(downButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                )
                .addGroup(
                    inputPanelLayout.createParallelGroup()
                        .addComponent(numberButtons.get(2), DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(numberButtons.get(5), DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(numberButtons.get(8), DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(rightButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, Short.MAX_VALUE)
                )
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createSequentialGroup()
                .addGroup(
                    inputPanelLayout.createParallelGroup()
                        .addComponent(numberButtons.get(0))
                        .addComponent(numberButtons.get(1))
                        .addComponent(numberButtons.get(2))
                )
                .addGroup(
                    inputPanelLayout.createParallelGroup()
                        .addComponent(numberButtons.get(3))
                        .addComponent(numberButtons.get(4))
                        .addComponent(numberButtons.get(5))
                )
                .addGroup(
                    inputPanelLayout.createParallelGroup()
                        .addComponent(numberButtons.get(6))
                        .addComponent(numberButtons.get(7))
                        .addComponent(numberButtons.get(8))
                )
                .addGroup(
                    inputPanelLayout.createParallelGroup()
                        .addComponent(clearButton)
                )
                .addGroup(
                    inputPanelLayout.createParallelGroup()
                        .addComponent(upButton)
                )
                .addGroup(
                    inputPanelLayout.createParallelGroup()
                        .addComponent(leftButton)
                        .addComponent(rightButton)
                )
                .addGroup(
                    inputPanelLayout.createParallelGroup()
                        .addComponent(downButton)
                )
        );

        this.buttonPanel = new JPanel();
        this.buttonPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        GroupLayout buttonPanelLayout = createGroupLayout(this.buttonPanel);

        this.startButton = createButton("Start");
        this.pauseButton = createButton("Pause");
        this.stopButton = createButton("Stop");
        this.resetButton = createButton("Reset");
        this.clearAllButton = createButton("Clear All");
        this.saveSetupButton = createButton("Save");
        this.loadSetupButton = createButton("Load");

        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createSequentialGroup()
                .addGroup(
                    buttonPanelLayout.createParallelGroup()
                        .addComponent(this.startButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(this.stopButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(this.clearAllButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(this.saveSetupButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                )
                .addGroup(
                    buttonPanelLayout.createParallelGroup()
                        .addComponent(this.pauseButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(this.resetButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(this.loadSetupButton, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                )
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createSequentialGroup()
                .addGroup(
                    buttonPanelLayout.createParallelGroup()
                        .addComponent(this.startButton)
                        .addComponent(this.pauseButton)
                )
                .addGroup(
                    buttonPanelLayout.createParallelGroup()
                        .addComponent(this.stopButton)
                        .addComponent(this.resetButton)
                )
                .addGroup(
                    buttonPanelLayout.createParallelGroup()
                        .addComponent(this.clearAllButton)
                )
                .addGroup(
                    buttonPanelLayout.createParallelGroup()
                        .addComponent(this.saveSetupButton)
                        .addComponent(this.loadSetupButton)
                )

        );

        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup()
                .addComponent(this.rulesPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.logicStagesPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.inputPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.buttonPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createSequentialGroup()
                .addComponent(this.rulesPanel)
                .addComponent(this.logicStagesPanel)
                .addComponent(this.inputPanel)
                .addComponent(this.buttonPanel)
        );

        makeNotFocusable(this.controlPanel);
        enablePanelsForStopped();
    }

    private GroupLayout createGroupLayout(JPanel panel) {
        GroupLayout rulesPanelLayout = new GroupLayout(panel);
        panel.setLayout(rulesPanelLayout);
        return rulesPanelLayout;
    }

    private JButton createButton(String label) {
        JButton startButton = new JButton(label);
        startButton.setActionCommand(label.toLowerCase().replace("\\s", ""));
        startButton.addActionListener(this);
        return startButton;
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
            enablePanelsForStarted();
            this.gameState.start(buildGameParameters());
        } else if (actionCommand.equals("pause")) {
            this.gameState.togglePause();
            if (this.pauseButton.getText().equals("Pause")) {
                this.pauseButton.setText("Resume");
            } else if (this.pauseButton.getText().equals("Resume")) {
                this.pauseButton.setText("Pause");
            }
        } else if (actionCommand.equals("stop")) {
            enablePanelsForStopped();
            this.gameState.stop();
        } else if (actionCommand.equals("reset")) {
            this.gameState.reset();
        } else if (actionCommand.equals("done")) {
            enablePanelsForStopped();
        } else if (actionCommand.equals("up")) {
            this.gameState.moveSelectedCellUp();
        } else if (actionCommand.equals("down")) {
            this.gameState.moveSelectedCellDown();
        } else if (actionCommand.equals("left")) {
            this.gameState.moveSelectedCellLeft();
        } else if (actionCommand.equals("right")) {
            this.gameState.moveSelectedCellRight();
        } else if (actionCommand.equals("clear")) {
            this.gameState.getSelectedCells().forEach(cell -> cell.setInitialValue(null));
        } else if (actionCommand.equals("clearAll")) {
            this.gameState.clear();
        } else if (actionCommand.equals("save")) {
            File file = new File("save.txt");
            try {
                FileWriter fileWriter = new FileWriter(file);
                for (Cell cell : this.gameState.getCells()) {
                    fileWriter.write(Optional.ofNullable(cell.getInitialValue()).map(String::valueOf).orElse("0"));
                }
                fileWriter.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else if (actionCommand.equals("load")) {
            File file = new File("save.txt");
            try {
                FileReader fileReader = new FileReader(file);
                List<Cell> cellsList = this.gameState.getCells();
                for (Cell cell : cellsList) {
                    int value = Integer.parseInt(String.valueOf((char) fileReader.read()));
                    if (value > 0) {
                        cell.setInitialValue(value);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        char[] chars = actionCommand.toCharArray();
        if (chars.length > 0) {
            char aChar = chars[0];
            if (aChar >= '1' && aChar <= '9') {
                this.gameState.getSelectedCells().forEach(cell -> cell.setInitialValue(Integer.valueOf(String.valueOf(aChar))));
            }
        }

        repaint();
    }

    private GameParameters buildGameParameters() {
        List<RuleIdentifier> rules = new ArrayList<>();
        List<LogicStageIdentifier> logicStages = new ArrayList<>();
        if (this.standardRulesCheckbox.isSelected()) {
            rules.add(SUDOKU_ROW_RULE);
            rules.add(SUDOKU_COLUMN_RULE);
            rules.add(SUDOKU_REGION_RULE);
        }
        if (this.leadingDiagonalsRuleCheckbox.isSelected()) {
            rules.add(LEADING_DIAGONAL_DOWN_RULE);
            rules.add(LEADING_DIAGONAL_UP_RULE);
        }
        if (this.knightsMoveRuleCheckbox.isSelected()) {
            rules.add(KNIGHTS_MOVE_RULE);
        }
        if (this.kingsMoveRuleCheckbox.isSelected()) {
            rules.add(KINGS_MOVE_RULE);
        }

        if (this.magicCentreSquareCheckbox.isSelected()) {
            logicStages.add(CENTRE_REGION_MAGIC_SQUARE);
        }

        if (this.singleCellEliminationCheckbox.isSelected()) {
            logicStages.add(SINGLE_CELL_ELIMINATION);
        }
        if (this.sharedPossibilitiesEliminationCheckbox.isSelected()) {
            logicStages.add(SHARED_POSSIBILITIES_ELIMINATION);
        }
        if (this.multipleCellEliminationCheckbox.isSelected()) {
            logicStages.add(MULTIPLE_CELL_ELIMINATION);
        }
        if (this.onlyCellWithPossibilityCheckbox.isSelected()) {
            logicStages.add(ONLY_CELL_WITH_POSSIBILITY);
        }

        return new GameParameters(rules, logicStages);
    }

    private void enablePanelsForStarted() {
        setComponentEnabled(this.rulesPanel, false);
        setComponentEnabled(this.logicStagesPanel, false);
        setComponentEnabled(this.inputPanel, false);

        setComponentEnabled(this.startButton, false);
        setComponentEnabled(this.pauseButton, true);
        setComponentEnabled(this.stopButton, true);
        setComponentEnabled(this.resetButton, false);
        setComponentEnabled(this.clearAllButton, false);
        setComponentEnabled(this.saveSetupButton, false);
        setComponentEnabled(this.loadSetupButton, false);
    }

    private void enablePanelsForStopped() {
        setComponentEnabled(this.rulesPanel, true);
        setComponentEnabled(this.logicStagesPanel, true);
        setComponentEnabled(this.inputPanel, true);

        setComponentEnabled(this.startButton, true);
        setComponentEnabled(this.pauseButton, false);
        setComponentEnabled(this.stopButton, false);
        setComponentEnabled(this.resetButton, true);
        setComponentEnabled(this.clearAllButton, true);
        setComponentEnabled(this.saveSetupButton, true);
        setComponentEnabled(this.loadSetupButton, true);
    }

    private void setComponentEnabled(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof Container) {
            Arrays.stream(((Container) component).getComponents()).forEach(subComponent -> setComponentEnabled(subComponent, enabled));
        }
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
            this.gameState.getSelectedCells().forEach(cell -> cell.setInitialValue(Integer.valueOf(String.valueOf(e.getKeyChar()))));
        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            this.gameState.getSelectedCells().forEach(cell -> cell.setInitialValue(null));
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
