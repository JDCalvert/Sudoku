package calvert.jd.sudoku.panels;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class SourceLinkedCheckBox extends JCheckBox implements ActionListener {

    private final Consumer<Boolean> consumer;

    public SourceLinkedCheckBox(String text, Consumer<Boolean> consumer) {
        super(text);
        this.consumer = consumer;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this && nonNull(this.consumer)) {
            this.consumer.accept(isSelected());
        }
    }
}
