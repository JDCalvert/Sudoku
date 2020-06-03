package calvert.jd.sudoku.ui.components;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class LinkedCheckBox extends JCheckBox implements ActionListener {

    private final Consumer<Boolean> changeConsumer;

    public LinkedCheckBox(String label, Consumer<Boolean> changeConsumer) {
        super(label);

        addActionListener(this);
        this.changeConsumer = changeConsumer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this) {
            this.changeConsumer.accept(this.isSelected());
        }
    }
}
