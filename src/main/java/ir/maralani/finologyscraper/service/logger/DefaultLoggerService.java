package ir.maralani.finologyscraper.service.logger;

import ir.maralani.finologyscraper.model.Product;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Default implementation of {@link LoggerService}.
 *
 * @author Amir
 */
@Service
public class DefaultLoggerService implements LoggerService {

    /**
     * Custom logger to log input.
     */
    private final CustomLogger customLogger;

    /**
     * Constructor.
     *
     * @param environment Spring Environment.
     * @throws IOException When an exception happens during initialization of a logger.
     */
    public DefaultLoggerService(Environment environment) throws IOException {
        customLogger = CustomLoggerFactory.getLogger(LoggerType.valueOf(environment.getProperty("logger.service.type")));
    }

    /**
     * Log the input.
     *
     * @param product Input {@link Product} to be logged.
     */
    @Override
    public void log(Product product) {
        customLogger.log(product);
    }

    /**
     * Types of currently supported loggers.
     * Display of products on web is not considered a logger.
     */
    public enum LoggerType {
        CONSOLE,
        FILE
    }
}

