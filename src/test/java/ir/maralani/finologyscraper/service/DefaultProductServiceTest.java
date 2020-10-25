package ir.maralani.finologyscraper.service;

import ir.maralani.finologyscraper.service.product.DefaultProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DefaultProductServiceTest {
    @Autowired
    public DefaultProductService defaultProductService;

    @Test
    public void getScannedPages(){
        Assertions.assertThat(defaultProductService.getScannedPages()).isEmpty();
        defaultProductService.getScannedPages().add("new path");
        Assertions.assertThat(defaultProductService.getScannedPages().size()).isEqualTo(1);
    }
}
