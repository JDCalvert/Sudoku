package calvert.jd.sudoku.actioncontrol;

import calvert.jd.sudoku.game.GameState;

/**
 * Listener interface for updates from a {@link GameState} object.
 */
public interface GameStateListener {

    /**
     * This will be called when the GameState updates
     */
    void update();

    /**
     * This will be called when the GameState has finished processing, either because of error or completion
     */
    void done();
}
