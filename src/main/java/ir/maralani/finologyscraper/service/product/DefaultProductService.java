package ir.maralani.finologyscraper.service.product;

import ir.maralani.finologyscraper.model.Product;
import ir.maralani.finologyscraper.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of {@link ProductService}.
 *
 * @author Amir
 */
@Service
public class DefaultProductService implements ProductService {

    /**
     * Product Repository.
     */
    private final ProductRepository productRepository;

    /**
     * Holds the paths of the products that have been inserted in the DB.
     */
    private final Set<String> scannedPages = new HashSet<>();

    /**
     * Constructor.
     *
     * @param productRepository Product DAO.
     */
    public DefaultProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public Set<String> getScannedPages() {
        return scannedPages;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
