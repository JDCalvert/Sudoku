package calvert.jd.sudoku.actioncontrol;

/**
 * Interface for logging game data
 */
public interface GameLoggingListener {

    /**
     * Clear all logging
     */
    void clear();

    /**
     * Accept a line of logging
     *
     * @param text The line to log
     */
    void log(String text);
}
