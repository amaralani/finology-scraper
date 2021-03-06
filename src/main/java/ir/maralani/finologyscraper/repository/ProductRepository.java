package ir.maralani.finologyscraper.repository;

import ir.maralani.finologyscraper.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Product DAO to for accessing and manipulating {@link Product}s.
 *
 * @author Amir
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
