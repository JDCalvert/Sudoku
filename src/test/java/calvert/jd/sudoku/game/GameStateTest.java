package calvert.jd.sudoku.game;

import calvert.jd.sudoku.game.logic.LogicStageIdentifier;
import calvert.jd.sudoku.game.save.SaveGame;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public class GameStateTest {

    private final GameState gameState = new GameState();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final File file;

    public GameStateTest(File file) {
        this.file = file;

        this.gameState.setDoUpdates(false);
    }

    @Parameters(name = "{0}")
    public static List<File> getFiles() {
        File savesFolder = new File("src\\main\\resources\\saves");
        File[] saves = savesFolder.listFiles();
        if (nonNull(saves)) {
            return Arrays.stream(saves).collect(Collectors.toList());
        } else {
            return emptyList();
        }
    }

    @Test(timeout = 5000)
    public void completePuzzle() throws Exception {
        SaveGame saveGame = this.objectMapper.readValue(this.file, SaveGame.class);

        this.gameState.getCells().forEach(cell ->
            saveGame.getCells().stream()
                .filter(parameterCell -> cell.getI() == parameterCell.getI() && cell.getJ() == parameterCell.getJ())
                .findFirst()
                .ifPresentOrElse(
                    parameterCell -> cell.setInitialValue(parameterCell.getValue()),
                    () -> cell.setInitialValue(null)
                )
        );

        GameParameters parameters = new GameParameters(
            saveGame.getRules(),
            Stream.concat(
                saveGame.getConstraints().stream(),
                Arrays.stream(LogicStageIdentifier.values()).filter(logicStageIdentifier -> !logicStageIdentifier.isConstraint())
            ).collect(Collectors.toList())
        );

        this.gameState.start(parameters);

        pollUntilDone();

        assertThat(this.gameState.isComplete(), is(true));

        System.out.println(this.file.getName() + ": numQueueProcesses=" + this.gameState.getNumQueueProcesses() + " numUpdates=" + this.gameState.getNumUpdates());
    }

    private void pollUntilDone() throws InterruptedException {
        while (!this.gameState.isComplete() && !this.gameState.isError()) {
            Thread.sleep(100);
        }
    }
}