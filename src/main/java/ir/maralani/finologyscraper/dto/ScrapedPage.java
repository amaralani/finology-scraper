package ir.maralani.finologyscraper.dto;

import ir.maralani.finologyscraper.model.Product;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Holds the necessary data of a scraped page.
 *
 * @author Amir
 */
@Data
public class ScrapedPage implements Serializable {
    /**
     * The product data in the scraped page.
     */
    private Product product;

    /**
     * All the links inside the path.
     */
    private Set<String> links;
}
