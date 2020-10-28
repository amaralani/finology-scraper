package ir.maralani.finologyscraper.service.logger;

import ir.maralani.finologyscraper.model.Product;

/**
 * Methods related to displaying the products.
 *
 * @author Amir
 */
public interface LoggerService {
    /**
     * Display the product through appropriate logger.
     *
     * @param product Product to be logged.
     */
    void log(Product product);
}
