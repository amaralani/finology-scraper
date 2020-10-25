package ir.maralani.finologyscraper.dto;

import ir.maralani.finologyscraper.model.Product;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
     * Related products gathered as a list of paths.
     */
    private List<String> relatedProducts;
}
