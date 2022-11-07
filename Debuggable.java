package blockchain;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface Debuggable {
    Logger logger = Logger.getGlobal();

    default void turnDebugOn() {
        logger.setLevel(Level.ALL);
    }

    default void turnDebugOff() {
        logger.setLevel(Level.OFF);
    }
}
