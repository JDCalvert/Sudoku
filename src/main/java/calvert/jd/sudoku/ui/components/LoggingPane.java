package calvert.jd.sudoku.ui.components;

import calvert.jd.sudoku.actioncontrol.GameLoggingListener;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class LoggingPane extends JScrollPane implements GameLoggingListener {

    private final JScrollBar verticalScrollBar;
    private final StyledDocument doc;

    public LoggingPane() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);

        setViewportView(textPane);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        setMinimumSize(new Dimension(0, 0));

        this.doc = textPane.getStyledDocument();
        this.verticalScrollBar = getVerticalScrollBar();
    }

    @Override
    public void clear() {
        try {
            this.doc.remove(0, this.doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(String line) {
        try {
            this.doc.insertString(this.doc.getLength(), line + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        this.verticalScrollBar.setValue(this.verticalScrollBar.getMaximum());
        super.paint(g);
    }
}
