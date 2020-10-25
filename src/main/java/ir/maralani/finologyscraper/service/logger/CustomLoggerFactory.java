package ir.maralani.finologyscraper.service.logger;

import java.io.IOException;

public class CustomLoggerFactory {

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
