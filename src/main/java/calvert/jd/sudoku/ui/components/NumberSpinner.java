package calvert.jd.sudoku.ui.components;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.function.Consumer;

public class NumberSpinner extends JSpinner implements ChangeListener {

    private final Consumer<Integer> changeConsumer;
    private final SpinnerNumberModel numberModel;

    public NumberSpinner(Consumer<Integer> changeConsumer, SpinnerNumberModel numberModel) {
        super(numberModel);

        this.numberModel = numberModel;
        this.changeConsumer = changeConsumer;

        this.addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source == this) {
            this.changeConsumer.accept(this.numberModel.getNumber().intValue());
        }
    }
}
