package ir.maralani.finologyscraper.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Product entity.
 *
 * @author Amir
 */
@Entity
@Table(name = "product")
@Data
public class Product implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Float price;
    @Column(nullable = false)
    private String description;
    private String extraInformation;

    @Override
    public String toString() {
        return "[\n" +
                "Name: " + name + "\n" +
                "Price: " + price + "\n" +
                "Description: " + description + '\n' +
                "ExtraInformation: " + extraInformation + '\n' +
                "]\n";
    }
}
