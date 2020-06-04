package calvert.jd.sudoku.game.save;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SaveCell {
    private final int i;
    private final int j;
    private final int value;

    @JsonCreator
    public SaveCell(
        @JsonProperty("i") int i,
        @JsonProperty("j") int j,
        @JsonProperty("value") int value
    ) {
        this.i = i;
        this.j = j;
        this.value = value;
    }

    public int getI() {
        return this.i;
    }

    public int getJ() {
        return this.j;
    }

    public int getValue() {
        return this.value;
    }
}
