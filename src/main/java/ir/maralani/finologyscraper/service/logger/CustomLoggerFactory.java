package ir.maralani.finologyscraper.service.logger;

import java.io.IOException;

/**
 * Factory to create loggers based on type.
 *
 * @author Amir
 */
public class CustomLoggerFactory {

    /**
     * Create a logger based on type.
     *
     * @param loggerType Type of the logger to be created.
     * @return A new instance of the requested logger.
     * @throws IOException If the requested {@link FileLogger} cannot be created.
     */
    public static CustomLogger getLogger(DefaultLoggerService.LoggerType loggerType) throws IOException {
        switch (loggerType) {
            case FILE:
                return new FileLogger();
            case CONSOLE:
            default:
                return new ConsoleLogger();
        }
    }
}
