package ir.maralani.finologyscraper.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private Float price;
    private String description;
    private String extraInformation;
}
