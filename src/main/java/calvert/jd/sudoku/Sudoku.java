package calvert.jd.sudoku;

import calvert.jd.sudoku.panels.SudokuFrame;

import javax.swing.*;
import java.awt.*;

public class Sudoku {

    public static void main(String[] args) {
        String windowsLAF=  "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        try  {
            UIManager.setLookAndFeel(windowsLAF);
        }
        catch (Exception e)  {
            e.printStackTrace();
        }
        EventQueue.invokeLater(() -> new SudokuFrame().setVisible(true));
    }
}
