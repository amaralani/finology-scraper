package ir.maralani.finologyscraper.service.logger;

import ir.maralani.finologyscraper.model.Product;

/**
 * Logger interface.
 *
 * @author Amir
 */
public interface CustomLogger {

    /**
     * Display the product information.
     *
     * @param product Product to be displayed.
     */
    void log(Product product);
}
