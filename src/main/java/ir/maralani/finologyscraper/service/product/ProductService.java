package ir.maralani.finologyscraper.service.product;

import ir.maralani.finologyscraper.model.Product;

import java.util.List;
import java.util.Set;

/**
 * Handles all methods related to processing a {@link Product}.
 *
 * @author Amir
 */
public interface ProductService {

    /**
     * Persist the {@link Product}.
     *
     * @param product The item to be persisted.
     */
    void save(Product product);

    /**
     * Returns a set of product paths which are inserted in the DB.
     *
     * @return A set of {@link String} paths.
     */
    Set<String> getScannedPages();

    /**
     * Get all available products in DB.
     *
     * @return A list of {@link Product}.
     */
    List<Product> getAllProducts();
}
