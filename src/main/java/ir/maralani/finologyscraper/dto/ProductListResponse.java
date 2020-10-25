package ir.maralani.finologyscraper.dto;

import ir.maralani.finologyscraper.model.Product;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A DTO to hold product list data.
 *
 * @author Amir
 */
@Data
public class ProductListResponse {
    private List<ProductDTO> products;

    /**
     * Converter method to simplify the conversion of a list of {@link Product} to a list of {@link ProductDTO}.
     *
     * @param products A list of {@link Product}.
     * @return {@code this} instance, with filled data.
     */
    public ProductListResponse fromProductList(List<Product> products) {
        this.products = products.stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();
            product.setName(product.getName());
            product.setPrice(product.getPrice());
            product.setDescription(product.getDescription());
            product.setExtraInformation(product.getExtraInformation());
            return productDTO;
        }).collect(Collectors.toList());
        return this;
    }
}
