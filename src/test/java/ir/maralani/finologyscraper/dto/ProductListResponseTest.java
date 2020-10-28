package ir.maralani.finologyscraper.dto;

import ir.maralani.finologyscraper.dto.ProductDTO;
import ir.maralani.finologyscraper.dto.ProductListResponse;
import ir.maralani.finologyscraper.model.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Test for {@link ProductListResponse}.
 *
 * @author Amir
 */
@SpringBootTest
public class ProductListResponseTest {

    /**
     * Call {@link ProductListResponse#fromProductList(List)} with empty list.
     * Should return a {@link ProductListResponse} with empty productDTO list.
     */
    @Test
    public void emptyList_ShouldReturnEmptyProducts() {
        ProductListResponse productListResponse = new ProductListResponse();
        productListResponse.fromProductList(new ArrayList<>());
        Assertions.assertThat(productListResponse.getProducts()).isEmpty();
    }

    /**
     * Sanity test for the converter.
     * Check if all of the fields are converted properly.
     */
    @Test
    public void filledArray_ShouldConvertSuccessfully() {
        Product product = new Product();
        product.setDescription("desc");
        product.setExtraInformation("extra info");
        product.setName("name");
        product.setPrice("price");
        product.setPath("path");

        ProductListResponse productListResponse = new ProductListResponse();
        productListResponse.fromProductList(Collections.singletonList(product));

        Assertions.assertThat(productListResponse.getProducts()).isNotEmpty();
        Assertions.assertThat(productListResponse.getProducts()).hasSize(1);

        Optional<ProductDTO> optionalProductDTO = productListResponse.getProducts().stream().findFirst();

        Assertions.assertThat(optionalProductDTO).isPresent();
        Assertions.assertThat(optionalProductDTO.get().getDescription()).isEqualTo(product.getDescription());
        Assertions.assertThat(optionalProductDTO.get().getExtraInformation()).isEqualTo(product.getExtraInformation());
        Assertions.assertThat(optionalProductDTO.get().getLink()).isEqualTo(product.getPath());
        Assertions.assertThat(optionalProductDTO.get().getName()).isEqualTo(product.getName());
        Assertions.assertThat(optionalProductDTO.get().getPrice()).isEqualTo(product.getPrice());
    }
}
