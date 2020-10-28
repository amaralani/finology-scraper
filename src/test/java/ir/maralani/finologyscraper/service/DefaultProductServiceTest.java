package ir.maralani.finologyscraper.service;

import ir.maralani.finologyscraper.model.Product;
import ir.maralani.finologyscraper.repository.ProductRepository;
import ir.maralani.finologyscraper.service.product.DefaultProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for {@link DefaultProductService}.
 */
@SpringBootTest
public class DefaultProductServiceTest {

    /**
     * Actual service to be tested.
     */
    @Autowired
    public DefaultProductService defaultProductService;

    /**
     * Mocked Product DAO.
     */
    @MockBean
    public ProductRepository productRepositoryMock;

    /**
     * Dummy list for products.
     */
    private List<Product> dummyProductList;

    @BeforeEach
    public void before() {
        dummyProductList = new ArrayList<>();
        dummyProductList.add(createProduct("1"));
        dummyProductList.add(createProduct("b"));
        Mockito.doReturn(dummyProductList).when(productRepositoryMock).findAll();
    }

    @Test
    public void getScannedPages() {
        Assertions.assertThat(defaultProductService.getScannedPages()).isEmpty();
        defaultProductService.getScannedPages().add("new path");
        Assertions.assertThat(defaultProductService.getScannedPages().size()).isEqualTo(1);
    }

    @Test
    public void getProductList() {
        List<Product> allProducts = defaultProductService.getAllProducts();

        Assertions.assertThat(allProducts).containsAll(dummyProductList);
    }


    private Product createProduct(String id) {
        Product product = new Product();
        product.setName("name" + id);
        product.setDescription("description" + id);
        product.setPath("path" + id);
        product.setPrice("price" + id);
        product.setExtraInformation("extra-info" + id);
        return product;
    }
}
