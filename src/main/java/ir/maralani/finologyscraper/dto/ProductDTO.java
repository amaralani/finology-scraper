package ir.maralani.finologyscraper.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private String price;
    private String description;
    private String extraInformation;
    private String link;
}
